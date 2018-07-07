package learning_tests;

import androML.dynamic_analysis.DynamicAnalysis;
import androML.dynamic_analysis.adb.AdbAdapter;
import helper.TestConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DynamicClickerTest {
    private static final Logger LOG = LoggerFactory.getLogger(DynamicClickerTest.class);
    private static final String[] DEVICES = DynamicAnalysis.DEVICES;
    private static final int DEVICE_AMOUNT = DynamicAnalysis.DEVICE_AMOUNT;
    private static final String TEST_DEVICE = DEVICES[0];

    public static final String PACKAGE_NAME = "snet.de.tu_berlin.de.cellserviceapp";
    public static final String ACTIVITY_HOME = "ActivityHome";
    public static final String BUTTON_CENTER_POS_PATTERN = ".*android.widget.Button.*\\((\\d+), (\\d+)\\).*";
    public static final boolean RESTART = false;
    public static final String NEWLINE_OPERATOR = "\n";
    private String testApkPath = getClass().getClassLoader().getResource(TestConstants.TEST_APK).getPath();
    private AdbAdapter adbAdapter;

    @Before
    public void initializeRuntime() {
        adbAdapter = new AdbAdapter(TEST_DEVICE);
        adbAdapter.deleteAPILog();
        adbAdapter.deleteLocalLog();
    }

    @Test
    public void testAPILogWithoutInteraction() {
        adbAdapter.startActivity(PACKAGE_NAME, ACTIVITY_HOME);
        String content = adbAdapter.getContentOfAPILog();
        adbAdapter.stopApp(PACKAGE_NAME);
        adbAdapter.deleteAPILog();
        Assert.assertTrue(content.contains(PACKAGE_NAME));
    }

    @Test
    public void testApiLogWithInteraction() {
        adbAdapter.startActivity(PACKAGE_NAME, ACTIVITY_HOME);
        String viewHierarchy = adbAdapter.dumpViewHierarchy();
        String[] viewHierarchyArray = viewHierarchy.split(NEWLINE_OPERATOR);
        Pattern p = Pattern.compile(BUTTON_CENTER_POS_PATTERN);

        checkViewHierarchyForClickables(viewHierarchyArray, p);

        String apiLog = adbAdapter.getContentOfAPILog();
        Assert.assertTrue(apiLog.contains(PACKAGE_NAME));
    }

    private void checkViewHierarchyForClickables(String[] viewHierarchyArray, Pattern p) {
        for (String view : viewHierarchyArray) {
            Matcher m = p.matcher(view);
            if (m.matches()) {
                performClick(m);
            }
        }
    }

    private void performClick(Matcher m) {
        int x = Integer.valueOf(m.group(1));
        int y = Integer.valueOf(m.group(2));
        adbAdapter.startTapCommand(x, y);
        if (RESTART) {
            adbAdapter.stopApp(PACKAGE_NAME);
            adbAdapter.startActivity(PACKAGE_NAME, ACTIVITY_HOME);
        }
    }

}
