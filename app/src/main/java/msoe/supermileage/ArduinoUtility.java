package msoe.supermileage;


import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.io.UnsupportedEncodingException;
import java.util.List;

import msoe.supermileage.entities.Config;

/**
 * Handles communication with an Arduino.
 *
 * @author braithwaitec
 */
public class ArduinoUtility {

    private final String ACTION_USB_PERMISSION = "msoe.supermileage.USB_PERMISSION";
    private final String TOKEN_ENTRY = "[";
    private final String TOKEN_EXIT = "]";
    private final String TOKEN_DATA = "D";
    private final String TOKEN_ERROR = "E";
    private final String TOKEN_MARKER = "M";
    private final String TOKEN_GENERAL = "G";
    private final String TOKEN_TRIGGER = "T";
    private final String TOKEN_RESPONSE = "R";

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

    /**
     * The number of measurements that are expected in a single set of data
     */
    private int numberOfMeasurements;

    private UsbInputHandler usbInputHandler;

    public interface UsbInputHandler {
        void onInputReceived(String text);
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

    /**
     * Set how this class formats the data that will be sent to the usb input handlers
     * @param config
     */
    public void setupFromConfig(Config config) {
        this.numberOfMeasurements = config.getMeasurements().size();
    }

    private UsbDevice getArduinoDevice() {
        UsbDevice result = null;

        List<UsbSerialDriver> usbSerialDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(this.usbManager);
        if (usbSerialDrivers.isEmpty()) {
            // nothing is connected
        } else {
            int i = 0;
            while (result == null && i < usbSerialDrivers.size()) {
                UsbSerialDriver usbSerialDriver = usbSerialDrivers.get(i);
                // Check for Arduino Vendor ID
                if (usbSerialDriver.getDevice().getVendorId() == 0x2341 || usbSerialDriver.getDevice().getVendorId() == 0x1A86) {
                    result = usbSerialDriver.getDevice();
                }
                i++;
            }
        }
        return result;
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

        

        this.usbInputHandler.onInputReceived(bytesAsText);
    }
}
