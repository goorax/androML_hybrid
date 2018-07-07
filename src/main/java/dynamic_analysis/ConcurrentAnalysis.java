package androML.dynamic_analysis;

import androML.AndroMLConfig;
import androML.database.Database;
import androML.database.ElasticAdapter;
import androML.dynamic_analysis.analyzer.InteractiveAnalyzer;
import androML.dynamic_analysis.analyzer.ComponentAnalyzer;
import androML.dynamic_analysis.analyzer.DynamicAnalyzer;
import org.json.JSONObject;

import java.util.Queue;
import java.util.concurrent.Callable;

public class ConcurrentAnalysis implements Callable {
    private static final String API_CALLS = "api_calls";
    private static final String PKG_NAME = "pkg_name";
    private static final String SOURCE = "_source";

    private AndroMLConfig config;
    private DynamicAnalyzer da;
    private Database db;
    private Queue<App> appQueue;
    private String targetIndex;

    public ConcurrentAnalysis(Queue<App> appQueue, String targetIndex, String deviceID) {
        this.appQueue = appQueue;
        this.targetIndex = targetIndex;
        this.config = new AndroMLConfig();
        db = new ElasticAdapter(config);
        initializeDynamicAnalysis(deviceID);
    }

    private void initializeDynamicAnalysis(String deviceID) {
        if (config.isDynamicAnalysisInteractive()) {
            da = new InteractiveAnalyzer(config, deviceID);
        } else {
            da = new ComponentAnalyzer(deviceID);
        }
    }

    @Override
    public Object call() throws Exception {
        while (!appQueue.isEmpty()) {
            App app = appQueue.poll();
            analyzeApp(app);
        }
        return null;
    }

    private void analyzeApp(App app) {
        int reboots = 0;
        if (app.getActivities().size() <= config.getDynamicActivitiesSize()) {
            if (db.isDocumentPresent(app.getId(), targetIndex)) {
                JSONObject doc = db.receiveDocument(app.getId(), targetIndex);
                if (!hasReport(doc)) {
                    analyzeAndStoreReport(app, reboots);
                }
            } else {
                analyzeAndStoreReport(app, reboots);
            }
        }
    }

    private void analyzeAndStoreReport(App app, int reboots) {
        da.analyze(app, reboots);
        buildDynamicReport(app);
    }

    private void buildDynamicReport(App app) {
        JSONObject dynamicReport = new JSONObject();
        dynamicReport.put(PKG_NAME, app.getPackageName());
        String apiCallsAsString = transformApiCallsToString(app);
        dynamicReport.put(API_CALLS, apiCallsAsString);
        storeDynamicReport(app, dynamicReport);
    }

    private String transformApiCallsToString(App app) {
        StringBuilder apiCallsAsString = new StringBuilder();
        if (!app.getApiCalls().isEmpty()) {
            for (JSONObject jo : app.getApiCalls().values()) {
                apiCallsAsString.append(jo.toString());
                apiCallsAsString.append(",");
            }
            apiCallsAsString.deleteCharAt(apiCallsAsString.lastIndexOf(","));
        }
        return apiCallsAsString.toString();
    }

    private void storeDynamicReport(App app, JSONObject dynamicReport) {
        if (db.isDocumentPresent(app.getId(), targetIndex)) {
            if (!app.getApiCalls().isEmpty()) {
                db.updateDocument(app.getId(), dynamicReport, targetIndex);
            }
        } else {
            db.createDocument(app.getId(), dynamicReport, targetIndex);
        }
    }

    private boolean hasReport(JSONObject doc) {
        if (doc.has(SOURCE)) {
            JSONObject source = doc.getJSONObject(SOURCE);
            if (source.has(API_CALLS)) {
                String apiCalls = source.getString(API_CALLS);
                if (!apiCalls.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

}
