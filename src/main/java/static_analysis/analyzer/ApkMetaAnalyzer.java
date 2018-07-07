package androML.static_analysis.analyzer;

import androML.AndroMLConfig;
import androML.static_analysis.reports.ApkMetaReport;
import androML.static_analysis.reports.Report;
import net.dongliu.apk.parser.ApkFile;
import net.dongliu.apk.parser.bean.ApkMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

final public class ApkMetaAnalyzer implements StaticAnalyzer {
    private static final Logger LOG = LoggerFactory.getLogger(ApkMetaAnalyzer.class);
    private final AndroMLConfig config;
    private ApkFile apkFile;
    private ApkMeta apkMeta;
    private Manifest manifest;
    private ApkMetaReport report;

    public ApkMetaAnalyzer(AndroMLConfig config) {
        this.config = config;
    }

    @Override
    public void analyze(File file) {
        apkFile = loadApkFile(file);
        apkMeta = loadApkMeta();
        manifest = createManifest();
        String appFileName = file.getName();
        report = new ApkMetaReport(appFileName, apkMeta, manifest);
    }

    @Override
    public Report getReport() {
        return report;
    }

    private ApkFile loadApkFile(File file) {
        ApkFile apkFile = null;
        try {
            apkFile = new ApkFile(file);
        } catch (IOException e) {
            LOG.error("Generation of ApkFile failed.", e);
        }
        return apkFile;
    }

    private ApkMeta loadApkMeta() {
        ApkMeta apkMeta = null;
        try {
            apkMeta = apkFile.getApkMeta();
        } catch (IOException e) {
            LOG.error("Generation of ApkMeta failed.", e);
        }
        if (apkMeta == null) {
            return new ApkMeta();
        } else {
            return apkMeta;
        }
    }

    private Manifest createManifest() {
        try {
            return new Manifest(apkFile.getManifestXml());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
