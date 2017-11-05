package msoe.supermileage.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import msoe.supermileage.App;
import msoe.supermileage.R;

public class CollectionActivity extends AppCompatActivity {
    private App app;

    private Toolbar toolbar;

    private String serverName;
    private String serverIP;
    private String serverPort;
    private String config;
    private ImageView serverAvailabilityImageView;
    private TextView statusTextView;
    private ToggleButton startStopToggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        this.app = (App) getApplication();
        this.app.setActivity(this);

        this.toolbar = findViewById(R.id.collection_toolbar);
        setSupportActionBar(this.toolbar);
        this.toolbar.setTitle("Super Mileage App");

        this.serverName = getIntent().getStringExtra(App.EXTRA_SM_SERVER_NAME);
        this.serverIP = getIntent().getStringExtra(App.EXTRA_SM_SERVER_IP);
        this.serverPort = getIntent().getStringExtra(App.EXTRA_SM_SERVER_PORT);
        this.config = getIntent().getStringExtra(App.EXTRA_SM_CONFIG);

        this.serverAvailabilityImageView = findViewById(R.id.server_available_imageview);

        TextView serverNameTextview = findViewById(R.id.server_name_textview);
        serverNameTextview.setText(this.serverName);

        this.statusTextView = findViewById(R.id.status_textview);

        this.startStopToggleButton = findViewById(R.id.data_send_togglebutton);

    }
}
