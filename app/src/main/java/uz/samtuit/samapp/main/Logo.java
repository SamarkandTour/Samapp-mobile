package uz.samtuit.samapp.main;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.LinkedList;

import uz.samtuit.samapp.util.CustomDialog;
import uz.samtuit.samapp.util.TourFeature;
import uz.samtuit.samapp.util.TourFeatureList;
import uz.samtuit.sammap.main.R;


public class Logo extends ActionBarActivity {
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

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final boolean isWifiConn = networkInfo.isConnected();
        networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean isMobileConn = networkInfo.isConnected();

        SQLiteDatabase APP_DB = openOrCreateDatabase("SamTour_data",MODE_PRIVATE,null);
        ConfigurePropertiesDB configurePropertiesDB = new ConfigurePropertiesDB(APP_DB);
        configurePropertiesDB.RepairDB();
        Cursor APP_PROPERTIES = APP_DB.rawQuery("Select `app_first_launch` from app_properties", null);
        APP_PROPERTIES.moveToFirst();
        AP_FIRSTLAUNCH = Boolean.parseBoolean(APP_PROPERTIES.getString(0));
        Log.e("FL",AP_FIRSTLAUNCH+"");

        if(AP_FIRSTLAUNCH)
        {
            Intent first_launch_intent = new Intent(Logo.this, FirstLaunch.class);
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

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (updateAvailable && isWifiConn) {
                    mUpdateAvalDialog = new CustomDialog(Logo.this,
                            R.string.title_dialog_update_available,
                            R.string.dialog_update_available,
                            R.string.yes,
                            R.string.no,
                            yesClickListener,
                            noClickListener);
                    mUpdateAvalDialog.show();
                } else {
                    Intent mainMap = new Intent(Logo.this, MainMap.class);
                    startActivity(mainMap);
                }
            }
        }, 3000);
    }

    private View.OnClickListener yesClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mUpdateAvalDialog.dismiss();

            // Start to download at Background

            Intent mainMap = new Intent(Logo.this, MainMap.class);
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

            Intent mainMap = new Intent(Logo.this, MainMap.class);
            startActivity(mainMap);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }
}