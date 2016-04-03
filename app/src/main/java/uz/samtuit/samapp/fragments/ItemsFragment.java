package uz.samtuit.samapp.fragments;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uz.samtuit.samapp.main.R;
import uz.samtuit.samapp.util.GlobalsClass;

/**
 * A simple {@link Fragment} subclass.
 */
public class ItemsFragment extends Fragment {



    public ItemsFragment() {
        Log.e("Fragment", "Test");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_items, container, false);
    }

}
