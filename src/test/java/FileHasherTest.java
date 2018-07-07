package androML;

import androML.static_analysis.FileHasher;
import net.dongliu.apk.parser.ApkFile;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileHasherTest {
    public static final String SHA256 = "SHA-256";
    public static final String TEST_HASH = "dd506f0f5b113c0d4d8015f727ef67c8ad7bd1a4658e7dd0f50c82ece0abb571";
    public static final String TEST_PKGNAME = "snet.de.tu_berlin.de.cellserviceapp";
    public static final String UNDERSCORE = "_";
    private String testApkPath = getClass().getClassLoader().getResource(helper.TestConstants.TEST_APK).getPath();

    @Test
    public void testFileHasher() {
        String hash = hashFileWithSHA256();
        Assert.assertTrue(hash.equals(TEST_HASH));
    }

    private String hashFileWithSHA256() {
        File file = new File(testApkPath);
        String sha256Hash = "";
        sha256Hash = FileHasher.hashFile(file, SHA256);
        return sha256Hash;
    }

    @Test
    public void testDatabaseIdGeneration() {
        ApkFile apkFile = loadApkFile();
        String pkgName = "";
        try {
            pkgName = apkFile.getApkMeta().getPackageName();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file = new File(testApkPath);
        String databaseId = FileHasher.buildDatabaseId(file, pkgName, SHA256);

        Assert.assertTrue(databaseId.equals(TEST_PKGNAME + UNDERSCORE + TEST_HASH));
    }

    private ApkFile loadApkFile() {
        ApkFile apkFile = null;
        try {
            apkFile = new ApkFile(new File(testApkPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return apkFile;
    }

    @Test
    public void test() {
        int[] array = {1,2,7,5,7,11};
        int result = outlier(array);
        int k  = 0;
    }

    private int outlier(int[] array) {
        List<Integer> odds = new ArrayList<>();
        List<Integer> evens = new ArrayList<>();
        for (int value : array) {
            if (value % 2 == 0) {
                evens.add(value);
            } else {
                odds.add(value);
            }
            if (evens.size() >= 1 && odds.size() >= 1 && evens.size() + odds.size() >= 3) {
                int k  = 0;
            }
        }
        return 0 ;
    }

}
