package uz.samtuit.samapp.main;

import android.content.Context;
import android.content.Intent;
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
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
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
import com.mapbox.mapboxsdk.views.MapView;
import com.mapbox.mapboxsdk.views.util.OnMapOrientationChangeListener;

import java.util.ArrayList;
import java.util.List;

import uz.samtuit.samapp.util.BitmapUtil;
import uz.samtuit.samapp.util.CustomDialog;
import uz.samtuit.samapp.util.GlobalsClass;
import uz.samtuit.samapp.util.ItineraryList;
import uz.samtuit.samapp.util.MenuItems;
import uz.samtuit.samapp.util.SystemSetting;
import uz.samtuit.samapp.util.TourFeature;

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
    //private ImageView mAnimMyPosImage;
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
        ImageView mAnimMyPosImage = (ImageView) findViewById(R.id.myPositon);
        animGPS = (AnimationDrawable) mAnimMyPosImage.getBackground();

        setMapView(); //Set MapView configuration

        setUserLocationOverlay(); //Set User Location Overlay

        setNavigationOverlay(); // Set Navigation Overlay

        drawBottomMenuIcons(); //Generate bottom Menu items

        setMarkerIcons();
        drawFeatures(FeatureType.ITINERARY, null);
    }

    private void setMapView() {
        compass = (ImageView)findViewById(R.id.compass);

        TileLayer mbTileLayer = new MBTilesLayer(this, GlobalsClass.mapFileName);
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
                    animGPS.stop();
                    animGPS.selectDrawable(0); // Return to first frame

                    if (isSearchMyLocEnabled && !isNavigationEnabled) {
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

    private void setSearchMyLocEnabled(boolean enabled) {
        isSearchMyLocEnabled = enabled;

        if (enabled) {
            //Show icon animation until my location is recognized by first GPS signal
            myLocationOverlay.goToMyPosition(true);
            isSearchMyLocEnabled = true;
            animGPS.start();
            marqueeText.setText(R.string.marquee_searching_you);

            if(isNavigationEnabled) {
                mSensorManager.registerListener(mListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_NORMAL);
            }
        } else {
            if (animGPS.isRunning()) {
                animGPS.stop();
                animGPS.selectDrawable(0); // Return to first frame
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
                intent = new Intent(MainMap.this, LanguageSettingActivity.class);
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
    protected void onResume() {
        super.onResume();

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

            FeatureType featureType = FeatureType.valueOf(extras.getString("featureType"));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("OnDestroy", "beep");
        ItineraryList list = ItineraryList.getInstance();
        list.clearItineraryFeatureList();
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
                    TourFeature tourFeature = (TourFeature) featureObj;
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
                BitmapUtil.BitmapWithText markerimg = new BitmapUtil.BitmapWithText(MainMap.this, new Integer(++index).toString(), R.drawable.poi_bg);
                marker.setMarker(markerimg);
                marker.setToolTip(new CustomInfoWindow(MainMap.this, mapView, featureType, tourFeature));
                marker.setTitle(title);
                itineraryMarkers.add(marker); // For adding as ItemizedIconOverlay
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

    // To make custom layer order, 0:Map(default), 2:Path(default), 2:UserLoc(default), 3:Marker(unused), 4:TourFeatures, 5:Itinerary, 6:MyLocation
    void drawOverlay(int overlayIndex, ArrayList<Marker> markersList) {
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
