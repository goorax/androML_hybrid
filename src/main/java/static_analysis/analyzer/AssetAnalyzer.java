package androML.static_analysis.analyzer;

import androML.AndroMLConfig;
import androML.static_analysis.reports.AssetReport;
import androML.static_analysis.reports.Report;
import org.apache.commons.collections.list.UnmodifiableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

final public class AssetAnalyzer implements StaticAnalyzer {
    private static final Logger LOG = LoggerFactory.getLogger(AssetAnalyzer.class);
    private static final String ASSET_PREFIX = "^((assets/)|(lib/)).*";

    private final AndroMLConfig config;
    private List<String> assets;
    private ZipFile zipFile;
    private AssetReport report;

    public AssetAnalyzer(AndroMLConfig config) {
        this.config = config;
        assets = new ArrayList<>();
    }

    @Override
    public void analyze(File file) {
        assets.clear();
        loadZipFile(file);
        checkZipEntriesForAssets();
        analyzeAssets();
        buildReport();
    }

    private void buildReport() {
        report = new AssetReport(assets);
    }

    private void loadZipFile(File file) {
        try {
            zipFile = new ZipFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void analyzeAssets() {
        for (String assetName : assets) {
            zipFile.getEntry(assetName);
            // todo fill with logic
        }
    }

    private void checkZipEntriesForAssets() {
        for (Enumeration<? extends ZipEntry> zipEntries = zipFile.entries(); zipEntries.hasMoreElements(); ) {
            ZipEntry zipEntry = zipEntries.nextElement();
            String entryName = zipEntry.getName();
            LOG.trace(entryName);
            if (checkForAsset(entryName)) {
                assets.add(entryName);
            }
        }
    }

    private boolean checkForAsset(String entry) {
        if (entry.matches(ASSET_PREFIX)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Report getReport() {
        return report;
    }

    public List<String> getAssets() {
        return UnmodifiableList.decorate(assets);
    }

}
