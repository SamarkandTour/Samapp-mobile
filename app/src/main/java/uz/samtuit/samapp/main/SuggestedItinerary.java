package uz.samtuit.samapp.main;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;

import uz.samtuit.samapp.util.GlobalsClass;
import uz.samtuit.samapp.util.TourFeature;
import uz.samtuit.sammap.main.R;


public class SuggestedItinerary extends ActionBarActivity {
    public static ArrayList<LinkedList<TourFeature>> itineraryListArray;

    Toolbar toolbar;
    ViewPager pager;
    ViewPagerAdapter vpadapter;
    SlidingTabLayout tabs;
    private final static int maxDaysCount = 5;
    private LinkedList<TourFeature> itineraryListFromGlobal;
    private LinkedList<TourFeature> itineraryListDay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_itinerary);
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

        CharSequence Titles[] = new CharSequence[maxDaysCount];
        for (int i = 0; i < maxDaysCount; i++) {
            Titles[i] = "Day " + (i + 1);
        }
        vpadapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles, maxDaysCount);

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

        InitItineraryListArray();
    }

    // Gather each feature in itineraryList by day
    private void InitItineraryListArray() {
        GlobalsClass globals = (GlobalsClass)this.getApplicationContext();
        itineraryListFromGlobal = globals.getItineraryFeatures();
        itineraryListArray = new ArrayList<LinkedList<TourFeature>>();

        for (int i = 0; i < maxDaysCount; i++) {
            itineraryListDay = new LinkedList<TourFeature>();
            itineraryListArray.add(itineraryListDay);
        }

        for (TourFeature v : itineraryListFromGlobal) {
            int index = v.getDay() - 1;

            if (index != -1) {
                itineraryListArray.get(index).add(v);
            } else {
                Toast.makeText(this, R.string.Err_wrong_geojson_file, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_content, R.anim.slide_in);
    }

}

