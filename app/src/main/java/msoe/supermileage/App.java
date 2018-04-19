package msoe.supermileage;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.location.LocationManager;

import io.objectbox.BoxStore;
import msoe.supermileage.entities.MyObjectBox;
import msoe.supermileage.entities.Server;

/**
 * Holds global application state.
 *
 * @author braithwaitec
 */
public class App extends Application {

    public static final String EXTRA_SM_SERVER_NAME = "msoe.supermileage.EXTRA_SM_SERVER_NAME";
    public static final String EXTRA_SM_SERVER_IP = "msoe.supermileage.EXTRA_SM_SERVER_IP";
    public static final String EXTRA_SM_SERVER_PORT = "msoe.supermileage.EXTRA_SM_SERVER_PORT";
    public static final String EXTRA_SM_CONFIG = "msoe.supermileage.EXTRA_SM_CONFIG";

    /**
     * The entry point for using ObjectBox.
     * BoxStore is the direct interface to the database and manages Boxes.
     */
    private BoxStore boxStore;

    private WebUtility webUtility;
    private LocationUtility locationUtility;
    private ArduinoUtility arduinoUtility;
    private Activity currentActivity;
    private Server selectedServer;

    public Server getSelectedServer() {
        return this.selectedServer;
    }

    public void setSelectedServer(Server selectedServer) {
        this.selectedServer = selectedServer;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // MyObjectBox is generated based on entity classes
        // MyObjectBox supplies a builder to set up a BoxStore for the app.
        this.boxStore = MyObjectBox.builder().androidContext(App.this).build();

        this.arduinoUtility = new ArduinoUtility(this);

        this.locationUtility = new LocationUtility(this, (LocationManager) getSystemService(Context.LOCATION_SERVICE));

        this.webUtility = new WebUtility(this, this.arduinoUtility, this.locationUtility);
    }

    public BoxStore getBoxStore() {
        return boxStore;
    }

    public WebUtility getWebUtility() {
        return webUtility;
    }

    public Context getCurrentActivity() {
        return currentActivity;
    }

    public void setActivity(Activity currentActivity) {
        this.currentActivity = currentActivity;
    }

    public void startDataProcessing(String ipAddress, String port) {
        this.arduinoUtility.connect(null);
        this.locationUtility.startMonitoringLocation();
        this.webUtility.connectTo(ipAddress, port);
    }

    public void stopDataProcessing() {
        this.webUtility.disconnect();
        this.locationUtility.stopMonitoringLocation();
        this.arduinoUtility.disconnect();
    }

}
