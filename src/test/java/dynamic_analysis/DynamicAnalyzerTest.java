package dynamic_analysis;

import androML.AndroMLConfig;
import androML.database.ElasticAdapter;
import androML.dynamic_analysis.App;
import androML.dynamic_analysis.DynamicAnalysis;
import androML.dynamic_analysis.adb.AdbAdapter;
import androML.dynamic_analysis.analyzer.DynamicAnalyzer;
import androML.dynamic_analysis.analyzer.InteractiveAnalyzer;
import androML.dynamic_analysis.analyzer.ComponentAnalyzer;
import androML.dynamic_analysis.runner.UIPatterns;
import helper.TestConstants;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class DynamicAnalyzerTest {
    private static final String LOCALHOST = "localhost";
    private static final String[] SOURCES = {"ApkMetaReport.app_file_name", "ApkMetaReport.pkg_name",
            "ApkMetaReport.activities", "ApkMetaReport.services"};
    private static final String ANDROID_MALWARE_2017 = "android_malware_2017";
    private static final String PKG_NAME_CLICKERAPP = "dailab.de.clickerapp";
    private static final String PKG_NAME_CELLSERVICEAPP = "snet.de.tu_berlin.de.cellserviceapp";
    private static final String TESTINDEX = "testindex";
    private static final String ELASTIC_USER = "elastic";
    private static final String ELASTIC_PW = "changeme";

    private static final String[] DEVICES = DynamicAnalysis.DEVICES;
    private static final int DEVICE_AMOUNT = DynamicAnalysis.DEVICE_AMOUNT;
    private static final String TEST_DEVICE = DEVICES[0];

    private String clickerAppPath = getClass().getClassLoader().getResource(TestConstants.CLICKERAPP_APK).getPath();
    private String cellServicePath = getClass().getClassLoader().getResource(TestConstants.TEST_APK).getPath();

    private ElasticAdapter elasticAdapter;
    private AndroMLConfig config;

    @Before
    public void initializeTest() {
        config = mock(AndroMLConfig.class);
        given(config.getPath()).willReturn(new AndroMLConfig().getPath());
        given(config.getElasticHost()).willReturn(LOCALHOST);
        given(config.getElasticPort()).willReturn(9200);
        given(config.getElasticUser()).willReturn(ELASTIC_USER);
        given(config.getElasticPw()).willReturn(ELASTIC_PW);
        given(config.getElasticIndex()).willReturn(TESTINDEX);
        given(config.getDynamicTimeThresholdInteractive()).willReturn(600000l);
        elasticAdapter = new ElasticAdapter(config);
    }

    @Test
    public void testInteractiveDynamicAnalysisCellService() {
        App app = prepareCellServiceApp();
        DynamicAnalyzer da = new InteractiveAnalyzer(config, TEST_DEVICE);
        int reboots = 0;
        da.analyze(app, reboots);
        String apiLog = app.getPlainApiLog();
        Assert.assertTrue(apiLog.contains(PKG_NAME_CELLSERVICEAPP));
        Assert.assertFalse(app.getApiCalls().isEmpty());
    }

    @Test
    public void testSimpleDynamicAnalysisCellService() {
        App app = prepareCellServiceApp();
        DynamicAnalyzer da = new ComponentAnalyzer(TEST_DEVICE);
        int reboots = 0;
        da.analyze(app, reboots);
        String apiLog = app.getPlainApiLog();
        Assert.assertTrue(apiLog.contains(PKG_NAME_CELLSERVICEAPP));
        Assert.assertFalse(app.getApiCalls().isEmpty());
    }

    @Test
    public void testSpecificMalwareApp() {
        DynamicAnalyzer da = new ComponentAnalyzer(TEST_DEVICE);
        String appId = "643a0bad5972d88c93e62ff4f9ea71cbf82c6cdcb8e7ce7f7eefb15a36dd4321";
        JSONObject result = elasticAdapter.receiveCustomSourcesFromID(ANDROID_MALWARE_2017, appId, SOURCES);
        App app = new App(result, config.getPath());
        int reboots = 0;
        da.analyze(app, reboots);
        String apiLog = app.getPlainApiLog();
        Assert.assertTrue(apiLog.contains(app.getPackageName()));
    }

    private App prepareCellServiceApp() {
        Set<String> activities = new HashSet<>();
        Set<String> services = new HashSet<>();
        activities.add("ActivityHome");
        activities.add("ActivitySettings");
        activities.add("ActivityMap");
        activities.add("ActivityHotSpots");
        activities.add("ActivityAbout");
        activities.add("ActivityStatistics");
        services.add("de.tu_berlin.snet.cellservice.CellService");
        App app = new App("2", cellServicePath);
        app.setPackageName(PKG_NAME_CELLSERVICEAPP);
        app.setActivities(activities);
        return app;
    }

    @Test
    public void testDynamicAnalysisInteractiveClickerApp() {
        DynamicAnalyzer da = new InteractiveAnalyzer(config, TEST_DEVICE);
        Set<String> activities = new HashSet<>();
        activities.add("MainActivity");
        App app = new App("1", clickerAppPath);
        app.setPackageName(PKG_NAME_CLICKERAPP);
        app.setActivities(activities);
        int reboots = 0;
        da.analyze(app, reboots);
        String apiLog = app.getPlainApiLog();
        Assert.assertTrue(apiLog.contains(PKG_NAME_CLICKERAPP));
        Assert.assertFalse(app.getApiCalls().isEmpty());
    }

    @Test
    public void testErrorDialogHandling() {
        DynamicAnalyzer da = new ComponentAnalyzer(TEST_DEVICE);
        da.checkForErrorAndPermissionDialog();
        Assert.assertTrue(false);
    }

    @Test
    public void testDeterminism() {
        AdbAdapter aa = new AdbAdapter(TEST_DEVICE);
        String rawViewHierarchy = aa.dumpRawViewHierarchy();
        boolean isDeterministic = UIPatterns.isDeterministic(rawViewHierarchy);
        Assert.assertEquals(isDeterministic, true);
    }

}
