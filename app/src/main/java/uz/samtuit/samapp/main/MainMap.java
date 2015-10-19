package uz.samtuit.samapp.main;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;


import com.cocoahero.android.geojson.FeatureCollection;
import com.mapbox.mapboxsdk.api.ILatLng;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.Icon;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.overlay.PathOverlay;
import com.mapbox.mapboxsdk.tileprovider.tilesource.MBTilesLayer;
import com.mapbox.mapboxsdk.tileprovider.tilesource.TileLayer;
import com.mapbox.mapboxsdk.util.DataLoadingUtils;
import com.mapbox.mapboxsdk.views.MapView;
import com.mapbox.mapboxsdk.views.util.OnMapOrientationChangeListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

import uz.samtuit.samapp.util.TourFeature;
import uz.samtuit.samapp.util.TourFeatureList;
import uz.samtuit.sammap.main.R;


public class MainMap extends ActionBarActivity {
    private LinearLayout linLay;
    private Button newBtn;
    private ArrayList<MenuItems> Items = new ArrayList<MenuItems>();
    private ImageView btn,compass;
    private SlidingDrawer slidingDrawer;
    private boolean updateAvailable = false;
    private int height;
    private MapView mapView;
    private EditText searchText;
    private Boolean AP_FIRSTLAUNCH;
    private static ArrayList<TourFeature> Hotels;
    private static ArrayList<TourFeature> Shops;
    private static ArrayList<TourFeature> Attractions;
    private static ArrayList<TourFeature> Foods;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        final GlobalsClass globalVariables = (GlobalsClass)getApplicationContext();
        SQLiteDatabase APP_DB = openOrCreateDatabase("SamTour_data", MODE_PRIVATE, null);
        ConfigurePropertiesDB configurePropertiesDB = new ConfigurePropertiesDB(APP_DB);
        configurePropertiesDB.RepairDB();
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final boolean isWifiConn = networkInfo.isConnected();
        networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean isMobileConn = networkInfo.isConnected();
        Cursor APP_PROPERTIES = APP_DB.rawQuery("Select `app_first_launch` from app_properties", null);
        APP_PROPERTIES.moveToFirst();
        AP_FIRSTLAUNCH = Boolean.parseBoolean(APP_PROPERTIES.getString(0));
        Log.e("FL",AP_FIRSTLAUNCH+"");
        if(AP_FIRSTLAUNCH)
        {
            Intent first_launch_intent = new Intent(MainMap.this, FirstLaunch.class);
            startActivity(first_launch_intent);
        }
        APP_PROPERTIES = APP_DB.rawQuery("Select * from app_properties",null);
        APP_PROPERTIES.moveToFirst();
        globalVariables.setApplicationLanguage(APP_PROPERTIES.getString(2));
        globalVariables.setApplicationVersion(APP_PROPERTIES.getString(1));
        globalVariables.setApplicationName(APP_PROPERTIES.getString(0));
        Log.e("LANG",globalVariables.getApplicationLanguage());
        TourFeatureList tourFeatureList = new TourFeatureList();
        String ChoosenLang = globalVariables.getApplicationLanguage();
        Hotels = tourFeatureList.getTourFeatureList(getApplicationContext(), "data/" + ChoosenLang + "/hotels.geojson");
        globalVariables.setFeatures("hotel", Hotels);
        Log.e("SIZE", Hotels.size() + "");
        tourFeatureList = new TourFeatureList();
        Foods = tourFeatureList.getTourFeatureList(getApplicationContext(),"data/" + ChoosenLang + "/foodndrinks.geojson");
        globalVariables.setFeatures("foodndrink", Foods);
        Log.e("SIZE", Foods.size() + "");
        tourFeatureList = new TourFeatureList();
        Attractions = tourFeatureList.getTourFeatureList(getApplicationContext(), "data/" + ChoosenLang + "/attractions.geojson");
        globalVariables.setFeatures("attraction", Attractions);
        Log.e("SIZE", Attractions.size() + "");
        tourFeatureList = new TourFeatureList();
        Shops = tourFeatureList.getTourFeatureList(getApplicationContext(), "data/" + ChoosenLang + "/shoppings.geojson");
        Log.e("SIZE", Shops.size() + "");
        globalVariables.setFeatures("shopping", Shops);

        Bundle extras = getIntent().getExtras();
        Locale locale = new Locale(globalVariables.getApplicationLanguage());
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;

        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        getBaseContext().setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main_map);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mapView = (MapView)findViewById(R.id.mapview);


        //initialize app_global data



        //search text typeface
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");
        searchText = (EditText)findViewById(R.id.search_text);
        searchText.setTypeface(tf);

        //MapView Settings

        compass = (ImageView)findViewById(R.id.compass);

        TileLayer mbTileLayer = new MBTilesLayer(this, "samarkand.mbtiles");
        mapView.setTileSource(mbTileLayer);
        mapView.setCenter(new ILatLng() {
            @Override
            public double getLatitude() {
                return 39.65487;
            }

            @Override
            public double getLongitude() {
                return 66.97562;
            }

            @Override
            public double getAltitude() {
                return 0;
            }
        });


