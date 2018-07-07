package androML.database;

import org.json.JSONObject;

import java.util.List;

public interface Database {

    void createDatabase(String databaseName);

    void createDatabaseWithMapping(String databaseName, JSONObject mapping);

    void removeDatabase(String databaseName);

    boolean isDatabasePresent(String databaseName);

    boolean isDocumentPresent(String key, String databaseName);

    List<String> receiveAllDatabaseIds(String databaseName);

    List<String> receiveAllDatabaseIdsFrom(String databaseName, int from, int size);

    JSONObject receiveCustomSourcesFromID(String databaseName, String id, String[] sourceArray);

    void createDocument(String key, JSONObject content, String databaseName);

    void updateDocument(String key, JSONObject content, String databaseName);

    JSONObject receiveDocument(String key, String databaseName);

    void removeDocument(String key, String rev, String databaseName);

}
