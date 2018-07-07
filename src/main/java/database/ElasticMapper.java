package androML.database;

import androML.static_analysis.reports.ApkMetaReport;
import androML.static_analysis.reports.AssetReport;
import androML.static_analysis.reports.ByteCodeReport;
import org.json.JSONObject;

final public class ElasticMapper {

    private static final String MAPPINGS = "mappings";
    private static final String APK_META_REPORT = "ApkMetaReport";
    private static final String ASSETS_REPORT = "AssetReport";
    private static final String PROPERTIES = "properties";
    private static final String TYPE = "type";

    private static final JSONObject TYPE_INTEGER = new JSONObject().put(TYPE, "integer");
    private static final JSONObject TYPE_TEXT = new JSONObject().put(TYPE, "text");
    private static final JSONObject TYPE_KEYWORD = new JSONObject().put(TYPE, "keyword");
    public static final String REPORT = "report";
    public static final String BYTECODE_REPORT = "BytecodeReport";
    public static final String PKG_NAME = "pkg_name";
    public static final String API_CALLS = "api_calls";

    public synchronized static JSONObject getStaticReportMapping() {
        JSONObject mappings = new JSONObject();
        JSONObject properties = new JSONObject();
        JSONObject report = new JSONObject();
        properties.put(PROPERTIES, buildMappingChildsForStaticReport());
        report.put(REPORT, properties);
        mappings.put(MAPPINGS, report);
        return mappings;
    }

    public synchronized static JSONObject getDynamicReportMapping() {
        JSONObject mappings = new JSONObject();
        JSONObject properties = new JSONObject();
        JSONObject report = new JSONObject();
        properties.put(PROPERTIES, buildMappingChildsForDynamicReport());
        report.put(REPORT, properties);
        mappings.put(MAPPINGS, report);
        return mappings;
    }

    private static JSONObject buildMappingChildsForDynamicReport() {
        JSONObject report = new JSONObject();
        report.put(PKG_NAME, TYPE_KEYWORD);
        report.put(API_CALLS, TYPE_TEXT);
        return report;
    }

    private static JSONObject buildMappingChildsForStaticReport() {
        JSONObject report = new JSONObject();
        report.put(APK_META_REPORT, buildApkMetaReportMapping());
        report.put(ASSETS_REPORT, buildAssetsReportMapping());
        report.put(BYTECODE_REPORT, buildBytecodeReportMapping());
        return report;
    }

    private static JSONObject buildApkMetaReportMapping() {
        JSONObject properties = new JSONObject();
        JSONObject propertyContent = buildApkMetaReportSingleProperties();
        properties.put(PROPERTIES, propertyContent);
        return properties;
    }

    private static JSONObject buildApkMetaReportSingleProperties() {
        JSONObject properties = new JSONObject();
        properties.put(ApkMetaReport.MIN_SDK, TYPE_INTEGER);
        properties.put(ApkMetaReport.APP_FILENAME, TYPE_KEYWORD);
        properties.put(ApkMetaReport.VERSION_CODE, TYPE_INTEGER);
        properties.put(ApkMetaReport.TARGET_SDK, TYPE_INTEGER);
        properties.put(ApkMetaReport.APP_NAME, TYPE_KEYWORD);
        properties.put(ApkMetaReport.PKG_NAME, TYPE_KEYWORD);
        properties.put(ApkMetaReport.VERSION_NAME, TYPE_KEYWORD);
        properties.put(ApkMetaReport.ACTIVITIES, TYPE_KEYWORD);
        properties.put(ApkMetaReport.RECEIVERS, TYPE_KEYWORD);
        properties.put(ApkMetaReport.SERVICES, TYPE_KEYWORD);
        properties.put(ApkMetaReport.ADDITIONAL_PERMISSIONS, TYPE_KEYWORD);
        properties.put(ApkMetaReport.USEFEATURES, TYPE_KEYWORD);
        properties.put(ApkMetaReport.PERMISSIONS, TYPE_KEYWORD);
        return properties;
    }

    private static JSONObject buildAssetsReportMapping() {
        JSONObject properties = new JSONObject();
        JSONObject propertyContent = buildAssetReportSingleProperties();
        properties.put(PROPERTIES, propertyContent);
        return properties;
    }

    private static JSONObject buildAssetReportSingleProperties() {
        JSONObject properties = new JSONObject();
        properties.put(AssetReport.ASSETS, TYPE_KEYWORD);
        return properties;
    }

    private static JSONObject buildBytecodeReportMapping() {
        JSONObject properties = new JSONObject();
        JSONObject propertyContent = buildBytecodeReportSingleProperties();
        properties.put(PROPERTIES, propertyContent);
        return properties;
    }

    private static JSONObject buildBytecodeReportSingleProperties() {
        JSONObject properties = new JSONObject();
        properties.put(ByteCodeReport.FILTERED_CLASS_NAMES, TYPE_KEYWORD);
        properties.put(ByteCodeReport.FILTERED_METHOD_NAMES, TYPE_KEYWORD);
        properties.put(ByteCodeReport.FILTERED_INVOKE_REFS, TYPE_KEYWORD);
        properties.put(ByteCodeReport.FILTERED_URIS, TYPE_KEYWORD);
        properties.put(ByteCodeReport.TOTAL_PACKAGE_AMOUNT, TYPE_INTEGER);
        properties.put(ByteCodeReport.TOTAL_CLASSES_AMOUNT, TYPE_INTEGER);
        properties.put(ByteCodeReport.TOTAL_SYSTEM_CLASSES_AMOUNT, TYPE_INTEGER);
        properties.put(ByteCodeReport.TOTAL_METHOD_AMOUNT, TYPE_INTEGER);
        return properties;
    }


}
