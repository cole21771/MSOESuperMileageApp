package msoe.supermileage.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import msoe.supermileage.App;
import msoe.supermileage.R;
import msoe.supermileage.entities.Car;
import msoe.supermileage.entities.Config;
import msoe.supermileage.entities.Server;
import msoe.supermileage.fragments.AddServerFragment;
import msoe.supermileage.fragments.SelectCarFragment;
import msoe.supermileage.fragments.SelectServerFragment;

public class SetupActivity
        extends AppCompatActivity
        implements SelectServerFragment.OnFragmentInteractionListener,
        SelectCarFragment.OnFragmentInteractionListener,
        AddServerFragment.OnFragmentInteractionListener {

    private App app;

    private Box<Server> serverBox;
    private List<Server> servers;

    private Server selectedServer;
    private Car selectedCar;
    private Config selectedConfig;

    public enum SetupActivityFragmentType {
        NONE,
        SELECT_SERVER,
        ADD_SERVER,
        SELECT_CAR,
        ADD_CAR,
        SELECT_CONFIG,
        ADD_CONFIG
    }

    public List<Server> getServers() {
        return servers;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        this.app = (App) getApplication();
        this.app.setActivity(this);
        this.servers = new ArrayList<>(10);

        BoxStore boxStore = this.app.getBoxStore();
        this.serverBox = boxStore.boxFor(Server.class);

        refreshServers();

        if (findViewById(R.id.fragment_container) == null) {
            // shouldn't happen
        } else {
            if (savedInstanceState == null) {
                // create a fragment and load it to container
                Fragment fragment = new SelectServerFragment();

                // because this was started from an intent
                fragment.setArguments(getIntent().getExtras());

                // add fragment to the container
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(
                                R.id.fragment_container,
                                fragment
                        )
                        .commit();
            } else {
                // being restored from previous state
            }
        }
    }

    @Override
    public void swapFragments(SetupActivityFragmentType type) {
        Fragment fragment = null;


        switch (type) {
            case NONE:
                break;
            case SELECT_SERVER:
                fragment = new SelectServerFragment();
                break;
            case ADD_SERVER:
                fragment = new AddServerFragment();
                break;
            case SELECT_CAR:
                fragment = new SelectCarFragment();
                break;
            case ADD_CAR:
                break;
            case SELECT_CONFIG:
//                fragment = SelectConfigFragment.newInstance("", "");
                break;
            case ADD_CONFIG:
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();
            transaction.replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void addServer(String name, String ipAddress) {
        Server server = new Server(name, ipAddress);
        serverBox.put(server);
        refreshServers();
    }

    private void refreshServers() {
        this.servers.clear();
        this.servers.addAll(serverBox.getAll());

        for (Server server : this.servers) {
            server.checkIsReachable();
        }
    }
}
