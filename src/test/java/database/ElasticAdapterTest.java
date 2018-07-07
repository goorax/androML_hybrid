package database;

import androML.AndroMLConfig;
import androML.database.ElasticAdapter;
import androML.database.ElasticMapper;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class ElasticAdapterTest {
    public static final String LOCALHOST = "localhost";
    private static final String ELASTIC_USER = "elastic";
    private static final String ELASTIC_PW = "changeme";
    public static final String TESTINDEX = "testindex";
    public static final String REPORT = "report";
    public static final String TESTKEY = "testkey";
    public static final String TESTKEY2 = "testkey2";
    public static final String TEST_KEY = "test";
    public static final String SOURCE = "_source";
    public static final String TEST_VALUE = "this is a test value";
    public static final String FOUND = "found";
    public static final String UPDATED_TEST_VALUE = "Updated test value";
    public static final String EMPTY_STRING = "";
    private ElasticAdapter elasticAdapter;
    private AndroMLConfig config;

    @Before
    public void initializeTest() {
        // DO NOT REMOVE THIS MOCKING!!!
        config = mock(AndroMLConfig.class);
        given(config.getElasticUser()).willReturn(ELASTIC_USER);
        given(config.getElasticPw()).willReturn(ELASTIC_PW);
        given(config.getElasticHost()).willReturn(LOCALHOST);
        given(config.getElasticPort()).willReturn(9200);
        given(config.getElasticIndex()).willReturn(TESTINDEX);
        elasticAdapter = new ElasticAdapter(config);
        elasticAdapter.removeDatabase(TESTINDEX);
    }

    @Test
    public void testCreateDatabase() {
        String index = config.getElasticIndex();
        elasticAdapter.createDatabase(index);
        boolean present = elasticAdapter.isDatabasePresent(index);
        Assert.assertTrue(present);
    }

    @Test
    public void testDeleteDatabase() {
        String index = config.getElasticIndex();
        elasticAdapter.createDatabase(index);
        elasticAdapter.removeDatabase(index);
        boolean present = elasticAdapter.isDatabasePresent(index);
        Assert.assertFalse(present);
    }

    @Test
    public void testCreateDatabaseWithMapping() {
        String index = config.getElasticIndex();
        JSONObject mapping = ElasticMapper.getStaticReportMapping();
        elasticAdapter.createDatabaseWithMapping(index, mapping);
        JSONObject receivedMapping = elasticAdapter.getMappingFromDatabase(index, REPORT);
        Assert.assertEquals(receivedMapping.getJSONObject(index).toString().length(), mapping.toString().length());
    }

    @Test
    public void testCreateDocument() {
        String index = config.getElasticIndex();
        JSONObject content = buildTestDocument();
        elasticAdapter.createDatabase(index);
        elasticAdapter.createDocument(TESTKEY, content, index);
        JSONObject document = elasticAdapter.receiveDocument(TESTKEY, index);
        Assert.assertEquals(document.getJSONObject(SOURCE).getString(TEST_KEY), content.getString(TEST_KEY));
    }

    @Test
    public void testReceiveAllDatabaseIds() {
        createTestDocuments();
        List<String> ids = elasticAdapter.receiveAllDatabaseIds(TESTINDEX);
        Assert.assertEquals(ids.size(), 10);
    }

    private void createTestDocuments() {
        elasticAdapter.createDatabase(TESTINDEX);
        for (int i = 0; i < 10; i++) {
            elasticAdapter.createDocument(TESTKEY + i, buildTestDocument(), config.getElasticIndex());
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
    }

    @Test
    public void testReceiveCustomSourcesFromId() {
        createTestDocuments();
        String[] sources = {"test"};
        List<String> ids = elasticAdapter.receiveAllDatabaseIds(TESTINDEX);
        Assert.assertEquals(ids.size(), 10);
        JSONObject result = elasticAdapter.receiveCustomSourcesFromID(TESTINDEX, ids.get(0), sources);
        Assert.assertTrue(result.has("_source"));
        Assert.assertTrue(result.getJSONObject("_source").has("test"));
    }

    @Test
    public void testRemoveDocument() {
        String index = config.getElasticIndex();
        elasticAdapter.createDatabase(index);
        elasticAdapter.createDocument(TESTKEY, buildTestDocument(), index);
        elasticAdapter.removeDocument(TESTKEY, "", index);
        JSONObject result = elasticAdapter.receiveDocument(TESTKEY, index);
        Assert.assertFalse(result.getBoolean(FOUND));
    }

    @Test
    public void testIsDocumentPresent() {
        String index = config.getElasticIndex();
        elasticAdapter.createDatabase(index);
        elasticAdapter.createDocument(TESTKEY, buildTestDocument(), index);
        boolean isPresent = elasticAdapter.isDocumentPresent(TESTKEY, index);
        Assert.assertTrue(isPresent);
    }

    private JSONObject buildTestDocument() {
        JSONObject content = new JSONObject();
        content.put(TEST_KEY, TEST_VALUE);
        return content;
    }

    @Test
    public void testUpdateDocument() {
        String index = config.getElasticIndex();
        String value = EMPTY_STRING;
        elasticAdapter.createDatabase(index);
        elasticAdapter.createDocument(TESTKEY, buildTestDocument(), index);
        JSONObject content = new JSONObject();
        content.put(TEST_KEY, UPDATED_TEST_VALUE);
        elasticAdapter.updateDocument(TESTKEY, content, index);
        JSONObject document = elasticAdapter.receiveDocument(TESTKEY, index);
        value = extractValue(value, document);
        Assert.assertEquals(value, UPDATED_TEST_VALUE);
    }

    private String extractValue(String value, JSONObject document) {
        if (document.has(SOURCE)) {
            JSONObject source = document.getJSONObject(SOURCE);
            if (source.has(TEST_KEY)) {
                value = source.getString(TEST_KEY);
            }
        }
        return value;
    }
}
