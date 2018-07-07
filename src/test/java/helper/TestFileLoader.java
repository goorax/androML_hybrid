package helper;

import net.dongliu.apk.parser.ApkFile;

import java.io.File;
import java.io.IOException;

final public class TestFileLoader {

    private TestFileLoader() {
    }

    public static File loadRawFile(String path) {
        return new File(path);
    }

    public static ApkFile loadApkFile(String path) {
        ApkFile apkFile = null;
        try {
            apkFile = new ApkFile(loadRawFile(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return apkFile;
    }

}
