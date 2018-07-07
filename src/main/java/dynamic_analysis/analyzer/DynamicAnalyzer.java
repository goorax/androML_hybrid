package androML.dynamic_analysis.analyzer;

import androML.dynamic_analysis.App;
import androML.dynamic_analysis.adb.AdbAdapter;
import androML.dynamic_analysis.runner.ClickRunner;
import androML.dynamic_analysis.runner.Interactions;
import androML.dynamic_analysis.runner.UIPatterns;
import androML.static_analysis.reports.Report;
import org.slf4j.Logger;

public abstract class DynamicAnalyzer {
    static final int INITIAL_LAUNCH_WAITING_TIME = 6000;

    Logger LOG;
    AdbAdapter aa;
    ClickRunner clickRunner;

    public abstract void analyze(App app, int reboots);

    public abstract Report getReport();

    void initializeAnalysis(String packageName) {
        LOG.info("Stop app {} and delete all logs.", packageName);
        aa.stopApp(packageName);
        aa.deleteAllLogs();
        LOG.info("Check for unlocking of device.");
        aa.unlockDevice();
        LOG.info("Start initial launch of app.");
        aa.launchApp(packageName);
        checkForErrorAndPermissionDialog();
        aa.sleep(INITIAL_LAUNCH_WAITING_TIME);
    }


    void fixEmptyApiLog() {
        aa.rebootDevice();
        if (aa.isDeviceReady()) {
            aa.unlockDevice();
        }
    }

    public void checkForErrorAndPermissionDialog() {
        String view = aa.dumpRawViewHierarchy();
        LOG.info("Checking for permission dialog.");
        handlePermissionDialog(view);
        LOG.info("Checking for error dialog.");
        handleExceptionDialog(view);
    }

    void handlePermissionDialog(String view) {
        while (UIPatterns.isPermissionDialog(view)) {
            LOG.info("Found permission dialog! Clicking allow ...");
            Interactions.allowPermission(aa, view);
            view = aa.dumpRawViewHierarchy();
        }
    }

    void handleExceptionDialog(String view) {
        int maxRetries = 3;
        while (UIPatterns.isExceptionDialog(view) && maxRetries-- > 0) {
            LOG.info("Found error dialog! Click away..");
            Interactions.pushExceptionButton(aa, view);
            view = aa.dumpRawViewHierarchy();
        }
    }

    void checkAndReboot(App app, int reboots, String packageName) {
        if (app.getApiCalls().isEmpty() && reboots == 0) {
            LOG.info("API LOG is empty. Rebooting the device...");
            fixEmptyApiLog();
            reboots++;
            LOG.info("Device is rebooted. Try app {} again.", packageName);
            analyze(app, reboots);
        }
    }

    void finalizeAnalysis(App app, int reboots, String packageName) {
        LOG.info("Finalize interactive analysis.");
        app.setPlainApiLog(aa.getContentOfAPILog());
        checkAndReboot(app, reboots, packageName);
        LOG.info("Uninstall app '{}'.", packageName);
        aa.uninstallApp(packageName);
        checkForErrorAndPermissionDialog();
    }
}
