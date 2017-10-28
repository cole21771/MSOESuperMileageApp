package msoe.supermileage.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import msoe.supermileage.R;
import msoe.supermileage.activities.SetupActivity;


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

        Button button = (Button) view.findViewById(R.id.add_car_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                listener.swapFragments(SetupActivity.SetupActivityFragmentType.ADD_CAR);
            }
        });

        // Setup the cars expandable list view
        ExpandableListView carsListView = (ExpandableListView) view.findViewById(R.id.cars_expandablelistview);


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

    private class CarsExpandableListAdapter extends BaseExpandableListAdapter {

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            Object result = null;
            if (groupPosition == 0) {
                setupActivity.getLocalCars().get(childPosition);
            } else {
                setupActivity.getRemoteCars().get(childPosition);
            }
            return result;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View result = convertView;

            if (result == null) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                result = inflater.inflate(R.layout.listrow_details_cars, null);
                result.setClickable(true);
                result.setLongClickable(true);
            }

            TextView textView = (TextView) result.findViewById(R.id.car_name_textview);
            if (groupPosition == 0) {
                textView.setText(setupActivity.getLocalCars().get(childPosition).getName());
            } else {
                textView.setText(setupActivity.getRemoteCars().get(childPosition).getName());
            }

            return result;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            if (groupPosition == 0) {
                return setupActivity.getLocalCars().size();
            } else {
                return setupActivity.getRemoteCars().size();
            }
        }
        @Override
        public Object getGroup(int groupPosition) {
            if (groupPosition == 0) {
                return setupActivity.getLocalCars();
            } else {
                return setupActivity.getRemoteCars();
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
                result = inflater.inflate(R.layout.listrow_group_cars, null);
            }

            TextView textView = (TextView) result.findViewById(R.id.group_name_textview);
            textView.setText(groupPosition == 0 ? getString(R.string.local_cars) : getString(R.string.remote_cars));

            return result;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }
}
