package uz.samtuit.samapp.main;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.mapbox.mapboxsdk.util.NetworkUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;

import uz.samtuit.samapp.util.CustomDialog;
import uz.samtuit.samapp.util.GlobalsClass;
import uz.samtuit.samapp.util.TourFeature;
import uz.samtuit.samapp.util.TourFeatureList;
import uz.samtuit.sammap.main.R;


public class LogoActivity extends ActionBarActivity {
    boolean updateAvailable = true;
    boolean AP_FIRSTLAUNCH = false;
    private static ArrayList<TourFeature> Hotels;
    private static ArrayList<TourFeature> Shops;
    private static ArrayList<TourFeature> Attractions;
    private static ArrayList<TourFeature> Foods;
    private static LinkedList<TourFeature> Itinerary;
    private CustomDialog mUpdateAvalDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_logo);

        SQLiteDatabase APP_DB = openOrCreateDatabase("SamTour_data",MODE_PRIVATE,null);
        ConfigurePropertiesDB configurePropertiesDB = new ConfigurePropertiesDB(APP_DB);
        configurePropertiesDB.RepairDB();
        Cursor APP_PROPERTIES = APP_DB.rawQuery("Select `app_first_launch` from app_properties", null);
        APP_PROPERTIES.moveToFirst();
        AP_FIRSTLAUNCH = Boolean.parseBoolean(APP_PROPERTIES.getString(0));
        //Log.e("FL",AP_FIRSTLAUNCH+"");

        if(AP_FIRSTLAUNCH)
        {
            Intent first_launch_intent = new Intent(LogoActivity.this, FirstLaunch.class);
            startActivity(first_launch_intent);
        }

        APP_PROPERTIES = APP_DB.rawQuery("Select * from app_properties",null);
        APP_PROPERTIES.moveToFirst();

        GlobalsClass globals = (GlobalsClass)getApplicationContext();
        globals.setApplicationLanguage(APP_PROPERTIES.getString(2));
        globals.setApplicationVersion(APP_PROPERTIES.getString(1));
        globals.setApplicationName(APP_PROPERTIES.getString(0));
        Log.e("LANG",globals.getApplicationLanguage());

        TourFeatureList tourFeatureList = new TourFeatureList();
        String ChoosenLang = globals.getApplicationLanguage();
        Hotels = tourFeatureList.getTourFeatureList(getApplicationContext(), "data/" + ChoosenLang + "/hotels.geojson");
        globals.setFeatures(GlobalsClass.FeatureType.HOTEL, Hotels);
        Log.e("SIZE", Hotels.size() + "");
        tourFeatureList = new TourFeatureList();
        Foods = tourFeatureList.getTourFeatureList(getApplicationContext(),"data/" + ChoosenLang + "/foodndrinks.geojson");
        globals.setFeatures(GlobalsClass.FeatureType.FOODNDRINK, Foods);
        Log.e("SIZE", Foods.size() + "");
        tourFeatureList = new TourFeatureList();
        Attractions = tourFeatureList.getTourFeatureList(getApplicationContext(), "data/" + ChoosenLang + "/attractions.geojson");
        globals.setFeatures(GlobalsClass.FeatureType.ATTRACTION, Attractions);
        Log.e("SIZE", Attractions.size() + "");
        tourFeatureList = new TourFeatureList();
        Shops = tourFeatureList.getTourFeatureList(getApplicationContext(), "data/" + ChoosenLang + "/shoppings.geojson");
        Log.e("SIZE", Shops.size() + "");
        globals.setFeatures(GlobalsClass.FeatureType.SHOPPING, Shops);
        TourFeatureList itineraryList = new TourFeatureList();

        Itinerary = itineraryList.getItineraryFeatureList(getApplicationContext(), "data/" + ChoosenLang + "/itinerary_mixed_1day.geojson");
        Log.e("SIZE", Itinerary.size() + "");
        globals.setItineraryFeatures(Itinerary);
        TourFeatureList.ItineraryWriteToGeoJSONFile(this, Itinerary, ChoosenLang + "_MyItinerary.geojson");

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
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
                }
            }
        }, 3000);
    }

    public static String readableFileSize(long size) {
        if(size<=0) return "0";
        final String[] units = new String[] {"B","KB","MB","GB","TB"};
        int digitGroups = (int)(Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups))+" "+units[digitGroups];
    }

    public boolean isExternalWritable(){
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state))
        return true;
        return false;
    }
    public boolean isExternalReadable(){
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)||Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
            return true;
        return false;
    }

    private View.OnClickListener yesClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mUpdateAvalDialog.dismiss();

            // Start to download at Background

            Intent mainMap = new Intent(LogoActivity.this, MainMap.class);
            Bundle bundle = new Bundle();
            bundle.putString("Type", "itinerary");
            mainMap.putExtras(bundle);
            startActivity(mainMap);
        }
    };

    private View.OnClickListener noClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mUpdateAvalDialog.dismiss();

            Intent mainMap = new Intent(LogoActivity.this, MainMap.class);
            startActivity(mainMap);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }
}