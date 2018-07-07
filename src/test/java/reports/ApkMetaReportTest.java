package reports;

import androML.static_analysis.reports.ApkMetaReport;
import androML.static_analysis.analyzer.Manifest;
import helper.TestConstants;
import helper.TestFileLoader;
import net.dongliu.apk.parser.ApkFile;
import net.dongliu.apk.parser.bean.ApkMeta;
import net.dongliu.apk.parser.bean.UseFeature;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;

public class ApkMetaReportTest {

    public static final String APP_FILENAME = "test.apk";
    public static final String APP_NAME = "CellServiceApp";
    public static final String PKG_NAME = "snet.de.tu_berlin.de.cellserviceapp";
    public static final String USEFEATURE_NAME = "android.hardware.location.network";
    public static final String PERMISSION_NAME = "android.permission.ACCESS_NETWORK_STATE";
    public static final String MIN_SDK = "17";
    public static final String MAX_SDK = null;
    public static final String TARGET_SDK = "25";
    public static final String VERSION_NAME = "1.0";
    public static final long VERSION_CODE = 1l;
    public static final int USEFEATURE_LIST_SIZE = 4;
    public static final int PERMISSION_LIST_SIZE = 13;
    public static final String NAME = "name";
    public static final int ACTIVITY_AMOUNT = 7;
    public static final int SERVICE_AMOUNT = 1;
    public static final int RECEIVERS_AMOUNT = 0;
    public static final int ADDITIONAL_PERMISSIONS_AMOUNT = 0;
    public static final String TEST_ACTIVITY = "snet.de.tu_berlin.de.cellserviceapp.ActivityStatistics";
    public static final int ACTIVITIES_SIZE = 7;
    public static final String TEST_SERVICE = "de.tu_berlin.snet.cellservice.CellService";
    public static final int SERVICES_SIZE = 1;
    public static final int RECEIVERS_SIZE = 0;
    public static final int ADDITIONAL_PERMISSIONS_SIZE = 0;

    private String testApkPath = getClass().getClassLoader().getResource(TestConstants.TEST_APK).getPath();

    private ApkFile apkFile;
    private ApkMetaReport report;
    private JSONObject reportJSON;

