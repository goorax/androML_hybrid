package androML.dynamic_analysis;

import androML.AndroMLConfig;
import androML.database.Database;
import androML.database.ElasticMapper;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

final public class DynamicAnalysis {
    private static final Logger LOG = LoggerFactory.getLogger(DynamicAnalysis.class);

    public static final String[] SOURCES = {"ApkMetaReport.app_file_name", "ApkMetaReport.pkg_name",
            "ApkMetaReport.activities", "ApkMetaReport.services"};

    private static final String VIRUS_SHARE_PREFIX = "VirusShare_";
    public static final String[] DEVICES = {"0654413c0ac5c1c1", "03e1adf2d0081a68","0dd5774af1644d5b"};
    public static final int DEVICE_AMOUNT = DEVICES.length;

    private Database db;
    private AndroMLConfig config;

    public DynamicAnalysis(AndroMLConfig config, Database db) {
        this.config = config;
        this.db = db;
    }

    public void checkForStartingAnalysis() {
        if (config.isDynamicAnalysis()) {
            Set<App> apps = initializeDynamicAnalysis();
            String targetIndex = receiveTargetIndex();
            createDynamicDatabase(targetIndex);
            startDynamicAnalysis(apps, targetIndex);
        }
    }

    private String receiveTargetIndex() {
        String targetIndex;
        if (config.isDynamicAnalysisInteractive()) {
            targetIndex = config.getDynamicIndexInteractive();
        } else {
            targetIndex = config.getDynamicIndexSimple();
        }
        return targetIndex;
    }

    private Set<App> initializeDynamicAnalysis() {
        List<String> ids = db.receiveAllDatabaseIdsFrom(config.getElasticIndex(),
                config.getDynamicQueryFrom(), config.getDynamicQuerySize());
        return prepareApps(ids);
    }


    private void createDynamicDatabase(String targetIndex) {
        if (!db.isDatabasePresent(targetIndex)) {
            db.createDatabaseWithMapping(targetIndex, ElasticMapper.getDynamicReportMapping());
        }
    }

    public Set<App> prepareApps(List<String> ids) {
        Set<App> apps = new HashSet<>();
        for (String id : ids) {
            if (!id.startsWith(VIRUS_SHARE_PREFIX)) {
                JSONObject result = db.receiveCustomSourcesFromID(config.getElasticIndex(), id, SOURCES);
                App app = new App(result, config.getPath());
                apps.add(app);
            }
        }
        return apps;
    }

    private void startDynamicAnalysis(Set<App> apps, String targetIndex) {
        ExecutorService es = Executors.newFixedThreadPool(DEVICE_AMOUNT);
        List<Callable<Object>> worker = new ArrayList<Callable<Object>>();
        Queue<App> appQueue = new ConcurrentLinkedQueue<>();
        appQueue.addAll(apps);
        for (String deviceID : DEVICES) {
            ConcurrentAnalysis daThread = new ConcurrentAnalysis(appQueue, targetIndex, deviceID);
            worker.add(daThread);
        }

        try {
            List<Future<Object>> results = es.invokeAll(worker);
        } catch (InterruptedException e) {
            LOG.error("InvokeAll worker failed.");
        }
    }
}
