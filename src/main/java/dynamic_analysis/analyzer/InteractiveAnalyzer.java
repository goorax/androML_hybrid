package androML.dynamic_analysis.analyzer;

import androML.AndroMLConfig;
import androML.dynamic_analysis.adb.AdbAdapter;
import androML.dynamic_analysis.App;
import androML.dynamic_analysis.clicks.ClickSequence;
import androML.dynamic_analysis.runner.ClickRunner;
import androML.dynamic_analysis.clicks.ClickExtractor;
import androML.static_analysis.reports.Report;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

final public class InteractiveAnalyzer extends DynamicAnalyzer {
    private static final Logger LOG = LoggerFactory.getLogger(InteractiveAnalyzer.class);

    private ConcurrentLinkedQueue<ClickSequence> clicks;
    private Set<Integer> processedViews;
    private AndroMLConfig config;
    private ClickExtractor clickExtractor;

    public InteractiveAnalyzer(AndroMLConfig config, String deviceID) {
        this.config = config;
        clicks = new ConcurrentLinkedQueue<>();
        processedViews = new HashSet<>();
        aa = new AdbAdapter(deviceID);
        clickRunner = new ClickRunner();
        clickExtractor = new ClickExtractor();
        super.LOG = LOG;
    }

    @Override
    public void analyze(App app, int reboots) {
        String packageName = app.getPackageName();
        LOG.info("Start interactive dynamic analysis of app {}.", packageName);
        if (aa.installApp(app.getPath())) {
            initializeInteractiveAnalysis(app);
            performInteractiveAnalysis();
            finalizeAnalysis(app, reboots, packageName);
            LOG.info("Finished interactive dynamic analysis of app {}.", packageName);
        } else {
            LOG.error("APP {} could not be installed and analyzed.", packageName);
        }
    }

    private void performInteractiveAnalysis() {
        LOG.info("Perform interactive analysis...");
        long threshold = config.getDynamicTimeThresholdInteractive();
        LOG.info("Time threshold is set to " + threshold + " ms.");
        long startTime = System.currentTimeMillis();
        while (!clicks.isEmpty()) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            if (elapsedTime >= threshold) {
                LOG.info("Elapsed time hit threshold of " + threshold + " ms.");
                break;
            }
            startClickRunner();
        }
        LOG.info("Ineractive analysis finished.");
    }


    private void initializeInteractiveAnalysis(App app) {
        LOG.info("Initialize interactive analysis.");
        initializeAnalysis(app.getPackageName());
        buildInitialClicks(app);
    }

    private void buildInitialClicks(App app) {
        LOG.info("Build initial clicks for analysis.");
        Pair<String, String> pkgAct = aa.getFocusedPkgAndActivity();
        String pkg = pkgAct.getKey();
        String activity = pkgAct.getValue();
        String composed = pkg + "." + activity;
        boolean containsActivity = app.getActivities().contains(activity) || app.getActivities().contains(composed);
        if (app.getPackageName().equals(pkg) && containsActivity) {
            collectInitialClicks(pkg, activity);
        }
    }

    private void startClickRunner() {
        ClickSequence currentClick = clicks.poll();
        LOG.info("Click queue size: {}", clicks.size());
        String view = clickRunner.returnViewAfterClicking(aa, currentClick);
        Integer viewHash = view.hashCode();
        if (view != "" && !processedViews.contains(viewHash)) {
            LOG.info("Click made view change! Extracting new clicks ...");
            List<ClickSequence> freshClicks = clickExtractor.extractClicksFromView(currentClick, view);
            LOG.info("Found {} new clicks.", freshClicks.size());
            clicks.addAll(freshClicks);
        }
    }

    private void collectInitialClicks(String packageName, String activity) {
        ClickSequence initialClick = new ClickSequence("", packageName, activity, new ArrayList<>());
        String initialView = clickRunner.returnViewAfterClicking(aa, initialClick);
        processedViews.add(initialView.hashCode());
        List<ClickSequence> freshClicks = clickExtractor.extractClicksFromView(initialClick, initialView);
        clicks.addAll(freshClicks);
    }

    @Override
    public Report getReport() {
        return null;
    }
}
