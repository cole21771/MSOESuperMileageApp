package msoe.supermileage.fragments;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import msoe.supermileage.R;
import msoe.supermileage.activities.SetupActivity;
import msoe.supermileage.entities.Server;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SelectServerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SelectServerFragment extends Fragment {

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

        void selectServer(Server server);
    }

    public SelectServerFragment() {
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
        View view = inflater.inflate(R.layout.fragment_select_server, container, false);

        Button button = (Button) view.findViewById(R.id.newServerBtn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                listener.swapFragments(SetupActivity.SetupActivityFragmentType.ADD_SERVER);
            }
        });

        // Setup the servers list view
        ListView serversListView = (ListView) view.findViewById(R.id.serversListView);
        serversListView.setItemsCanFocus(false);
        serversListView.setAdapter(new ServersAdapter());
        serversListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectServer(position);
            }
        });
        serversListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
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

    private void selectServer(int position) {
        Server server = null;
        server = setupActivity.getServers().get(position);
        listener.selectServer(server);
    }

    private class ServersAdapter implements ListAdapter {

        public ServersAdapter() {

        }

        @Override
        public boolean areAllItemsEnabled() {
            boolean result = true;
            int i = 0;
            while (i < setupActivity.getServers().size() && result) {
                Server server = setupActivity.getServers().get(i);
                result = server.isReachable();
                i++;
            }
            return true;
        }

        @Override
        public boolean isEnabled(int position) {
            return setupActivity.getServers().get(position).isReachable();
        }

        @Override
        public void registerDataSetObserver(DataSetObserver dataSetObserver) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

        }

        @Override
        public int getCount() {
            return setupActivity.getServers().size();
        }

        @Override
        public Object getItem(int position) {
            return setupActivity.getServers().get(position).getName();
        }

        @Override
        public long getItemId(int position) {
            return setupActivity.getServers().get(position).getId();
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup container) {
            View result = convertView;

            if (result == null) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                result = inflater.inflate(R.layout.listview_item_server, container, false);
                result.setClickable(true);
                result.setLongClickable(true);

                TextView textView = (TextView) result.findViewById(R.id.serverName);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectServer(position);
                    }
                });

                ImageView imageView = (ImageView) result.findViewById(R.id.serverIndicator);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectServer(position);
                    }
                });
            }

            Server server = setupActivity.getServers().get(position);
            TextView textView = (TextView) result.findViewById(R.id.serverName);
            textView.setText(server.getName());
            ImageView imageView = (ImageView) result.findViewById(R.id.serverIndicator);
            imageView.setImageResource(server.isReachable() ? android.R.drawable.presence_online : android.R.drawable.presence_offline);

            return result;
        }

        @Override
        public int getItemViewType(int position) {
            return 1;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return setupActivity.getServers().isEmpty();
        }
    }

}
