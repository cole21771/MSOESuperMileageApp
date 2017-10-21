package msoe.supermileage;


import android.app.PendingIntent;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import java.util.Map;

class ArduinoUtility {
    public final String ACTION_USB_PERMISSION = "com.supermileage.USB_PERMISSION";
    private final App app;

    private UsbManager usbManager;
    private UsbDevice device;
    private UsbDeviceConnection connection;

    public ArduinoUtility(App app) {
        this.app = app;
    }

    public boolean connect() {
        boolean result = false;

        Map<String, UsbDevice> usbDevices = usbManager.getDeviceList();

        if (!usbDevices.isEmpty() && (usbDevices.containsValue(0x2341) || usbDevices.containsValue(0x1A86))) {

            PendingIntent pi = PendingIntent.getBroadcast(this.app, 0, new Intent(ACTION_USB_PERMISSION), 0);
            usbManager.requestPermission(device, pi);
            result = true;
        }

        return result;
    }
}
