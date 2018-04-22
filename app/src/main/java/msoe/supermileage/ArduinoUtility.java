package msoe.supermileage;


import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;
import com.hoho.android.usbserial.driver.CdcAcmSerialDriver;
import com.hoho.android.usbserial.driver.ProbeTable;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Handles communication with an Arduino.
 *
 * @author braithwaitec
 */
public class ArduinoUtility {

    private final String ACTION_USB_PERMISSION = "msoe.supermileage.USB_PERMISSION";
    private final char TOKEN_PACKET_START = '[';
    private final char TOKEN_PACKET_END = ']';
    private final String TOKEN_PACKET_SEPARATOR = ",";
    private final String TOKEN_DATA = "D";
    private final String TOKEN_DATA_ALT = "d";
    private final String TOKEN_ERROR = "E";
    private final String TOKEN_ERROR_ALT = "e";
    private final String TOKEN_GENERAL = "G";
    private final String TOKEN_MARKER = "M";
    private final String TOKEN_TRIGGER = "T";
    private final String TOKEN_RESPONSE = "R";
    private final String TOKEN_GENERAL_ALT = "g";
    private final String TOKEN_MARKER_ALT = "m";
    private final String TOKEN_TRIGGER_ALT = "t";
    private final String TOKEN_RESPONSE_ALT = "r";

    private final App app;
    private final UsbManager usbManager;
    private final StringBuilder inputBuilder;

    private UsbSerialDevice usbSerialDevice;


    private final UsbSerialInterface.UsbReadCallback handleUsbSerialDeviceRead = new UsbSerialInterface.UsbReadCallback() {
        @Override
        public void onReceivedData(byte[] bytes) {
            if (usbInputHandler != null) {
                try {
                    String bytesAsText = new String(bytes, "UTF-8");
                    processInput(bytesAsText);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private UsbInputHandler usbInputHandler;

    public interface UsbInputHandler {
        void onDataPacketReceived(JSONArray jsonArray);

        void onGeneralPacketReceived(JSONArray jsonArray);

        void onErrorPacketReceived(JSONArray jsonArray);

        void onMarkerPacketReceived(JSONArray jsonArray);

        void onResponsePacketReceived(JSONArray jsonArray);

        void onTriggerPacketReceived(JSONArray jsonArray);
    }

    public ArduinoUtility(App app) {
        this.app = app;
        this.usbManager = (UsbManager) this.app.getSystemService(Context.USB_SERVICE);
        this.inputBuilder = new StringBuilder(1024);
    }

    public void handleUsbInput(UsbInputHandler usbInputHandler) {
        this.usbInputHandler = usbInputHandler;
    }

    public void connect(UsbDevice usbDevice) {
        if (this.usbSerialDevice == null) {

            if (usbDevice == null) {
                usbDevice = getArduinoDevice();
            }

            if (usbDevice == null) {
                // TODO handle no usb device
            } else {
                UsbDeviceConnection usbConnection = this.usbManager.openDevice(usbDevice);

                if (usbConnection == null) {
                    requestDevicePermission(usbDevice);
                } else {
                    this.usbSerialDevice = UsbSerialDevice.createUsbSerialDevice(usbDevice, usbConnection);

                    // Open the device and set it up
                    this.usbSerialDevice.open();
                    this.usbSerialDevice.setBaudRate(115200);
                    this.usbSerialDevice.setDataBits(UsbSerialInterface.DATA_BITS_8);
                    this.usbSerialDevice.setStopBits(UsbSerialInterface.STOP_BITS_1);
                    this.usbSerialDevice.setParity(UsbSerialInterface.PARITY_NONE);
                    this.usbSerialDevice.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                    this.usbSerialDevice.read(handleUsbSerialDeviceRead);
                }
            }
        }
    }

    public void disconnect() {
        if (this.usbSerialDevice != null) {
            this.usbSerialDevice.close();
            this.usbSerialDevice = null;
        }
    }

    private UsbDevice getArduinoDevice() {
        List<UsbSerialDriver> usbSerialDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(this.usbManager);
        if (usbSerialDrivers.isEmpty()) {
            ProbeTable deviceTable = new ProbeTable();
            deviceTable.addProduct(0x2341, 0x003D, CdcAcmSerialDriver.class);
            UsbSerialProber prober = new UsbSerialProber(deviceTable);
            usbSerialDrivers = prober.findAllDrivers(usbManager);

            if (usbSerialDrivers.isEmpty()) {
                return null;
            }
        }

        return this.findArduino(usbSerialDrivers);
    }

    private UsbDevice findArduino(List<UsbSerialDriver> usbSerialDrivers) {
        for (UsbSerialDriver driver: usbSerialDrivers) {
            int vendorId = driver.getDevice().getVendorId();
            if (vendorId == 0x2341 || vendorId == 0x1A86) {
                return driver.getDevice();
            }
        }

        return null;
    }

    private void requestDevicePermission(UsbDevice usbDevice) {
        PendingIntent pi = PendingIntent.getBroadcast(this.app, 0, new Intent(ACTION_USB_PERMISSION), 0);
        this.usbManager.requestPermission(usbDevice, pi);

        IntentFilter intentFilter = new IntentFilter(ACTION_USB_PERMISSION);
        this.app.registerReceiver(this.broadcastReceiver, intentFilter);
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {

                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false) && device != null) {
                        connect(device);
                    }
                }
            }
        }
    };

