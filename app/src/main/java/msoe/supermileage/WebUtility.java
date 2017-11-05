package msoe.supermileage;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;


class WebUtility {
    private static final String DATA_ARGUMENT = "newData";
    private static final String LOCATION_ARGUMENT = "newLocation";

    private Socket socket;
    private String ipAddress;

    private final App app;
    private final ArduinoUtility arduinoUtility;
    private final LocationUtility locationUtility;


    public WebUtility(App app, ArduinoUtility arduinoUtility, LocationUtility locationUtility) {
        this.app = app;
        this.arduinoUtility = arduinoUtility;
        this.locationUtility = locationUtility;

    }

    public void connectTo(String ipAddress) {
        assert ipAddress != null;

        if (!ipAddress.contains(":3000")) {
            ipAddress += ":3000";
        }

        if (!ipAddress.contains("http://")) {
            ipAddress = "http://" + ipAddress;
        }

        try {
            this.socket = IO.socket(ipAddress);
            this.socket.connect();
            this.ipAddress = ipAddress;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void post(String argument, String data) {
        assert data != null;

        if (this.socket != null) {
            this.socket.emit(argument, data);
        }
    }
}
