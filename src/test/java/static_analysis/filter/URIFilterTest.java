package static_analysis.filter;

import androML.static_analysis.analyzer.filters.URIFilter;
import helper.TestConstants;
import learning_tests.DexLib2Test;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

public class URIFilterTest {
    private String testApkPath = getClass().getClassLoader().getResource(TestConstants.TEST_APK).getPath();
    private static final int URI_SIZE = 36;

    @Test
    public void testURIFiltering() {
        DexBackedDexFile dexFile = DexLib2Test.loadDexFile(testApkPath);
        Set<String> uris = URIFilter.filterURIsFrom(dexFile.getStrings());
        Assert.assertTrue(uris.size() == URI_SIZE);
    }
}
