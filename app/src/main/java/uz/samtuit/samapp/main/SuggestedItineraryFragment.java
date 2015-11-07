package uz.samtuit.samapp.main;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import java.util.LinkedList;

import uz.samtuit.samapp.util.GlobalsClass;
import uz.samtuit.samapp.util.TourFeature;
import uz.samtuit.sammap.main.R;

public class SuggestedItineraryFragment extends Fragment implements AbsListView.OnItemClickListener {

    private static final String SI_DAY = "Day";
    private static int day;
    private LinkedList<TourFeature> data;
    //private ArrayList<LinkedList<TourFeature>[]> data;


    private AbsListView mListView;

    private SugItineraryAdapter adapter;

    public static SuggestedItineraryFragment newInstance(int day) {
        SuggestedItineraryFragment fragment = new SuggestedItineraryFragment();
        Bundle args = new Bundle();
        args.putInt(SI_DAY, day);
        fragment.setArguments(args);
        return fragment;
    }

    public SuggestedItineraryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getData(this.getActivity());

        if (getArguments() != null) {
            day = getArguments().getInt(SI_DAY);
            try {
                Log.e("NUll ", "onCreate " + day);
            }catch (NullPointerException ex)
            {
                ex.printStackTrace();
            }
        }
        adapter = new SugItineraryAdapter(getActivity(),
                R.layout.itinerary_list_item, data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sugesteditinerary, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(adapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    private void getData(Context context)
    {
        GlobalsClass globals = (GlobalsClass)context.getApplicationContext();
        data = globals.getItineraryFeatures();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
