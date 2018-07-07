package learning_tests;

import helper.TestConstants;
import helper.TestFileLoader;
import net.dongliu.apk.parser.ApkFile;
import net.dongliu.apk.parser.bean.ApkMeta;
import net.dongliu.apk.parser.bean.CertificateMeta;
import net.dongliu.apk.parser.bean.Permission;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;

public class ApkAnalysisTest {
    private static final String SHA_RSA = "SHA1withRSA";
    private static final String MANIFEST = "manifest";

    ApkFile apkFile;
    private String testApkPath = getClass().getClassLoader().getResource(TestConstants.TEST_APK).getPath();

    @Before
    public void initializeApkFile() {
        apkFile = TestFileLoader.loadApkFile(testApkPath);
    }

    @Test
    public void testApkParserApkFileLoading() {
        Assert.assertThat(apkFile, instanceOf(ApkFile.class));
    }

    @Test
    public void testApkParserManifestDecoding() {
        String manifest = "";
        try {
            manifest = apkFile.getManifestXml();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Assert.assertFalse(manifest.isEmpty());
        Assert.assertTrue(manifest.contains(MANIFEST));
    }


    @Test
    public void testApkParserApkMetaLoading() {
        ApkMeta apkMeta = loadApkMeta();
        Assert.assertThat(apkMeta, instanceOf(ApkMeta.class));
    }

    private ApkMeta loadApkMeta() {
        ApkMeta apkMeta = null;
        try {
            apkMeta = apkFile.getApkMeta();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return apkMeta;
    }

    @Test
    public void testApkParserPermissionLoading() {
        ApkMeta apkMeta = loadApkMeta();
        List<Permission> permissionList = apkMeta.getPermissions();
        Assert.assertThat(permissionList, instanceOf(List.class));
    }

    @Test
    public void testCertificateMeta() {
        List<CertificateMeta> certificateMetaList = new ArrayList<>();
        try {
            certificateMetaList.addAll(apkFile.getCertificateMetaList());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        CertificateMeta certificateMeta = certificateMetaList.get(0);
        Assert.assertFalse(certificateMetaList.isEmpty());
        Assert.assertTrue(certificateMeta.getSignAlgorithm().equals(SHA_RSA));
    }

}
