package androML.database;

import androML.AndroMLConfig;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

final public class DatabaseAdapter implements Database {
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseAdapter.class);
    private static final int OK = 200;
    private static final int CREATED = 201;
    private static final int EXISTS = 412;
    private static final String SLASH = "/";
    private static final String REV_TAG = "?rev=";
    private static final String ROWS = "rows";
    private static final String ALL_DOCS = "_all_docs";
    private static final String ID = "id";
    private static final String REST_ERROR = "Failed to send REST request.";

    private AndroMLConfig config;
    private String dbRootEndpoint;

    public DatabaseAdapter(AndroMLConfig config, DatabaseEndpoint databaseEndpoint) {
        this.config = config;
        this.dbRootEndpoint = databaseEndpoint.getDbEndpoint();
    }

    @Override
    public void createDatabase(String databaseName) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.put(dbRootEndpoint + databaseName)
                    .basicAuth(config.getCouchdbUser(), config.getCouchdbPw()).asJson();
        } catch (UnirestException e) {
            LOG.error(REST_ERROR, e);
        }
        handleCreateDatabaseResponse(databaseName, response);
    }

    @Override
    public void createDatabaseWithMapping(String databaseName, JSONObject mapping) {
        // todo implement
    }

    private void handleCreateDatabaseResponse(String databaseName, HttpResponse<JsonNode> response) {
        switch (response.getStatus()) {
            case CREATED:
                LOG.info("Database '{}' created successfully.", databaseName);
                break;
            case EXISTS:
                LOG.info("Database '{}' already exists.", databaseName);
                break;
            default:
                try {
                    throw new DatabaseException("Creation of database failed.");
                } catch (DatabaseException e) {
                    LOG.error("Creation of database '{}' failed.", databaseName, e);
                }
                break;
        }
    }

    @Override
    public void removeDatabase(String databaseName) {
        // handle with care, because the REST call will delete NON-EMPTY databases as well
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.delete(dbRootEndpoint + databaseName)
                    .basicAuth(config.getCouchdbUser(), config.getCouchdbPw()).asJson();
        } catch (UnirestException e) {
            LOG.error(REST_ERROR, e);
        }
        handleDeletionOfDatabaseResponse(databaseName, response);
    }

    private void handleDeletionOfDatabaseResponse(String databaseName, HttpResponse<JsonNode> response) {
        if (response.getStatus() == OK) {
            LOG.info("Database '{}' deleted successfully.", databaseName);
        } else {
            LOG.error("Deletion of database '{}' failed.", databaseName);
        }
    }

    @Override
    public boolean isDatabasePresent(String databaseName) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.get(dbRootEndpoint + databaseName)
                    .basicAuth(config.getCouchdbUser(), config.getCouchdbPw()).asJson();
        } catch (UnirestException e) {
            LOG.error(REST_ERROR, e);
        }
        if (response.getStatus() == OK) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<String> receiveAllDatabaseIds(String databaseName) {
        List<String> ids = new ArrayList<>();
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.get(dbRootEndpoint + databaseName + SLASH + ALL_DOCS)
                    .basicAuth(config.getCouchdbUser(), config.getCouchdbPw()).asJson();
        } catch (UnirestException e) {
            LOG.error(REST_ERROR, e);
        }
        JSONArray resultArray = response.getBody().getObject().getJSONArray(ROWS);
        Iterator it = resultArray.iterator();
        while (it.hasNext()) {
            JSONObject result = (JSONObject) it.next();
            ids.add(result.getString(ID));
        }
        return ids;
    }

    @Override
    public List<String> receiveAllDatabaseIdsFrom(String databaseName, int from, int size) {
        // todo implement
        return null;
    }

    @Override
    public JSONObject receiveCustomSourcesFromID(String databaseName, String id, String[] sourceArray) {
        // todo implement
        return null;
    }

    @Override
    public void createDocument(String key, JSONObject content, String databaseName) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.put(dbRootEndpoint + databaseName + SLASH + key)
                    .basicAuth(config.getCouchdbUser(), config.getCouchdbPw()).body(content).asJson();
        } catch (UnirestException e) {
            LOG.error(REST_ERROR, e);
        }
        handleCreationOfDocumentResponse(key, response);
    }

    @Override
    public void updateDocument(String key, JSONObject content, String databaseName) {
        // todo implement
    }

    private void handleCreationOfDocumentResponse(String key, HttpResponse<JsonNode> response) {
        if (response.getStatus() == CREATED) {
            LOG.info("Document with key '{}' successfully created.", key);
        } else {
            LOG.error("Document with key '{}' not successfully created", key);
        }
    }

    @Override
    public boolean isDocumentPresent(String key, String databaseName) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.get(dbRootEndpoint + databaseName + SLASH + key)
                    .basicAuth(config.getCouchdbUser(), config.getCouchdbPw()).asJson();
        } catch (UnirestException e) {
            LOG.error(REST_ERROR, e);
        }
        if (response.getStatus() == OK) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public JSONObject receiveDocument(String key, String databaseName) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.get(dbRootEndpoint + databaseName + SLASH + key)
                    .basicAuth(config.getCouchdbUser(), config.getCouchdbPw()).asJson();
        } catch (UnirestException e) {
            LOG.error(REST_ERROR, e);
        }

        if (response.getStatus() == OK) {
            LOG.info("Document with key '{}' was successfully received.", key);
            return response.getBody().getObject();
        } else {
            LOG.error("Document with key '{}' was not successfully received.", key);
            return new JSONObject();
        }
    }

    @Override
    public void removeDocument(String key, String rev, String databaseName) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.delete(dbRootEndpoint + databaseName + SLASH + key + REV_TAG + rev)
                    .basicAuth(config.getCouchdbUser(), config.getCouchdbPw()).asJson();
        } catch (UnirestException e) {
            LOG.error(REST_ERROR, e);
        }

        if (response.getStatus() == OK) {
            LOG.info("Document with key '{}' was successfully deleted.", key);
        } else {
            LOG.error("Document with key '{}' was not successfully deleted.", key);
        }

    }

    static class DatabaseException extends Exception {
        public DatabaseException(String message) {
            super(message);
        }
    }
}
