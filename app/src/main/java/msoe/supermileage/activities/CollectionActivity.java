package msoe.supermileage.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import msoe.supermileage.App;
import msoe.supermileage.R;

public class CollectionActivity extends AppCompatActivity {
    private App app;

    private Toolbar toolbar;

    private String serverName;
    private String serverIP;
    private String config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        this.app = (App) getApplication();
        this.app.setActivity(this);

        this.toolbar = findViewById(R.id.setup_toolbar);
        setSupportActionBar(this.toolbar);
        this.toolbar.setTitle("Super Mileage App");

        this.serverName = getIntent().getStringExtra(App.EXTRA_SM_SERVER_NAME);
        this.serverIP = getIntent().getStringExtra(App.EXTRA_SM_SERVER_IP);
        this.config = getIntent().getStringExtra(App.EXTRA_SM_CONFIG);

        
    }
}
