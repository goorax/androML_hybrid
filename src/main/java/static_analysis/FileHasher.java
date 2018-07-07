package androML.static_analysis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

final public class FileHasher {
    private static final Logger LOG = LoggerFactory.getLogger(FileHasher.class);
    public static final String SHA256 = "SHA-256";
    private static final int BYTE_BUFFER = 1024;
    private static final String UNDERSCORE = "_";
    public static final int HEX_FF = 0xff;
    public static final int HEX_100 = 0x100;
    public static final int DEC_16 = 16;

    private FileHasher() {
    }

    public synchronized static String buildDatabaseId(File file, String packageName, String algorithm) {
        StringBuilder dataBaseId = new StringBuilder();
        dataBaseId.append(packageName);
        dataBaseId.append(UNDERSCORE);
        dataBaseId.append(hashFile(file, algorithm));
        return dataBaseId.toString();
    }

    public synchronized static String hashFile(File file, String algorithm) {
        byte[] byteHash = new byte[0];
        try {
            FileInputStream in = new FileInputStream(file);
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            byteHash = processMessageDigest(in, messageDigest);
        } catch (NoSuchAlgorithmException | IOException e) {
            LOG.error("Calculation of byteHash for file failed.", e);
        }
        return convertByteArrayToString(byteHash);
    }

    private static byte[] processMessageDigest(FileInputStream in, MessageDigest messageDigest) throws IOException {
        byte[] buffer = new byte[BYTE_BUFFER];
        int bytesRead = -1;
        while((bytesRead = in.read(buffer)) != -1) {
            messageDigest.update(buffer, 0, bytesRead);
        }
        byte[] byteHash = messageDigest.digest();
        return byteHash;
    }

    private static String convertByteArrayToString(byte[] byteHash) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < byteHash.length; i++) {
            sb.append(Integer.toString((byteHash[i] & HEX_FF) + HEX_100, DEC_16)
                    .substring(1));
        }
        return sb.toString();
    }
}
