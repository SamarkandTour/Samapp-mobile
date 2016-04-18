package uz.samtuit.samapp.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.mapbox.mapboxsdk.api.ILatLng;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.GpsLocationProvider;
import com.mapbox.mapboxsdk.overlay.ItemizedIconOverlay;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.overlay.Overlay;
import com.mapbox.mapboxsdk.overlay.UserLocationOverlay;
import com.mapbox.mapboxsdk.tileprovider.tilesource.MBTilesLayer;
import com.mapbox.mapboxsdk.tileprovider.tilesource.TileLayer;
import com.mapbox.mapboxsdk.util.NetworkUtils;
import com.mapbox.mapboxsdk.views.MapView;
import com.mapbox.mapboxsdk.views.util.OnMapOrientationChangeListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


import uz.samtuit.samapp.util.BitmapUtil;
import uz.samtuit.samapp.util.CustomDialog;
import uz.samtuit.samapp.util.Downloader;
import uz.samtuit.samapp.util.GlobalsClass;
import uz.samtuit.samapp.util.MenuItems;
import uz.samtuit.samapp.util.SystemBarTintManager;
import uz.samtuit.samapp.util.SystemSetting;
import uz.samtuit.samapp.util.TourFeature;
import uz.samtuit.samapp.util.TypefaceHelper;

