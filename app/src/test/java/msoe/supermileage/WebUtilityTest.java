package msoe.supermileage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.assertTrue;


@RunWith(MockitoJUnitRunner.class)
public class WebUtilityTest {

    WebUtility target;

    @Mock
    App app;
    @Mock
    private ArduinoUtility arduinoUtility;
    @Mock
    private LocationUtility locationUtility;

    @Before
    public void setUp() throws Exception {
        this.target = new WebUtility(app, arduinoUtility, locationUtility);
    }

    @After
    public void tearDown() throws Exception {
        this.target = null;
    }

    @Test
    public void connectTo() throws Exception {
        target.connectTo("74.125.136.105", "443");
    }

    @Test
    public void disconnect() throws Exception {
        target.disconnect();
    }

    @Test
    public void isReachable() throws Exception {
        assertTrue(WebUtility.isReachable("74.125.136.105", 443));
    }

}