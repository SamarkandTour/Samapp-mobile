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
    private MyItineraryAdapter adapter;
    private int day = 0;
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

        if (getArguments() != null) {
            day = getArguments().getInt(SI_DAY);
            try {
                Log.e("SugItineraryFragment ", "onCreate " + day);
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
        }
        try{
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        adapter = new MyItineraryAdapter(day);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_suggesteditinerary_list, container, false);
        view.setTag(TAG);
        adapter = new MyItineraryAdapter(day);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.it_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setTag(day);
        mRecyclerView.setOnClickListener(this);

        return view;
    }

    public interface SendListener{
        public void reDrawList();
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("TEg",day+"");
        adapter = new MyItineraryAdapter(day);
        mRecyclerView.setAdapter(adapter);
    }
}
