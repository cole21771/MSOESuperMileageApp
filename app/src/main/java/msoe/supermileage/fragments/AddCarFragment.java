package msoe.supermileage.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
 * {@link AddCarFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class AddCarFragment extends Fragment {

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

        void addCar(String name);
    }

    public AddCarFragment() {
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
        final View view = inflater.inflate(R.layout.fragment_add_car, container, false);

        Button button = (Button) view.findViewById(R.id.add_car_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String name = ((EditText) view.findViewById(R.id.car_name_textview)).getText().toString();

                if (name.length() > 0) {
                    listener.addCar(name);
                }

                listener.swapFragments(SetupActivity.SetupActivityFragmentType.SELECT_CAR);
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