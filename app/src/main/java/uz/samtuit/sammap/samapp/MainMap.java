package uz.samtuit.sammap.samapp;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.HorizontalScrollView;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class MainMap extends ActionBarActivity {
    private LinearLayout linLay,vertical;
    private HorizontalScrollView hscv;
    private Button newBtn;
    private ArrayList<MenuItems> Items = new ArrayList<MenuItems>();
    private ImageView btn;
    private SlidingDrawer slidingDrawer;
    private boolean updateAvailable = true;
    private int height;
    private MapView mapView;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Double longitude, latitude;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //MapView Settings
        mapView = (MapView)findViewById(R.id.mapview);

        TileLayer mbTileLayer = new MBTilesLayer(this, "samarkand_20150727.mbtiles");
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

        checkGpsSetting();
        mapView.setUserLocationEnabled(true);
        mapView.setZoom(18);
        mapView.setMapRotationEnabled(true);
        //end

        //startLocationService();

        Bundle extras = getIntent().getExtras();
        if(extras!=null)
        {
            double lat,longt;
            lat = extras.getDouble("lat");
            longt = extras.getDouble("long");
            LatLng loc = new LatLng(lat,longt);
            mapView.getController().goTo(loc, null);
            Marker m = new Marker(extras.getString("name"),"Best Hotel",new LatLng(lat,longt));
            mapView.addMarker(m);
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
        item = new MenuItems(1,"Attractions","drawable/attractions");
        Items.add(item);
        item = new MenuItems(2,"Food & Drink","drawable/food_drink");
        Items.add(item);
        item = new MenuItems(3,"Hotels","drawable/hotel");
        Items.add(item);
        item = new MenuItems(4,"Shopping","drawable/shop");
        Items.add(item);
        item = new MenuItems(5,"My Schedule","drawable/my_schuld");
        Items.add(item);
        item = new MenuItems(6,"Itinerary Wizard","drawable/itinerary");
        Items.add(item);
        item = new MenuItems(7,"Train Timetable","drawable/trains");
        Items.add(item);
        item = new MenuItems(8,"Tashkent -> Samarkand","drawable/shop");
        Items.add(item);
        item = new MenuItems(9,"About This App","drawable/about_app");
        Items.add(item);
        btn = (ImageView)findViewById(R.id.slideButton);
        slidingDrawer = (SlidingDrawer)findViewById(R.id.slidingDrawer);
        slidingDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                btn.setBackgroundResource(R.drawable.menu_down_arrow);
            }
        });
        slidingDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                btn.setBackgroundResource(R.drawable.menu_up_arrow);
            }
        });

        Animation fadeIn = AnimationUtils.loadAnimation(MainMap.this,R.anim.fade_in);
        btn.startAnimation(fadeIn);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                Animation fadeOut = AnimationUtils.loadAnimation(MainMap.this, R.anim.fade_out);
                btn.startAnimation(fadeOut);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
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
    private void startLocationService() {

        LocationManager manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        GPSListener gpsListener = new GPSListener();
        long minTime = 10000; // 10s
        float minDistance = 0;

        manager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                minTime,
                minDistance,
                gpsListener);

        // Even though position is not confirmed, Find the recentest current position.
        try {
            Location lastLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation != null) {
                latitude = lastLocation.getLatitude();
                longitude = lastLocation.getLongitude();

                String msg = "Latitude : "+ latitude + "\nLongitude:"+ longitude;
                Log.i("GPS", "startLocationService():lastLocation:" + msg);
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        String msg = "LocationService Started!!!";
        Log.d("GPS", "startLocationService():" + msg);
    }

    /**
     * GPS Location Listener
     */
    private class GPSListener implements LocationListener {

        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            String msg = "Latitude : "+ latitude + "\nLongitude:"+ longitude;
            Log.d("GPS", "GPSListener():onLocationChanged():" + msg);

        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

    }

    public static String readAsset(AssetManager mgr, String path) {
        String contents = "";
        InputStream is = null;
        BufferedReader reader = null;
        try {
            is = mgr.open(path);
            reader = new BufferedReader(new InputStreamReader(is));
            contents = reader.readLine();
            String line = null;
            while ((line = reader.readLine()) != null) {
                contents += '\n' + line;
            }
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                }
            }
        }
        return contents;
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
            case "Itinerary Wizard":
                intent = new Intent(MainMap.this, ItineraryWizardActivity.class);
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

    public void HotelsRun() {
        Intent hotels = new Intent(MainMap.this,HotelsActivity.class);
        startActivity(hotels);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Load GeoJSON
        try {
            FeatureCollection features = DataLoadingUtils.loadGeoJSONFromAssets(MainMap.this, "Hotel_20150728.geojson");
            ArrayList<Object> uiObjects = DataLoadingUtils.createUIObjectsFromGeoJSONObjects(features, null);

            for (Object obj : uiObjects) {
                if (obj instanceof Marker) {
                    ((Marker) obj).setIcon(new Icon(MainMap.this,Icon.Size.SMALL, "city", "#00ff00"));
                            mapView.addMarker((Marker) obj);
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
