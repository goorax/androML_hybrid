package adb;

import androML.dynamic_analysis.DynamicAnalysis;
import androML.dynamic_analysis.adb.AdbAdapter;
import helper.TestConstants;
import javafx.util.Pair;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdbAdapterTest {
    private static final String[] DEVICES = DynamicAnalysis.DEVICES;
    private static final int DEVICE_AMOUNT = DynamicAnalysis.DEVICE_AMOUNT;
    private static final String TEST_DEVICE = DEVICES[0];

    public static final String PACKAGE_NAME = "dailab.de.clickerapp";
    public static final String ACTIVITY_HOME = "MainActivity";
    private static final String BUTTON_POS_PATTERN = ".*android.widget.Button.*\\((\\d+), (\\d+)\\).*";
    public static final String NEWLINE_OPERATOR = "\n";
    public static final String CMD_LS = "ls";
    public static final String SRC = "src";
    public static final String ADB_DEVICES_L = "adb devices -l";
    public static final String DEVICE = "device:";
    public static final String FRAMELAYOUT = "android.widget.FrameLayout";
    public static final String HOME_SCREEN_IDENTIFIER = "com.google.android.googlequicksearchbox:id/workspace";
    private String appPath = getClass().getClassLoader().getResource(TestConstants.CLICKERAPP_APK).getPath();

    private AdbAdapter aa;

    @Before
    public void initializeAdb() {
        aa = new AdbAdapter(TEST_DEVICE);
        aa.deleteAllLogs();
    }

    @Test
    public void testExecuteCommandWithResult() {
        String result = aa.executeCommandWithResult(CMD_LS);
        Assert.assertTrue(result.contains(SRC));
    }

    @Test
    public void testAdbDevices() {
        String result = aa.executeCommandWithResult(ADB_DEVICES_L);
        Assert.assertTrue(result.contains(DEVICE));
    }

    @Test
    public void testAppInstall() {
        aa.uninstallApp(PACKAGE_NAME);
        boolean success = aa.installApp(appPath);
        Assert.assertTrue(success);
    }

    @Test
    public void testIsAppInstalled() {
        aa.installApp(appPath);
        boolean isInstalled = aa.isAppInstalled(PACKAGE_NAME);
        Assert.assertTrue(isInstalled);
    }

    @Test
    public void testAppUninstall() {
        aa.uninstallApp(PACKAGE_NAME);
        boolean isInstalled = aa.isAppInstalled(appPath);
        Assert.assertFalse(isInstalled);
    }

    @Test
    public void testLaunchApp() {
        installTestApp();
        aa.launchApp(PACKAGE_NAME);
        boolean isRunning = aa.isAppRunning(PACKAGE_NAME);
        Assert.assertTrue(isRunning);
    }

    @Test
    public void testStartActivity() {
        installTestApp();
        aa.startActivity(PACKAGE_NAME, ACTIVITY_HOME);
        boolean isActivityInFocus = aa.isActivityInFocus(PACKAGE_NAME, ACTIVITY_HOME);
        Assert.assertTrue(isActivityInFocus);
    }

    @Test
    public void testStopApp() {
        installTestApp();
        aa.startActivity(PACKAGE_NAME, ACTIVITY_HOME);
        aa.stopApp(PACKAGE_NAME);
        boolean isActivityInFocus = aa.isActivityInFocus(PACKAGE_NAME, ACTIVITY_HOME);
        Assert.assertFalse(isActivityInFocus);
    }

    @Test
    public void testDumpViewHierarchy() {
        installTestApp();
        aa.startActivity(PACKAGE_NAME, ACTIVITY_HOME);
        String viewHierarchy = aa.dumpViewHierarchy();
        Assert.assertTrue(viewHierarchy.length() > 0);
        Assert.assertTrue(viewHierarchy.contains(FRAMELAYOUT));
    }

    @Test
    public void testIsAppRunning() {
        installTestApp();
        aa.startActivity(PACKAGE_NAME, ACTIVITY_HOME);
        boolean isAppRunning = aa.isAppRunning(PACKAGE_NAME);
        Assert.assertTrue(isAppRunning);
    }

    @Test
    public void testIsActivityInFocus() {
        installTestApp();
        aa.startActivity(PACKAGE_NAME, ACTIVITY_HOME);
        boolean isActivityInFocus = aa.isActivityInFocus(PACKAGE_NAME, ACTIVITY_HOME);
        aa.stopApp(PACKAGE_NAME);
        boolean isActivityNotInFocus = !aa.isActivityInFocus(PACKAGE_NAME, ACTIVITY_HOME);
        Assert.assertTrue(isActivityInFocus);
        Assert.assertTrue(isActivityNotInFocus);
    }

    @Test
    public void testGetFocusedActivityAndPkg() {
        installTestApp();
        aa.startActivity(PACKAGE_NAME, ACTIVITY_HOME);
        Pair<String, String> pkgAct = aa.getFocusedPkgAndActivity();
        aa.stopApp(PACKAGE_NAME);
        Assert.assertEquals(pkgAct.getKey(), PACKAGE_NAME);
        Assert.assertEquals(pkgAct.getValue(), ACTIVITY_HOME);
    }

    @Test
    public void testDeleteApiLog() {
        installTestApp();
        aa.startActivity(PACKAGE_NAME, ACTIVITY_HOME);
        // click test3 button
        aa.startTapCommand(840, 1323);
        String log = aa.getContentOfAPILog();
        Assert.assertFalse(log.isEmpty());
        aa.deleteAPILog();
        String freshLog = aa.getContentOfAPILog();
        Assert.assertTrue(log.length() > freshLog.length());
    }

    @Test
    public void testDeleteLocalLog() {
        aa.executeCommandWithResult("touch error.log");
        aa.deleteLocalLog();
        String result = aa.executeCommandWithResult("file error.log");
        Assert.assertTrue(result.contains("No such file or directory"));
    }

    @Test
    public void testGetContentOfApiLog() {
        installTestApp();
        aa.startActivity(PACKAGE_NAME, ACTIVITY_HOME);
        String content = aa.getContentOfAPILog();
        Assert.assertFalse(content.isEmpty());
    }

    @Test
    public void testTapCommand() {
        installTestApp();
        aa.startActivity(PACKAGE_NAME, ACTIVITY_HOME);
        String view = aa.dumpViewHierarchy();
        String newView = tapAndGetNewView(view);
        Assert.assertFalse(newView.equals(""));
        Assert.assertFalse(view.equals(newView));
    }

    private String tapAndGetNewView(String view) {
        String newView = String.valueOf(view);
        String[] viewLines = view.split(NEWLINE_OPERATOR);
        Pattern p = Pattern.compile(BUTTON_POS_PATTERN);
        for (String line : viewLines) {
            Matcher m = p.matcher(line);
            if (m.matches()) {
                int x = Integer.valueOf(m.group(1));
                int y = Integer.valueOf(m.group(2));
                aa.startTapCommand(x, y);
                newView = aa.dumpViewHierarchy();
                break;
            }
        }
        return newView;
    }

    @Test
    public void testBackKey() {
        installTestApp();
        aa.startActivity(PACKAGE_NAME, ACTIVITY_HOME);
        String view = aa.dumpViewHierarchy();
        String newView = tapAndGetNewView(view);
        Assert.assertFalse(newView.equals(""));
        Assert.assertFalse(view.equals(newView));
        aa.pressBackKey();
        newView = aa.dumpViewHierarchy();
        Assert.assertTrue(view.equals(newView));
    }

    @Test
    public void testReboot() {
        aa.rebootDevice();
        boolean isReadyAgain = aa.isDeviceReady();
        Assert.assertTrue(isReadyAgain);
    }

    @Test
    public void testRebootAndUnlock() {
        String homeScreenElement = HOME_SCREEN_IDENTIFIER;
        aa.rebootDevice();
        boolean isReadyAgain = aa.isDeviceReady();
        Assert.assertTrue(isReadyAgain);
        aa.unlockDevice();
        String homeScreen = aa.dumpRawViewHierarchy();
        Assert.assertTrue(homeScreen.contains(homeScreenElement));
    }

    @Test
    public void testUnlock() {
        String homeScreenElement = HOME_SCREEN_IDENTIFIER;
        aa.unlockDevice();
        String homeScreen = aa.dumpRawViewHierarchy();
        Assert.assertTrue(homeScreen.contains(homeScreenElement));
    }

    @After
    public void cleanUpApp() {
        aa.stopApp(PACKAGE_NAME);
        aa.uninstallApp(PACKAGE_NAME);
    }

    private boolean installTestApp() {
        return aa.installApp(appPath);
    }
}
