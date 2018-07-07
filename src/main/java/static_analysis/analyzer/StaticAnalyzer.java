package androML.static_analysis.analyzer;

import androML.static_analysis.reports.Report;

import java.io.File;

public interface StaticAnalyzer {

    void analyze(File file);

    Report getReport();
}
