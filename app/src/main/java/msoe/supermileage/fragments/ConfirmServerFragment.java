package msoe.supermileage.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import msoe.supermileage.R;
import msoe.supermileage.activities.SetupActivity;

/**
 * UI for confirming a server.
 * <p>
 * A simple {@link Fragment} subclass.
 *
 * @author braithwaitec
 */
public class ConfirmServerFragment extends Fragment {

    private OnFragmentInteractionListener listener;

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

        void addServer(String name, String ipAddress, String port);

        void confirmServer();
    }

    public ConfirmServerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_confirm_server, container, false);

        Button reloadBtn = view.findViewById(R.id.reloadBtn);
        reloadBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });

        Button selectServerBtn = view.findViewById(R.id.selectServerBtn);
        selectServerBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                listener.confirmServer();
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

}
