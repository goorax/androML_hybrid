package database;

import androML.AndroMLConfig;
import androML.database.DatabaseAdapter;
import androML.database.DatabaseEndpoint;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class DatabaseAdapterTest {
    public static final String TEST_DATABASE = "testdb";
    public static final String TEST_ID = "12345";
    public static final String TEST_KEY = "testkey";
    public static final String TEST_VALUE = "testvalue";
    private AndroMLConfig config;
    private DatabaseAdapter dba;

    @Before
    public void initializeTests() {
        config = new AndroMLConfig();
        dba = new DatabaseAdapter(config, new DatabaseEndpoint(config));
    }

    @Test
    public void createDatabase() throws Exception {
        dba.createDatabase(TEST_DATABASE);
        Assert.assertTrue(dba.isDatabasePresent(TEST_DATABASE));
    }

    @Test
    public void removeDatabase() throws Exception {
        dba.createDatabase(TEST_DATABASE);
        dba.removeDatabase(TEST_DATABASE);
        Assert.assertFalse(dba.isDatabasePresent(TEST_DATABASE));
    }

    @Test
    public void receiveAllDatabaseIds() throws Exception {
        buildDatabaseAndCreateDocument();
        List<String> ids = dba.receiveAllDatabaseIds(TEST_DATABASE);
        String id = ids.get(0);
        Assert.assertFalse(ids.isEmpty());
        Assert.assertTrue(id.equals(TEST_ID));
    }

    @Test
    public void createDocument() throws Exception {
        buildDatabaseAndCreateDocument();
        Assert.assertTrue(dba.isDocumentPresent(TEST_ID, TEST_DATABASE));
    }

    @Test
    public void receiveDocument() throws Exception {
        buildDatabaseAndCreateDocument();
        JSONObject document = dba.receiveDocument(TEST_ID, TEST_DATABASE);
        String value = document.getString(TEST_KEY);
        Assert.assertTrue(value.equals(TEST_VALUE));
    }

    @Test
    public void removeDocument() throws Exception {
        buildDatabaseAndCreateDocument();
        JSONObject document = dba.receiveDocument(TEST_ID, TEST_DATABASE);
        String rev = document.getString("_rev");
        dba.removeDocument(TEST_ID, rev, TEST_DATABASE);
        Assert.assertFalse(dba.isDocumentPresent(TEST_ID, TEST_DATABASE));
    }

    private void buildDatabaseAndCreateDocument() {
        JSONObject value = buildJSONObject();
        dba.createDatabase(TEST_DATABASE);
        dba.createDocument(TEST_ID, value, TEST_DATABASE);
    }

    private JSONObject buildJSONObject() {
        JSONObject testJsonObject = new JSONObject();
        testJsonObject.put(TEST_KEY, TEST_VALUE);
        return testJsonObject;
    }

    @After
    public void cleanupDatabase() {
        dba.removeDatabase(TEST_DATABASE);
    }

}