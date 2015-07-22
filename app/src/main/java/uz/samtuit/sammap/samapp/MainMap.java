package uz.samtuit.sammap.samapp;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.mapbox.mapboxsdk.api.ILatLng;
import com.mapbox.mapboxsdk.tileprovider.tilesource.MBTilesLayer;
import com.mapbox.mapboxsdk.tileprovider.tilesource.TileLayer;
import com.mapbox.mapboxsdk.views.MapView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class MainMap extends ActionBarActivity {
    LinearLayout linLay,vertical;
    HorizontalScrollView hscv;
    Button newBtn;
    private ArrayList<MenuItems> Items = new ArrayList<MenuItems>();
    Button btn;
    boolean updateAvailable = true;
    int height;
    MapView mapView;
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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
        //MapView Settings
        mapView = (MapView)findViewById(R.id.mapview);

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
        mapView.setUserLocationEnabled(true);
        mapView.setZoom(17);
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

}
