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
import android.widget.Toast;

import java.util.LinkedList;

import uz.samtuit.samapp.util.ActionItem;
import uz.samtuit.samapp.util.QuickAction;
import uz.samtuit.samapp.util.TourFeature;
import uz.samtuit.sammap.main.R;

public class MyItineraryFragment extends Fragment implements AbsListView.OnItemClickListener {
    private final String SI_DAY = "Day";
    private AbsListView mListView;
    private MyItineraryAdapter adapter;
    private LinkedList<TourFeature> data;
    final int ID_ADD = 1;
    final int ID_DELETE = 2;
    final int ID_REFRESH = 3;

    public MyItineraryFragment() {
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
        adapter = new MyItineraryAdapter(getActivity(), R.layout.itinerary_list_item, data);
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
        data = MyItineraryActivity.itineraryListArray.get(day);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        ActionItem item_add = new ActionItem(ID_ADD,"ADD",getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
        ActionItem item_del = new ActionItem(ID_DELETE,"DELETE",getResources().getDrawable(R.drawable.abc_ic_clear_mtrl_alpha));
        ActionItem item_ref = new ActionItem(ID_REFRESH,"REFRESH",getResources().getDrawable(R.drawable.abc_ic_clear_mtrl_alpha));
        QuickAction mQuickAction = new QuickAction(getActivity().getApplicationContext(),view);
        mQuickAction.addActionItem(item_add);
        mQuickAction.addActionItem(item_add);
        mQuickAction.addActionItem(item_add);
        mQuickAction.addActionItem(item_add);
        mQuickAction.addActionItem(item_del);
        mQuickAction.addActionItem(item_ref);
        mQuickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
            @Override
            public void onItemClick(QuickAction source, int pos, int actionId) {
                String s = "Refresh item clicked";
                switch (actionId) {
                    case ID_ADD:
                        s = "Add item clicked";
                        break;
                    case ID_DELETE:
                        s = "Delete item clicked";
                        break;
                    case ID_REFRESH:
                        s = "Refresh item clicked";
                        break;
                }
                Toast.makeText(getActivity().getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }
        });
        Toast.makeText(getActivity().getApplicationContext(),view.getId() + " " , Toast.LENGTH_LONG).show();
        mQuickAction.show(view);
    }

    public void moveToNextDay()
    {
        Toast.makeText(getActivity().getApplicationContext(), "MoveToTheNextDay", Toast.LENGTH_SHORT).show();
    }
}
