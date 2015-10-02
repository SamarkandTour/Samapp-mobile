package uz.samtuit.sammap.samapp;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class SuggestedItinerary extends ActionBarActivity {

    Button btn;
    LinearLayout ll;
    FrameLayout fl;
    TextView tv;
    Toolbar toolbar;
    ViewPager pager;
    ViewPagerAdapter vpadapter;
    ListView lv;
    Point[] dat;
    ArrayList<Point[]> data;
    SugItineraryAdapter adapter;
    SlidingTabLayout tabs;


    private final static int daysCount = 2;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary_wizard);
        CharSequence Titles[] = new CharSequence[daysCount];
        for(int i = 0; i < daysCount; i++)
            Titles[i] = "Day "+(i+1);
        toolbar = (Toolbar)findViewById(R.id.si_toolbar);
        toolbar.setTitle("Suggested Itinerary");
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.ColorPrimary));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_content, R.anim.slide_in);
            }
        });

        vpadapter = new ViewPagerAdapter(getSupportFragmentManager(),Titles,daysCount);

        pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(vpadapter);

        tabs = (SlidingTabLayout)findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);

        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.selector);
            }
        });
        tabs.setViewPager(pager);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_content, R.anim.slide_in);
    }



}

