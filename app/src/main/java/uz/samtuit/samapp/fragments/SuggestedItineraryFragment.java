package uz.samtuit.samapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import uz.samtuit.samapp.adapters.MyItineraryAdapter;
import uz.samtuit.samapp.helpers.ItineraryHelper;
import uz.samtuit.samapp.main.R;
import uz.samtuit.samapp.main.SuggestedItineraryActivity;
import uz.samtuit.samapp.util.GlobalsClass;
import uz.samtuit.samapp.util.TourFeature;

public class SuggestedItineraryFragment extends Fragment {
    private final String SI_DAY = "Day";
    private MyItineraryAdapter adapter;
    private int day = 0;
    private static final String TAG = "SuggestedItineraryFragment";

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ImageButton mAddBtn;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_suggesteditinerary_list, container, false);
        view.setTag(TAG);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.it_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setTag(day);
        mAddBtn = (ImageButton) view.findViewById(R.id.add_new_itinerary_item);
        int size = getItinerarySizeByDay(getContext(), day+1);
        if(size==0) {
            mAddBtn.setVisibility(View.VISIBLE);
            mAddBtn.setOnClickListener(addNewItineraryBtnClickListener());
        }
        return view;
    }

    private int getItinerarySizeByDay(Context context, int itineraryDay) {
        GlobalsClass globalsClass = (GlobalsClass)context.getApplicationContext();
        LinkedList<TourFeature> itineraryFeatures = globalsClass.getItineraryFeatures();
        int count = 0;

        if (itineraryFeatures == null) {
            return 0;
        }

        for (TourFeature v : itineraryFeatures) {
            if (itineraryDay == v.getDay()) {
                count++;
            }
        }

        return count;
    }


    public void modifyMode(boolean state){
        adapter = new MyItineraryAdapter(getContext(), day, state, true, getFragmentManager(), this);
        mRecyclerView.setAdapter(adapter);
    }

    public void showAddButton(boolean state, final int day, final int indexToAssign) {
        if (!state) {
            mAddBtn.setVisibility(View.GONE);
        } else {
            mAddBtn.setOnClickListener(addNewItineraryBtnClickListener());
            mAddBtn.setVisibility(View.VISIBLE);
        }
    }

    public View.OnClickListener addNewItineraryBtnClickListener() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = 0;
                for (int i = 1; i < day; i++)
                    index += getItinerarySizeByDay(getContext(), i) - 1;
                final int indexToAssign = index;
                mAddBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ItineraryHelper.addNewItemFromItinerary(getFragmentManager(), day, indexToAssign);
                    }
                });
            }
        };
        return listener;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getItinerarySizeByDay(getContext(), day + 1)!=0) {
            mAddBtn.setVisibility(View.GONE);
        } else {
            mAddBtn.setVisibility(View.VISIBLE);
        }
        adapter = new MyItineraryAdapter(getContext(), day, SuggestedItineraryActivity.modify, false, getFragmentManager(),this);
        mRecyclerView.setAdapter(adapter);

    }

}
