package uz.samtuit.samapp.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import uz.samtuit.samapp.adapters.MyItineraryAdapter;
import uz.samtuit.samapp.main.R;
import uz.samtuit.samapp.main.SuggestedItineraryActivity;

public class SuggestedItineraryFragment extends Fragment implements RecyclerView.OnClickListener {
    private final String SI_DAY = "Day";
    private MyItineraryAdapter adapter;
    private int day = 0;
    private static final String TAG = "SuggestedItineraryFragment";

    private RecyclerView mRecyclerView;
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
        mRecyclerView.setOnClickListener(this);

        return view;
    }


    public void modifyMode(boolean state){
        adapter = new MyItineraryAdapter(getContext(), day, state, true, getFragmentManager());
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onResume() {
        super.onResume();
        adapter = new MyItineraryAdapter(getContext(), day, SuggestedItineraryActivity.modify, false, getFragmentManager());
        mRecyclerView.setAdapter(adapter);
    }
}
