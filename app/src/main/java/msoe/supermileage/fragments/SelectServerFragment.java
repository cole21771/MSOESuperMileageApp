package msoe.supermileage.fragments;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import msoe.supermileage.App;
import msoe.supermileage.R;
import msoe.supermileage.activities.SetupActivity;
import msoe.supermileage.entities.Server;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SelectServerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SelectServerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectServerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener listener;

    private Box<Server> serverBox;
    private List<Server> servers;

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
    }

    public SelectServerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SelectServerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SelectServerFragment newInstance(String param1, String param2) {
        SelectServerFragment fragment = new SelectServerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        BoxStore boxStore = ((App) getActivity().getApplication()).getBoxStore();
        serverBox = boxStore.boxFor(Server.class);
        servers = serverBox.getAll();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_server, container, false);

        Button button = (Button) view.findViewById(R.id.btnNewServer);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                listener.swapFragments(SetupActivity.SetupActivityFragmentType.ADD_SERVER);
            }
        });

        // Setup the servers list view
        ListView serversListView = (ListView) view.findViewById(R.id.serversListView);
        serversListView.setAdapter(new ServersAdapter());

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

    private class ServersAdapter implements ListAdapter {

        public ServersAdapter() {

        }

        @Override
        public boolean areAllItemsEnabled() {
            boolean result = true;
            int i = 0;
            while (i < servers.size() && result) {
                Server server = servers.get(i);
                result = server.isReachable();
                i++;
            }
            return true;
        }

        @Override
        public boolean isEnabled(int position) {
            return servers.get(position).isReachable();
        }

        @Override
        public void registerDataSetObserver(DataSetObserver dataSetObserver) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

        }

        @Override
        public int getCount() {
            return servers.size();
        }

        @Override
        public Object getItem(int position) {
            return servers.get(position).getName();
        }

        @Override
        public long getItemId(int position) {
            return servers.get(position).getId();
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            View result = convertView;

            if (result == null) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                result = inflater.inflate(R.layout.listview_item_server, container, false);
            }

            Server server = servers.get(position);
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
            return servers.isEmpty();
        }
    }

    public void addServer(String name, String ipAddress) {
        Server server = new Server(name, ipAddress);
        serverBox.put(server);
    }

}
