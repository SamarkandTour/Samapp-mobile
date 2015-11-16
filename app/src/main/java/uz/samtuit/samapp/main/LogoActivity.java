package uz.samtuit.samapp.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;

import com.mapbox.mapboxsdk.util.NetworkUtils;

import java.util.ArrayList;
import java.util.LinkedList;

import uz.samtuit.samapp.util.CustomDialog;
import uz.samtuit.samapp.util.GlobalsClass;
import uz.samtuit.samapp.util.TourFeature;
import uz.samtuit.samapp.util.TourFeatureList;
import uz.samtuit.sammap.main.R;


public class LogoActivity extends ActionBarActivity {
    private boolean updateAvailable = true;
    private static ArrayList<TourFeature> Hotels;
    private static ArrayList<TourFeature> Shops;
    private static ArrayList<TourFeature> Attractions;
    private static ArrayList<TourFeature> Foods;
    private static LinkedList<TourFeature> Itinerary;
    private CustomDialog mUpdateAvalDialog;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Set language for UI
                Intent first_launch_intent = new Intent(LogoActivity.this, LanguageSettingActivity.class);
                startActivityForResult(first_launch_intent, 0);
            }
        }, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        GlobalsClass globals = (GlobalsClass)getApplicationContext();

        // Since language selection has finished, Set features lists
        TourFeatureList tourFeatureList = new TourFeatureList();
        String ChoosenLang = globals.getApplicationLanguage();

        path = GlobalsClass.GeoJSONFileName[GlobalsClass.FeatureType.HOTEL.ordinal()];
        Hotels = tourFeatureList.getTourFeatureList(getApplicationContext(), ChoosenLang + path);
        globals.setFeatures(GlobalsClass.FeatureType.HOTEL, Hotels);
        Log.e("SIZE", Hotels.size() + "");

        path = GlobalsClass.GeoJSONFileName[GlobalsClass.FeatureType.FOODNDRINK.ordinal()];
        tourFeatureList = new TourFeatureList();
        Foods = tourFeatureList.getTourFeatureList(getApplicationContext(), ChoosenLang + path);
        globals.setFeatures(GlobalsClass.FeatureType.FOODNDRINK, Foods);
        Log.e("SIZE", Foods.size() + "");

        path = GlobalsClass.GeoJSONFileName[GlobalsClass.FeatureType.ATTRACTION.ordinal()];
        tourFeatureList = new TourFeatureList();
        Attractions = tourFeatureList.getTourFeatureList(getApplicationContext(), ChoosenLang + path);
        globals.setFeatures(GlobalsClass.FeatureType.ATTRACTION, Attractions);
        Log.e("SIZE", Attractions.size() + "");

        path = GlobalsClass.GeoJSONFileName[GlobalsClass.FeatureType.SHOPPING.ordinal()];
        tourFeatureList = new TourFeatureList();
        Shops = tourFeatureList.getTourFeatureList(getApplicationContext(), ChoosenLang + path);
        Log.e("SIZE", Shops.size() + "");
        globals.setFeatures(GlobalsClass.FeatureType.SHOPPING, Shops);
        TourFeatureList itineraryList = new TourFeatureList();

        Itinerary = itineraryList.getItineraryFeatureList(getApplicationContext(), ChoosenLang + "_itinerary_mixed_nightlife_onenhalf_day.geojson");
        Log.e("SIZE", Itinerary.size() + "");
        globals.setItineraryFeatures(Itinerary);
        TourFeatureList.ItineraryWriteToGeoJSONFile(this, Itinerary, ChoosenLang + "_MyItinerary.geojson");

        // Check update available
        if (updateAvailable && NetworkUtils.isNetworkAvailable(LogoActivity.this)) {
            mUpdateAvalDialog = new CustomDialog(LogoActivity.this,
                    R.string.title_dialog_update_available,
                    R.string.dialog_update_available,
                    R.string.yes,
                    R.string.no,
                    yesClickListener,
                    noClickListener);
            mUpdateAvalDialog.show();
        } else {
            Intent mainMap = new Intent(LogoActivity.this, MainMap.class);
            startActivity(mainMap);
            finish();
        }
    }

    private View.OnClickListener yesClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        mUpdateAvalDialog.dismiss();

        // Start to download at Background

        Intent mainMap = new Intent(LogoActivity.this, MainMap.class);
        startActivity(mainMap);
        finish();
        }
    };

    private View.OnClickListener noClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        mUpdateAvalDialog.dismiss();

        Intent mainMap = new Intent(LogoActivity.this, MainMap.class);
        startActivity(mainMap);
        finish();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }
}