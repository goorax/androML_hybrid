package androML.dynamic_analysis;

import org.apache.commons.collections.map.UnmodifiableMap;
import org.apache.commons.collections.set.UnmodifiableSet;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

final public class App {
    private static final String APK_META_REPORT = "ApkMetaReport";
    private static final String SOURCE = "_source";
    private static final String PKG_NAME = "pkg_name";
    private static final String APP_FILE_NAME = "app_file_name";
    private static final String ACTIVITIES = "activities";
    private static final String SERVICES = "services";
    private static final String ID = "_id";
    private static final String DOLLAR = "$";
    private static final String ESCAPED_DOLLAR = "\\$";

    private String id;
    private String path;
    private String fileName;
    private String packageName;
    private Set<String> activities;
    private Set<String> services;
    private String plainApiLog;
    private Map<Integer, JSONObject> apiCalls;

    public App(String id, String path) {
        this.id = id;
        this.path = path;
        initializeDataStructures();
    }

    public App(JSONObject app, String path) {
        initializeDataStructures();
        if (app.has(ID)) {
            this.id = app.getString(ID);
        }
        processSourceFromJSON(app, path);
    }

    private void processSourceFromJSON(JSONObject app, String path) {
        if (app.has(SOURCE)) {
            JSONObject source = app.getJSONObject(SOURCE);
            if (source.has(APK_META_REPORT)) {
                JSONObject report = source.getJSONObject(APK_META_REPORT);
                setGeneralAppFields(report, path);
                setActivitiesField(report);
                setServicesField(report);
            }
        }
    }

    private void setServicesField(JSONObject report) {
        JSONArray services = report.getJSONArray(SERVICES);
        for (int i = 0; i < services.length(); i++) {
            this.services.add(services.getString(i));
        }
    }

    private void setActivitiesField(JSONObject report) {
        JSONArray activities = report.getJSONArray(ACTIVITIES);
        for (int i = 0; i < activities.length(); i++) {
            String activity = filterActivityForAnonymousNames(activities.getString(i));
            this.activities.add(activity);
        }
    }

    private void setGeneralAppFields(JSONObject report, String path) {
        this.fileName = report.getString(APP_FILE_NAME);
        this.path = path + getFileName();
        this.packageName = report.getString(PKG_NAME);
    }

    private void initializeDataStructures() {
        this.activities = new HashSet<>();
        this.services = new HashSet<>();
        this.apiCalls = new HashMap<>();
    }

    private String filterActivityForAnonymousNames(String activity) {
        if (activity.contains(DOLLAR)) {
            String[] splitted = activity.split(ESCAPED_DOLLAR);
            activity = splitted[0];
        }
        return activity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Set<String> getActivities() {
        return UnmodifiableSet.decorate(activities);
    }

    public Set<String> getActivitiesAsModifiableSet() {
        return activities;
    }

    public void setActivities(Set<String> activities) {
        this.activities.addAll(activities);
    }

    public Set<String> getServices() {
        return UnmodifiableSet.decorate(services);
    }

    public Set<String> getServicesAsModifiableSet() {
        return services;
    }

    public void setServices(Set<String> services) {
        this.services.addAll(services);
    }

    public String getPlainApiLog() {
        return plainApiLog;
    }

    public void setPlainApiLog(String plainApiLog) {
        this.plainApiLog = plainApiLog;
        setApiCalls();
    }

    private void setApiCalls() {
        ApiLogParser parser = new ApiLogParser();
        this.apiCalls.putAll(parser.parseApiLog(getPlainApiLog(), getPackageName()));
    }

    public Map<Integer, JSONObject> getApiCalls() {
        return UnmodifiableMap.decorate(apiCalls);
    }

    public String getPath() {
        return path;
    }
}
