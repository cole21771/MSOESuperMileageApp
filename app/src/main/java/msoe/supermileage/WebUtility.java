package msoe.supermileage;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;


public class WebUtility {
    private static final String DATA_ARGUMENT = "newData";
    private static final String LOCATION_ARGUMENT = "newLocation";

    private Socket socket;
    private final App app;


    public WebUtility(App app, ArduinoUtility arduinoUtility, LocationUtility locationUtility) {
        this.app = app;

        arduinoUtility.handleUsbInput(new ArduinoUtility.UsbInputHandler() {
            @Override
            public void onInputReceived(String text) {
                post(DATA_ARGUMENT, text);
            }
        });

        locationUtility.handleLocationInput(new LocationUtility.LocationInputHandler() {
            @Override
            public void onInputReceived(String text) {
                post(LOCATION_ARGUMENT, text);
            }
        });
    }

    public void connectTo(String ipAddress, String port) {
        assert ipAddress != null;
        assert port != null;

        String url = "http://" + ipAddress + ":" + port;

        try {
            this.socket = IO.socket(url);
            this.socket.connect();
        } catch (URISyntaxException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void disconnect() {
        if (this.socket == null) {

        } else {
            this.socket.disconnect();
            this.socket = null;
        }
    }

    private void post(String argument, String data) {
        assert argument != null;
        assert data != null;

        if (this.socket == null) {
            System.out.println(String.format("Socket closed. Argument '%s' data '%s'", argument, data));
        } else {
            this.socket.emit(argument, data);
        }
    }
}
