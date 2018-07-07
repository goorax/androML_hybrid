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

final public class ElasticAdapter implements Database {

    public static final String FOUND = "found";
    public static final String UPDATE = "/_update";
    public static final String DOC = "doc";

    private enum DbType {
        DB_CREATE, DB_DELETE, DOC_CREATE, DOC_UPDATE, DOC_DELETE
    }

    private static final Logger LOG = LoggerFactory.getLogger(ElasticAdapter.class);
    private static final String REST_ERROR = "Failed to sent REST request.";
    private static final String DB_ERROR = "Error in database response.";
    private static final String MAPPING = "_mapping/";
    private static final String REPORT = "/report/";
    private static final int OK = 200;
    private static final String HTTP = "http://";
    private static final String AT = "@";
    private static final String COLON = ":";
    private static final String SLASH = "/";
    private static final String SEARCH_ALL = "/_search?q=*:*?_source=false";
    private static final int CREATED = 201;
    private final AndroMLConfig config;
    private final String endpoint;

    public ElasticAdapter(AndroMLConfig config) {
        this.config = config;
        String auth = config.getElasticUser() + COLON + config.getElasticPw();
        endpoint = HTTP + auth + AT + config.getElasticHost() + COLON + config.getElasticPort() + SLASH;
    }

    @Override
    public void createDatabaseWithMapping(String databaseName, JSONObject mapping) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.put(endpoint + databaseName).body(mapping).asJson();
        } catch (UnirestException e) {
            LOG.error(REST_ERROR, e);
        }
        handleResponse(databaseName, DbType.DB_CREATE, response);
    }


    public JSONObject getMappingFromDatabase(String databaseName, String mappingName) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.get(endpoint + databaseName + SLASH + MAPPING + mappingName).asJson();
        } catch (UnirestException e) {
            LOG.error(REST_ERROR, e);
        }
        return response.getBody().getObject();
    }

    @Override
    public void createDatabase(String databaseName) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.put(endpoint + databaseName).asJson();
        } catch (UnirestException e) {
            LOG.error(REST_ERROR, e);
        }
        handleResponse(databaseName, DbType.DB_CREATE, response);
    }

    @Override
    public boolean isDatabasePresent(String databaseName) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.head(endpoint + databaseName).asJson();
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
    public void removeDatabase(String databaseName) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.delete(endpoint + databaseName).asJson();
        } catch (UnirestException e) {
            LOG.error(REST_ERROR, e);
        }
        handleResponse(databaseName, DbType.DB_DELETE, response);
    }

    @Override
    public List<String> receiveAllDatabaseIds(String databaseName) {
        List<String> databaseIds = new ArrayList<>();
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.get(endpoint + databaseName + "/_search")
                    .queryString("q", "*")
                    //.queryString("_source", "_id")
                    .asJson();
        } catch (UnirestException e) {
            LOG.error(REST_ERROR, e);
        }
        extractIds(databaseIds, response);
        return databaseIds;
    }

    @Override
    public List<String> receiveAllDatabaseIdsFrom(String databaseName, int from, int size) {
        List<String> databaseIds = new ArrayList<>();
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.get(endpoint + databaseName + "/_search")
                    .queryString("from", from)
                    .queryString("size", size)
                    .queryString("q", "*:*")
                    .queryString("_source", "_id")
                    .asJson();
        } catch (UnirestException e) {
            LOG.error(REST_ERROR, e);
        }
        extractIds(databaseIds, response);
        return databaseIds;
    }

    private void extractIds(List<String> databaseIds, HttpResponse<JsonNode> response) {
        try {
            JSONArray hits = response.getBody().getObject().getJSONObject("hits").getJSONArray("hits");
            Iterator it = hits.iterator();
            while (it.hasNext()) {
                JSONObject entry = (JSONObject) it.next();
                databaseIds.add(entry.getString("_id"));
            }
        } catch (Exception e) {
            LOG.error("Iterating over ids of index failed.", e);
        }
    }

    public JSONObject receiveCustomSourcesFromID(String databaseName, String id, String[] sourceArray) {
        HttpResponse<JsonNode> response = null;
        StringBuilder sources = buildSourcesString(sourceArray);
        try {
            response = Unirest.get(endpoint + databaseName + REPORT + id + SLASH)
                    .queryString("_source_include", sources.toString())
                    .asJson();
        } catch (UnirestException e) {
            LOG.error(REST_ERROR, e);
        }
        return response.getBody().getObject();
    }

    private StringBuilder buildSourcesString(String[] sourceArray) {
        StringBuilder sources = new StringBuilder();
        for (String source : sourceArray) {
            sources.append(source);
            sources.append(",");
        }
        sources.deleteCharAt(sources.lastIndexOf(","));
        return sources;
    }

    public void receiveCustomSourcesFromIndex(String databaseName) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.get(endpoint + databaseName + "/report/")
                    .queryString("q", "*:*")
                    .queryString("_source", "ApkMetaReport.app_file_name,ApkMetaReport.pkg_name")
                    .asJson();
        } catch (UnirestException e) {
            LOG.error(REST_ERROR, e);
        }
    }


    @Override
    public void createDocument(String key, JSONObject content, String databaseName) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.put(endpoint + databaseName + REPORT + key).body(content).asJson();
        } catch (UnirestException e) {
            LOG.error(REST_ERROR, e);
        }
        handleResponse(key, DbType.DOC_CREATE, response);
    }

    public void createDocumentWithCustomType(String key, JSONObject content, String databaseName, String type) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.put(endpoint + databaseName + type + key).body(content).asJson();
        } catch (UnirestException e) {
            LOG.error(REST_ERROR, e);
        }
        handleResponse(key, DbType.DOC_CREATE, response);
    }

    @Override
    public void updateDocument(String key, JSONObject content, String databaseName) {
        JSONObject update = new JSONObject();
        update.put(DOC, content);
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.post(endpoint + databaseName + REPORT + key + UPDATE).body(update).asJson();
        } catch (UnirestException e) {
            LOG.error(REST_ERROR, e);
        }
        handleResponse(key, DbType.DOC_UPDATE, response);
    }

    @Override
    public JSONObject receiveDocument(String key, String databaseName) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.get(endpoint + databaseName + REPORT + key).asJson();
        } catch (UnirestException e) {
            LOG.error(REST_ERROR, e);
        }
        return response.getBody().getObject();
    }

    @Override
    public boolean isDocumentPresent(String key, String databaseName) {
        boolean isPresent = false;
        JSONObject result = receiveDocument(key, databaseName);
        if (result.has(FOUND)) {
            isPresent = result.getBoolean(FOUND);
        }
        return isPresent;
    }


    @Override
    public void removeDocument(String key, String rev, String databaseName) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.delete(endpoint + databaseName + REPORT + key).asJson();
        } catch (UnirestException e) {
            LOG.error(REST_ERROR, e);
        }
        handleResponse(key, DbType.DOC_DELETE, response);
    }


    private void handleResponse(String name, DbType dbType, HttpResponse<JsonNode> response) {
        if ((response.getStatus() == OK) || (response.getStatus() == CREATED)) {
            switch (dbType) {
                case DB_CREATE:
                    LOG.info("Database '{}' created successfully.", name);
                    break;
                case DB_DELETE:
                    LOG.info("Database '{}' deleted successfully.", name);
                    break;
                case DOC_CREATE:
                    LOG.info("Document '{}' created successfully.", name);
                    break;
                case DOC_UPDATE:
                    LOG.info("Document '{}' updated successfully.", name);
                    break;
                case DOC_DELETE:
                    LOG.info("Document '{}' deleted successfully.", name);
                    break;
                default:
                    throwElasticException(DB_ERROR);
                    break;
            }
        } else {
            throwElasticException(DB_ERROR);
        }
    }

    private void throwElasticException(String databaseName) {
        try {
            throw new ElasticException(DB_ERROR);
        } catch (ElasticException e) {
            LOG.error(DB_ERROR, databaseName, e);
        }
    }

    static class ElasticException extends Exception {
        public ElasticException(String message) {
            super(message);
        }
    }
}
