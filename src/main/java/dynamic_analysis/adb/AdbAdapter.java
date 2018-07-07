package androML.dynamic_analysis.adb;

import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final public class AdbAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(AdbAdapter.class);
    public static final int DEFAULT_SLEEP_TIME = 1200;
    public static final String NEWLINE_OPERATOR = "\n";
    public static final String SUCCESS = "Success";
    public static final String DEVICE = "device:";
    public static final String LOCK_SCREEN_IDENTIFIER = "com.android.systemui:id/battery";
    public static final String ALREADY_INSTALLED = "INSTALL_FAILED_ALREADY_EXISTS";
    public static final String APK_REGEX = ".*\\.apk$";
    
    private AdbCommands cmd;

    public AdbAdapter(String deviceID) {
       this.cmd = new AdbCommands(deviceID);
    }

    public boolean isAppInstalled(String packageName) {
        String result = "";
        result = executeCommandWithResult(String.format(cmd.isAppInstalled, packageName));
        result = result.replace("package:", "").replace(NEWLINE_OPERATOR, "");
        return result.equals(packageName);
    }

    public boolean installApp(String pathParam) {
        String result = "";
        String path = pathParam.replaceAll(" ", "_");
        if (!isApkFile(path)) {
            path = transformToApkFile(path);
            result = executeCommandWithResult(String.format(cmd.installApp, path));
            sleep(3000);
            try {
                if (path.startsWith("/tmp/")) {
                    executeCommand(String.format("rm %s", path));
                }
            } catch (IOException e) {
                LOG.error("Removing of temp file failed.", e);
            }
        } else {
            result = executeCommandWithResult(String.format(cmd.installApp, path));
            sleep();
        }
        boolean isInstalled = result.contains(SUCCESS) || result.contains(ALREADY_INSTALLED);
        return isInstalled;
    }

    private boolean isApkFile(String path) {
        return path.matches(APK_REGEX);
    }

    private String transformToApkFile(String path) {
        String fileName = executeCommandWithResult(String.format("basename %s", path));
        String pathReplacement = "/tmp/" + fileName.replace(NEWLINE_OPERATOR, "") + ".apk";
        try {
            executeCommand(String.format("cp %s %s", path, pathReplacement));
        } catch (IOException e) {
            LOG.error("Renaming to .apk file failed.", e);
        }
        path = pathReplacement;
        return path;
    }

    public void uninstallApp(String packageName) {
        try {
            executeCommand(String.format(cmd.uninstallApp, packageName));
            sleep();
        } catch (IOException e) {
            LOG.error("Uninstalling of app package {} failed.", packageName, e);
        }
    }

    public String dumpViewHierarchy() {
        String hierarchy = "";
        try {
            hierarchy = executeCommandWithResult(cmd.dumpHierarchy);
        } catch (Exception e) {
            LOG.error("Dump command was not successful", e);
        }
        StringBuilder cleanedHierarchy = cleanHierarchy(hierarchy);
        return cleanedHierarchy.toString();
    }

    public String dumpRawViewHierarchy() {
        String hierarchy = "";
        try {
            hierarchy = executeCommandWithResult(cmd.dumpHierarchy);
        } catch (Exception e) {
            LOG.error("Dump command was not successful", e);
        }
        return hierarchy;
    }

    public StringBuilder cleanHierarchy(String hierarchy) {
        StringBuilder cleanedHierarchy = new StringBuilder();
        Pattern pattern = Pattern.compile("(\\s*)([a-zA-Z0-9\\.]+)(.*)(\\((\\d+), (\\d+)\\)).*");
        for (String line : hierarchy.split(NEWLINE_OPERATOR)) {
            Matcher m = pattern.matcher(line);
            createCleanHierarchyFromMatch(cleanedHierarchy, m);
        }
        return cleanedHierarchy;
    }

    private void createCleanHierarchyFromMatch(StringBuilder cleanedHierarchy, Matcher m) {
        if (m.matches()) {
            cleanedHierarchy.append(m.group(1).length());
            cleanedHierarchy.append(m.group(2));
            cleanedHierarchy.append(m.group(4));
            cleanedHierarchy.append(NEWLINE_OPERATOR);
        }
    }

    public boolean isAppRunning(String packageName) {
        String result = "";
        try {
            result = executeCommandWithResult(cmd.getRunningPkg);
        } catch (Exception e) {
            LOG.error("isAppRunning command was not successful", e);
        }
        return result.contains(packageName);
    }

    public Pair<String, String> getFocusedPkgAndActivity() {
        String result = "";
        try {
            result = executeCommandWithResult(cmd.getFocusedPkgAndActivity);
        } catch (Exception e) {
            LOG.error("isActivityInFocus command was not successful", e);
        }
        return extractActivityInformation(result);
    }

    public boolean isActivityInFocus(String packageName, String activityName) {
        String result = "";
        try {
            result = executeCommandWithResult(cmd.getFocusedPkgAndActivity);
        } catch (Exception e) {
            LOG.error("isActivityInFocus command was not successful", e);
        }

        Pair<String, String> pkgAct = extractActivityInformation(result);
        return pkgAct.getKey().equals(packageName) && pkgAct.getValue().equals(activityName);
    }

    private Pair<String, String> extractActivityInformation(String result) {
        String[] pkgActivity = result.replace(NEWLINE_OPERATOR, "").split("/");
        if (pkgActivity.length == 2) {
            String recentPkg = pkgActivity[0];
            String recentActivity = pkgActivity[1].replace("}", "");
            recentActivity = recentActivity.replaceFirst("^\\.", "");
            return new Pair(recentPkg, recentActivity);
        } else {
            return new Pair<>("","");
        }
    }

    public void launchApp(String packageName) {
        try {
            executeCommand(String.format(cmd.launchApp, packageName));
        } catch (IOException e) {
            LOG.error("Launching of app {} failed.", packageName, e);
        }
    }

    public void startTapCommand(int x, int y) {
        try {
            executeCommand(String.format(cmd.tap, x, y));
        } catch (IOException e) {
            LOG.error("Tapping on position ({}, {}) failed.", x, y, e);
        }
    }

    public void rebootDevice() {
        /*try {
            executeCommand(cmd.reboot);
        } catch (IOException e) {
            LOG.error("Reboot of device failed.", e);
        }*/
    }

    public boolean isDeviceReady() {
        String ready = "";
        while (!ready.contains(DEVICE)) {
            ready = executeCommandWithResult(cmd.adbDevices);
        }
        String view = "";
        while (!view.contains(LOCK_SCREEN_IDENTIFIER)) {
            view = dumpRawViewHierarchy();
        }
        sleep(10000);
        return true;
    }

    public void wakeDeviceUp() {
        try {
            executeCommand(cmd.wakeup);
        } catch (IOException e) {
            LOG.error("Wake up of device failed.", e);
        }
    }

    public void unlockDevice() {
        String view = dumpRawViewHierarchy();
        if (view.contains(LOCK_SCREEN_IDENTIFIER)) {
            try {
                executeCommand(cmd.powerButton);
                executeCommand(cmd.unlockBySwipe);
            } catch (IOException e) {
                LOG.error("Unlocking of device failed.", e);
            }
            sleep(3000);
        }
    }

    public void pressBackKey() {
        try {
            executeCommand(cmd.back);
        } catch (IOException e) {
            LOG.error("Press back button failed.", e);
        }
    }

    public void startActivity(String packageName, String activityName) {
        if (!activityName.contains(".")) {
            activityName = "." + activityName;
        }
        try {
            executeCommand(String.format(cmd.startActivity, packageName, activityName));
        } catch (IOException e) {
            LOG.error("Starting activity {}/{} failed.", packageName, activityName, e);
        }
    }

    public void startService(String packageName, String serviceName) {
        if (!serviceName.contains(".")) {
            serviceName = "." + serviceName;
        }
        try {
            executeCommand(String.format(cmd.startService, packageName, serviceName));
        } catch (IOException e) {
            LOG.error("Starting service {}/{} failed.", packageName, serviceName, e);
        }
    }

    public void stopApp(String pkgName) {
        try {
            executeCommand(cmd.stopApp + pkgName);
        } catch (IOException e) {
            LOG.error("Stopping app {} failed.", pkgName, e);
        }
    }


    public void deleteAPILog() {
        try {
            executeCommand(String.format(cmd.delAPILog, cmd.apiLog));
            sleep();
        } catch (IOException e) {
            LOG.error("Deleting API log failed.", e);
        }
    }

    public void deleteLocalLog() {
        try {
            executeCommand(cmd.delLocalLog);
        } catch (IOException e) {
            LOG.error("Deleting local log failed.", e);
        }
    }

    public String getContentOfAPILog() {
        StringBuilder content = new StringBuilder();
        try {
            executeCommand(String.format(cmd.copyToErrorLog, cmd.apiLog));
            executeCommand(cmd.pullErrorLog);
            BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream("error.log")));
            String line;
            try {
                while ((line = input.readLine()) != null) {
                    if (line.matches("^[0-9].*") && line.contains("I/Xposed")) {
                        content.append(line + "\n");
                    }
                }
            } catch (IOException e) {
                LOG.error("Reading file was not successful", e);
            }
        } catch (Exception e) {
            LOG.error("Get local log failed.", e);
        }
        return content.toString();
    }

    private void executeCommand(String command) throws IOException {
        Runtime rt = Runtime.getRuntime();
        rt.exec(command);
        sleep();
    }

    public String executeCommandWithResult(String command) {
        String content = "";
        Runtime rt = Runtime.getRuntime();
        final Process p;
        try {
            p = rt.exec(command);
            content = new CommandResult(p).call();
            p.waitFor();
            sleep();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    public void deleteAllLogs() {
        deleteAPILog();
        deleteLocalLog();
    }

    public void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            LOG.error("Sleeping failed.", e);
        }
    }

    public void sleep() {
        try {
            Thread.sleep(DEFAULT_SLEEP_TIME);
        } catch (InterruptedException e) {
            LOG.error("Sleeping failed.", e);
        }
    }


    private class CommandResult implements Callable<String> {
        private Process p;

        public CommandResult(Process p) {
            this.p = p;
        }

        @Override
        public String call() throws Exception {
            StringBuilder result = new StringBuilder();
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            try {
                while ((line = input.readLine()) != null) {
                    result.append(line + "\n");
                }
            } catch (IOException e) {
                LOG.error("Dump command was not successful", e);
            }
            return result.toString();
        }
    }
}