//        checkGpsSetting();
        mapView.setUserLocationEnabled(true);
        mapView.setZoom(18);
        mapView.setMapRotationEnabled(true);
        mapView.setOnMapOrientationChangeListener(new OnMapOrientationChangeListener() {
            @Override
            public void onMapOrientationChange(float v) {
                compass.setRotation(mapView.getMapOrientation());
            }
        });

        //end



        //generate Menu items
        MenuItems item = new MenuItems(0,"About City","drawable/ic_s_about_city_h","about_city");
        Items.add(item);
        item = new MenuItems(1,"Attractions","drawable/ic_s_attractions_h","attraction");
        Items.add(item);
        item = new MenuItems(2,"Food & Drink","drawable/ic_s_food_and_drink_h","foodndrink");
        Items.add(item);
        item = new MenuItems(3,"Hotels","drawable/ic_s_hotel_h","hotel");
        Items.add(item);
        item = new MenuItems(4,"Shopping","drawable/ic_s_shop_h","shopping");
        Items.add(item);
        item = new MenuItems(5,"Suggested Itinerary","drawable/my_schedule_h","my_schedule");
        Items.add(item);
        item = new MenuItems(6,"About This App","drawable/ic_s_about_h","about_app");
        Items.add(item);
        btn = (ImageView)findViewById(R.id.slideButton);
        slidingDrawer = (SlidingDrawer)findViewById(R.id.slidingDrawer);
        slidingDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                btn.setImageResource(R.drawable.menu_pick_down);
            }
        });
        slidingDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                btn.setImageResource(R.drawable.menu_pick_up);
            }
        });

        linLay = (LinearLayout)findViewById(R.id.menuScrollLinear);
        linLay.post(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < Items.size(); i++) {
                    final int index = i;
                    newBtn = new Button(MainMap.this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(linLay.getHeight() - 20, LinearLayout.LayoutParams.MATCH_PARENT);
                    params.setMargins(5, 10, 5, 10);
                    newBtn.setLayoutParams(params);
                    newBtn.setText(" ");
                    String path = Items.get(index).imageSrc;
                    int mainImgResource = getResources().getIdentifier(path, null, getPackageName());
                    newBtn.setBackground(getResources().getDrawable(mainImgResource));
                    newBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent = GetIntent(Items.get(index).Title);
                            intent.putExtra("action",Items.get(index).tag);
                            startActivity(intent);
                        }
                    });
                    linLay.addView(newBtn);
                }
            }
        });
        if(extras!=null)
        {
            double lat,longt;
            lat = extras.getDouble("lat");
            longt = extras.getDouble("long");

            LatLng loc = new LatLng(lat,longt);
            mapView.getController().animateTo(loc);
        }
        else
        {
            String lang  = globalVariables.getApplicationLanguage();
            String[] files = {"data/"+lang+"/hotels.geojson", "data/"+lang+"/foodndrinks.geojson", "data/"+lang+"/attractions.geojson", "data/"+lang+"/shoppings.geojson"};
            Drawable[] drawables = {
                    getResources().getDrawable(R.drawable.hotel_marker),
                    getResources().getDrawable(R.drawable.food_marker),
                    getResources().getDrawable(R.drawable.attraction_marker),
                    getResources().getDrawable(R.drawable.shop_marker)
            };
            for(int i = 0; i < files.length; i++)
            {
                try {
                    FeatureCollection features = DataLoadingUtils.loadGeoJSONFromAssets(MainMap.this, files[i]);
                    ArrayList<Object> uiObjects = DataLoadingUtils.createUIObjectsFromGeoJSONObjects(features, null);

                    for (Object obj : uiObjects) {
                        if (obj instanceof Marker) {
                            Marker m = (Marker)obj;
                            m.setIcon(new Icon(drawables[i]));
                            mapView.addMarker(m);
                        } else if (obj instanceof PathOverlay) {
                            mapView.getOverlays().add((PathOverlay) obj);
                        }
                    }
                    if (uiObjects.size() > 0) {
                        mapView.invalidate();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }



        //Location Settings
        /*
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Marker m = new Marker(mapView,"Here", "Your Current Location",new LatLng(location.getLatitude(),location.getLongitude()));
                m.setIcon(new Icon(MainMap.this, Icon.Size.LARGE, "land-use", "00FF00"));
                mapView.addMarker(m);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        */
        //end

    }

    /**
     * Check current GPS setting
     * if GPS is OFF, let it turn ON
     *
     * @return
     *  false: Need to turn on GPS
     *  ture: Already GPS On
     *
     */

    //compass click
    public void CompassClick(View view)
    {
        mapView.setMapOrientation(0);
        compass.setRotation(0);
    }

    //Search
    public void Search(View view)
    {
        EditText searchText = (EditText)findViewById(R.id.search_text);
        if(searchText.getVisibility()==View.VISIBLE)
        {
            //search
            searchText.setVisibility(View.INVISIBLE);
        }
        else {
            searchText.setVisibility(View.VISIBLE);
        }
    }

    private boolean checkGpsSetting() {
        int gpsStatus = 0;

        try {
            gpsStatus = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        Log.d("GPS", "checkGpsSetting():LOCATION_MODE:" + gpsStatus);

        if (gpsStatus == 0) { // if LOCATION_MODE_OFF
            AlertDialog.Builder gsDialog = new AlertDialog.Builder(this);
            gsDialog.setTitle("Location Service Setting");
            gsDialog.setMessage("To find your correct position, you should turn on GPS.\nDo you want to turn on the GPS?");
            gsDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Go to the GPS setting
                    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    startActivity(intent);
                }
            })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    }).create().show();
            return false;

        } else {
            return true; // Already GPS ON
        }
    }

    /**
     * Start Location Service
     */
