package static_analysis;

import androML.AndroMLConfig;
import helper.TestConstants;
import helper.TestFileLoader;
import androML.static_analysis.analyzer.ByteCodeAnalyzer;
import androML.static_analysis.reports.ByteCodeReport;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.mockito.Mockito.mock;

public class ByteCodeAnalyzerTest {
    private static final int TOTAL_PACKAGE_AMOUNT= -1;
    private static final int TOTAL_CLASSES_AMOUNT = 3870;
    private static final int TOTAL_SYSTEM_CLASSES_AMOUNT = 2111;
    private static final int TOTAL_METHOD_AMOUNT = 40462;
    private static final int FILTERED_CLASSES_SIZE = 1759;
    private static final int FILTERED_INVOKE_REFS_SIZE = 12337;
    private static final int FILTERED_METHOD_NAMES_SIZE = 2;
    private static final int FILTERED_URIS_SIZE = 36;

    private String testApkPath = getClass().getClassLoader().getResource(TestConstants.TEST_APK).getPath();
    private ByteCodeAnalyzer byteCodeAnalyzer;

    @Before
    public void initializeByteCodeAnalyzer() {
        AndroMLConfig config = mock(AndroMLConfig.class);
        byteCodeAnalyzer = new ByteCodeAnalyzer(config);
    }

    @Test
    public void testGeneralReportCreation() {
        startAnalysis();
        Assert.assertFalse(byteCodeAnalyzer.getReport() == null);
    }

    private void startAnalysis() {
        File file = TestFileLoader.loadRawFile(testApkPath);
        byteCodeAnalyzer.analyze(file);
    }

    @Test
    public void testTotalPackageAmount() {
        JSONObject report = startAnalysisAndGetReport();
        int totalPackageAmount = report.getInt(ByteCodeReport.TOTAL_PACKAGE_AMOUNT);
        Assert.assertEquals(totalPackageAmount, TOTAL_PACKAGE_AMOUNT);
    }

    @Test
    public void testTotalClassesAmount() {
        JSONObject report = startAnalysisAndGetReport();
        int totalClassesAmount = report.getInt(ByteCodeReport.TOTAL_CLASSES_AMOUNT);
        Assert.assertEquals(totalClassesAmount, TOTAL_CLASSES_AMOUNT);
    }

    @Test
    public void testTotalSystemClassesAmount() {
        JSONObject report = startAnalysisAndGetReport();
        int totalSystemClassesAmount = report.getInt(ByteCodeReport.TOTAL_SYSTEM_CLASSES_AMOUNT);
        Assert.assertEquals(totalSystemClassesAmount, TOTAL_SYSTEM_CLASSES_AMOUNT);
    }

    @Test
    public void testTotalMethodAmount() {
        JSONObject report = startAnalysisAndGetReport();
        int totalMethodAmount = report.getInt(ByteCodeReport.TOTAL_METHOD_AMOUNT);
        Assert.assertEquals(totalMethodAmount, TOTAL_METHOD_AMOUNT);
    }

    @Test
    public void testFilteredClassNames() {
        JSONObject report = startAnalysisAndGetReport();
        JSONArray classNames = report.getJSONArray(ByteCodeReport.FILTERED_CLASS_NAMES);
        Assert.assertEquals(classNames.length(), FILTERED_CLASSES_SIZE);
    }

    private JSONObject startAnalysisAndGetReport() {
        startAnalysis();
        return byteCodeAnalyzer.getReport().getReportAsJSON();
    }

    @Test
    public void testFilteredInvokeReferences() {
        JSONObject report = startAnalysisAndGetReport();
        JSONArray invokeRefs = report.getJSONArray(ByteCodeReport.FILTERED_INVOKE_REFS);
        Assert.assertEquals(invokeRefs.length(), FILTERED_INVOKE_REFS_SIZE);
    }

    @Test
    public void testFilteredMethods() {
        JSONObject report = startAnalysisAndGetReport();
        JSONArray methodNames = report.getJSONArray(ByteCodeReport.FILTERED_METHOD_NAMES);
        Assert.assertEquals(methodNames.length(), FILTERED_METHOD_NAMES_SIZE);
    }

    @Test
    public void testFilteredURIs() {
        JSONObject report = startAnalysisAndGetReport();
        JSONArray uris = report.getJSONArray(ByteCodeReport.FILTERED_URIS);
        Assert.assertEquals(uris.length(), FILTERED_URIS_SIZE);
    }

}
