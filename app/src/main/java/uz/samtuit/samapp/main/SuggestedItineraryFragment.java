package uz.samtuit.samapp.main;

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

import uz.samtuit.samapp.util.TourFeature;

public class SuggestedItineraryFragment extends Fragment implements AbsListView.OnItemClickListener {
    private final String SI_DAY = "Day";
    private AbsListView mListView;
    private SuggestedItineraryAdapter adapter;
    private LinkedList<TourFeature> data;

    public SuggestedItineraryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int day = 0;

        if (getArguments() != null) {
            day = getArguments().getInt(SI_DAY);
            try {
                Log.e("SugItineraryFragment ", "onCreate " + day);
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
        }
        getData(day);
        adapter = new SuggestedItineraryAdapter(getActivity(), R.layout.itinerary_list_adapter, data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_suggesteditinerary_list, container, false);

        if (data.size() != 0) {
            // Set the adapter
            mListView = (AbsListView) view.findViewById(android.R.id.list);
            ((AdapterView<ListAdapter>) mListView).setAdapter(adapter);

            // Set OnItemClickListener so we can be notified on item clicks
            mListView.setOnItemClickListener(this);
        } else {
            // Draw "Add" button
        }

        return view;
    }

    private void getData(int day) {
        data = SuggestedItineraryActivity.itineraryListArray.get(day);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
