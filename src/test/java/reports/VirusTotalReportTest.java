package reports;

import androML.static_analysis.reports.VirusTotalReport;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

public class VirusTotalReportTest {
    private static final String TEST_HASH = "c8b6ccc0f8b1aea1bcaab4dce266cf76fb47ff170ddc7eef808ec3798c816d2f";
    private static final String TEST_SHA1 = "0ede6a8df1c8ff0ce8b6fabcadc9ffec6ea1919c";
    public static final String SHA_1 = "sha1";

    @Test
    public void testVirusTotalReportCreation() {
        VirusTotalReport report = new VirusTotalReport(TEST_HASH);
        JSONObject result = report.getReportAsJSON();
        Assert.assertEquals(result.getString(SHA_1), TEST_SHA1);
    }
}
