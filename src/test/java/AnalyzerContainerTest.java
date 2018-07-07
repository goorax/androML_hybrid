import androML.AndroMLConfig;
import androML.database.ElasticAdapter;
import androML.dynamic_analysis.App;
import androML.dynamic_analysis.DynamicAnalysis;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class AnalyzerContainerTest {
    private static final String LOCALHOST = "localhost";
    private static final String TESTINDEX = "testindex";
    private static final String BENIGN2017 = "android_benign_2017";
    private static final String ELASTIC_USER = "elastic";
    private static final String ELASTIC_PW = "changeme";

    private final String TEST_APP_PATH = getClass().getClassLoader().getResource("test.apk").getPath();

    private ElasticAdapter elasticAdapter;
    private AndroMLConfig config;
    private List<String> testIds;


    @Before
    public void initializeTest() {
        config = mock(AndroMLConfig.class);
        mockAndroML();
        elasticAdapter = new ElasticAdapter(config);
        initializeTestIds();
        removeTestIndex();
        createTestIndex();
    }

    private void mockAndroML() {
        given(config.isDynamicAnalysisInteractive()).willReturn(false);
        given(config.getPath()).willReturn(TEST_APP_PATH);
        given(config.getElasticHost()).willReturn(LOCALHOST);
        given(config.getElasticPort()).willReturn(9200);
        given(config.getElasticUser()).willReturn(ELASTIC_USER);
        given(config.getElasticPw()).willReturn(ELASTIC_PW);
        given(config.getElasticIndex()).willReturn(BENIGN2017);
        given(config.isDynamicAnalysis()).willReturn(true);
        given(config.getDynamicIndexInteractive()).willReturn(TESTINDEX);
        given(config.getDynamicIndexSimple()).willReturn(TESTINDEX);
        given(config.getDynamicTimeThresholdInteractive()).willReturn(120000l);
    }

    private void removeTestIndex() {
        elasticAdapter.removeDatabase(TESTINDEX);
    }

    private void createTestIndex() {
        elasticAdapter.createDatabase(TESTINDEX);
    }

    private void initializeTestIds() {
        testIds = new ArrayList<>();
        testIds.add("269d9f5626b544b87b97292ecc79da61d2bd71ea1e7c90fab7f634c32d8d33a4");
        testIds.add("cee87cc5d93f1e5427ff9127a160904cd96d6b259c54eb79c8d787255227a7fa");
        testIds.add("73926d6fbf5094ca43ecea2ad248a59ff22ce95fcf7c51306ed9b1a418a27ab7");
        testIds.add("7d67463cc06fa9de74b06e00401a58fa760185c6b609c8d51d98b944d9a046df");
        testIds.add("1dec1c8ef3dd38c8ad9b7d9e4be8912f5d0e39e68e38e07c088ed5e8ef6a5e88");
    }

    private Set<App> prepareTestApps() {
        Set<String> retainedActivites = createRetainedActivities();
        Set<App> testApps = new HashSet<>();
        String path = new File(TEST_APP_PATH).getParent() + "/";
        for (String id : testIds) {
            JSONObject result = elasticAdapter.receiveCustomSourcesFromID(config.getElasticIndex(),
                    id, DynamicAnalysis.SOURCES);
            App app = getCleanedAppForTesting(retainedActivites, path, result);
            testApps.add(app);
        }
        return testApps;
    }

    private App getCleanedAppForTesting(Set<String> retainedActivites, String path, JSONObject result) {
        App app = new App(result, path);
        app.getActivitiesAsModifiableSet().retainAll(retainedActivites);
        app.getServicesAsModifiableSet().clear();
        return app;
    }

    private Set<String> createRetainedActivities() {
        Set<String> retainedActivies = new HashSet<>();
        retainedActivies.add("com.androapplite.weather.weatherproject.MapActivity");
        retainedActivies.add("com.androapplite.weather.weatherproject.activity.SettingActivity");
        retainedActivies.add("com.androapplite.weather.weatherproject.activity.MainAppActivity");

        retainedActivies.add("com.onexsoftech.callernameannouncer.CallerNameSpkActivity");
        retainedActivies.add("com.onexsoftech.callernameannouncer.MainPage");
        retainedActivies.add("com.onexsoftech.callernameannouncer.Splash");

        retainedActivies.add("com.easytouch.activity.MainActivity");
        retainedActivies.add("com.easytouch.activity.ThemeActivity");
        retainedActivies.add("com.startapp.android.publish.AppWallActivity");

        retainedActivies.add("com.ansca.corona.CoronaActivity");
        retainedActivies.add("com.ansca.corona.CameraActivity");
        retainedActivies.add("com.fusepowered.ap.MainActivity");

        //retainedActivies.add("com.android.packageinstaller.permission.ui.GrantPermissionsActivity");
        retainedActivies.add("com.microsoft.o365suite.o365shell.applauncher.activities.AppLauncherActivity");
        retainedActivies.add("com.microsoft.office.powerpoint.PPTActivity");
        retainedActivies.add("net.hockeyapp.android.ExpiryInfoActivity");
        return retainedActivies;
    }

    private boolean evaluateAppResults(Set<App> testApps) {
        boolean resultsEmpty = false;
        for (App app : testApps) {
            resultsEmpty &= app.getApiCalls().isEmpty();
        }
        return resultsEmpty;
    }

    @Test
    public void testDynamicAnalysis() {
        Set<App> testApps = prepareTestApps();
        DynamicAnalysis da = new DynamicAnalysis(config, elasticAdapter);
        da.checkForStartingAnalysis();
        boolean resultsEmpty = evaluateAppResults(testApps);
        Assert.assertFalse(resultsEmpty);
    }

    @After
    public void cleanUp() {
        removeTestIndex();
    }

}
