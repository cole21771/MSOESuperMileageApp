package msoe.supermileage.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import msoe.supermileage.R;
import msoe.supermileage.activities.SetupActivity;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddServerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class AddServerFragment extends Fragment {

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

        void addServer(String name, String ipAddress, String port);
    }

    public AddServerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_add_server, container, false);

        final EditText nameEditText = ((EditText) view.findViewById(R.id.serverNameEditText));
        final EditText ipAddressEditText = ((EditText) view.findViewById(R.id.serverIPEditText));
        final EditText portEditText = ((EditText) view.findViewById(R.id.serverPortEditText));
        final Button saveButton = view.findViewById(R.id.addServerBtn);

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Unused
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Unused
            }

            @Override
            public void afterTextChanged(Editable editable) {
                saveButton.setEnabled(
                        nameEditText.getText().length() > 0 &&
                                ipAddressEditText.getText().length() > 0 &&
                                portEditText.getText().length() > 0
                );
            }
        };

        nameEditText.addTextChangedListener(watcher);
        ipAddressEditText.addTextChangedListener(watcher);
        portEditText.addTextChangedListener(watcher);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = ((EditText) view.findViewById(R.id.serverNameEditText)).getText().toString();
                String ipAddress = ((EditText) view.findViewById(R.id.serverIPEditText)).getText().toString();
                String port = ((EditText) view.findViewById(R.id.serverPortEditText)).getText().toString();

                if (name.length() > 0 && ipAddress.length() > 0 && port.length() > 0) {
                    listener.addServer(name, ipAddress, port);
                }

                listener.swapFragments(SetupActivity.SetupActivityFragmentType.SELECT_SERVER);
            }
        });
        saveButton.setEnabled(false);

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
