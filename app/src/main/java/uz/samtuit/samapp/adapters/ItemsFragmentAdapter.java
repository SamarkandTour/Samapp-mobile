package uz.samtuit.samapp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import uz.samtuit.samapp.fragments.ItemsFragment;

/**
 * Created by Bakha on 12.03.2016.
 */



public class ItemsFragmentAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;

    public ItemsFragmentAdapter(android.support.v4.app.FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }


    @Override
    public int getCount() {
        return this.fragments.size();
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        return new ItemsFragment();
    }
}
