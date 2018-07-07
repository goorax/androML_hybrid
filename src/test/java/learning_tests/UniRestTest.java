package learning_tests;

import androML.AndroMLConfig;
import androML.database.DatabaseEndpoint;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;

public class UniRestTest {
    public static final int OK = 200;
    public static final int NOT_ALLOWED = 405;

    private String dbEndPoint;

    @Before
    public void initDatabase() {
        AndroMLConfig config = new AndroMLConfig();
        dbEndPoint = new DatabaseEndpoint(config).getDbEndpoint();
    }

    @Test
    public void testPut() {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.put(dbEndPoint).asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        Assert.assertThat(response, instanceOf(HttpResponse.class));
        Assert.assertTrue(response.getStatus() == NOT_ALLOWED);
    }

    @Test
    public void testGet() {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.get(dbEndPoint).asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        Assert.assertThat(response.getBody().getObject(), instanceOf(JSONObject.class));
        Assert.assertTrue(response.getStatus() == OK);
    }

    @Test
    public void testDelete() {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.delete(dbEndPoint).asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        Assert.assertThat(response.getBody().getObject(), instanceOf(JSONObject.class));
        Assert.assertTrue(response.getStatus() == NOT_ALLOWED);
    }
}
