package uz.samtuit.samapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import uz.samtuit.samapp.adapters.ItemsFragmentAdapter;
import uz.samtuit.samapp.main.ItemsListActivity;
import uz.samtuit.samapp.main.R;
import uz.samtuit.samapp.main.SuggestedItineraryActivity;
import uz.samtuit.samapp.util.GlobalsClass;
import uz.samtuit.samapp.util.MenuItems;

/**
 * Created by Bakha on 12.03.2016.
 */
public class TourFeaturesDialogFragmentWindow extends DialogFragment{
    private int currentDay;
    private int indexToAssign;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle extras = getArguments();
        indexToAssign = extras.getInt("index");
        currentDay = extras.getInt("current_day");


        View view = inflater.inflate(R.layout.items_fragment_dialog, container);

        View.OnClickListener onClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String action = MenuItems.MainMenu.HOTEL.toString();
                switch (v.getId()) {
                    case R.id.attraction_picker:
                        action = MenuItems.MainMenu.ATTRACTION.toString();
                        break;
                    case R.id.food_picker:
                        action = MenuItems.MainMenu.FOODNDRINK.toString();
                        break;
                    case R.id.shop_picker:
                        action = MenuItems.MainMenu.SHOPPING.toString();
                        break;
                    default:
                        action = MenuItems.MainMenu.HOTEL.toString();
                        break;
                }
                Intent intent = new Intent(getActivity(), ItemsListActivity.class);
                intent.putExtra("action",action);
                intent.putExtra("selected_day", currentDay);
                intent.putExtra("index", indexToAssign);
                intent.putExtra("from_itinerary",true);
                startActivity(intent);
                getDialog().cancel();
            }
        };

        ((Button)view.findViewById(R.id.hotels_picker)).setOnClickListener(onClick);

        ((Button)view.findViewById(R.id.attraction_picker)).setOnClickListener(onClick);

        ((Button)view.findViewById(R.id.shop_picker)).setOnClickListener(onClick);

        ((Button)view.findViewById(R.id.food_picker)).setOnClickListener(onClick);


        getDialog().getWindow().setTitle(getResources().getString(R.string.add_to_itinerary_select_category));

        return view;
    }
}
