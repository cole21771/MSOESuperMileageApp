package msoe.supermileage.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import msoe.supermileage.App;
import msoe.supermileage.R;
import msoe.supermileage.entities.Config;
import msoe.supermileage.entities.Server;
import msoe.supermileage.fragments.AddServerFragment;
import msoe.supermileage.fragments.ConfirmServerFragment;
import msoe.supermileage.fragments.SelectServerFragment;

/**
 * Activity that handles selecting/editing the server that will be communicated with.
 *
 * @author braithwaitec
 */
public class SetupActivity
        extends AppCompatActivity
        implements SelectServerFragment.OnFragmentInteractionListener,
        AddServerFragment.OnFragmentInteractionListener,
        ConfirmServerFragment.OnFragmentInteractionListener {

    private App app;

    private Toolbar toolbar;

    private Box<Server> serverBox;
    private Box<Config> configBox;

    private List<Server> servers;

    private Server selectedServer;

    public enum SetupActivityFragmentType {
        NONE,
        SELECT_SERVER,
        ADD_SERVER,
        CONFIRM_SERVER,
    }

    public List<Server> getServers() {
        return servers;
    }

    public Server getSelectedServer() {
        return selectedServer;
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
        this.configBox = boxStore.boxFor(Config.class);

        refreshServers();

        this.toolbar = findViewById(R.id.setup_toolbar);
        setSupportActionBar(this.toolbar);
        this.toolbar.setTitle(R.string.app_name);

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
        assert type != SetupActivityFragmentType.NONE;

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
            case CONFIRM_SERVER:
                fragment = new ConfirmServerFragment();
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
    public void selectServer(Server server) {
        assert server != null;

        this.selectedServer = server;
        this.swapFragments(SetupActivityFragmentType.CONFIRM_SERVER);
    }

    @Override
    public void addServer(String name, String ipAddress, String port) {
        assert name != null;
        assert ipAddress != null;
        assert port != null;

        Server server = new Server(name, ipAddress, port);
        serverBox.put(server);
        refreshServers();
    }

    public void refreshServers() {
        this.servers.clear();
        this.servers.addAll(serverBox.getAll());

        for (final Server server : this.servers) {
            server.checkReachable(serverBox);
        }
    }

    private void finishSetup() {
        Intent intent = new Intent(this, CollectionActivity.class);
        intent.putExtra(App.EXTRA_SM_SERVER_NAME, this.selectedServer.getName());
        intent.putExtra(App.EXTRA_SM_SERVER_IP, this.selectedServer.getIpAddress());
        intent.putExtra(App.EXTRA_SM_SERVER_PORT, this.selectedServer.getPort());
        startActivity(intent);
    }

}
