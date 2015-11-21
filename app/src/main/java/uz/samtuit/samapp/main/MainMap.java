package uz.samtuit.samapp.main;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.cocoahero.android.geojson.FeatureCollection;
import com.mapbox.mapboxsdk.api.ILatLng;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.GpsLocationProvider;
import com.mapbox.mapboxsdk.overlay.Icon;
import com.mapbox.mapboxsdk.overlay.ItemizedIconOverlay;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.overlay.UserLocationOverlay;
import com.mapbox.mapboxsdk.tileprovider.tilesource.MBTilesLayer;
import com.mapbox.mapboxsdk.tileprovider.tilesource.TileLayer;
import com.mapbox.mapboxsdk.util.DataLoadingUtils;
import com.mapbox.mapboxsdk.views.MapView;
import com.mapbox.mapboxsdk.views.util.OnMapOrientationChangeListener;

import java.util.ArrayList;

import uz.samtuit.samapp.util.BitmapUtil;
import uz.samtuit.samapp.util.CustomDialog;
import uz.samtuit.samapp.util.GlobalsClass;
import uz.samtuit.samapp.util.MenuItems;
import uz.samtuit.samapp.util.SystemSetting;
import uz.samtuit.samapp.util.TourFeatureList;
import uz.samtuit.sammap.main.R;

import static uz.samtuit.samapp.util.GlobalsClass.FeatureType;


public class MainMap extends ActionBarActivity {
    private GlobalsClass globalVariables;
    private LinearLayout linLay;
    private Button newBtn;
    private ArrayList<MenuItems> Items = new ArrayList<MenuItems>();
    private ImageView btn,compass;
    private SlidingDrawer slidingDrawer;
    private MapView mapView;
    private boolean isSearchMyLocEnabled;
    private GpsLocationProvider mGpsLocProvider;
    private UserLocationOverlay myLocationOverlay;
    private TextView marqueeText;
    private Animation animGPS;
    private ImageView mAnimMyPosImage;
    private CustomDialog mGPSSettingDialog;
    private ArrayList<Drawable> markerDrawables;
    private NavigationView mNavigationView;
    private FrameLayout mainLayout;
    private SensorManager mSensorManager;
    private Location mDestinationLoc;
    private boolean isNavigationEnabled;
    private ProgressBar progressBar;
    private GeomagneticField geoField;
    private Marker pressedMarker;
    private boolean isNotified;

    private final int POPULATE_HOTELS_MARKERS = 100;
    private final int POPULATE_FOODS_MARKERS = 101;
    private final int POPULATE_ATTRACTIONS_MARKERS = 102;
    private final int POPULATE_SHOPS_MARKERS = 103;
    private final int POPULATE_ITINERARY_MARKERS = 104;
    private final int HANDLE_REQUEST = 105;
    private final int DRAW_MENU = 105;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        globalVariables = (GlobalsClass)getApplicationContext();

        setContentView(R.layout.activity_main_map);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mapView = (MapView)findViewById(R.id.mapview);
        mainLayout = (FrameLayout) findViewById(R.id.mainLayout);
        marqueeText = (TextView)findViewById(R.id.marqueeText);
        progressBar = (ProgressBar)findViewById(R.id.waitProgressBar);

        setMapView(); //Set MapView configuration

        setUserLocationOverlay(); //Set User Location Overlay

        setNavigationOverlay(); // Set Navigation Overlay

        drawBottomMenuIcons(); //Generate bottom Menu items

        setMarkerIcons();
        drawFeatures(FeatureType.ITINERARY);

