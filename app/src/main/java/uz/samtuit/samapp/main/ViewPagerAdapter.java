package uz.samtuit.samapp.main;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

import uz.samtuit.samapp.fragments.SuggestedItineraryFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created
    private final String SI_DAY = "Day";
    private Context mContext;
    private static final String TAG = "DAY_";
    private Map<Integer, String> mFragmentTags;
    private FragmentManager mFragmentManager;
    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm,CharSequence mTitles[], int mNumbOfTabsumb, Context mContext) {
        super(fm);
        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;
        this.mContext = mContext;
        mFragmentManager = fm;
        mFragmentTags = new HashMap<Integer, String>();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment)super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        fragment = new SuggestedItineraryFragment();

        Bundle args = new Bundle();
        args.putInt(SI_DAY, position);
        Log.e("New Tab", position + "");
        fragment.setArguments(args);

        return fragment;
    }

    public Fragment getRegisteredFragment(int posigion) {
        return registeredFragments.get(posigion);
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