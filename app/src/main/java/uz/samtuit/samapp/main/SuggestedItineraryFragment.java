package uz.samtuit.samapp.main;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import java.util.LinkedList;

import uz.samtuit.samapp.util.ItineraryItem;
import uz.samtuit.samapp.util.TourFeature;

public class SuggestedItineraryFragment extends Fragment implements RecyclerView.OnClickListener {
    private final String SI_DAY = "Day";
    private AbsListView mListView;
    private MyItineraryAdapter adapter;
    private LinkedList<TourFeature> data;
    private static final String TAG = "SuggestedItineraryFragment";

//    private enum LayoutManagerType{
//        GRID_LAYOUT_MANAGER,
//        LINEAR_LAYOUT_MANAGER
//    }
//
//    protected LayoutManagerType mCurrentLayoutManagerType;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

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
        try{
            getData(day);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        WindowManager wm = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        adapter = new MyItineraryAdapter(data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_suggesteditinerary_list, container, false);
        view.setTag(TAG);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.it_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        if (data != null && data.size() != 0) {
            mRecyclerView.setAdapter(adapter);
            mRecyclerView.setTag(SI_DAY);
            mRecyclerView.setOnClickListener(this);
        } else {
            // Draw "Add" button
        }

        return view;
    }

    private void getData(int day) {
        data = SuggestedItineraryActivity.itineraryListArray.get(day);
    }


    @Override
    public void onClick(View v) {

    }
}
