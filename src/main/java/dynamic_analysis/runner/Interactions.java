package androML.dynamic_analysis.runner;

import androML.dynamic_analysis.adb.AdbAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Interactions {
    private static final Logger LOG = LoggerFactory.getLogger(Interactions.class);

    private static final String NEWLINE_OPERATOR = "\n";
    private static final String BUTTON_PERMISSION_POS_PATTERN = ".*com\\.android\\.packageinstaller:id/permission_allow_button Allow.*\\((\\d+), (\\d+)\\).*";
    private static final String BUTTON_CENTER_POS_PATTERN = ".*android.widget.Button.*OK.*\\((\\d+), (\\d+)\\).*";


    public static synchronized void pressBackKeys(AdbAdapter aa, int pressAmount) {
        for (int i = 0; i < pressAmount; i++) {
            LOG.info("Pressing back key.");
            aa.pressBackKey();
        }
    }

    public static synchronized void allowPermission(AdbAdapter aa, String view) {
        view = view.replace(NEWLINE_OPERATOR, "");
        Pattern pattern = Pattern.compile(BUTTON_PERMISSION_POS_PATTERN);
        matchAndTap(aa, view, pattern);
    }

    public static synchronized void pushExceptionButton(AdbAdapter aa, String view) {
        view = view.replace(NEWLINE_OPERATOR, "");
        Pattern pattern = Pattern.compile(BUTTON_CENTER_POS_PATTERN);
        matchAndTap(aa, view, pattern);
    }

    private static void matchAndTap(AdbAdapter aa, String view, Pattern pattern) {
        Matcher m = pattern.matcher(view);
        if (m.matches()) {
            int x = Integer.valueOf(m.group(1));
            int y = Integer.valueOf(m.group(2));
            aa.startTapCommand(x, y);
        }
    }
}
