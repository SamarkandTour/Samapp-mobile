package uz.samtuit.samapp.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;

import uz.samtuit.samapp.adapters.ViewPagerAdapter;
import uz.samtuit.samapp.fragments.SuggestedItineraryFragment;
import uz.samtuit.samapp.fragments.TourFeaturesDialogFragmentWindow;
import uz.samtuit.samapp.util.GlobalsClass;
import uz.samtuit.samapp.util.ItineraryList;
import uz.samtuit.samapp.util.TourFeature;
import uz.samtuit.samapp.util.TourFeatureList;


public class SuggestedItineraryActivity extends ActionBarActivity {
    public static HashMap<?, ?>[] itineraryByDayArray;
    public static boolean modify = false;
    Toolbar toolbar;
    ViewPager pager;
    ViewPagerAdapter vpadapter;
    SlidingTabLayout tabs;
    private MenuItem mModifyAction;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            GlobalsClass globalVariables = (GlobalsClass)getApplicationContext();
            ArrayList<TourFeature> featureList = globalVariables.getTourFeatures(GlobalsClass.FeatureType.HOTEL);

            // When Features are out of memory, App should need to restart from the start
            if (featureList == null) {
                SharedPreferences pref = globalVariables.getApplicationContext().getSharedPreferences("SamTour_Pref", 0);
                String currentLang = pref.getString("app_lang", null);
                TourFeatureList.loadAllFeaturesToMemory(this, currentLang);
            }
        }

        setContentView(R.layout.activity_itinerary);

        toolbar = (Toolbar)findViewById(R.id.si_toolbar);
        toolbar.setTitle(R.string.title_suggested_itinerary);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.itinerary_primary));
        toolbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.itinerary_primary)));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeActivity();
            }
        });


        CharSequence Titles[] = new CharSequence[ItineraryList.MAX_ITINERARY_DAYS];
        itineraryByDayArray = new HashMap<?, ?>[ItineraryList.MAX_ITINERARY_DAYS];
        for (int i = 0; i < ItineraryList.MAX_ITINERARY_DAYS; i++) {
            Titles[i] = getResources().getString(R.string.day, (i + 1));
            itineraryByDayArray[i] = new HashMap<Integer, TourFeature>();
        }


        pager = (ViewPager)findViewById(R.id.pager);
        vpadapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles, ItineraryList.MAX_ITINERARY_DAYS, this);
        pager.setAdapter(vpadapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //Log.e("PAGE", "Selected" + position);
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
        tabs.setBackgroundColor(getResources().getColor(R.color.itinerary_primary));

        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.selector);
            }
        });
        tabs.setViewPager(pager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_my_itinerary, menu);
        mModifyAction = menu.findItem(R.id.action_modify);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment page = vpadapter.getRegisteredFragment(pager.getCurrentItem());
        if(item.getItemId()==R.id.action_modify){

            if(modify){
                if (page != null) {
                    ((SuggestedItineraryFragment)page).modifyMode(false);
                }
                item.setTitle(R.string.modify);
                item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            } else {
                if (page != null) {
                    ((SuggestedItineraryFragment)page).modifyMode(true);
                }
                item.setTitle(R.string.btn_done);
                item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            }
            modify=!modify;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        closeActivity();
    }

    private void closeActivity() {
        if (modify) {
            Fragment page = vpadapter.getRegisteredFragment(pager.getCurrentItem());
            if (page != null) {
                ((SuggestedItineraryFragment)page).modifyMode(false);
            }
            mModifyAction.setTitle(getResources().getString(R.string.modify));
            mModifyAction.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            modify = false;
        } else {
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
}