        handleExtraRequest();
    }

    private void setMapView() {
        compass = (ImageView)findViewById(R.id.compass);

        TileLayer mbTileLayer = new MBTilesLayer(this, "samarkand.mbtiles");
        mapView.setTileSource(mbTileLayer);
        mapView.setCenter(new ILatLng() { // Registon
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
    }

    private void drawBottomMenuIcons() {
        MenuItems item = new MenuItems(0,"Itinerary Wizard","drawable/ic_s_about_city_h", MenuItems.MainMenu.ITINERARYWIZARD);
        Items.add(item);
        item = new MenuItems(1,"Suggested Itinerary","drawable/itnbtn_selector", MenuItems.MainMenu.ITINERARY);
        Items.add(item);
        item = new MenuItems(2,"Hotels","drawable/ic_s_hotel_h", MenuItems.MainMenu.HOTEL);
        Items.add(item);
        item = new MenuItems(3,"Food & Drink","drawable/ic_s_food_and_drink_h", MenuItems.MainMenu.FOODNDRINK);
        Items.add(item);
        item = new MenuItems(4,"Attractions","drawable/ic_s_attractions_h", MenuItems.MainMenu.ATTRACTION);
        Items.add(item);
        item = new MenuItems(5,"Shopping","drawable/ic_s_shop_h", MenuItems.MainMenu.SHOPPING);
        Items.add(item);
        item = new MenuItems(6,"About This App","drawable/setting", MenuItems.MainMenu.SETTING);
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
                            intent = GetActivityName(Items.get(index).mainMenu);
                            intent.putExtra("action", Items.get(index).mainMenu.toString());
                            startActivity(intent);
                        }
                    });
                    linLay.addView(newBtn);
                }
            }
        });
    }

    private void setMarkerIcons() {
        markerDrawables = new ArrayList<Drawable>();
        markerDrawables.add(FeatureType.HOTEL.ordinal(), getResources().getDrawable(R.drawable.hotel_marker));
        markerDrawables.add(FeatureType.FOODNDRINK.ordinal(), getResources().getDrawable(R.drawable.food_marker));
        markerDrawables.add(FeatureType.ATTRACTION.ordinal(), getResources().getDrawable(R.drawable.attraction_marker));
        markerDrawables.add(FeatureType.SHOPPING.ordinal(), getResources().getDrawable(R.drawable.shop_marker));
    }

    private void drawFeatures(FeatureType featureType) {
        PopulateMarkers populateMarkers = new PopulateMarkers();
        populateMarkers.execute(featureType);
    }

    public void onTooltipBtnClick(View v){
        pressedMarker.closeToolTip();

        if (SystemSetting.checkGPSStatus(MainMap.this) == 0) { // If GPS is OFF
            mGPSSettingDialog = new CustomDialog(MainMap.this,
                    R.string.title_dialog_gps_setting,
                    R.string.dialog_gps_setting,
                    R.string.yes,
                    R.string.no,
                    yesClickListener,
                    noClickListener);
            mGPSSettingDialog.show();
        } else {
            isSearchMyLocEnabled = true;
            animGPS = AnimationUtils.loadAnimation(MainMap.this, R.anim.scale);
            mAnimMyPosImage.startAnimation(animGPS);
            marqueeText.setText(getString(R.string.marquee_searching_target) + pressedMarker.getTitle());

            mDestinationLoc.setLongitude(pressedMarker.getPosition().getLongitude());
            mDestinationLoc.setLatitude(pressedMarker.getPosition().getLatitude());

            // Indicate the marker as destination and turn on the navigation
            setNavigationEnable(true);

            // When selected other destination, init a distance
            mNavigationView.setDistance(0);
            mNavigationView.invalidate();
        }
    }

    private void setUserLocationOverlay() {
        mGpsLocProvider = new GpsLocationProvider(this){
            @Override
            public void onLocationChanged(Location location) {
                super.onLocationChanged(location);

                //Show icon animation until my location is recognized by first GPS signal
                if (isSearchMyLocEnabled || isNavigationEnabled) {
                    myLocationOverlay.goToMyPosition(true);
                    mAnimMyPosImage.clearAnimation(); //Stop icon animation
                    if (isSearchMyLocEnabled && !isSearchMyLocEnabled) {
                        isSearchMyLocEnabled = false;
                        marqueeText.setText("");
                    } else if (isSearchMyLocEnabled && isNavigationEnabled) {
                        isSearchMyLocEnabled = false;
                        marqueeText.setText(getString(R.string.marquee_guiding) + pressedMarker.getTitle());
                    }
                }

                // Check the value of mDestinationLoc
                // Because the distanceTo function has default WGS84 major axis, always some value will be returned
                if (isNavigationEnabled && (mDestinationLoc.getLongitude() != 0 && mDestinationLoc.getLatitude() != 0)) {
                    // declination: the difference between true north and magnetic north
                    geoField = new GeomagneticField(
                            Double.valueOf(location.getLatitude()).floatValue(),
                            Double.valueOf(location.getLongitude()).floatValue(),
                            Double.valueOf(location.getAltitude()).floatValue(),
                            System.currentTimeMillis()
                    );
                    mNavigationView.setDeclination(geoField.getDeclination());
                    mNavigationView.setDistance(location.distanceTo(mDestinationLoc));
                    mNavigationView.setBearing(location.bearingTo(mDestinationLoc));

                    // Notify one time as the value of Settings menu when I arrived near the destination
                    if (location.distanceTo(mDestinationLoc) < 70) { // 70m
                        marqueeText.setEnabled(true);
                        marqueeText.setTextColor(Color.RED);
                        marqueeText.setText(R.string.marquee_arrival_noti);

                        if (!isNotified) {
                            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(500);

                            SoundPool soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
                            int soundId = soundPool.load(getApplicationContext(), R.raw.dingdong, 1);
                            soundPool.play(soundId, 1, 1, 0, 0, 1);

                            isNotified = true;
                        }
                    } else {
                        marqueeText.setEnabled(false);
                    }
                }
            }

            @Override
            public void onProviderDisabled(String provider) { // When turn off GPS by shortcut
                super.onProviderDisabled(provider);
                setSearchMyLocEnabled(false);
                setNavigationEnable(false);
            }

            @Override
            public void onProviderEnabled(String provider) { // When turn on GPS by shortcut
                super.onProviderEnabled(provider);
                setSearchMyLocEnabled(true);
            }
        };

        // Too often updates will consume too much battery
        mGpsLocProvider.setLocationUpdateMinTime(5000); //5s
        mGpsLocProvider.setLocationUpdateMinDistance(5); //5m

        myLocationOverlay = new UserLocationOverlay(mGpsLocProvider, mapView);
        mAnimMyPosImage = (ImageView)findViewById(R.id.myPositon);
    }

    private void setNavigationOverlay() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mNavigationView = new NavigationView(this);
        mainLayout.addView(mNavigationView);
        mNavigationView.setVisibility(View.INVISIBLE);
        mDestinationLoc = new Location(LocationManager.GPS_PROVIDER);
    }

    private void setSearchMyLocEnabled(boolean enabled) {
        isSearchMyLocEnabled = enabled;

        if (enabled) {
            //Show icon animation until my location is recognized by first GPS signal
            myLocationOverlay.goToMyPosition(true);
            animGPS = AnimationUtils.loadAnimation(this, R.anim.scale);
            mAnimMyPosImage.startAnimation(animGPS);
            marqueeText.setText(R.string.marquee_searching_you);

            if(isNavigationEnabled) {
                mSensorManager.registerListener(mListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_NORMAL);
            }
        } else {
            mAnimMyPosImage.clearAnimation();
            marqueeText.setText("");

            if(isNavigationEnabled) {
                mSensorManager.unregisterListener(mListener);
            }
        }
    }

    //myLocation button click
    public void myLocationClick(View view)
    {
        if(SystemSetting.checkGPSStatus(this) == 0) { // If GPS is OFF
            mGPSSettingDialog = new CustomDialog(this,
                    R.string.title_dialog_gps_setting,
                    R.string.dialog_gps_setting,
                    R.string.yes,
                    R.string.no,
                    yesClickListener,
                    noClickListener);
            mGPSSettingDialog.show();
        } else {
            setSearchMyLocEnabled(true);
        }
    }

    // GPS Setting Dialog
    private View.OnClickListener yesClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mGPSSettingDialog.dismiss();

            // Go to the GPS setting
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            startActivity(intent);
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

    private void setNavigationEnable(boolean isEnabled) {
        isNavigationEnabled = isEnabled;

        ToggleButton toggleButton = (ToggleButton)findViewById(R.id.naviToggleBtn);
        toggleButton.setChecked(isNavigationEnabled);

        if(isNavigationEnabled) {
            mNavigationView.setVisibility(View.VISIBLE);
            mSensorManager.registerListener(mListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_NORMAL);

            if (mDestinationLoc.getLongitude() == 0 && mDestinationLoc.getLatitude() == 0) {
                Toast.makeText(MainMap.this, R.string.toast_select_destination, Toast.LENGTH_LONG).show();
                marqueeText.setText(R.string.toast_select_destination);
            } else {
                if (!isSearchMyLocEnabled) {// In case the GPS is not working
                    marqueeText.setText(getString(R.string.marquee_guiding) + pressedMarker.getTitle());
                }
            }
        } else {
            mNavigationView.setVisibility(View.INVISIBLE);
            mSensorManager.unregisterListener(mListener);
            if (!isSearchMyLocEnabled) {
                marqueeText.setText("");
            }
            mDestinationLoc.setLongitude(0);
            mDestinationLoc.setLatitude(0);
        }
    }

    public void onNaviToggleClick(View view) {
        setNavigationEnable(!isNavigationEnabled);
    }

    private Intent GetActivityName(MenuItems.MainMenu mainMenu)
    {
        Intent intent = null;
        switch (mainMenu)
        {
            case ITINERARYWIZARD:
                intent = new Intent(MainMap.this, DaySelectWizardActivity.class);
                break;
            case ITINERARY:
                intent = new Intent(MainMap.this, SuggestedItineraryActivity.class);
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
        }
        return intent;
    }

    private void handleExtraRequest() {
        Bundle extras = getIntent().getExtras();
        if (extras!=null) {
            switch (extras.getString("type")){
                case "features":
                    drawFeatures(FeatureType.valueOf(extras.getString("featureType")));
                    break;

                case "feature":
                    String name = extras.getString("name");

                    double lat = 0 ,longt = 0;
                    lat = extras.getDouble("lat");
                    longt = extras.getDouble("long");
                    LatLng loc = new LatLng(lat, longt);

                    FeatureType featureType = FeatureType.valueOf(extras.getString("featureType"));
                    Marker marker = new Marker(name, "", loc);
                    marker.setIcon(new Icon(markerDrawables.get(featureType.ordinal())));

                    mapView.addMarker(marker);
                    mapView.getController().animateTo(loc);
                    break;
            }
        }
    }

    private final SensorEventListener mListener = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}

        public void onSensorChanged(SensorEvent event) {
            if (isNavigationEnabled && mNavigationView.hasDistance()) { // Start navigation when the value of distance is
                mNavigationView.setAzimuth(event.values[0]);
                mNavigationView.invalidate();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        if(SystemSetting.checkGPSStatus(this) != 0) { // If GPS is ON, Always indicate my location on the map
            myLocationOverlay.enableMyLocation();
            myLocationOverlay.setDrawAccuracyEnabled(true);
            myLocationOverlay.setOverlayIndex(5); // To make be layers like, 0: Map, 3:TourFeatures, 4:Itinerary, 5:MyLocation
            mapView.getOverlays().add(myLocationOverlay);

            if(isSearchMyLocEnabled) {
                setSearchMyLocEnabled(true);
            } else if (isNavigationEnabled) {
                setNavigationEnable(true);
            }
        } else { // GPS is OFF
            setSearchMyLocEnabled(false);
            setNavigationEnable(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        myLocationOverlay.disableMyLocation(); // Don't forget to prevent battery leak.
        setSearchMyLocEnabled(false);
    }

    class PopulateMarkers extends AsyncTask<FeatureType, Pair<FeatureType,Object>, Void>{

        ArrayList<Marker> itineraryMarkers = new ArrayList<Marker>();
        ArrayList<Marker> featuresMarkers = new ArrayList<Marker>();
        FeatureType markersFeatureType = FeatureType.NONE;
        FeatureCollection features = null;
        Marker marker = null;
        int index = 0;

        @Override
        protected Void doInBackground(FeatureType... params) {
            String path = GlobalsClass.GeoJSONFileName[params[0].ordinal()];
            String lang = getApplicationContext().getSharedPreferences("SamTour_Pref", 0).getString("app_lang", null);
            markersFeatureType = params[0];

            try {
                features = TourFeatureList.loadGeoJSONFromExternalFilesDir(MainMap.this, lang + path);
                ArrayList<Object> uiObjects = DataLoadingUtils.createUIObjectsFromGeoJSONObjects(features, null);
                for (Object obj : uiObjects) {
                    publishProgress(new Pair<FeatureType, Object>(markersFeatureType, obj));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Pair<FeatureType, Object>... values) {
            super.onProgressUpdate(values);

            marker = (Marker)values[0].second;
            String title = null;

            if (values[0].first == FeatureType.ITINERARY) {
                title = globalVariables.getItineraryFeatures().get(index).getString("name");
                BitmapUtil.BitmapWithText markerimg = new BitmapUtil.BitmapWithText(MainMap.this, new Integer(++index).toString(), R.drawable.poi_bg);
                marker.setMarker(markerimg);
                marker.setToolTip(new CustomInfoWindow(MainMap.this, mapView, index-1)); // Set as array index
                marker.setTitle(title);
                mapView.addMarker(marker);
                itineraryMarkers.add(marker); // For adding as ItemizedIconOverlay
            }
            else { // TourFeatures
                title = globalVariables.getTourFeatures(values[0].first).get(index++).getString("name");
                marker.setMarker(markerDrawables.get(values[0].first.ordinal()));
                marker.setTitle(title);
                mapView.addMarker(marker);
                featuresMarkers.add(marker); // For adding as ItemizedIconOverlay
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            ArrayList markers =  null;
            int overlayIndex = 0;

            if (markersFeatureType == FeatureType.ITINERARY) {
                markers = itineraryMarkers;
                overlayIndex = 4;
            } else {
                markers = featuresMarkers;
                overlayIndex = 3;
            }

            ItemizedIconOverlay iOverlay = new ItemizedIconOverlay(MainMap.this, markers, new ItemizedIconOverlay.OnItemGestureListener<Marker>() {
                @Override
                public boolean onItemSingleTapUp(int i, Marker marker) {
                    pressedMarker = marker;
                    return false;
                }

                @Override
                public boolean onItemLongPress(int index, Marker item) {
                    return false;
                }
            });

            iOverlay.setOverlayIndex(overlayIndex); // To make be layers like, 0: Map, 3:TourFeatures, 4:Itinerary, 5:MyLocation
            mapView.addItemizedOverlay(iOverlay);

            progressBar.setVisibility(View.GONE);
        }
    }
}
