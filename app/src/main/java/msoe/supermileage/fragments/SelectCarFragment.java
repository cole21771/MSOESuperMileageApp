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
import msoe.supermileage.entities.Car;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SelectCarFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SelectCarFragment extends Fragment {

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

        void selectCar(Car car);
    }

    public SelectCarFragment() {
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
        View view = inflater.inflate(R.layout.fragment_select_car, container, false);

        Button button = view.findViewById(R.id.add_car_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                listener.swapFragments(SetupActivity.SetupActivityFragmentType.ADD_CAR);
            }
        });

        // Setup the local cars list view
        ListView localCarsListView = view.findViewById(R.id.local_cars_listview);
        localCarsListView.setAdapter(new CarsAdapter(this.setupActivity, this.setupActivity.getLocalCars()));
        localCarsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.selectCar(
                        setupActivity.getLocalCars().get(position)
                );
            }
        });
        localCarsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                //TODO select the car
                boolean result = true;
                return result;
            }
        });

        ListView remoteCarsListView = view.findViewById(R.id.remote_cars_listview);
        remoteCarsListView.setAdapter(new CarsAdapter(this.setupActivity, this.setupActivity.getRemoteCars()));
        remoteCarsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.selectCar(
                        setupActivity.getRemoteCars().get(position)
                );
            }
        });
        remoteCarsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                //TODO select the car
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

    private class CarsAdapter extends ArrayAdapter<Car> {

        public CarsAdapter(Context context, List<Car> cars) {
            super(context, 0, cars);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View result = convertView;

            final Car car = getItem(position);

            if (result == null) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                result = inflater.inflate(R.layout.listrow_details_cars, null);
                result.setClickable(true);
                result.setLongClickable(true);

                if (car != null) {
                    Button button = result.findViewById(R.id.select_car_button);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.selectCar(
                                    car
                            );
                        }
                    });
                }
            }

            TextView textView = result.findViewById(R.id.car_name_textview);
            if (car != null) {
                textView.setText(car.getName());
            }

            return result;
        }
    }
}
