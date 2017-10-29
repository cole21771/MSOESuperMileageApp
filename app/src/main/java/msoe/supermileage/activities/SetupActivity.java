package msoe.supermileage.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import msoe.supermileage.App;
import msoe.supermileage.R;
import msoe.supermileage.entities.Car;
import msoe.supermileage.entities.Config;
import msoe.supermileage.entities.Server;
import msoe.supermileage.fragments.AddCarFragment;
import msoe.supermileage.fragments.AddConfigFragment;
import msoe.supermileage.fragments.AddServerFragment;
import msoe.supermileage.fragments.SelectCarFragment;
import msoe.supermileage.fragments.SelectConfigFragment;
import msoe.supermileage.fragments.SelectServerFragment;

public class SetupActivity
        extends AppCompatActivity
        implements SelectServerFragment.OnFragmentInteractionListener,
        SelectCarFragment.OnFragmentInteractionListener,
        AddServerFragment.OnFragmentInteractionListener,
        AddCarFragment.OnFragmentInteractionListener,
        SelectConfigFragment.OnFragmentInteractionListener,
        AddConfigFragment.OnFragmentInteractionListener {

    private App app;

    private Box<Server> serverBox;
    private Box<Car> carBox;
    private Box<Config> configBox;

    private List<Server> servers;
    private List<Car> localCars, remoteCars;
    private List<Config> localConfigs, remoteConfigs;


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

    public List<Car> getLocalCars() {
        return localCars;
    }

    public List<Car> getRemoteCars() {
        return remoteCars;
    }

    public List<Config> getLocalConfigs() {
        return localConfigs;
    }

    public List<Config> getRemoteConfigs() {
        return remoteConfigs;
    }

    public Server getSelectedServer() {
        return selectedServer;
    }

    public Car getSelectedCar() {
        return selectedCar;
    }

    public Config getSelectedConfig() {
        return selectedConfig;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        this.app = (App) getApplication();
        this.app.setActivity(this);

        this.servers = new ArrayList<>(10);
        this.localCars = new ArrayList<>(10);
        this.remoteCars = new ArrayList<>(10);
        this.localConfigs = new ArrayList<>(10);
        this.remoteConfigs = new ArrayList<>(10);

        BoxStore boxStore = this.app.getBoxStore();
        this.serverBox = boxStore.boxFor(Server.class);
        this.carBox = boxStore.boxFor(Car.class);
        this.configBox = boxStore.boxFor(Config.class);

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
                fragment = new AddCarFragment();
                break;
            case SELECT_CONFIG:
                fragment = new SelectConfigFragment();
                break;
            case ADD_CONFIG:
                fragment = new AddConfigFragment();
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
    public void selectConfig(Config config) {
        // TODO
    }

    @Override
    public void selectCar(Car car) {
        assert car != null;

        this.selectedCar = car;

        refreshConfigs();

        this.swapFragments(SetupActivityFragmentType.SELECT_CONFIG);
    }

    @Override
    public void selectServer(Server server) {
        assert server != null;

        this.selectedServer = server;

        refreshCars();

        this.swapFragments(SetupActivityFragmentType.SELECT_CAR);
    }

    @Override
    public void addServer(String name, String ipAddress) {
        Server server = new Server(name, ipAddress);
        serverBox.put(server);
        refreshServers();
    }

    @Override
    public void addCar(String name) {
        Car car = new Car(name);
        carBox.put(car);
        refreshCars();
    }

    @Override
    public void addConfig(String name, String json) {
        Config config = new Config(name, json);
        configBox.put(config);

        selectedCar.getConfigs().add(config);

        refreshConfigs();
    }

    private void refreshServers() {
        this.servers.clear();
        this.servers.addAll(serverBox.getAll());

        for (Server server : this.servers) {
            server.checkIsReachable();
        }
    }

    private void refreshCars() {
        this.localCars.clear();
        this.localCars.addAll(carBox.getAll());

        this.remoteCars.clear();
//        this.remoteCars.addAll(this.selectedServer.getCars().toArray());
    }

    private void refreshConfigs() {
        this.localConfigs.clear();
        this.localConfigs.addAll(selectedCar.getConfigs());

        this.remoteConfigs.clear();
//        this.remoteConfigs.addAll(this.selectedCar.getConfigs().toArray());
    }
}
