package msoe.supermileage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URISyntaxException;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class WebUtility {
    protected static final String GET_SELECTED_CONFIG = "getSelectedConfig";
    protected static final String NEW_DATA = "newData";
    protected static final String LOCATION_ARGUMENT = "newLocation";

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

        arduinoUtility.handleUsbInput(new ArduinoUtility.UsbInputHandler() {

            @Override
            public void onInputReceived(String text) {
                postArduinoData(text);
            }
        });
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

    public void postArduinoData(String text) {
        if (socket == null || !socket.connected()) {
            System.out.println(String.format("Socket closed. Argument '%s' data '%s'", NEW_DATA, text));
        } else {
            socket.emit(NEW_DATA, text);
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
