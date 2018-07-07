package learning_tests;

import androML.AndroMLConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ConfigurationTest {
    AndroMLConfig config;

    @Before
    public void initConfiguration() {
        config = new AndroMLConfig();
    }

    @Test
    public void testDatabaseHostLoading() {
        String databaseHost = config.getCouchdbHost();
        Assert.assertFalse(databaseHost.isEmpty());
    }
}
