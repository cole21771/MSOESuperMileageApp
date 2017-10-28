package msoe.supermileage.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import msoe.supermileage.App;
import msoe.supermileage.R;
import msoe.supermileage.fragments.SelectCarFragment;
import msoe.supermileage.fragments.SelectServerFragment;

public class SetupActivity
        extends AppCompatActivity
        implements SelectServerFragment.OnFragmentInteractionListener,
        SelectCarFragment.OnFragmentInteractionListener {

    private App app;

    public enum SetupActivityFragmentType {
        NONE,
        SELECT_SERVER,
        ADD_SERVER,
        SELECT_CAR,
        ADD_CAR,
        SELECT_CONFIG,
        ADD_CONFIG
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

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

        this.app = (App) getApplication();
        this.app.setActivity(this);
    }

    @Override
    public void swapFragments(SetupActivityFragmentType type) {
        Fragment fragment = null;


        switch (type) {
            case NONE:
                break;
            case SELECT_SERVER:
                fragment = SelectServerFragment.newInstance("", "");
                break;
            case ADD_SERVER:
                break;
            case SELECT_CAR:
                fragment = SelectCarFragment.newInstance("", "");
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
}
