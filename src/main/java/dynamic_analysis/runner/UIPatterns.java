package androML.dynamic_analysis.runner;

public class UIPatterns {
     public static final String CALENDAR_IDENTIFICATION = ".*(( )*android\\.view\\.View  \\d+ \\(\\d+, \\d+\\)){28}.*";
     private static final String NEWLINE_OPERATOR = "\n";
     private static final String GERMAN_EXCEPTION_DIALOG = ".*android:id/message.*wurde beendet\\..*";
     private static final String ENGLISH_EXCEPTION_DIALOG = ".*android:id/message.*Unfortunately.*has stopped\\..*";
     private static final String EXCEPTION_GOOGLE_PLAY_SERVICES = ".*android\\.widget\\.TextView.*android:id/message.*Unfortunately, Google Play services for Instant Apps has stopped\\..*";
     private static final String PERMISSION_DIALOG = ".*com\\.android\\.packageinstaller:id/permission_allow_button Allow.*";

     public static synchronized boolean isExceptionDialog(String freshViewDump) {
          freshViewDump = freshViewDump.replace(NEWLINE_OPERATOR, "");
          boolean isGermanExceptionDialog = freshViewDump.matches(GERMAN_EXCEPTION_DIALOG);
          boolean isEnglishExceptionDialog = freshViewDump.matches(ENGLISH_EXCEPTION_DIALOG);
          boolean isPlayServicesCrash = freshViewDump.matches(EXCEPTION_GOOGLE_PLAY_SERVICES);
          return isGermanExceptionDialog || isEnglishExceptionDialog || isPlayServicesCrash;
     }

     public static synchronized boolean isPermissionDialog(String freshViewDump) {
          //todo this only recognizes english permission request
          freshViewDump = freshViewDump.replace(NEWLINE_OPERATOR, "");
          return freshViewDump.matches(PERMISSION_DIALOG);
     }

     public static synchronized boolean isDeterministic(String freshRawViewDump) {
          String cleanedRawView = freshRawViewDump.replaceAll("\n","");
          if (cleanedRawView.matches(UIPatterns.CALENDAR_IDENTIFICATION)) {
               return false;
          }
          return true;
     }
}
