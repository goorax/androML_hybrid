package androML.static_analysis.reports;

import net.dongliu.apk.parser.bean.ApkMeta;
import net.dongliu.apk.parser.bean.UseFeature;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import androML.static_analysis.analyzer.Manifest;

import java.util.List;

final public class ApkMetaReport implements Report {
    private static final Logger LOG = LoggerFactory.getLogger(ApkMetaReport.class);
    
    private static final String REPORT_NAME = "ApkMetaReport";

    public static final String APP_FILENAME = "app_file_name";
    public static final String APP_NAME = "app_name";
    public static final String PKG_NAME = "pkg_name";
    public static final String MIN_SDK = "min_sdk";
    public static final String MAX_SDK = "max_sdk";
    public static final String TARGET_SDK = "target_sdk";
    public static final String VERSION_NAME = "version_name";
    public static final String VERSION_CODE = "version_code";
    public static final String PERMISSIONS = "permissions";
    public static final String USEFEATURES = "usefeatures";
    public static final String ACTIVITIES = "activities";
    public static final String SERVICES = "services";
    public static final String RECEIVERS = "receiver";
    public static final String PROVIDERS = "providers";
    public static final String INTENTS = "intents";
    public static final String ADDITIONAL_PERMISSIONS = "additional_permissions";

    private final String appFileName;
    private final String appName;
    private final String pkgName;
    private final String maxSDKVersion;
    private final String minSDKVersion;
    private final String targetSDKVersion;
    private final String versionName;
    private final long versionCode;
    private final Manifest manifest;
    private final List<UseFeature> useFeatures;
    private final List<String> permissions;

    public ApkMetaReport(String appFileName, ApkMeta apkMeta, Manifest manifest) {
        this.appFileName = appFileName;
        this.manifest = manifest;
        appName = apkMeta.getName();
        pkgName = apkMeta.getPackageName();
        maxSDKVersion = apkMeta.getMaxSdkVersion();
        minSDKVersion = apkMeta.getMinSdkVersion();
        targetSDKVersion = apkMeta.getTargetSdkVersion();
        versionName = apkMeta.getVersionName();
        versionCode = apkMeta.getVersionCode();
        useFeatures = apkMeta.getUsesFeatures();
        permissions = apkMeta.getUsesPermissions();
    }

    @Override
    public JSONObject getReportAsJSON() {
        JSONObject reportJSON = new JSONObject();
        reportJSON.put(APP_FILENAME, getAppFileName());
        reportJSON.put(APP_NAME, getAppName());
        reportJSON.put(PKG_NAME, getPkgName());
        reportJSON.put(MIN_SDK, getMinSDKVersion());
        reportJSON.put(MAX_SDK, getMaxSDKVersion());
        reportJSON.put(TARGET_SDK, getTargetSDKVersion());
        reportJSON.put(VERSION_NAME, getVersionName());
        reportJSON.put(VERSION_CODE, getVersionCode());
        reportJSON.put(PERMISSIONS, getPermissions());
        reportJSON.put(USEFEATURES, getUseFeatures());

        Manifest.ManifestHandler manifestHandler = manifest.getManifestHandler();
        reportJSON.put(ACTIVITIES, manifestHandler.getActivities());
        reportJSON.put(SERVICES, manifestHandler.getServices());
        reportJSON.put(RECEIVERS, manifestHandler.getReceivers());
        reportJSON.put(PROVIDERS, manifestHandler.getProviders());
        reportJSON.put(ADDITIONAL_PERMISSIONS, manifestHandler.getPermissions());
        reportJSON.put(INTENTS, manifestHandler.getIntents());
        return reportJSON;
    }

    @Override
    public String getReportName() {
        return REPORT_NAME;
    }

    public String getAppFileName() {
        return appFileName;
    }

    public String getAppName() {
        return appName;
    }

    public String getPkgName() {
        return pkgName;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public String getMaxSDKVersion() {
        return maxSDKVersion;
    }

    public String getMinSDKVersion() {
        return minSDKVersion;
    }

    public String getTargetSDKVersion() {
        return targetSDKVersion;
    }

    public List<UseFeature> getUseFeatures() {
        return useFeatures;
    }

    public String getVersionName() {
        return versionName;
    }

    public long getVersionCode() {
        return versionCode;
    }

    public Manifest getManifest() {
        return manifest;
    }

}
