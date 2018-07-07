package learning_tests;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.Assert;
import org.junit.Test;

public class VirusTotalApiTest {

    private static final String API_KEY = "<INSERT API KEY>";
    private static final String TEST_RESOURCE = "c8b6ccc0f8b1aea1bcaab4dce266cf76fb47ff170ddc7eef808ec3798c816d2f";
    private static final String ENDPOINT = "https://www.virustotal.com/vtapi/v2/file/report";
    private static final String TEST_SHA1 = "0ede6a8df1c8ff0ce8b6fabcadc9ffec6ea1919c";
    private static final String APIKEY = "apikey";
    private static final String RESOURCE = "resource";
    public static final String SHA_1 = "sha1";
    public static final String VIRUSTOTAL_HTTP_PREFIX = "https://www.virustotal.com/de/file/";
    public static final String ANALYSIS = "/analysis/";

    @Test
    public void testFileScanReportRequest() {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.post(ENDPOINT).queryString(APIKEY, API_KEY).queryString(RESOURCE, TEST_RESOURCE).asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(response.getBody().getObject().getString(SHA_1), TEST_SHA1);
    }

    @Test
    public void testCrawlingOfOfficialReport() {
        StringBuilder endpoint = new StringBuilder();
        endpoint.append(VIRUSTOTAL_HTTP_PREFIX);
        endpoint.append(TEST_RESOURCE);
        endpoint.append(ANALYSIS);
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.get(ENDPOINT).asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(true);
    }
}
