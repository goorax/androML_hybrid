package learning_tests;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElasticSearchTest {
    private static final Logger LOG = LoggerFactory.getLogger(ElasticSearchTest.class);
    public static final String TEST_INDEX = "test";
    public static final String ENDPOINT = "http://localhost:9200/";
    public static final int OK = 200;
    public static final String MAPPINGS = "mappings";
    public static final String TESTOBJECT = "testobject";
    public static final String MESSAGE = "message";
    public static final String TEXT = "text";
    public static final String PROPERTIES = "properties";
    public static final String TYPE = "type";
    public static final String SLASH = "/";
    public static final String _MAPPING = "_mapping";
    public static final int ID = 1;
    public static final String EXAMPLE_MESSAGE = "This is a message.";
    public static final int CREATED_ID = 201;
    public static final String ERROR_MESSAGE = "Failed to sent REST request.";

    @Test
    public void createTestIndex() {
        deleteTestIndexWithResponse();
        HttpResponse<JsonNode> response = createTestIndexWithResponse();
        Assert.assertEquals(response.getStatus(), OK);
    }

    @Test
    public void deleteTestIndex() {
        createTestIndexWithResponse();
        HttpResponse<JsonNode> response = deleteTestIndexWithResponse();
        Assert.assertEquals(response.getStatus(), OK);
    }

    private HttpResponse<JsonNode> createTestIndexWithResponse() {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.put(ENDPOINT + TEST_INDEX).asJson();
        } catch (UnirestException e) {
            LOG.error(ERROR_MESSAGE, e);
        }
        return response;
    }

    private HttpResponse<JsonNode> deleteTestIndexWithResponse() {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.delete(ENDPOINT + TEST_INDEX).asJson();
        } catch (UnirestException e) {
            LOG.error(ERROR_MESSAGE, e);
        }
        return response;
    }

    @Test
    public void checkIfIndexExists() {
        createTestIndexWithResponse();
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.head(ENDPOINT + TEST_INDEX).asJson();
        } catch (UnirestException e) {
            LOG.error(ERROR_MESSAGE, e);
        }
        Assert.assertEquals(response.getStatus(), OK);
    }

    private JSONObject createPropertiesOf(String name, String type) {
        JSONObject typeObject = new JSONObject();
        JSONObject nameObject = new JSONObject();
        JSONObject properties = new JSONObject();
        typeObject.put(TYPE, type);
        nameObject.put(name, typeObject);
        properties.put(PROPERTIES, nameObject);
        return properties;
    }

    @Test
    public void createTestIndexWithMapping() {
        deleteTestIndexWithResponse();
        HttpResponse<JsonNode> response = createIndexWithMappingWithResponse();
        Assert.assertEquals(response.getStatus(), OK);
    }

    private HttpResponse<JsonNode> createIndexWithMappingWithResponse() {
        JSONObject mapping = new JSONObject();
        JSONObject properties = createPropertiesOf(MESSAGE, TEXT);
        mapping.put(MAPPINGS, new JSONObject().put(TESTOBJECT, properties));
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.put(ENDPOINT + TEST_INDEX).body(mapping).asJson();
        } catch (UnirestException e) {
            LOG.error(ERROR_MESSAGE, e);
        }
        return response;
    }

    @Test
    public void getTestIndexMapping() {
        createIndexWithMappingWithResponse();
        HttpResponse<JsonNode> response = null;
        String endpoint = ENDPOINT + TEST_INDEX + SLASH + _MAPPING + SLASH + TESTOBJECT;
        try {
            response = Unirest.get(endpoint).asJson();
        } catch (UnirestException e) {
            LOG.error(ERROR_MESSAGE, e);
        }
        Assert.assertEquals(response.getStatus(), OK);
        Assert.assertTrue(response.getBody().toString().contains(TESTOBJECT));
    }

    @Test
    public void createTestDocument() {
        createIndexWithMappingWithResponse();
        HttpResponse<JsonNode> response = createDocumentWithResponse();
        Assert.assertEquals(response.getStatus(), CREATED_ID);
    }

    private HttpResponse<JsonNode> createDocumentWithResponse() {
        HttpResponse<JsonNode> response = null;
        String endpoint = ENDPOINT + TEST_INDEX + SLASH + TESTOBJECT + SLASH + ID;
        JSONObject content = new JSONObject();
        content.put(MESSAGE, EXAMPLE_MESSAGE);
        try {
            response = Unirest.put(endpoint).body(content).asJson();
        } catch (UnirestException e) {
            LOG.error(ERROR_MESSAGE, e);
        }
        return response;
    }

    @Test
    public void getTestDocument() {
        createIndexWithMappingWithResponse();
        createDocumentWithResponse();
        HttpResponse<JsonNode> response = null;
        String endpoint = ENDPOINT + TEST_INDEX + SLASH + TESTOBJECT + SLASH + ID;
        try {
            response = Unirest.get(endpoint).asJson();
        } catch (UnirestException e) {
            LOG.error(ERROR_MESSAGE, e);
        }
        Assert.assertEquals(response.getStatus(), OK);
        Assert.assertTrue(response.getBody().toString().contains(EXAMPLE_MESSAGE));
    }

    @After
    public void cleanUp() {
        deleteTestIndexWithResponse();
    }
}
