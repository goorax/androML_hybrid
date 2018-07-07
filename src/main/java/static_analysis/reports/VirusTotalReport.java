package androML.static_analysis.reports;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

final public class VirusTotalReport implements Report {
    private static final Logger LOG = LoggerFactory.getLogger(VirusTotalReport.class);
    private static final String REPORT_NAME = "VirusTotalReport";
    private static final String ENDPOINT = "https://www.virustotal.com/vtapi/v2/file/report";
    private static final String APIKEY = "apikey";
    private static final String API_KEY = "<INSERT API KEY>";
    private static final String RESOURCE = "resource";
    private static final int OK = 200;
    private static final String REST_ERROR = "Failed to send REST request.";
    public static final int SEC_SLEEP = 40;

    private final String hash;

    public VirusTotalReport(String hash) {
        this.hash = hash;
    }

    private JSONObject buildVirusTotalReport(String hash) {
        HttpResponse<JsonNode> response = null;
        do {
            try {
                response = Unirest.post(ENDPOINT).queryString(APIKEY, API_KEY).queryString(RESOURCE, hash).asJson();
                if (checkForValidResponse(response)) {
                    TimeUnit.SECONDS.sleep((long) Math.random() * SEC_SLEEP);
                }
            } catch (UnirestException | InterruptedException e) {
                LOG.error(REST_ERROR);
            }
        } while (checkForValidResponse(response));
        return response.getBody().getObject();
    }

    private boolean checkForValidResponse(HttpResponse<JsonNode> response) {
        return response.getStatus() != OK;
    }

    @Override
    public String getReportName() {
        return REPORT_NAME;
    }

    @Override
    public JSONObject getReportAsJSON() {
        return buildVirusTotalReport(hash);
    }
}
