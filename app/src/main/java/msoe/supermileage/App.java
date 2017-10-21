package msoe.supermileage;

import android.app.Application;

import io.objectbox.BoxStore;
import msoe.supermileage.entities.MyObjectBox;

public class App extends Application {

    /**
     * The entry point for using ObjectBox.
     * BoxStore is the direct interface to the database and manages Boxes.
     */
    private BoxStore boxStore;

    private WebUtility webUtility;

    @Override
    public void onCreate() {
        super.onCreate();

        // MyObjectBox is generated based on entity classes
        // MyObjectBox supplies a builder to set up a BoxStore for the app.
        this.boxStore = MyObjectBox.builder().androidContext(App.this).build();

        this.webUtility = new WebUtility();
    }

    public BoxStore getBoxStore() {
        return boxStore;
    }
}
