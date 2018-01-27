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

    private static final String SAMPLE_CONFIG_RESPONSE = "{\"vehicleType\":\"electric\",\"data\":[{\"label\":\"Speed\",\"color\":\"#f00\",\"min\":0,\"max\":35,\"units\":\"MPH\",\"showGraph\":true,\"displayAlways\":false},{\"label\":\"Motor RPM\",\"color\":\"#0f0\",\"min\":0,\"max\":3500,\"units\":\"RPM\",\"showGraph\":true,\"displayAlways\":false},{\"label\":\"Joules\",\"color\":\"#00f\",\"min\":0,\"max\":1000000,\"units\":\"J\",\"showGraph\":true,\"displayAlways\":true},{\"label\":\"Volts\",\"color\":\"#0ff\",\"min\":0,\"max\":30,\"units\":\"V\",\"showGraph\":true,\"displayAlways\":false},{\"label\":\"Current\",\"color\":\"#f0f\",\"min\":0,\"max\":50,\"units\":\"A\",\"showGraph\":true,\"displayAlways\":false},{\"label\":\"Lap Number\",\"color\":\"#ff7f00\",\"min\":0,\"max\":10,\"units\":\"\",\"showGraph\":false,\"displayAlways\":true}]}";
    public static final String GOOGLE_IP_ADDRESS = "74.125.136.105";
    public static final String GOOGLE_PORT = "443";
    private WebUtility target;

    @Mock
    private App app;
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
        target.connectTo(GOOGLE_IP_ADDRESS, GOOGLE_PORT);
    }

    @Test
    public void disconnect() throws Exception {
        target.disconnect();
    }

    @Test
    public void postArduinoData() throws Exception {
        target.connectTo("localhost", "3000");
        target.postArduinoData(SAMPLE_CONFIG_RESPONSE);
    }

    @Test
    public void isReachable() throws Exception {
        assertTrue(WebUtility.isReachable(GOOGLE_IP_ADDRESS, Integer.parseInt(GOOGLE_PORT)));
    }

}