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

/**
 * Handles communication with an Arduino.
 *
 * @author braithwaitec
 */
public class ArduinoUtility {

    private final String ACTION_USB_PERMISSION = "msoe.supermileage.USB_PERMISSION";

    private final App app;
    private final UsbManager usbManager;

    private UsbSerialDevice usbSerialDevice;
    private final UsbSerialInterface.UsbReadCallback handleUsbSerialDeviceRead = new UsbSerialInterface.UsbReadCallback() {
        @Override
        public void onReceivedData(byte[] bytes) {
            if (usbInputHandler != null) {
                try {
                    String bytesAsText = new String(bytes, "UTF-8");
                    usbInputHandler.onInputReceived(bytesAsText);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private UsbInputHandler usbInputHandler;

    public interface UsbInputHandler {
        void onInputReceived(String text);
    }

    public ArduinoUtility(App app) {
        this.app = app;
        this.usbManager = (UsbManager) this.app.getSystemService(Context.USB_SERVICE);
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
}
