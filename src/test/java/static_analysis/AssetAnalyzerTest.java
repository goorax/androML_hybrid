package static_analysis;

import androML.AndroMLConfig;
import androML.static_analysis.analyzer.AssetAnalyzer;
import helper.TestConstants;
import helper.TestFileLoader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.mockito.Mockito.mock;

public class AssetAnalyzerTest {
    public static final int ASSET_LIST_SIZE = 4;
    public static final String MIME_TYPE_APK = "application/vnd.android.package-archive";
    private String testApkPath = getClass().getClassLoader().getResource(TestConstants.TEST_APK).getPath();
    private AssetAnalyzer assetAnalyzer;

    @Before
    public void initializeAssetAnalyzer() {
        AndroMLConfig config = mock(AndroMLConfig.class);
        assetAnalyzer = new AssetAnalyzer(config);
    }

    @Test
    public void testZipEntryFilter() {
        File file = TestFileLoader.loadRawFile(testApkPath);
        assetAnalyzer.analyze(file);
        Assert.assertTrue(assetAnalyzer.getAssets().size() == ASSET_LIST_SIZE);
    }

    @Test
    public void testMimeTypeAnalyzerForFile() {
        File file = TestFileLoader.loadRawFile(testApkPath);
        String type = null;
        try {
             type = Files.probeContentType(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(type, MIME_TYPE_APK);
    }


}
