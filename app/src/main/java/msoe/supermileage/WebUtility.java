package msoe.supermileage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URISyntaxException;

import io.socket.client.Ack;
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
    protected static final String GET_SELECTED_CONFIG_ERROR = "error";
    protected static final String GET_SELECTED_CONFIG_DATA = "data";
    private final ArduinoUtility.UsbInputHandler arduinoInputHandler = new ArduinoUtility.UsbInputHandler() {


        @Override
        public void onDataPacketReceived(JSONArray jsonArray) {
            postArduinoData(jsonArray.toString());
        }

        @Override
        public void onGeneralPacketReceived(JSONArray jsonArray) {

        }

        @Override
        public void onErrorPacketReceived(JSONArray jsonArray) {

        }

        @Override
        public void onMarkerPacketReceived(JSONArray jsonArray) {

        }

        @Override
        public void onResponsePacketReceived(JSONArray jsonArray) {

        }

        @Override
        public void onTriggerPacketReceived(JSONArray jsonArray) {

        }
    };

    private Socket socket;
    private final App app;

    private Emitter.Listener connectHandler = new Emitter.Listener() {

        @Override
        public void call(Object... args) {

            System.out.println("Socket connected.");
            socket.emit(GET_SELECTED_CONFIG, "Yo", new Ack() {


                @Override
                public void call(Object... args) {
                    System.out.println("getConfig callback");
                    for (int i = 0; i < args.length; i++) {
                        Object obj = args[i];
                        System.out.printf("Object %d: %n", i, obj);
                    }
                    if (args.length == 1) {
                        try {
                            JSONObject jsonObject = (JSONObject) args[0];
                            if (jsonObject.getBoolean(GET_SELECTED_CONFIG_ERROR)) {
                                System.out.println(jsonObject.getString(GET_SELECTED_CONFIG_DATA));
                            } else {
                                app.configurationReceived(jsonObject);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("The number of args for getting selected config is not 1");
                    }
                }
            });
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
        locationUtility.handleLocationInput(new LocationUtility.LocationInputHandler() {

            @Override
            public void onInputReceived(String text) {
                postLocationData(text);
            }
        });
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

    public void postArduinoData(String json) {
        if (socket == null || !socket.connected()) {
            System.out.println(String.format("Socket closed. Argument '%s' data '%s'", NEW_DATA, json));
        } else {
            socket.emit(NEW_DATA, json);
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
