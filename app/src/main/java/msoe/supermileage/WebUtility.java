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
    protected static final String NEW_DATA_ARGUMENT = "newData";
    protected static final String LOCATION_ARGUMENT = "newLocation";
    protected static final String BATTERY_ARGUMENT = "batteryLife";
    protected static final String MARKER_ARGUMENT = "new-marker";
    protected static final String ERROR_ARGUMENT = "new-error";
    protected static final String GET_SELECTED_CONFIG_ERROR = "error";
    protected static final String GET_SELECTED_CONFIG_DATA = "data";
    private final ArduinoUtility.UsbInputHandler arduinoInputHandler = new ArduinoUtility.UsbInputHandler() {


        @Override
        public void onDataPacketReceived(JSONArray jsonArray) {
            post(NEW_DATA_ARGUMENT, jsonArray.toString());
        }

        @Override
        public void onGeneralPacketReceived(JSONArray jsonArray) {

        }

        @Override
        public void onErrorPacketReceived(JSONArray jsonArray) {
            post(ERROR_ARGUMENT, jsonArray.toString());
        }

        @Override
        public void onMarkerPacketReceived(JSONArray jsonArray) {
            post(MARKER_ARGUMENT, jsonArray.toString());
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
            post(LOCATION_ARGUMENT, json);
        }
    };

    private Socket socket;
    private final App app;

    private Emitter.Listener connectHandler = new Emitter.Listener() {

        @Override
        public void call(Object... args) {

            System.out.println("Socket connected.");
            app.connectionDidChange(true);
        }
    };

    private Emitter.Listener disconnectListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println("Socket disconnected.");
            socket = null;
            app.connectionDidChange(false);
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

    public boolean isConnected() {
        return socket != null && socket.connected();
    }

    private void post(String serverArg, String json) {
        if (socket == null || !socket.connected()) {
            String message = String.format("Socket closed. Argument: '%s' data: '%s'", serverArg, json);
            System.out.println(message);
            app.toast(message);
        } else {
            socket.emit(serverArg, json);
        }
    }
}
