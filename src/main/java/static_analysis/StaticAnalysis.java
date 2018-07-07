package androML.static_analysis;

import androML.AndroMLConfig;
import androML.database.Database;
import androML.static_analysis.reports.Report;
import androML.static_analysis.reports.VirusTotalReport;
import androML.static_analysis.analyzer.ApkMetaAnalyzer;
import androML.static_analysis.analyzer.AssetAnalyzer;
import androML.static_analysis.analyzer.ByteCodeAnalyzer;
import androML.static_analysis.analyzer.StaticAnalyzer;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

final public class StaticAnalysis {
    private static final Logger LOG = LoggerFactory.getLogger(StaticAnalysis.class);

    private static final String SOURCE_INCLUDE_VIRUSTOTAL_SHA1 = "?_source_include=VirusTotalReport.sha1";
    private static final String SOURCE = "_source";
    private static final String VIRUS_TOTAL_REPORT = "VirusTotalReport";

    private List<StaticAnalyzer> staticAnalyzers;
    private Database db;
    private AndroMLConfig config;

    public StaticAnalysis(AndroMLConfig config, Database db) {
        this.db = db;
        this.config = config;
        staticAnalyzers = new ArrayList<>();
    }

    public void checkForStartingAnalysis() {
        if (config.isStaticAnalysis()) {
            initializeStaticAnalyzers();
            startStaticAnalysis(config.getPath());
        }
    }

    private void initializeStaticAnalyzers() {
        staticAnalyzers.add(new ApkMetaAnalyzer(config));
        staticAnalyzers.add(new AssetAnalyzer(config));
        staticAnalyzers.add(new ByteCodeAnalyzer(config));
    }

    private void startAnalyzers(File file, List<Report> reports) {
        LOG.info("Analyzing file " + file.getName());
        for (StaticAnalyzer staticAnalyzer : staticAnalyzers) {
            staticAnalyzer.analyze(file);
            reports.add(staticAnalyzer.getReport());
        }
    }

    private void startStaticAnalysis(String directoryPath) {
        File[] directory = loadDirectory(directoryPath);
        if (directory != null) {
            for (File file : directory) {
                if (file.isDirectory()) {
                    startStaticAnalysis(file.getAbsolutePath());
                } else {
                    processApkFile(file);
                }
            }
        }
    }

    private void processApkFile(File file) {
        List<Report> reports = new ArrayList<>();
        try {
            String apkHash = FileHasher.hashFile(file, FileHasher.SHA256);
            String checkForVirusTotalReport = apkHash + SOURCE_INCLUDE_VIRUSTOTAL_SHA1;
            JSONObject response = db.receiveDocument(checkForVirusTotalReport, config.getElasticIndex());
            if (hasReport(response)) {
                LOG.info("The report with key {} is already present.", apkHash);
                startAnalyzers(file, reports);
                updateReports(reports, apkHash);
            } else {
                startAnalyzers(file, reports);
                storeReports(reports, apkHash);
            }
        } catch (Exception e) {
            LOG.error("Could not parse APK: {} ", file.getName());
        }
    }

    private boolean hasReport(JSONObject response) {
        return response.has(SOURCE) && response.getJSONObject(SOURCE).has(VIRUS_TOTAL_REPORT);
    }

    private void updateReports(List<Report> reports, String apkHash) {
        JSONObject finalReport = buildFinalReport(reports);
        db.updateDocument(apkHash, finalReport, config.getElasticIndex());
    }

    private void storeReports(List<Report> reports, String apkHash) {
        JSONObject finalReport = buildFinalReport(reports);
        addVirusTotalReport(apkHash, finalReport);
        db.createDocument(apkHash, finalReport, config.getElasticIndex());
    }

    private void addVirusTotalReport(String hash, JSONObject report) {
        VirusTotalReport virusTotalReport = new VirusTotalReport(hash);
        report.put(virusTotalReport.getReportName(), virusTotalReport.getReportAsJSON());
    }

    private JSONObject buildFinalReport(List<Report> reports) {
        JSONObject finalReport = new JSONObject();
        for (Report report : reports) {
            finalReport.put(report.getReportName(), report.getReportAsJSON());
        }
        return finalReport;
    }

    private File[] loadDirectory(String path) {
        File dir = new File(path);
        return dir.listFiles();
    }

}
