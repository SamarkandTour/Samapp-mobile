package uz.samtuit.samapp.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.HashMap;

import uz.samtuit.samapp.util.GlobalsClass;
import uz.samtuit.samapp.util.ItineraryList;
import uz.samtuit.samapp.util.TourFeature;


public class SuggestedItineraryActivity extends ActionBarActivity {
    public static HashMap<?, ?>[] itineraryByDayArray;

    Toolbar toolbar;
    ViewPager pager;
    ViewPagerAdapter vpadapter;
    SlidingTabLayout tabs;
    private GlobalsClass globals;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary);

        toolbar = (Toolbar)findViewById(R.id.si_toolbar);
        toolbar.setTitle(R.string.title_suggested_itinerary);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.itinerary_primary));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_content, R.anim.slide_in);
            }
        });

        CharSequence Titles[] = new CharSequence[ItineraryList.MAX_ITINERARY_DAYS];
        itineraryByDayArray = new HashMap<?, ?>[ItineraryList.MAX_ITINERARY_DAYS];
        for (int i = 0; i < ItineraryList.MAX_ITINERARY_DAYS; i++) {
            Titles[i] = "Day " + (i + 1);
            itineraryByDayArray[i] = new HashMap<Integer, TourFeature>();
        }

        globals = (GlobalsClass)this.getApplicationContext();

        pager = (ViewPager)findViewById(R.id.pager);
        vpadapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles, ItineraryList.MAX_ITINERARY_DAYS, this);
        pager.setAdapter(vpadapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.e("PAGE", "Scrolled" + position);
            }

            @Override
            public void onPageSelected(int position) {
                Log.e("PAGE", "Selected" + position);
                Fragment fragment = vpadapter.getRegisteredFragment(position);
                if (fragment != null) {
                    fragment.onResume();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

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
