package uz.samtuit.samapp.main;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;

import com.mapbox.mapboxsdk.api.ILatLng;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.GpsLocationProvider;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.overlay.UserLocationOverlay;
import com.mapbox.mapboxsdk.tileprovider.tilesource.MBTilesLayer;
import com.mapbox.mapboxsdk.tileprovider.tilesource.TileLayer;
import com.mapbox.mapboxsdk.views.MapView;
import com.mapbox.mapboxsdk.views.util.OnMapOrientationChangeListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

import uz.samtuit.samapp.util.CustomDialog;
import uz.samtuit.sammap.main.R;


public class MainMap extends ActionBarActivity {
    private LinearLayout linLay;
    private Button newBtn;
    private ArrayList<MenuItems> Items = new ArrayList<MenuItems>();
    private ImageView btn,compass;
    private SlidingDrawer slidingDrawer;
    private MapView mapView;
    private GpsLocationProvider mGpsLocProvider;
    private UserLocationOverlay myLocationOverlay;
    private EditText searchText;
    private static boolean isPressedMyPosBtn = true;
    private Animation anim;
    private ImageView mAnimMyPosImage;
    private CustomDialog mGPSSettingDialog;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        final GlobalsClass globalVariables = (GlobalsClass)getApplicationContext();

        Locale locale = new Locale(globalVariables.getApplicationLanguage());
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        getBaseContext().setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main_map);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mapView = (MapView)findViewById(R.id.mapview);

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

        mapView.setZoom(18);
        mapView.setMapRotationEnabled(true);
        mapView.setOnMapOrientationChangeListener(new OnMapOrientationChangeListener() {
            @Override
            public void onMapOrientationChange(float v) {
                    compass.setRotation(mapView.getMapOrientation());
            }
        });

        mGpsLocProvider = new GpsLocationProvider(this){
            @Override
            public void onLocationChanged(Location location) {
                super.onLocationChanged(location);

                if (isPressedMyPosBtn) {
                    myLocationOverlay.goToMyPosition(true);
                    mAnimMyPosImage.clearAnimation(); //Stop icon animation
                    isPressedMyPosBtn = false;
                }
            }

            @Override
            public void onProviderDisabled(String provider) { // When turn off GPS
                super.onProviderDisabled(provider);
                mAnimMyPosImage.clearAnimation();
            }
        };

        // Too often updates will consume too much battery
        mGpsLocProvider.setLocationUpdateMinTime(5000); //5s
        mGpsLocProvider.setLocationUpdateMinDistance(5); //5m

        myLocationOverlay = new UserLocationOverlay(mGpsLocProvider, mapView);
        mAnimMyPosImage = (ImageView)findViewById(R.id.myPositon);

        //generate bottom Menu items
        MenuItems item = new MenuItems(5,"About City","drawable/ic_s_about_city_h", MenuItems.MainMenu.ITINERARYWIZARD);
        Items.add(item);
        item = new MenuItems(6,"Suggested Itinerary","drawable/my_schedule_h", MenuItems.MainMenu.ITINERARY);
        Items.add(item);
        item = new MenuItems(1,"Hotels","drawable/ic_s_hotel_h", MenuItems.MainMenu.HOTEL);
        Items.add(item);
        item = new MenuItems(2,"Food & Drink","drawable/ic_s_food_and_drink_h", MenuItems.MainMenu.FOODNDRINK);
        Items.add(item);
        item = new MenuItems(3,"Attractions","drawable/ic_s_attractions_h", MenuItems.MainMenu.ATTRACTION);
        Items.add(item);
        item = new MenuItems(4,"Shopping","drawable/ic_s_shop_h", MenuItems.MainMenu.SHOPPING);
        Items.add(item);
        item = new MenuItems(7,"About This App","drawable/ic_s_about_h", MenuItems.MainMenu.SETTING);
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
                            intent = GetIntent(Items.get(index).mainMenu);
                            intent.putExtra("action", Items.get(index).mainMenu.toString());
                            startActivity(intent);
                        }
                    });
                    linLay.addView(newBtn);
                }
            }
        });

        Drawable[] markerDrawables = {
                getResources().getDrawable(R.drawable.hotel_marker),
                getResources().getDrawable(R.drawable.food_marker),
                getResources().getDrawable(R.drawable.attraction_marker),
                getResources().getDrawable(R.drawable.shop_marker)
        };

        Bundle extras = getIntent().getExtras();
        if(extras!=null)
        {
            switch (extras.getString("type")){
                case "itinerary":
                    globalVariables.getItineraryFeatures();
                    break;

                case "feature":
                    String name = extras.getString("name");

                    double lat = 0 ,longt = 0;
                    lat = extras.getDouble("lat");
                    longt = extras.getDouble("long");
                    LatLng loc = new LatLng(lat,longt);

                    int featureType = extras.getInt("featureType");

                    Marker m = new Marker(name, "", loc);
                    m.setImage(markerDrawables[featureType]);

                    mapView.addMarker(m);
                    mapView.getController().animateTo(loc);
                    break;
            }
        }
        else
        {

/*
                try {
                    FeatureCollection features = DataLoadingUtils.loadGeoJSONFromAssets(MainMap.this, "");
                    ArrayList<Object> uiObjects = DataLoadingUtils.createUIObjectsFromGeoJSONObjects(features, null);

                    for (Object obj : uiObjects) {
                        if (obj instanceof Marker) {
                            Marker m = (Marker)obj;
                            m.setIcon(new Icon(markerDrawables[i]));
                            mapView.addMarker(m);
                        } else if (obj instanceof PathOverlay) {
                            mapView.getOverlays().add((PathOverlay) obj);
                        }
                    }
                    if (uiObjects.size() > 0) {
                        //mapView.invalidate();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
*/
        }
    }

    private int checkGPSStatus() {
        int gpsStatus = 0;

        try {
            // LOCATION_MODE_HIGH_ACCURACY=3, LOCATION_MODE_BATTERY_SAVING=2, LOCATION_MODE_SENSORS_ONLY=1 or LOCATION_MODE_OFF=0.
            gpsStatus = Settings.Secure.getInt(this.getContentResolver(), Settings.Secure.LOCATION_MODE);

            Log.d("GPS", "isGPSEnabled():LOCATION_MODE:" + gpsStatus);

        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        return gpsStatus;
    }

    //myPosition button click
    public void myPositionClick(View view)
    {
        if(checkGPSStatus() == 0) { // If GPS is OFF
            mGPSSettingDialog = new CustomDialog(this,
                    R.string.title_dialog_gps_setting,
                    R.string.dialog_gps_setting,
                    R.string.yes,
                    R.string.no,
                    yesClickListener,
                    noClickListener);
            mGPSSettingDialog.show();
        } else {
            myLocationOverlay.goToMyPosition(true);
            isPressedMyPosBtn = true;
        }
    }

    private View.OnClickListener yesClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mGPSSettingDialog.dismiss();

            // Go to the GPS setting
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            startActivity(intent);

            myLocationOverlay.goToMyPosition(true);
            isPressedMyPosBtn = true;
        }
    };

    private View.OnClickListener noClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mGPSSettingDialog.dismiss();
        }
    };

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

    private Intent GetIntent(MenuItems.MainMenu mainMenu)
    {
        Intent intent = null;
        switch (mainMenu)
        {
            case ITINERARYWIZARD:
                intent = new Intent(MainMap.this, AboutCityActivity.class);
                break;
            case ITINERARY:
                intent = new Intent(MainMap.this, SuggestedItinerary.class);
                break;
            case HOTEL:
            case FOODNDRINK:
            case ATTRACTION:
            case SHOPPING:
                intent = new Intent(MainMap.this, ItemsListActivity.class);
                break;
            case SETTING:
                intent = new Intent(MainMap.this, AboutAppActivity.class);
                break;
//            case "My Schedule":
//                intent = new Intent(MainMap.this, MyScheduleActivity.class);
//                break;
//            case "Train Timetable":
//                intent = new Intent(MainMap.this, TrainsTimeTableActivity.class);
//                break;
//            case "Tashkent -> Samarkand":
//                intent = new Intent(MainMap.this, TashkentSamarkandActivity.class);
//                break;
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

        if(checkGPSStatus() != 0) { // If GPS is ON
            myLocationOverlay.enableMyLocation();
            myLocationOverlay.setDrawAccuracyEnabled(true);
            mapView.getOverlays().add(myLocationOverlay);

            if(isPressedMyPosBtn) {
                //Show icon animation until first GPS signal is enough to recognize the my location
                anim = AnimationUtils.loadAnimation(this, R.anim.scale);
                mAnimMyPosImage.startAnimation(anim);
            }
        } else {
            mAnimMyPosImage.clearAnimation();
        }

        mapView.invalidate();
    }

    @Override
    protected void onPause() {
        super.onPause();
        myLocationOverlay.disableMyLocation(); // Don't forget to prevent battery leak.
        mAnimMyPosImage.clearAnimation();
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
