package msoe.supermileage;

import org.json.JSONArray;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Utilities for working over the web.
 *
 * @author braithwaitec
 */
public class WebUtility {
    protected static final String GET_SELECTED_CONFIG = "getSelectedConfig";
    protected static final String NEW_DATA = "newData";
    protected static final String LOCATION_ARGUMENT = "newLocation";
    protected static final String BATTERY_ARGUMENT = "batteryLife";
    protected static final String MARKER_ARGUMENT = "new-marker";
    protected static final String ERROR_ARGUMENT = "new-error";
    protected static final String GET_SELECTED_CONFIG_ERROR = "error";
    protected static final String GET_SELECTED_CONFIG_DATA = "data";
    private final ArduinoUtility.UsbInputHandler arduinoInputHandler = new ArduinoUtility.UsbInputHandler() {


        @Override
        public void onDataPacketReceived(JSONArray jsonArray) {
            postArduinoData(NEW_DATA, jsonArray.toString());
        }

        @Override
        public void onGeneralPacketReceived(JSONArray jsonArray) {

        }

        @Override
        public void onErrorPacketReceived(JSONArray jsonArray) {
            postArduinoData(ERROR_ARGUMENT, jsonArray.toString());
        }

        @Override
        public void onMarkerPacketReceived(JSONArray jsonArray) {
            postArduinoData(MARKER_ARGUMENT, jsonArray.toString());
        }

        @Override
        public void onResponsePacketReceived(JSONArray jsonArray) {

        }

        @Override
        public void onTriggerPacketReceived(JSONArray jsonArray) {

        }
    };
    private final LocationUtility.LocationInputHandler locationInputHandler = new LocationUtility.LocationInputHandler() {

        @Override
        public void onInputReceived(String json) {
            postLocationData(json);
        }
    };

    private Socket socket;
    private final App app;

    private Emitter.Listener connectHandler = new Emitter.Listener() {

        @Override
        public void call(Object... args) {

            System.out.println("Socket connected.");
        }
    };

    private Emitter.Listener disconnectListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println("Socket disconnected.");
            socket = null;
        }
    };

    public WebUtility(App app, ArduinoUtility arduinoUtility, LocationUtility locationUtility) {
        assert app != null;

        this.app = app;

        arduinoUtility.handleUsbInput(arduinoInputHandler);
        locationUtility.handleLocationInput(locationInputHandler);
    }

    public void connectTo(String ipAddress, String port) {
        assert ipAddress != null;
        assert port != null;

        String url = "http://" + ipAddress + ":" + port;

        try {
            this.socket = IO.socket(url);
            this.socket.on(Socket.EVENT_CONNECT, connectHandler);
            this.socket.on(Socket.EVENT_DISCONNECT, disconnectListener);
            this.socket.connect();
        } catch (URISyntaxException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void disconnect() {
        if (this.socket == null) {
            System.out.println("Socket null");
        } else if (!socket.connected()) {
            System.out.println("Socket already disconnected");
        } else {
            this.socket.disconnect();
        }
    }

    public void postArduinoData(String serverArg, String json) {
        if (socket == null || !socket.connected()) {
            System.out.println(String.format("Socket closed. Argument '%s' data '%s'", NEW_DATA, json));
        } else {
            socket.emit(serverArg, json);
        }
    }

    public void postLocationData(String text) {
        if (socket == null || !socket.connected()) {
            System.out.println(String.format("Socket closed. Argument '%s' data '%s'", LOCATION_ARGUMENT, text));
        } else {
            socket.emit(LOCATION_ARGUMENT, text);
        }
    }

    public static boolean isReachable(String host, int port) {
        boolean result = false;
        try {
            java.net.Socket socket = new java.net.Socket();
            SocketAddress socketAddress = new InetSocketAddress(host, port);
            socket.connect(socketAddress, 1000);
            socket.close();
            result = true;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
}
