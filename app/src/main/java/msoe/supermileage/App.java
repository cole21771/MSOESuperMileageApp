package msoe.supermileage;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.location.LocationManager;

import io.objectbox.BoxStore;
import msoe.supermileage.entities.MyObjectBox;

public class App extends Application {

    private static final int REQUEST_CODE = 777;
    public static final String EXTRA_SM_SERVER_NAME = "msoe.supermileage.EXTRA_SM_SERVER_NAME";
    public static final String EXTRA_SM_SERVER_IP = "msoe.supermileage.EXTRA_SM_SERVER_IP";
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

    @Override
    public void onCreate() {
        super.onCreate();

        // MyObjectBox is generated based on entity classes
        // MyObjectBox supplies a builder to set up a BoxStore for the app.
        this.boxStore = MyObjectBox.builder().androidContext(App.this).build();

        this.webUtility = new WebUtility();

        this.locationUtility = new LocationUtility((LocationManager) getSystemService(Context.LOCATION_SERVICE), this);

        this.arduinoUtility = new ArduinoUtility(this);
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
}
