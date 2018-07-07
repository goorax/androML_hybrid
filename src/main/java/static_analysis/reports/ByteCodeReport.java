package androML.static_analysis.reports;

import androML.static_analysis.analyzer.ByteCodeRecord;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final public class ByteCodeReport implements Report {
    private static final Logger LOG = LoggerFactory.getLogger(ByteCodeReport.class);

    private static final String REPORT_NAME = "ByteCodeReport";
    public static final String TOTAL_PACKAGE_AMOUNT = "total_package_amount";
    public static final String TOTAL_CLASSES_AMOUNT = "total_classes_amount";
    public static final String TOTAL_SYSTEM_CLASSES_AMOUNT = "total_system_classes_amount";
    public static final String TOTAL_METHOD_AMOUNT = "total_method_amount";
    public static final String FILTERED_CLASS_NAMES = "filtered_class_names";
    public static final String FILTERED_METHOD_NAMES = "filtered_method_names";
    public static final String FILTERED_INVOKE_REFS = "filtered_invoke_refs";
    public static final String FILTERED_URIS = "filtered_uris";

    private ByteCodeRecord byteCodeRecord;

    public ByteCodeReport(ByteCodeRecord byteCodeRecord) {
        this.byteCodeRecord = byteCodeRecord;
    }

    @Override
    public String getReportName() {
        return REPORT_NAME;
    }

    @Override
    public JSONObject getReportAsJSON() {
        JSONObject reportJSON = new JSONObject();
        reportJSON.put(TOTAL_PACKAGE_AMOUNT, byteCodeRecord.getTotalPackageAmount());
        reportJSON.put(TOTAL_CLASSES_AMOUNT, byteCodeRecord.getTotalClassesAmount());
        reportJSON.put(TOTAL_SYSTEM_CLASSES_AMOUNT, byteCodeRecord.getTotalSystemClassesAmount());
        reportJSON.put(TOTAL_METHOD_AMOUNT, byteCodeRecord.getTotalMethodAmount());
        reportJSON.put(FILTERED_CLASS_NAMES, byteCodeRecord.getFilteredClassNames());
        reportJSON.put(FILTERED_METHOD_NAMES, byteCodeRecord.getFilteredDexMethodNames());
        reportJSON.put(FILTERED_INVOKE_REFS, byteCodeRecord.getFilteredInvokeReferences());
        reportJSON.put(FILTERED_URIS, byteCodeRecord.getFilteredUsedURIs());
        return reportJSON;
    }
}
