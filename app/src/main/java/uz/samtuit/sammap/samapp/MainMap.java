package uz.samtuit.sammap.samapp;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;

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

import java.util.ArrayList;


public class MainMap extends ActionBarActivity {
    private LinearLayout linLay;
    private Button newBtn;
    private ArrayList<MenuItems> Items = new ArrayList<MenuItems>();
    private ImageView btn,compass;
    private SlidingDrawer slidingDrawer;
    private boolean updateAvailable = true;
    private int height;
    private MapView mapView;
    private EditText searchText;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //search text typeface
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");
        searchText = (EditText)findViewById(R.id.search_text);
        searchText.setTypeface(tf);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //MapView Settings
        mapView = (MapView)findViewById(R.id.mapview);
        compass = (ImageView)findViewById(R.id.compass);

        TileLayer mbTileLayer = new MBTilesLayer(this, "Sample.mbtiles");
        mapView.setTileSource(mbTileLayer);
        mapView.setMinZoomLevel(mapView.getTileProvider().getMinimumZoomLevel());
        mapView.setMaxZoomLevel(mapView.getTileProvider().getMaximumZoomLevel());
        mapView.setCenter(mapView.getTileProvider().getCenterCoordinate());

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
        Bundle extras = getIntent().getExtras();
        if(extras!=null)
        {
            double lat,longt;
            lat = extras.getDouble("lat");
            longt = extras.getDouble("long");
            LatLng loc = new LatLng(lat,longt);
//            mapView.getController().goTo(loc, null);
            mapView.getController().animateTo(loc);
        }else
        {
            //Action when Update is Available
            if(updateAvailable)
            {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent splash = new Intent(MainMap.this, Splash.class);
                        startActivity(splash);

                    }
                }, 0);
                Log.e("Error","None of time");
            }
            //End
        }


        //generate Menu items
        MenuItems item = new MenuItems(0,"About City","drawable/about_city");
        Items.add(item);
        item = new MenuItems(1,"Attractions","drawable/ic_attraction");
        Items.add(item);
        item = new MenuItems(2,"Food & Drink","drawable/ic_foodanddrink");
        Items.add(item);
        item = new MenuItems(3,"Hotels","drawable/ic_hotel");
        Items.add(item);
        item = new MenuItems(4,"Shopping","drawable/ic_shop");
        Items.add(item);
        item = new MenuItems(5,"Suggested Itinerary","drawable/itinerary");
        Items.add(item);
        item = new MenuItems(6,"About This App","drawable/about_app");
        Items.add(item);
        btn = (ImageView)findViewById(R.id.slideButton);
        slidingDrawer = (SlidingDrawer)findViewById(R.id.slidingDrawer);
        slidingDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                btn.setRotation(180);
            }
        });
        slidingDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                btn.setRotation(0);
            }
        });

        linLay = (LinearLayout)findViewById(R.id.menuScrollLinear);
        linLay.post(new Runnable() {
            @Override
            public void run() {
                Log.e("TEST", "SIZE: " + linLay.getHeight());
                height = linLay.getHeight();
                for (int i = 0; i < Items.size(); i++) {
                    final int index = i;
                    newBtn = new Button(MainMap.this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(height - 20, LinearLayout.LayoutParams.MATCH_PARENT);
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
                            startActivity(intent);
                        }
                    });
                    linLay.addView(newBtn);
                }
            }
        });



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
                intent = new Intent(MainMap.this, HotelsActivity.class);
                break;
            case "Attractions":
                intent = new Intent(MainMap.this, AttractionsActivity.class);
                break;
            case "About This App":
                intent = new Intent(MainMap.this, AboutCityActivity.class);
                break;
            case "Shopping":
                intent = new Intent(MainMap.this, ShoppingActivity.class);
                break;
            case "Food & Drink":
                intent = new Intent(MainMap.this, FoodAndDrinkActivity.class);
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
        String[] files = {"hotels_0_001.geojson", "foodanddrinks_0_001.geojson", "attractions_0_001.geojson","shops_0_001.geojson"};
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
}
