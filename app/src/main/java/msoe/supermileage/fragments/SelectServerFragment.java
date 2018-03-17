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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import msoe.supermileage.R;
import msoe.supermileage.activities.SetupActivity;
import msoe.supermileage.entities.Server;


/**
 * UI for selecting a server.
 * <p>
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SelectServerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 *
 * @author braithwaitec
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

        Button button = view.findViewById(R.id.newServerBtn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                listener.swapFragments(SetupActivity.SetupActivityFragmentType.ADD_SERVER);
            }
        });

        // Setup the servers list view
        ListView serversListView = view.findViewById(R.id.serversListView);
        serversListView.setItemsCanFocus(false);
        serversListView.setAdapter(new ServersAdapter(this.setupActivity, this.setupActivity.getServers()));
        serversListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.selectServer(setupActivity.getServers().get(position));
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


    private class ServersAdapter extends ArrayAdapter<Server> {

        public ServersAdapter(Context context, List<Server> servers) {
            super(context, 0, servers);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View result = convertView;

            final Server server = getItem(position);

            if (result == null) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                result = inflater.inflate(R.layout.listview_item_server, parent, false);
                result.setClickable(true);
                result.setLongClickable(true);

                TextView textView = result.findViewById(R.id.serverName);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.selectServer(server);
                    }
                });

                ImageView imageView = result.findViewById(R.id.serverIndicator);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.selectServer(server);
                    }
                });
            }

            TextView textView = result.findViewById(R.id.serverName);
            textView.setText(server.getName());
            ImageView imageView = result.findViewById(R.id.serverIndicator);
            imageView.setImageResource(server.isReachable() ? android.R.drawable.presence_online : android.R.drawable.presence_offline);

            return result;
        }
    }

}
