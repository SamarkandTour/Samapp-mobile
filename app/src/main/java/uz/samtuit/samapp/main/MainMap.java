package uz.samtuit.samapp.main;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;

import com.cocoahero.android.geojson.FeatureCollection;
import com.mapbox.mapboxsdk.api.ILatLng;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.GpsLocationProvider;
import com.mapbox.mapboxsdk.overlay.Icon;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.overlay.PathOverlay;
import com.mapbox.mapboxsdk.overlay.UserLocationOverlay;
import com.mapbox.mapboxsdk.tileprovider.tilesource.MBTilesLayer;
import com.mapbox.mapboxsdk.tileprovider.tilesource.TileLayer;
import com.mapbox.mapboxsdk.util.DataLoadingUtils;
import com.mapbox.mapboxsdk.views.MapView;
import com.mapbox.mapboxsdk.views.util.OnMapOrientationChangeListener;

import java.util.ArrayList;

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
    private GpsLocationProvider mGpsLocProvider;
    private UserLocationOverlay myLocationOverlay;
    private EditText searchText;
    private static boolean isPressedMyPosBtn = true;
    private Animation anim;
    private ImageView mAnimMyPosImage;
    private CustomDialog mGPSSettingDialog;
    private ArrayList<Drawable> markerDrawables;
    private NavigationView mNavigationView;
    private FrameLayout mainLayout;
    private SensorManager mSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        globalVariables = (GlobalsClass)getApplicationContext();

        getBaseContext().setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main_map);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mapView = (MapView)findViewById(R.id.mapview);
        mainLayout = (FrameLayout) findViewById(R.id.mainLayout);

        //search text typeface
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");
        searchText = (EditText)findViewById(R.id.search_text);
        searchText.setTypeface(tf);

        setMapView(); //Set MapView configuration

        setUserLocationOverlay(); //Set User Location Overlay

        drawBottomMenuIcons(); //Generate bottom Menu items

        setMarkerIcons();
        drawItinerary();

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
    }

    private void setMarkerIcons() {
        markerDrawables = new ArrayList<Drawable>();
        markerDrawables.add(FeatureType.HOTEL.ordinal(), getResources().getDrawable(R.drawable.hotel_marker));
        markerDrawables.add(FeatureType.FOODNDRINK.ordinal(), getResources().getDrawable(R.drawable.food_marker));
        markerDrawables.add(FeatureType.ATTRACTION.ordinal(), getResources().getDrawable(R.drawable.attraction_marker));
        markerDrawables.add(FeatureType.SHOPPING.ordinal(), getResources().getDrawable(R.drawable.shop_marker));
    }

    private void drawItinerary() {
        try {
            FeatureCollection features = TourFeatureList.loadGeoJSONFromExternalFilesDir(this, globalVariables.getApplicationLanguage() + "_MyItinerary.geojson");
            ArrayList<Object> uiObjects = DataLoadingUtils.createUIObjectsFromGeoJSONObjects(features, null);

            for (Object obj : uiObjects) {
                if (obj instanceof Marker) {
                    Marker m = (Marker)obj;
                    //m.setIcon(new Icon(markerDrawables[i])); // Set Icons with order numbers
                    mapView.addMarker(m);
                } else if (obj instanceof PathOverlay) {
                    mapView.getOverlays().add((PathOverlay) obj);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUserLocationOverlay() {
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
    }

    //myPosition button click
    public void myPositionClick(View view)
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
        }
        return intent;
    }

    private void handleExtraRequest() {
        Bundle extras = getIntent().getExtras();
        if (extras!=null) {
            switch (extras.getString("type")){
                case "itinerary":
                    break;

                case "feature":
                    String name = extras.getString("name");

                    double lat = 0 ,longt = 0;
                    lat = extras.getDouble("lat");
                    longt = extras.getDouble("long");
                    LatLng loc = new LatLng(lat,longt);

                    FeatureType featureType = FeatureType.valueOf(extras.getString("featureType"));
                    Marker m = new Marker(name, "", loc);
                    m.setIcon(new Icon(markerDrawables.get(featureType.ordinal())));

                    mapView.addMarker(m);
                    mapView.getController().animateTo(loc);
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(SystemSetting.checkGPSStatus(this) != 0) { // If GPS is ON
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
}