    /**
     * Validates data before it gets sent to the usb input handler
     *
     * @param bytesAsText raw text from the Arduino
     */
    private void processInput(String bytesAsText) {
        this.inputBuilder.append(bytesAsText);

        int startIndex = -1;
        int endIndex = -1;
        int i = 0;
        while (i < this.inputBuilder.length() && (startIndex == -1 || endIndex == -1)) {
            char c = this.inputBuilder.charAt(i);

            // find the first starting character
            if (startIndex == -1) {
                if (c == TOKEN_PACKET_START) {
                    startIndex = i;
                }
            }

            // find the first ending character
            else {
                if (c == TOKEN_PACKET_END) {
                    endIndex = i;
                }
            }

            i++;
        }

        // process packet if there is one
        if (startIndex != -1 && endIndex != -1) {
            // extract a substring with the packet
            String packet = this.inputBuilder.substring(startIndex + 1, endIndex);
            String[] packetPieces = packet.split(TOKEN_PACKET_SEPARATOR);
            JSONArray jsonArray = new JSONArray();
            for (int j = 1; j < packetPieces.length; j++) {
                jsonArray.put(packetPieces[j]);
            }

            // determine the type of packet and send it
            switch (packetPieces[0]) {
                case TOKEN_DATA: {
                    this.usbInputHandler.onDataPacketReceived(jsonArray);
                    break;
                }
                case TOKEN_DATA_ALT: {
                    this.usbInputHandler.onDataPacketReceived(jsonArray);
                    break;
                }
                case TOKEN_GENERAL: {
                    this.usbInputHandler.onGeneralPacketReceived(jsonArray);
                    break;
                }
                case TOKEN_GENERAL_ALT: {
                    this.usbInputHandler.onGeneralPacketReceived(jsonArray);
                    break;
                }
                case TOKEN_ERROR: {
                    this.usbInputHandler.onErrorPacketReceived(jsonArray);
                    break;
                }
                case TOKEN_ERROR_ALT: {
                    this.usbInputHandler.onErrorPacketReceived(jsonArray);
                    break;
                }
                case TOKEN_MARKER: {
                    this.usbInputHandler.onMarkerPacketReceived(jsonArray);
                    break;
                }
                case TOKEN_MARKER_ALT: {
                    this.usbInputHandler.onMarkerPacketReceived(jsonArray);
                    break;
                }
                case TOKEN_RESPONSE: {
                    this.usbInputHandler.onResponsePacketReceived(jsonArray);
                    break;
                }
                case TOKEN_RESPONSE_ALT: {
                    this.usbInputHandler.onResponsePacketReceived(jsonArray);
                    break;
                }
                case TOKEN_TRIGGER: {
                    this.usbInputHandler.onTriggerPacketReceived(jsonArray);
                    break;
                }
                case TOKEN_TRIGGER_ALT: {
                    this.usbInputHandler.onTriggerPacketReceived(jsonArray);
                    break;
                }
            }

            app.onArduinoPacketReceived(jsonArray.toString());

            // delete the builder from 0 to end index
            this.inputBuilder.delete(0, endIndex + 1);
        }
    }
}
