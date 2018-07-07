package androML.static_analysis.reports;

import org.json.JSONObject;

public interface Report {

    String getReportName();

    JSONObject getReportAsJSON();
}
