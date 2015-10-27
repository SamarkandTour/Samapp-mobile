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

import java.util.ArrayList;

import uz.samtuit.sammap.main.R;

public class SuggestedItineraryFragment extends Fragment implements AbsListView.OnItemClickListener {

    private static final String SI_DAY = "Day";
    private static int day;
    private NewPoint[] dat;
    private ArrayList<NewPoint[]> data;


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
        genData();

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
                R.layout.itinerary_list_item, data.get(day));
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

    private void genData()
    {
        data = new ArrayList<NewPoint[]>();
        dat = new NewPoint[4];
        dat[0]= new NewPoint("Registan","39.65219, 66.94716","ADAS");
        dat[1]= new NewPoint("Guri Emir","39.64853428524564, 66.96916937828064","ADAS");
        dat[2]= new NewPoint("Rukhabad","39.65081839030569, 66.96821451187134","ADAS");
        dat[3]= new NewPoint("Afrasiab Hotel","39.66607, 66.97932","ADAS");
        data.add(dat);
        dat = new NewPoint[3];
        dat[2]= new NewPoint("Siyob Bazar","39.65219, 66.94716","ADAS");
        dat[1]= new NewPoint("Afrasiab","39.65552, 66.95680","ADAS");
        dat[0]= new NewPoint("Shakhizinda","39.66166, 66.97904","ADAS");
        data.add(dat);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
