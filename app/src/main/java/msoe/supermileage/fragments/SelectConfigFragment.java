package msoe.supermileage.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

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

        // Setup the configs expandable list view
        ExpandableListView configsListView = view.findViewById(R.id.configs_expandablelistview);
        configsListView.setAdapter(new ConfigsExpandableListAdapter());
        configsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO select the config
            }
        });
        configsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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

    private class ConfigsExpandableListAdapter extends BaseExpandableListAdapter {

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            Object result = null;
            if (groupPosition == 0) {
                setupActivity.getLocalConfigs().get(childPosition);
            } else {
                setupActivity.getRemoteConfigs().get(childPosition);
            }
            return result;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View result = convertView;

            if (result == null) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                result = inflater.inflate(R.layout.listrow_details_configs, null);
                result.setClickable(true);
                result.setLongClickable(true);

                Button button = result.findViewById(R.id.select_config_button);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.selectConfig(
                                groupPosition == 0 ? setupActivity.getLocalConfigs().get(childPosition) : setupActivity.getRemoteConfigs().get(childPosition)
                        );
                    }
                });
            }

            TextView textView = result.findViewById(R.id.config_name_textview);
            if (groupPosition == 0) {
                textView.setText(setupActivity.getLocalConfigs().get(childPosition).getName());
            } else {
                textView.setText(setupActivity.getRemoteConfigs().get(childPosition).getName());
            }

            return result;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            if (groupPosition == 0) {
                return setupActivity.getLocalConfigs().size();
            } else {
                return setupActivity.getRemoteConfigs().size();
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            if (groupPosition == 0) {
                return setupActivity.getLocalConfigs();
            } else {
                return setupActivity.getRemoteConfigs();
            }
        }

        @Override
        public int getGroupCount() {
            return 2;
        }


        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            View result = convertView;

            if (result == null) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                result = inflater.inflate(R.layout.listrow_group_configs, null);
            }

            TextView textView = result.findViewById(R.id.group_name_textview);
            textView.setText(groupPosition == 0 ? getString(R.string.local_configs) : getString(R.string.remote_configs));

            return result;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }
}
