package androML.database;

import androML.AndroMLConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final public class DatabaseEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseEndpoint.class);

    private static final String HTTP = "http://";
    private static final String AT = "@";
    private static final String COLON = ":";
    private static final String SLASH = "/";

    private AndroMLConfig config;
    private String dbEndpointWithBasicAuth;
    private String dbEndpoint;

    public DatabaseEndpoint(AndroMLConfig config) {
        this.config = config;
        dbEndpointWithBasicAuth = "";
        dbEndpoint = "";
        buildDbEndpointWithBasicAuth();
        buildDbEndpoint();
    }

    private void buildDbEndpointWithBasicAuth() {
        String userPart = HTTP + config.getCouchdbUser() + COLON + config.getCouchdbPw();
        String hostPart = config.getCouchdbHost() + COLON + config.getCouchdbPort() + SLASH;
        dbEndpointWithBasicAuth = userPart + AT + hostPart;
    }

    private void buildDbEndpoint() {
        dbEndpoint = HTTP + config.getCouchdbHost() + COLON + config.getCouchdbPort() + SLASH;
    }

    public String getDbEndpointWithBasicAuth() {
        return dbEndpointWithBasicAuth;
    }

    public String getDbEndpoint() {
        return dbEndpoint;
    }
}
