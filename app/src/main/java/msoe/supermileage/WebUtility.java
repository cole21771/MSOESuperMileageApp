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

        this.arduinoUtility.handleUsbInput(new ArduinoUtility.UsbInputHandler() {
            @Override
            public void onInputReceived(String text) {
                post(DATA_ARGUMENT, text);
            }
        });
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
        assert argument != null;
        assert data != null;

        if (this.socket == null) {
            System.out.println(String.format("Socket closed. Argument '%s' data '%s'", argument, data));
        } else {
            this.socket.emit(argument, data);
        }
    }
}
