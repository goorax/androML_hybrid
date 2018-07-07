package static_analysis;

import androML.AndroMLConfig;
import androML.static_analysis.analyzer.ApkMetaAnalyzer;
import helper.TestConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.mockito.Mockito.mock;

public class ApkMetaAnalyzerTest {
    private String testApkPath = getClass().getClassLoader().getResource(TestConstants.CAR2GO_APK).getPath();
    private ApkMetaAnalyzer apkMetaAnalyzer;

    @Before
    public void initializeAssetAnalyzer() {
        AndroMLConfig config = mock(AndroMLConfig.class);
        apkMetaAnalyzer = new ApkMetaAnalyzer(config);
    }

    @Test
    public void testApkMetaAnalyzer() {
        File file = new File(testApkPath);
        apkMetaAnalyzer.analyze(file);
        Assert.assertNotNull(apkMetaAnalyzer.getReport());
    }
}