    @Before
    public void initializeReportTest() {
        apkFile = TestFileLoader.loadApkFile(testApkPath);
        File file = TestFileLoader.loadRawFile(testApkPath);
        ApkMeta apkMeta = null;
        String manifestString = "";
        try {
            apkMeta = apkFile.getApkMeta();
            manifestString = apkFile.getManifestXml();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Manifest manifest = new Manifest(manifestString);
        report = new ApkMetaReport(file.getName(), apkMeta, manifest);
        reportJSON = report.getReportAsJSON();
    }

    @Test
    public void testManifestCreation() {
        String manifestString = "";
        try {
            manifestString = apkFile.getManifestXml();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Manifest manifest = new Manifest(manifestString);
        Manifest.ManifestHandler handler = manifest.getManifestHandler();
        Assert.assertEquals(handler.getPackageName(), PKG_NAME);
        Assert.assertEquals(handler.getActivities().size(), ACTIVITY_AMOUNT);
        Assert.assertEquals(handler.getServices().size(), SERVICE_AMOUNT);
        Assert.assertEquals(handler.getReceivers().size(), RECEIVERS_AMOUNT);
        Assert.assertEquals(handler.getPermissions().size(), ADDITIONAL_PERMISSIONS_AMOUNT);
    }

    @Test
    public void testReportObject() {
        Assert.assertThat(report, instanceOf(ApkMetaReport.class));
    }

    @Test
    public void testReportAppFileName() {
        String appFileName = report.getAppFileName();
        Assert.assertTrue(appFileName.equals(APP_FILENAME));
    }

    @Test
    public void testReportAppName() {
        String appName = report.getAppName();
        Assert.assertTrue(appName.equals(APP_NAME));
    }

    @Test
    public void testReportPkgName() {
        String pkgName = report.getPkgName();
        Assert.assertTrue(pkgName.equals(PKG_NAME));
    }

    @Test
    public void testReportMaxSDKVersion() {
        String maxSDKVersion = report.getMaxSDKVersion();
        // Special case because max version is null of test apk
        Assert.assertTrue(maxSDKVersion == MAX_SDK);
    }

    @Test
    public void testReportMinSDKVersion() {
        String minSDKVersion = report.getMinSDKVersion();
        Assert.assertTrue(minSDKVersion.equals(MIN_SDK));
    }

    @Test
    public void testReportTargetSDKVersion() {
        String targetSDKVersion = report.getTargetSDKVersion();
        Assert.assertTrue(targetSDKVersion.equals(TARGET_SDK));
    }

    @Test
    public void testReportVersionName() {
        String versionName = report.getVersionName();
        Assert.assertTrue(versionName.equals(VERSION_NAME));
    }

    @Test
    public void testReportVersionCode() {
        long versionCode = report.getVersionCode();
        Assert.assertTrue(versionCode == VERSION_CODE);
    }

    @Test
    public void testReportFeatures() {
        List<UseFeature> featureList = report.getUseFeatures();
        UseFeature feature = featureList.get(0);
        Assert.assertEquals(featureList.size(), USEFEATURE_LIST_SIZE);
        Assert.assertTrue(feature.getName().equals(USEFEATURE_NAME));
    }

    @Test
    public void testReportPermissions() {
        List<String> permissionList = report.getPermissions();
        String permission = permissionList.get(0);
        Assert.assertEquals(permissionList.size(), PERMISSION_LIST_SIZE);
        Assert.assertTrue(permission.equals(PERMISSION_NAME));
    }

    @Test
    public void testJSONAppFileName() {
        Assert.assertEquals(reportJSON.getString(report.APP_FILENAME), ApkMetaReportTest.APP_FILENAME);
    }


    @Test
    public void testJSONAppName() {
        Assert.assertEquals(reportJSON.getString(report.APP_NAME), ApkMetaReportTest.APP_NAME);
    }

    @Test
    public void testJSONPkgName() {
        Assert.assertEquals(reportJSON.getString(report.PKG_NAME), ApkMetaReportTest.PKG_NAME);
    }

    @Test
    public void testJSONMinSDK() {
        Assert.assertEquals(reportJSON.getString(report.MIN_SDK), ApkMetaReportTest.MIN_SDK);
    }

    @Test
    public void testJSONMaxSDK() {
        //Assert.assertEquals(reportJSON.getString(JSONBuilder.MAX_SDK), ApkMetaReportTest.MAX_SDK);
    }

    @Test
    public void testJSONTargetSDK() {
        Assert.assertEquals(reportJSON.getString(report.TARGET_SDK), ApkMetaReportTest.TARGET_SDK);
    }

    @Test
    public void testJSONVersionName() {
        Assert.assertEquals(reportJSON.getString(report.VERSION_NAME), ApkMetaReportTest.VERSION_NAME);
    }

    @Test
    public void testJSONVersionCode() {
        Assert.assertEquals(reportJSON.getLong(report.VERSION_CODE), ApkMetaReportTest.VERSION_CODE);
    }

    @Test
    public void testJSONPermissions() {
        JSONArray permissions = reportJSON.getJSONArray(report.PERMISSIONS);
        Assert.assertEquals(permissions.getString(0), ApkMetaReportTest.PERMISSION_NAME);
        Assert.assertEquals(permissions.length(), ApkMetaReportTest.PERMISSION_LIST_SIZE);
    }

    @Test
    public void testJSONUseFeatures() {
        JSONArray useFeatures = reportJSON.getJSONArray(report.USEFEATURES);
        Assert.assertEquals(useFeatures.getJSONObject(0).getString(NAME), ApkMetaReportTest.USEFEATURE_NAME);
        Assert.assertEquals(useFeatures.length(), ApkMetaReportTest.USEFEATURE_LIST_SIZE);
    }

    @Test
    public void testJSONActivities() {
        JSONArray activities = reportJSON.getJSONArray(report.ACTIVITIES);
        Assert.assertEquals(activities.get(0), TEST_ACTIVITY);
        Assert.assertEquals(activities.length(), ACTIVITIES_SIZE);
    }

    @Test
    public void testJSONServices() {
        JSONArray services = reportJSON.getJSONArray(report.SERVICES);
        Assert.assertEquals(services.get(0), TEST_SERVICE);
        Assert.assertEquals(services.length(), SERVICES_SIZE);
    }

    @Test
    public void testJSONReceivers() {
        JSONArray receivers = reportJSON.getJSONArray(report.RECEIVERS);
        Assert.assertEquals(receivers.length(), RECEIVERS_SIZE);
    }

    @Test
    public void testJSONAdditionalPermissions() {
        JSONArray additionalPermissions = reportJSON.getJSONArray(report.ADDITIONAL_PERMISSIONS);
        Assert.assertEquals(additionalPermissions.length(), ADDITIONAL_PERMISSIONS_SIZE);
    }

}
