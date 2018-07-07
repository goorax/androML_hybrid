package androML.dynamic_analysis.clicks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final public class ClickExtractor {
    private static final Logger LOG = LoggerFactory.getLogger(ClickExtractor.class);
    private static final String GENERAL_POS_PATTERN = "[1-9][0-9]?.*\\((\\d+), (\\d+)\\).*";
    private static final String NEWLINE_OPERATOR = "\n";

    public ClickExtractor() {

    }

    public List<ClickSequence> extractClicksFromView(ClickSequence click, String view) {
        List<ClickSequence> freshClicks = new ArrayList<>();
        Pattern pattern = Pattern.compile(GENERAL_POS_PATTERN);
        for (String viewElement : view.split(NEWLINE_OPERATOR)) {
            Matcher m = pattern.matcher(viewElement);
            if (m.matches()) {
                createFreshClickSequence(click, freshClicks, view, m);
            }
        }
        return freshClicks;
    }

    private void createFreshClickSequence(ClickSequence click, List<ClickSequence> freshClicks, String view, Matcher m) {
        int x = Integer.valueOf(m.group(1));
        int y = Integer.valueOf(m.group(2));
        ClickPosition freshClickPosition = new ClickPosition(x, y, view);
        List<ClickPosition> freshClickPath = new ArrayList<>(click.getClickPath());
        freshClickPath.add(freshClickPosition);
        ClickSequence freshClickSequence = new ClickSequence(click.getRootView(), click.getPackageName(),
                click.getEntryActivityName(), freshClickPath);
        freshClicks.add(freshClickSequence);
    }

}
