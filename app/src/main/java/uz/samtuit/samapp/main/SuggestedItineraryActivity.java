package uz.samtuit.samapp.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;

import uz.samtuit.samapp.util.GlobalsClass;
import uz.samtuit.samapp.util.ItineraryList;
import uz.samtuit.samapp.util.TourFeature;


public class SuggestedItineraryActivity extends ActionBarActivity {
    public static ArrayList<LinkedList<TourFeature>> itineraryListArray = new ArrayList<LinkedList<TourFeature>>();

    Toolbar toolbar;
    ViewPager pager;
    ViewPagerAdapter vpadapter;
    SlidingTabLayout tabs;
    private LinkedList<TourFeature> itineraryListFromGlobal;
    private LinkedList<TourFeature> itineraryItemDay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_itinerary);
        toolbar = (Toolbar)findViewById(R.id.si_toolbar);
        toolbar.setTitle(R.string.suggested_itinerary);
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



        CharSequence Titles[] = new CharSequence[ItineraryList.MAX_ITINERARY_DAYS];
        for (int i = 0; i < ItineraryList.MAX_ITINERARY_DAYS; i++) {
            Titles[i] = "Day " + (i + 1);
        }




        pager = (ViewPager)findViewById(R.id.pager);
        InitItineraryListArray();
        vpadapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles, ItineraryList.MAX_ITINERARY_DAYS);
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


    }

    // Gather each feature in itineraryList by day
    private void InitItineraryListArray() {
        GlobalsClass globals = (GlobalsClass)this.getApplicationContext();
        itineraryListFromGlobal = globals.getItineraryFeatures();

        itineraryListArray = new ArrayList<LinkedList<TourFeature>>();
        for(int i = 0; i < ItineraryList.MAX_ITINERARY_DAYS; i++){
            itineraryItemDay = new LinkedList<TourFeature>();
            itineraryListArray.add(itineraryItemDay);
        }


        if(itineraryListFromGlobal==null){
            return;
        }

        for(TourFeature v : itineraryListFromGlobal){
            int index = v.getDay() - 1;
            if(index != -1){
                itineraryListArray.get(index).add(v);
            }
            else {
                Toast.makeText(getApplicationContext(), R.string.Err_wrong_geojson_file, Toast.LENGTH_LONG).show();
            }
            Log.e("tag",itineraryListArray.size()+" "+itineraryListArray.get(0).size());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_content, R.anim.slide_in);

        Intent intent = new Intent(this, MainMap.class);
        intent.putExtra("type", "features");
        intent.putExtra("featureType", GlobalsClass.FeatureType.ITINERARY.toString());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

}

