package androML;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

final public class AndroMLConfig {
    private static final Logger LOG = LoggerFactory.getLogger(AndroMLConfig.class);

    public static final String STATIC_ANALYSIS = "static.analysis";
    public static final String DYNAMIC_ANALYSIS = "dynamic.analysis";

    private static final String PROPERTY_FILE = "androml.properties";

    private static final String COUCHDB_HOST = "couchdb.host";
    private static final String COUCHDB_PORT = "couchdb.port";
    private static final String COUCHDB_USER = "couchdb.user";
    private static final String COUCHDB_PW = "couchdb.pw";
    private static final String COUCHDB_NAME = "couchdb.name";

    private static final String ELASTIC_HOST = "elastic.host";
    private static final String ELASTIC_PORT = "elastic.port";
    private static final String ELASTIC_INDEX = "elastic.index";
    private static final String ELASTIC_USER = "elastic.user";
    private static final String ELASTIC_PW = "elastic.pw";

    private static final String PATH = "path";

    private static final String STATIC_INDEX = "static.index";

    private static final String DYNAMIC_INDEX_SIMPLE = "dynamic.index.simple";
    private static final String DYNAMIC_INDEX_INTERACTIVE = "dynamic_analysis.index.interactive";
    private static final String DYNAMIC_QUERY_FROM = "dynamic.query.from";
    private static final String DYNAMIC_QUERY_SIZE = "dynamic.query.size";
    private static final String DYNAMIC_ACTIVITIES_SIZE = "dynamic.activities.size";
    public static final String DYNAMIC_TYPE = "dynamic.type";
    public static final String INTERACTIVE = "interactive";
    public static final String DYNAMIC_TIMETHRESHOLD_INTERACTIVE = "dynamic.timethreshold.interactive";

    private Configuration config;

    private String couchdbHost;
    private int couchdbPort;
    private String couchdbUser;
    private String couchdbPw;
    private String couchdbName;

    private String elasticHost;
    private int elasticPort;
    private String elasticUser;
    private String elasticPw;
    private String elasticIndex;

    private String path;

    private boolean staticAnalysis;
    private String staticIndex;
    private String dynamicIndexSimple;
    private String dynamicIndexInteractive;
    private long dynamicTimeThresholdInteractive;
    private boolean dynamicAnalysis;
    private boolean dynamicAnalysisInteractive;
    private int dynamicQueryFrom;
    private int dynamicQuerySize;
    private int dynamicActivitiesSize;


    public AndroMLConfig() {
        loadConfigurationFromFile();
        loadConfigurationIntoFields();
    }

    private void loadConfigurationFromFile() {
        Configurations configs = new Configurations();
        try {
            URL url = getClass().getClassLoader().getResource(PROPERTY_FILE);
            config = configs.properties(url);
        } catch (ConfigurationException e) {
            LOG.error("Apache Commons Configuration loading failed.", e);
        }
    }

    private void loadConfigurationIntoFields() {
        couchdbHost = config.getString(COUCHDB_HOST);
        couchdbPort = config.getInt(COUCHDB_PORT);
        couchdbUser = config.getString(COUCHDB_USER);
        couchdbPw = config.getString(COUCHDB_PW);
        couchdbName = config.getString(COUCHDB_NAME);

        elasticHost = config.getString(ELASTIC_HOST);
        elasticPort = config.getInt(ELASTIC_PORT);
        elasticUser = config.getString(ELASTIC_USER);
        elasticPw = config.getString(ELASTIC_PW);
        elasticIndex = config.getString(ELASTIC_INDEX);

        path = config.getString(PATH);

        staticAnalysis = config.getBoolean(STATIC_ANALYSIS);
        staticIndex = config.getString(STATIC_INDEX);
        dynamicAnalysis = config.getBoolean(DYNAMIC_ANALYSIS);
        String type = config.getString(DYNAMIC_TYPE);
        if (type.equals(INTERACTIVE)) {
            dynamicAnalysisInteractive = true;
        }
        dynamicIndexSimple = config.getString(DYNAMIC_INDEX_SIMPLE);
        dynamicIndexInteractive = config.getString(DYNAMIC_INDEX_INTERACTIVE);
        dynamicTimeThresholdInteractive = config.getLong(DYNAMIC_TIMETHRESHOLD_INTERACTIVE);
        dynamicQueryFrom = config.getInt(DYNAMIC_QUERY_FROM);
        dynamicQuerySize = config.getInt(DYNAMIC_QUERY_SIZE);
        dynamicActivitiesSize = config.getInt(DYNAMIC_ACTIVITIES_SIZE);
    }

    public String getCouchdbHost() {
        return couchdbHost;
    }

    public int getCouchdbPort() {
        return couchdbPort;
    }

    public String getCouchdbName() {
        return couchdbName;
    }

    public String getCouchdbUser() {
        return couchdbUser;
    }

    public String getCouchdbPw() {
        return couchdbPw;
    }

    public String getElasticHost() {
        return elasticHost;
    }

    public int getElasticPort() {
        return elasticPort;
    }

    public String getElasticIndex() {
        return elasticIndex;
    }

    public String getElasticUser() {
        return elasticUser;
    }

    public String getElasticPw() { return elasticPw; }

    public String getPath() {
        return path;
    }

    public boolean isStaticAnalysis() {
        return staticAnalysis;
    }

    public String getStaticIndex() {
        return staticIndex;
    }

    public boolean isDynamicAnalysis() {
        return dynamicAnalysis;
    }

    public boolean isDynamicAnalysisInteractive() {
        return dynamicAnalysisInteractive;
    }

    public String getDynamicIndexSimple() {
        return dynamicIndexSimple;
    }

    public String getDynamicIndexInteractive() {
        return dynamicIndexInteractive;
    }

    public long getDynamicTimeThresholdInteractive() {
        return dynamicTimeThresholdInteractive;
    }

    public int getDynamicQueryFrom() {
        return dynamicQueryFrom;
    }

    public int getDynamicQuerySize() {
        return dynamicQuerySize;
    }

    public int getDynamicActivitiesSize() {
        return dynamicActivitiesSize;
    }
}
