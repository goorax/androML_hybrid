package androML.dynamic_analysis.runner;

import androML.dynamic_analysis.adb.AdbAdapter;
import androML.dynamic_analysis.clicks.ClickPosition;
import androML.dynamic_analysis.clicks.ClickSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final public class ClickRunner {
    private static final Logger LOG = LoggerFactory.getLogger(ClickRunner.class);

    public ClickRunner() {
    }

    public String returnViewAfterClicking(AdbAdapter aa, ClickSequence clickSequence) {
        String packageName = clickSequence.getPackageName();
        if (!aa.isAppRunning(packageName)) {
            aa.launchApp(packageName);
            return "";
        }
        performClicksInPath(aa, clickSequence);
        if (!aa.isAppRunning(packageName)) {
            aa.launchApp(packageName);
            return "";
        }
        String newViewHierarchy = checkAndReturnFreshViewDump(aa, clickSequence, packageName);
        updateRootView(clickSequence, newViewHierarchy);
        return newViewHierarchy;
    }

    private void updateRootView(ClickSequence clickSequence, String viewHierarchy) {
        if (!viewHierarchy.equals("")) {
            clickSequence.setRootView(viewHierarchy);
        }
    }

    private String checkAndReturnFreshViewDump(AdbAdapter aa, ClickSequence clickSequence, String packageName) {
        String newViewHierarchy = "";
        int pathLength = clickSequence.getClickPath().size();
        String freshRawViewDump = aa.dumpRawViewHierarchy();
        String freshViewDump = aa.cleanHierarchy(freshRawViewDump).toString();

        if (UIPatterns.isExceptionDialog(freshRawViewDump)) {
            Interactions.pushExceptionButton(aa, freshRawViewDump);
            aa.stopApp(packageName);
            return newViewHierarchy;
        }

        freshViewDump = handlePermissionDialogAndReturnView(aa, freshRawViewDump, freshViewDump);

        int position = containsAlreadyKnownView(clickSequence, freshViewDump);

        if (aa.isAppRunning(packageName)) {
            if (position == -1) {
                // if we found new fresh views
                if (UIPatterns.isDeterministic(freshRawViewDump)) {
                    newViewHierarchy = freshViewDump;
                }
                Interactions.pressBackKeys(aa, pathLength);
            } else {
                Interactions.pressBackKeys(aa, position);
            }
        } else {
            return "";
        }
        return newViewHierarchy;
    }

    private String handlePermissionDialogAndReturnView(AdbAdapter aa, String freshRawViewDump, String freshViewDump) {
        boolean isPermissionDialog = UIPatterns.isPermissionDialog(freshRawViewDump);
        while (isPermissionDialog) {
            Interactions.allowPermission(aa, freshRawViewDump);
            freshRawViewDump = aa.dumpRawViewHierarchy();
            freshViewDump = aa.cleanHierarchy(freshRawViewDump).toString();
            isPermissionDialog = UIPatterns.isExceptionDialog(freshRawViewDump);
        }
        return freshViewDump;
    }

    private int containsAlreadyKnownView(ClickSequence clickSequence, String freshViewDump) {
        for (ClickPosition pos : clickSequence.getClickPath()) {
            if (pos.getView().equals(freshViewDump)) {
                return clickSequence.getClickPath().indexOf(pos);
            }
        }
        return -1;
    }


    private void performClicksInPath(AdbAdapter aa, ClickSequence clickSequence) {
        LOG.info("# Click Sequence start");
        for (ClickPosition pos : clickSequence.getClickPath()) {
            LOG.info("Click App '{}' with Position ({}, {})", clickSequence.getPackageName(), pos.getX(), pos.getY());
            aa.startTapCommand(pos.getX(), pos.getY());
            aa.sleep(1500);
        }
        LOG.info("# Click sequence end");
    }


}
