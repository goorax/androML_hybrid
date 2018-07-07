package androML.dynamic_analysis.clicks;

import org.apache.commons.collections.list.UnmodifiableList;

import java.util.List;

final public class ClickSequence {
    private String rootView;
    private String packageName;
    private String entryActivityName;
    private List<ClickPosition> clickPath;

    public ClickSequence(String rootView, String packageName, String entryActivityName, List<ClickPosition> clickPath) {
        this.rootView = rootView;
        this.packageName = packageName;
        this.entryActivityName = entryActivityName;
        this.clickPath = clickPath;
    }

    public String getRootView() {
        return rootView;
    }

    public String getEntryActivityName() {
        return entryActivityName;
    }

    public List<ClickPosition> getClickPath() {
        return UnmodifiableList.decorate(clickPath);
    }

    public String getPackageName() {
        return packageName;
    }

    public void setRootView(String rootView) {
        this.rootView = rootView;
    }
}
