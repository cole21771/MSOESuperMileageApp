package msoe.supermileage.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import msoe.supermileage.App;
import msoe.supermileage.R;
import msoe.supermileage.WebUtility;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * Activity that collects data from an Arduino and sends it to a server.
 *
 * @author braithwaitec
 */
@RuntimePermissions
public class CollectionActivity extends AppCompatActivity implements App.AppUpdateListener {
    private final int REFRESH_INTERVAL = 2000;

    private App app;

    private Toolbar toolbar;

    private String serverName;
    private String serverIP;
    private String serverPort;
    private String config;
    private ImageView serverAvailabilityImageView;
    private TextView statusTextView;
    private TextView arduinoTextView;
    private TextView locationTextView;
    private ToggleButton startStopToggleButton;
    private ImageView serverImageView;
    private Handler handler;

    private Runnable onlineStatusChecker = new Runnable() {

        @Override
        public void run() {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    final boolean isReachable = WebUtility.isReachable(serverIP, Integer.parseInt(serverPort));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            serverImageView.setImageResource(isReachable ? android.R.drawable.presence_online : android.R.drawable.presence_offline);

                            handler.postDelayed(onlineStatusChecker, REFRESH_INTERVAL);
                        }
                    });
                }
            };
            thread.setDaemon(false);
            thread.start();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        this.app = (App) getApplication();
        this.app.setActivity(this);
        this.app.setUpdateListener(this);

        this.toolbar = findViewById(R.id.collection_toolbar);
        setSupportActionBar(this.toolbar);
        this.toolbar.setTitle(R.string.app_name);

        this.serverName = getIntent().getStringExtra(App.EXTRA_SM_SERVER_NAME);
        this.serverIP = getIntent().getStringExtra(App.EXTRA_SM_SERVER_IP);
        this.serverPort = getIntent().getStringExtra(App.EXTRA_SM_SERVER_PORT);
        this.config = getIntent().getStringExtra(App.EXTRA_SM_CONFIG);

        this.serverAvailabilityImageView = findViewById(R.id.server_available_imageview);

        TextView serverNameTextview = findViewById(R.id.server_name_textview);
        serverNameTextview.setText(this.serverName);

        this.serverImageView = findViewById(R.id.server_available_imageview);

        this.statusTextView = findViewById(R.id.status_textview);

        connectionChanged(app.isConnected());

        this.arduinoTextView = findViewById(R.id.arduino_textView);
        this.locationTextView = findViewById(R.id.location_textView);

        this.startStopToggleButton = findViewById(R.id.data_send_togglebutton);
        this.startStopToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    CollectionActivityPermissionsDispatcher.startDataProcessingWithPermissionCheck(CollectionActivity.this);
                } else {
                    stopDataProcessing();
                }
            }
        });

        // setup the refresh thread
        this.handler = handler = new Handler(Looper.getMainLooper());
        onlineStatusChecker.run();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        CollectionActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    public void stopDataProcessing() {
        app.stopDataProcessing();
    }

    @NeedsPermission({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE})
    public void startDataProcessing() {
        app.startDataProcessing(serverIP, serverPort);
    }

    @OnShowRationale({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE})
    void showRational(final PermissionRequest request) {
        showRationaleDialog("For Reasons", request);
    }

    @OnPermissionDenied({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE})
    void permissionDenied() {
        Toast.makeText(this, "permissionDenied -_-", Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE})
    void neverAskAgain() {
        Toast.makeText(this, "OnNeverAskAgain -_-", Toast.LENGTH_SHORT).show();
    }


    private void showRationaleDialog(String message, final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setPositiveButton("Yup", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton("Nope", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .setCancelable(false)
                .setMessage(message)
                .show();
    }

    @Override
    public void connectionChanged(boolean connected) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (app.isConnected()) {
                    statusTextView.setText("Connected");
                } else {
                    statusTextView.setText("Disconnected");
                }
            }
        });
    }

    @Override
    public void arduinoUpdate(final String json) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                arduinoTextView.setText(json == null ? "" : "arduino: " + json);
            }
        });
    }

    @Override
    public void locationUpdate(final String json) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                locationTextView.setText(json == null ? "" : "location: " + json);
            }
        });
    }

    // TODO update status field, start/stop button text ( startStopToggleButton.setText(R.string.stop); startStopToggleButton.setText(R.string.start);
}
