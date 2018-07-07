package androML.static_analysis.reports;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

final public class AssetReport implements Report {
    private static final Logger LOG = LoggerFactory.getLogger(AssetReport.class);

    private static final String REPORT_NAME = "AssetReport";
    public static final String ASSETS = "assets";

    private final List<String> assets;

    public AssetReport(List<String> assets) {
        this.assets = assets;
    }

    @Override
    public JSONObject getReportAsJSON() {
        JSONObject reportJSON = new JSONObject();
        reportJSON.put(ASSETS, assets);
        return reportJSON;
    }

    @Override
    public String getReportName() {
        return REPORT_NAME;
    }

}
