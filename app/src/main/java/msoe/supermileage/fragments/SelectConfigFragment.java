package msoe.supermileage.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import msoe.supermileage.R;
import msoe.supermileage.activities.SetupActivity;
import msoe.supermileage.entities.Config;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SelectConfigFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SelectConfigFragment extends Fragment {

    private OnFragmentInteractionListener listener;

    private SetupActivity setupActivity;

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void swapFragments(SetupActivity.SetupActivityFragmentType type);

        void selectConfig(Config config);
    }

    public SelectConfigFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setupActivity = (SetupActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_config, container, false);

        Button button = view.findViewById(R.id.add_config_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                listener.swapFragments(SetupActivity.SetupActivityFragmentType.ADD_CONFIG);
            }
        });

        // Setup the local configs list view
        ListView localConfigsListView = view.findViewById(R.id.local_configs_listview);
        localConfigsListView.setAdapter(new ConfigsAdapter(this.setupActivity, this.setupActivity.getLocalConfigs()));
        localConfigsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.selectConfig(
                        setupActivity.getLocalConfigs().get(position)
                );
            }
        });
        localConfigsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                //TODO select the config
                boolean result = true;
                return result;
            }
        });

        // Setup the remote configs list view
        ListView remoteConfigsListView = view.findViewById(R.id.remote_configs_listview);
        remoteConfigsListView.setAdapter(new ConfigsAdapter(this.setupActivity, this.setupActivity.getRemoteConfigs()));
        remoteConfigsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.selectConfig(
                        setupActivity.getRemoteConfigs().get(position)
                );
            }
        });
        remoteConfigsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                //TODO select the config
                boolean result = true;
                return result;
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    private class ConfigsAdapter extends ArrayAdapter<Config> {

        public ConfigsAdapter(Context context, List<Config> configs) {
            super(context, 0, configs);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View result = convertView;

            final Config config = getItem(position);

            if (result == null) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                result = inflater.inflate(R.layout.listrow_details_configs, null);
                result.setClickable(true);
                result.setLongClickable(true);

                if (config != null) {
                    Button button = result.findViewById(R.id.select_config_button);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.selectConfig(
                                    config
                            );
                        }
                    });
                }
            }

            TextView textView = result.findViewById(R.id.config_name_textview);
            if (config != null) {
                textView.setText(config.getName());
            }

            return result;
        }
    }

}
