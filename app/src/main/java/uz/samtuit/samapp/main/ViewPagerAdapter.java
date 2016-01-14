package uz.samtuit.samapp.main;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created
    private final String SI_DAY = "Day";
    private Context mContext;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private MyItineraryAdapter adapter;
    private int day = 0;
    private static final String TAG = "SuggestedItineraryFragment";

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm,CharSequence mTitles[], int mNumbOfTabsumb, Context mContext) {
        super(fm);
        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;
        this.mContext = mContext;
    }

    @Override
    public Object instantiateItem(View viewPager, int position) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.fragment_suggesteditinerary_list,null);
        mRecyclerView = (RecyclerView)v.findViewById(R.id.it_recycler_view);
        adapter = new MyItineraryAdapter(day);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setTag(SI_DAY);
        return v;
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {
        Fragment f;
        f = new SuggestedItineraryFragment();

        Bundle args = new Bundle();
        args.putInt(SI_DAY, position);
        Log.e("New Tab", position + "");
        f.setArguments(args);
        return f;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        notifyDataSetChanged();
    }

    public int getItemPosition(Object object){
        return POSITION_NONE;
    }

    // This method return the titles for the Tabs in the Tab Strip
    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip
    @Override
    public int getCount() {
        return NumbOfTabs;
    }
}