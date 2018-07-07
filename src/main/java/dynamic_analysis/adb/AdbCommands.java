package androML.dynamic_analysis.adb;

public class AdbCommands {
    private String deviceID;
    private String adb;
    public String apiLog;
    public String getRunningPkg;
    public String getFocusedPkgAndActivity;
    public String delAPILog;
    public String delLocalLog;
    public String stopApp;
    public String startActivity;
    public String startService;
    public String tap;
    public String back;
    public String home;
    public String wakeup;
    public String powerButton;
    public String unlockBySwipe;
    public String installApp;
    public String isAppInstalled;
    public String uninstallApp;
    public String dumpHierarchy;
    public String reboot;
    public String adbDevices;
    public String launchApp;
    public String pullErrorLog;
    public String copyToErrorLog;
    
    public AdbCommands(String deviceID) {
        this.deviceID = deviceID;
        this.adb = "adb -s " + this.deviceID + " ";
        this.apiLog = "/data/data/de.robv.android.xposed.installer/log/error.log";
        this.getRunningPkg = this.adb + " shell dumpsys activity activities | grep -i taskrecord | head -1";
        this.getFocusedPkgAndActivity = this.adb + " shell dumpsys activity activities | grep 'mFocusedActivity' | cut -d ' ' -f6";
        this.delAPILog = this.adb + " shell su -c '> %s'";
        this.delLocalLog = "rm -f error.log";
        this.stopApp = this.adb + " shell am force-stop ";
        this.startActivity = this.adb + " shell su -c 'am start -n %s/%s'";
        this.startService = this.adb + " shell su -c 'am startservice -n %s/%s'";
        this.tap = this.adb + " shell input tap %s %s";
        this.back = this.adb + " shell input keyevent 4";
        this.home = this.adb + " shell input keyevent 82";
        this.wakeup = this.adb + " shell input keyevent 224";
        this.powerButton = this.adb + " shell input keyevent 26";
        this.unlockBySwipe = this.adb + " shell input touchscreen swipe 930 880 930 380";
        this.installApp = this.adb + " install -tr %s";
        this.isAppInstalled = this.adb + " shell pm list packages | grep \"package:%s\"";
        this.uninstallApp = this.adb + " uninstall %s";
        this.dumpHierarchy = "dump -c " + this.deviceID;
        this.reboot = this.adb + " reboot";
        this.adbDevices = this.adb + " devices -l";
        this.launchApp = this.adb + " shell monkey -p %s 1";
        this.pullErrorLog = this.adb + " pull /sdcard/error.log .";
        this.copyToErrorLog = this.adb + " shell su -c 'cp %s /sdcard/error.log'";
    }
}