//    private void startLocationService() {
//
//        LocationManager manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//
//        GPSListener gpsListener = new GPSListener();
//        long minTime = 10000; // 10s
//        float minDistance = 0;
//
//        manager.requestLocationUpdates(
//                LocationManager.GPS_PROVIDER,
//                minTime,
//                minDistance,
//                gpsListener);
//
//        // Even though position is not confirmed, Find the recentest current position.
//
//        String msg = "LocationService Started!!!";
//        Log.d("GPS", "startLocationService():" + msg);
//    }

    /**
     * GPS Location Listener
     */
    private class GPSListener implements LocationListener {

        public void onLocationChanged(Location location) {

        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

    }

    private Intent GetIntent(String name)
    {
        Intent intent = null;
        switch (name)
        {
            case "Hotels":
                intent = new Intent(MainMap.this, ItemsListActivity.class);
                break;
            case "Attractions":
                intent = new Intent(MainMap.this, ItemsListActivity.class);
                break;
            case "About This App":
                intent = new Intent(MainMap.this, AboutAppActivity.class);
                break;
            case "Shopping":
                intent = new Intent(MainMap.this, ItemsListActivity.class);
                break;
            case "Food & Drink":
                intent = new Intent(MainMap.this, ItemsListActivity.class);
                break;
            case "About City":
                intent = new Intent(MainMap.this, AboutCityActivity.class);
                break;
            case "My Schedule":
                intent = new Intent(MainMap.this, MyScheduleActivity.class);
                break;
            case "Suggested Itinerary":
                intent = new Intent(MainMap.this, SuggestedItinerary.class);
                break;
            case "Train Timetable":
                intent = new Intent(MainMap.this, TrainsTimeTableActivity.class);
                break;
            case "Tashkent -> Samarkand":
                intent = new Intent(MainMap.this, TashkentSamarkandActivity.class);
                break;
        }
        return intent;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Load GeoJSON
//        GlobalsClass globals = (GlobalsClass)getApplicationContext();
//        String lang  = globals.getApplicationLanguage();
//        String[] files = {"data/"+lang+"/hotels.geojson", "data/"+lang+"/foodndrinks.geojson", "data/"+lang+"/attractions.geojson", "data/"+lang+"/shoppings.geojson"};
//        Drawable[] drawables = {
//            getResources().getDrawable(R.drawable.hotel_marker),
//            getResources().getDrawable(R.drawable.food_marker),
//            getResources().getDrawable(R.drawable.attraction_marker),
//            getResources().getDrawable(R.drawable.shop_marker)
//        };
//        for(int i = 0; i < files.length; i++)
//        {
//            try {
//                FeatureCollection features = DataLoadingUtils.loadGeoJSONFromAssets(MainMap.this, files[i]);
//                ArrayList<Object> uiObjects = DataLoadingUtils.createUIObjectsFromGeoJSONObjects(features, null);
//
//                for (Object obj : uiObjects) {
//                    if (obj instanceof Marker) {
//                        Marker m = (Marker)obj;
//                        m.setIcon(new Icon(drawables[i]));
//                        mapView.addMarker(m);
//                    } else if (obj instanceof PathOverlay) {
//                        mapView.getOverlays().add((PathOverlay) obj);
//                    }
//                }
//                if (uiObjects.size() > 0) {
//                    mapView.invalidate();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }
    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("app_properties.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
