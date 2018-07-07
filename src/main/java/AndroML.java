package androML;

import androML.database.Database;
import androML.database.ElasticAdapter;
import androML.dynamic_analysis.DynamicAnalysis;
import androML.static_analysis.StaticAnalysis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final public class AndroML {
    private static final Logger LOG = LoggerFactory.getLogger(AndroML.class);

    public static void main(String[] args) {
        AndroMLConfig config = new AndroMLConfig();
        Database db = new ElasticAdapter(config);

        initializeCoreIndex(config, db);

        StaticAnalysis sa = new StaticAnalysis(config, db);
        sa.checkForStartingAnalysis();

        DynamicAnalysis da = new DynamicAnalysis(config, db);
        da.checkForStartingAnalysis();
    }


    private static void initializeCoreIndex(AndroMLConfig config, Database db) {
        if (!db.isDatabasePresent(config.getElasticIndex())) {
            db.createDatabase(config.getElasticIndex());
        }
    }

}
