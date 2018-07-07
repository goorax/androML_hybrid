package androML.dynamic_analysis;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final public class ApiLogParser {
    private static final Logger LOG = LoggerFactory.getLogger(ApiLogParser.class);
    public static final String NEWLINE_OPERATOR = "\n";
    public static final String API_LOG_PATTERN = ".*Droidmon-apimonitor-([^:]*):(\\{.*\\})";

    public ApiLogParser() {

    }

    public synchronized Map<Integer, JSONObject> parseApiLog(String plainApiLog, String packageName) {
        String[] lines = plainApiLog.split(NEWLINE_OPERATOR);
        Pattern p = Pattern.compile(API_LOG_PATTERN);
        Map<Integer, JSONObject> apiCalls = new HashMap<>();

        for (String line : lines) {
            Matcher m = p.matcher(line);
            if (m.matches()) {
                extractInformationOfLogLine(packageName, apiCalls, m);
            }
        }
        return apiCalls;
    }

    private void extractInformationOfLogLine(String packageName, Map<Integer, JSONObject> apiCalls, Matcher m) {
        if (m.groupCount() == 2) {
            String logPkgName = m.group(1);
            if (logPkgName.equals(packageName)) {
                String content = m.group(2);
                Integer hash = content.hashCode();
                if (!apiCalls.containsKey(hash)) {
                    try {
                        JSONObject jo = new JSONObject(content);
                        apiCalls.put(hash, jo);
                    } catch (JSONException je) {
                        LOG.error("Error while parsing an API log line to JSON, line: {}", content, je);
                    }
                }
            }
        }
    }
}
