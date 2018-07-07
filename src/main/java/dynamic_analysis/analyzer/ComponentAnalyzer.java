package androML.dynamic_analysis.analyzer;

import androML.dynamic_analysis.adb.AdbAdapter;
import androML.dynamic_analysis.App;
import androML.dynamic_analysis.runner.ClickRunner;
import androML.static_analysis.reports.Report;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final public class ComponentAnalyzer extends DynamicAnalyzer {
    private static final Logger LOG = LoggerFactory.getLogger(ComponentAnalyzer.class);

    public ComponentAnalyzer(String deviceID) {
        aa = new AdbAdapter(deviceID);
        clickRunner = new ClickRunner();
        super.LOG = LOG;
    }

    @Override
    public void analyze(App app, int reboots) {
        String packageName = app.getPackageName();
        LOG.info("Start component (non-interactive) dynamic analysis of app {}.", packageName);
        if (aa.installApp(app.getPath())) {
            initializeComponentAnalysis(packageName);
            startActivities(app);
            startServices(app);
            finalizeAnalysis(app, reboots, packageName);
            LOG.info("Finished component (non-interactive) dynamic analysis of app {}.", packageName);
        } else {
            LOG.error("APP {} could not be installed and analyzed.", packageName);
        }
    }

    private void startServices(App app) {
        LOG.info("Launching found services.");
        for (String service : app.getServices()) {
            LOG.info("Launching service: {} ", service);
            aa.startService(app.getPackageName(), service);
            aa.sleep(2000);
            checkForErrorAndPermissionDialog();
        }
    }

    private void initializeComponentAnalysis(String packageName) {
        LOG.info("Initialize component Analysis.");
        initializeAnalysis(packageName);
    }

    private void startActivities(App app) {
        for (String activity : app.getActivities()) {
            LOG.info("Launching activity {}.", activity);
            aa.startActivity(app.getPackageName(), activity);
            aa.sleep(2000);
            checkForErrorAndPermissionDialog();
        }
    }

    @Override
    public Report getReport() {
        return null;
    }


}