import static uz.samtuit.samapp.util.GlobalsClass.FeatureType;
import static uz.samtuit.samapp.util.TourFeatureList.findFeatureByName;


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
    private AnimationDrawable animGPS;
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
    private Timer gpsTimer;
    private CustomDialog mMapDownloadDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_map);
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);
        tintManager.setStatusBarTintColor(Color.WHITE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        globalVariables = (GlobalsClass)getApplicationContext();
        mapView = (MapView)findViewById(R.id.mapview);
        mainLayout = (FrameLayout) findViewById(R.id.mainLayout);
        marqueeText = (TextView)findViewById(R.id.marqueeText);
        progressBar = (ProgressBar)findViewById(R.id.waitProgressBar);
        ImageView mAnimMyPosImage = (ImageView) findViewById(R.id.myPositon);
        animGPS = (AnimationDrawable) mAnimMyPosImage.getBackground();

        setMapView(); //Set MapView configuration

        setUserLocationOverlay(); //Set User Location Overlay

        setNavigationOverlay(); // Set Navigation Overlay

        drawBottomMenuIcons(); //Generate bottom Menu items

        setMarkerIcons();
        drawFeatures(FeatureType.ITINERARY, null);
    }

    private View.OnClickListener yesDownClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ArrayList<String> URLArrayList = new ArrayList<String>();

            mMapDownloadDialog.dismiss();

            if (NetworkUtils.isNetworkAvailable(MainMap.this)) {
                // Start to download at Background
                URLArrayList.add(GlobalsClass.mapDownloadURL);
                Downloader downloader = new Downloader(URLArrayList);
                downloader.startDownload(MainMap.this, "Sam Tour", "Map Database");
            } else {
                Toast.makeText(MainMap.this, R.string.Err_no_connection, Toast.LENGTH_LONG).show();
            }
        }
    };

    private View.OnClickListener noDownClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mMapDownloadDialog.dismiss();
        }
    };

    private void setMapView() {
        TileLayer mbTileLayer = null;

        compass = (ImageView)findViewById(R.id.compass);
        File mbtiles = new File(this.getExternalFilesDir(null), GlobalsClass.mapFileName);

        if (!mbtiles.exists()) { // If no mbtiles in ExternalDir, require to download map file from server

            if (!Downloader.isAlreadyDownloadRequest(this)) {
                mMapDownloadDialog = new CustomDialog(this,
                        R.string.title_dialog_map_download,
                        R.string.dialog_need_map_download,
                        R.string.btn_no,
                        R.string.btn_yes,
                        noDownClickListener,
                        yesDownClickListener);
                mMapDownloadDialog.show();
            }

            mbTileLayer = new MBTilesLayer(this, GlobalsClass.mapFileName);
        } else {
            mbTileLayer = new MBTilesLayer(mbtiles);
        }

        mbTileLayer.setAttribution("© OpenStreetMap Contributors");

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

    private void setTypefaces(){
        marqueeText.setTypeface(TypefaceHelper.getTypeface(getApplicationContext(), "RobotoCondensed-Regular"));
    }

    private void drawBottomMenuIcons() {
        MenuItems item = new MenuItems(0,"Itinerary Wizard","drawable/itinerary_wizard", MenuItems.MainMenu.ITINERARYWIZARD);
        Items.add(item);
        item = new MenuItems(1,"Suggested Itinerary","drawable/itinerary", MenuItems.MainMenu.ITINERARY);
        Items.add(item);
        item = new MenuItems(2,"Hotels","drawable/hotel", MenuItems.MainMenu.HOTEL);
        Items.add(item);
        item = new MenuItems(3,"Food & Drinks","drawable/food", MenuItems.MainMenu.FOODNDRINK);
        Items.add(item);
        item = new MenuItems(4,"Attractions","drawable/attraction", MenuItems.MainMenu.ATTRACTION);
        Items.add(item);
        item = new MenuItems(5,"Shopping","drawable/shop", MenuItems.MainMenu.SHOPPING);
        Items.add(item);
        item = new MenuItems(6,"About This App","drawable/settings", MenuItems.MainMenu.SETTING);
        Items.add(item);
        btn = (ImageView)findViewById(R.id.slideButton);

        Display display= ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();

        slidingDrawer = (SlidingDrawer)findViewById(R.id.slidingDrawer);
        slidingDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
               // btn.setAlpha(Color.GRAY);
                btn.setImageResource(R.drawable.menu_pick_down);
            }
        });
        slidingDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
               // btn.setAlpha(Color.WHITE);
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
        markerDrawables.add(FeatureType.HOTEL.ordinal(), getResources().getDrawable(R.drawable.hotel_m));
        markerDrawables.add(FeatureType.FOODNDRINK.ordinal(), getResources().getDrawable(R.drawable.food_m));
        markerDrawables.add(FeatureType.ATTRACTION.ordinal(), getResources().getDrawable(R.drawable.attr_m));
        markerDrawables.add(FeatureType.SHOPPING.ordinal(), getResources().getDrawable(R.drawable.shop_m));
    }

    private void drawFeatures(FeatureType featureType, TourFeature tourFeature) {
        PopulateMarkers populateMarkers = new PopulateMarkers();
        populateMarkers.execute(new Pair<FeatureType, TourFeature>(featureType, tourFeature));
    }

    public void onTooltipBtnClick(View v){
        pressedMarker.closeToolTip();

        if (SystemSetting.checkGPSStatus(MainMap.this) == 0) { // If GPS is OFF
            mGPSSettingDialog = new CustomDialog(MainMap.this,
                    R.string.title_dialog_gps_setting,
                    R.string.dialog_gps_setting,
                    R.string.btn_yes,
                    R.string.btn_no,
                    yesClickListener,
                    noClickListener);
            mGPSSettingDialog.show();
        } else {
            isSearchMyLocEnabled = true;
            animGPS.start();
            setGPSSignalTimer();
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
                globalVariables.setCurrentLoc(location);

                //Show icon animation until my location is recognized by first GPS signal
                if (isSearchMyLocEnabled || isNavigationEnabled) {
                    myLocationOverlay.goToMyPosition(true);

                    animGPS.stop();
                    animGPS.selectDrawable(0); // Return to first frame
                    cancelGPSSignalTimer();

                    if (isSearchMyLocEnabled && !isNavigationEnabled) {
                        isSearchMyLocEnabled = false;
                        marqueeText.setText("");
                    } else if (isSearchMyLocEnabled && isNavigationEnabled) {
                        isSearchMyLocEnabled = false;
                        if (pressedMarker != null) {
                            marqueeText.setText(getString(R.string.marquee_guiding) + pressedMarker.getTitle());
                        }
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
                        marqueeText.setTextColor(Color.RED);
                        marqueeText.setText(R.string.marquee_arrival_noti);

                        SharedPreferences defaultPref = PreferenceManager.getDefaultSharedPreferences(MainMap.this);
                        boolean settingValue = defaultPref.getBoolean("target_arrival_vibrate", true);

                        if (!isNotified && settingValue) {
                            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(500);
                            isNotified = true;
                        }
                    } else {
                        marqueeText.setTextColor(Color.BLACK);
                        marqueeText.setText(getString(R.string.marquee_guiding) + pressedMarker.getTitle());
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
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.setDrawAccuracyEnabled(true);

        // To make be layer order, 0:Map(default), 2:Path(default), 2:UserLoc(default), 3:Marker, 4:TourFeatures, 5:Itinerary, 6:MyLocation
        myLocationOverlay.setOverlayIndex(6);
        mapView.getOverlays().add(myLocationOverlay);
    }

    private void setNavigationOverlay() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mNavigationView = new NavigationView(this);
        mainLayout.addView(mNavigationView);
        mNavigationView.setVisibility(View.INVISIBLE);
        mDestinationLoc = new Location(LocationManager.GPS_PROVIDER);
    }

    private void cancelGPSSignalTimer() {
        if (gpsTimer != null) {
            gpsTimer.cancel();
        }
    }

    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.arg1 == 1) {
                Toast.makeText(MainMap.this, R.string.Warn_weak_gps_signal, Toast.LENGTH_LONG).show();
            }
        }
    };

    private void setGPSSignalTimer() {
        cancelGPSSignalTimer();

        gpsTimer = new Timer();
        gpsTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message msg = handler.obtainMessage();
                msg.arg1 = 1;
                handler.sendMessage(msg);
            }
        }, 30000L); // If you don't get GPS signal within 30s, display weak signal
    }

    private void setSearchMyLocEnabled(boolean enabled) {
        isSearchMyLocEnabled = enabled;

        if (enabled) {
            //Show icon animation until my location is recognized by first GPS signal
            myLocationOverlay.goToMyPosition(true);
            isSearchMyLocEnabled = true;
            animGPS.start();
            setGPSSignalTimer(); // If you don't get GPS signal within 10s, display weak signal
            marqueeText.setText(R.string.marquee_searching_you);

            if(isNavigationEnabled) {
                mSensorManager.registerListener(mListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_NORMAL);
            }
        } else {
            if (animGPS.isRunning()) {
                animGPS.stop();
                animGPS.selectDrawable(0); // Return to first frame
                cancelGPSSignalTimer();
            }
            marqueeText.setText("");

            if(isNavigationEnabled) {
                mSensorManager.unregisterListener(mListener);
            }
        }
    }

    //myLocation button click
    public void myLocationClick(View view) {
        if(SystemSetting.checkGPSStatus(this) == 0) { // If GPS is OFF
            mGPSSettingDialog = new CustomDialog(this,
                    R.string.title_dialog_gps_setting,
                    R.string.dialog_gps_setting,
                    R.string.btn_yes,
                    R.string.btn_no,
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

            // Check if there is a magnetic field sensor
            List<Sensor> sensor = mSensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
            if (sensor.size() == 0 ) {
                Toast.makeText(MainMap.this, R.string.toast_no_magnetic_sensor, Toast.LENGTH_LONG).show();
                return;
            }

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
                intent = new Intent(MainMap.this, WizardDaySelectActivity.class);
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
                intent = new Intent(MainMap.this, SettingsActivity.class);
                break;
        }
        return intent;
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
    protected void onRestart() {
        super.onRestart();
        Log.e("MainMapActivity", "onRestart()");

        // When Features are out of memory, App should need to restart from the start
        if (globalVariables.getTourFeatures(FeatureType.HOTEL).size() == 0) {
            Log.e("MainMapActivity", "onResume(), TourFeatureList size=" + globalVariables.getTourFeatures(FeatureType.HOTEL).size());
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        globalVariables.clearCurrnetLoc();

        if(SystemSetting.checkGPSStatus(this) != 0) { // If GPS is ON, Always indicate my location on the map
            myLocationOverlay.enableMyLocation();

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

    private void clearItineraryLayer() {
        for (Overlay overlay : mapView.getOverlays()) {
            if (overlay.getOverlayIndex() == 5) {
                mapView.removeOverlay(overlay);
            }
        }
    }

    private void clearAllLayersExceptForItinerary() {
        if (mapView.getItemizedOverlays().size() != 1) { // If there is only Itinerary layer, size is 1
            for (ItemizedIconOverlay iOverlay : mapView.getItemizedOverlays()) {
                // Layer's Index - 0:Map(default), 2:Path(default), 2:UserLoc(default),
                // 3:Marker(custom-but, now unused), 4:TourFeatures(custom), 5:Itinerary(custom), 6:MyLocation(custom)
                if (iOverlay.getOverlayIndex() == 4) {
                    mapView.removeOverlay(iOverlay);
                }
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Bundle extras = intent.getExtras();
        if (extras!=null) {
            clearAllLayersExceptForItinerary(); // If there are already drawn features, remove and redraw
            if (pressedMarker != null) { // Close opened tooltip
                pressedMarker.closeToolTip();
            }
            FeatureType featureType = FeatureType.valueOf(intent.getStringExtra("featureType"));
            switch (extras.getString("type")){
                case "features":
                    if (featureType == FeatureType.ITINERARY) {
                        clearItineraryLayer();
                    }
                    drawFeatures(featureType, null);
                    break;

                case "feature":
                    TourFeature tourFeature = findFeatureByName(MainMap.this, extras.getString("name"));
                    drawFeatures(featureType, tourFeature);
                    LatLng loc = new LatLng(tourFeature.getLatitude(), tourFeature.getLongitude());
                    mapView.getController().animateTo(loc);
                    break;
            }
        }
    }

    class PopulateMarkers extends AsyncTask<Pair<FeatureType, TourFeature>, Pair<FeatureType, TourFeature>, FeatureType>{

        ArrayList<Marker> itineraryMarkers;
        ArrayList<Marker> featuresMarkers;
        int index = 0;

        @Override
        protected FeatureType doInBackground(Pair<FeatureType, TourFeature>... params) {
            List features = null;
            FeatureType featureType = params[0].first;
            TourFeature feature = params[0].second;

            if (feature != null) { // For one feature
                featuresMarkers = new ArrayList<Marker>();
                publishProgress(new Pair<FeatureType, TourFeature>(featureType, feature));
            } else { // For features
                if (featureType == FeatureType.ITINERARY) {
                    features = globalVariables.getItineraryFeatures();
                    itineraryMarkers = new ArrayList<Marker>();
                } else {
                    features = globalVariables.getTourFeatures(featureType);
                    featuresMarkers = new ArrayList<Marker>();
                }

                if (features == null || features.size() == 0) {
                    return null;
                }

                for (Object featureObj : features) {
                    TourFeature tourFeature = (TourFeature)featureObj;
                    publishProgress(new Pair<FeatureType, TourFeature>(featureType, tourFeature));
                }
            }

            return featureType;
        }

        @Override
        protected void onProgressUpdate(Pair<FeatureType, TourFeature>... values) {
            super.onProgressUpdate(values);

            FeatureType featureType = values[0].first;
            TourFeature tourFeature = values[0].second;
            Marker marker = new Marker(tourFeature.getString("name"), "", new LatLng(tourFeature.getLatitude(), tourFeature.getLongitude()));
            String title = tourFeature.getString("name");

            if (featureType == FeatureType.ITINERARY) { // Itinerary Feature
                try{
                    if (tourFeature != null || tourFeature.getString("index") != null) {
                        marker.setMarker(findItitneryMakerImg(tourFeature.getString("category"), Integer.toString(++index)));
                        marker.setToolTip(new CustomInfoWindow(MainMap.this, mapView, featureType, tourFeature));
                        marker.setTitle(title);
                        itineraryMarkers.add(marker); // For adding as ItemizedIconOverlay
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
            else { // Tour Feature
                marker.setMarker(markerDrawables.get(featureType.ordinal()));
                marker.setToolTip(new CustomInfoWindow(MainMap.this, mapView, featureType, tourFeature));
                marker.setTitle(title);
                featuresMarkers.add(marker); // For adding as ItemizedIconOverlay
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(FeatureType featureType) {
            super.onPostExecute(featureType);

            if (featureType == null) {
                progressBar.setVisibility(View.GONE);
                return;
            }

            if (featureType == FeatureType.ITINERARY) {
                drawOverlay(5, itineraryMarkers);
            } else {
                drawOverlay(4, featuresMarkers);
            }
            progressBar.setVisibility(View.GONE);
        }

    }

    private Drawable findItitneryMakerImg(String featureType, String index) {
        int bitmapId = 0;

        switch (featureType) {
            case "attraction":
                bitmapId = R.drawable.attr_marker_bg;
                break;

            case "foodndrink":
                bitmapId = R.drawable.food_marker_bg;
                break;

            case "shopping":
                bitmapId = R.drawable.shop_marker_bg;
                break;

            case "hotel":
                bitmapId = R.drawable.hotel_marker_bg;
                break;
        }

        BitmapUtil.BitmapWithText markerImg = new BitmapUtil.BitmapWithText(MainMap.this, index, bitmapId);

        return markerImg;
    }


    // To make custom layer order, 0:Map(default), 2:Path(default), 2:UserLoc(default), 3:Marker(unused), 4:TourFeatures, 5:Itinerary, 6:MyLocation
    private void drawOverlay(int overlayIndex, ArrayList<Marker> markersList) {
        ItemizedIconOverlay iOverlay = new ItemizedIconOverlay(MainMap.this, markersList, new ItemizedIconOverlay.OnItemGestureListener<Marker>() {
            @Override
            public boolean onItemSingleTapUp(int i, Marker marker) {

                pressedMarker = marker;
                mapView.selectMarker(marker);
                return true; // Should be true because we handled this event
            }

            @Override
            public boolean onItemLongPress(int index, Marker item) {
                return false;
            }
        });

        iOverlay.setOverlayIndex(overlayIndex);
        mapView.addItemizedOverlay(iOverlay);
    }
}
