package learning_tests;

import androML.AndroMLConfig;
import androML.database.ElasticAdapter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MachineLearningTest {
    public static final String API_CALLS = "api_calls";
    public static final String SOURCE = "_source";
    public static final String HOOKS_JSON = "hooks.json";
    public static final String SVM_FILE = "svm_file";
    private ElasticAdapter ea;
    private AndroMLConfig config;

    private String hooksPath = getClass().getClassLoader().getResource(HOOKS_JSON).getPath();
    private String targetSVMPath = getClass().getClassLoader().getResource(SVM_FILE).getPath();
    private Map<String, Integer> hookMap;

    @Before
    public void initialize() {
        config = new AndroMLConfig();
        ea = new ElasticAdapter(config);
        hookMap = buildHookMap();
        createNewSVMFile();
    }

    private Map<String, Integer> buildHookMap() {
        Map<String, Integer> hookMap = new HashMap<>();
        JSONParser parser = new JSONParser();
        org.json.simple.JSONObject data = null;
        try {
            data = (org.json.simple.JSONObject) parser.parse(
                    new FileReader(hooksPath));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        JSONObject jo = new JSONObject(data.toJSONString());
        JSONArray hooks = jo.getJSONArray("hookConfigs");
        Iterator it = hooks.iterator();
        while (it.hasNext()) {
            JSONObject apiCall = (JSONObject) it.next();
            String className = apiCall.getString("class_name");
            String method = "";
            if (!apiCall.isNull("method")) {
                method = apiCall.getString("method");
            }
            String key = className + "." + method;
            hookMap.put(key, 0);
        }
        return hookMap;
    }

    @Test
    public void testProcessApiCalls() {
        List<String> ids = ea.receiveAllDatabaseIdsFrom(config.getDynamicIndexSimple(),0,50);
        for (String id : ids) {
            JSONObject result = ea.receiveDocument(id, config.getDynamicIndexSimple());
            if (result.has(SOURCE)) {
                JSONObject source = result.getJSONObject(SOURCE);
                if (source.has(API_CALLS)) {
                    String allApiCalls = source.getString(API_CALLS);
                    if (!allApiCalls.isEmpty()) {
                        allApiCalls = allApiCalls.replaceFirst("\\{", "[{");
                        allApiCalls = allApiCalls.concat("]");
                        JSONArray apiCalls = new JSONArray(allApiCalls);
                        Iterator it = apiCalls.iterator();
                        while (it.hasNext()) {
                            JSONObject call = (JSONObject) it.next();
                            buildKeyValues(call);
                        }
                    }
                }
            }
            Assert.assertFalse(hookMap.isEmpty());
            addHooksToSVMFile();
        }
    }

    private void buildKeyValues(JSONObject apiCall) {
        String className = apiCall.getString("class");
        String method = apiCall.getString("method");
        String key = className + "." + method;

        if (hookMap.containsKey(key)) {
            int amount = hookMap.get(key);
            hookMap.put(key, ++amount);
        }
    }

    private void addHooksToSVMFile() {
        int category = -1;
        if (config.getDynamicIndexSimple().contains("benign")) {
            category = 1;
        }
        StringBuilder line = new StringBuilder();
        line.append(category);
        line.append(" ");
        Integer[] values = new Integer[hookMap.values().size()];
        hookMap.values().toArray(values);
        for (int i = 0; i < hookMap.size(); i++) {
            line.append(String.format("%d:%d ", i + 1, values[i]));
        }
        line.append("\n");
        writeLineToFile(line);
    }

    private void writeLineToFile(StringBuilder line) {
        try {
            Files.write(Paths.get(targetSVMPath), line.toString().getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    private void createNewSVMFile() {
        try {
            Files.delete(Paths.get(targetSVMPath));
            Files.createFile(Paths.get(targetSVMPath));
        } catch (IOException e) {
            System.err.println(e);
        }
    }

}
