package msoe.supermileage.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import msoe.supermileage.App;
import msoe.supermileage.R;
import msoe.supermileage.entities.Server;
import msoe.supermileage.fragments.AddServerFragment;
import msoe.supermileage.fragments.SelectServerFragment;

/**
 * Activity that handles selecting/editing the server that will be communicated with.
 *
 * @author braithwaitec
 */
public class SetupActivity
        extends AppCompatActivity
        implements SelectServerFragment.OnFragmentInteractionListener,
        AddServerFragment.OnFragmentInteractionListener {

    private App app;

    private Toolbar toolbar;

    private Box<Server> serverBox;

    private List<Server> servers;

    public enum SetupActivityFragmentType {
        NONE,
        SELECT_SERVER,
        ADD_SERVER,
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

        this.app.setSelectedServer(server);

        Intent intent = new Intent(this, CollectionActivity.class);
        intent.putExtra(App.EXTRA_SM_SERVER_NAME, this.app.getSelectedServer().getName());
        intent.putExtra(App.EXTRA_SM_SERVER_IP, this.app.getSelectedServer().getIpAddress());
        intent.putExtra(App.EXTRA_SM_SERVER_PORT, this.app.getSelectedServer().getPort());
        startActivity(intent);
        finish();
    }

    @Override
    public void editServer(final Server server) {
        assert server != null;

        // create an alert dialog for editing
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Modify Server");
        alert.setTitle("Edit " + server.getName());

        // inflate the view and setup the edittext fields
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.view_edit_server, null);
        final EditText nameEditText = (EditText) textEntryView.findViewById(R.id.serverNameEditText);
        nameEditText.setText(server.getName());
        final EditText ipEditText = (EditText) textEntryView.findViewById(R.id.serverIPEditText);
        ipEditText.setText(server.getIpAddress());
        final EditText portEditText = (EditText) textEntryView.findViewById(R.id.serverPortEditText);
        portEditText.setText(server.getPort());

        alert.setView(textEntryView);
        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                server.setName(nameEditText.getText().toString());
                server.setIpAddress(ipEditText.getText().toString());
                server.setPort(portEditText.getText().toString());
                serverBox.put(server);
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });
        alert.show();
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

}
