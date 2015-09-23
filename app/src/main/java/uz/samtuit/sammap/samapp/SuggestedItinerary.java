package uz.samtuit.sammap.samapp;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.cocoahero.android.geojson.GeoJSON;
import com.cocoahero.android.geojson.GeoJSONObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class SuggestedItinerary extends Activity {

    Button btn;
    LinearLayout ll;
    FrameLayout fl;
    TextView tv;
    String day[] = {"one","two","three","five","six","seven","eight","nine","ten"};
    ListView lv;
    Point[] dat;
    ArrayList<Point[]> data;
    ArrayList<SugItineraryAdapter> adapters;
    SugItineraryAdapter adapter;


    private final static int daysCount = 2;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary_wizard);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");
        tv = (TextView)findViewById(R.id.suggestedItineraryTitle);
        tv.setTypeface(tf);
        try
        {

        }
        catch(Exception e){

        }
        genData();

        lv = (ListView)findViewById(R.id.listView);
        ll = (LinearLayout)findViewById(R.id.tabButtonsLayout);
        adapter = new SugItineraryAdapter(this, R.layout.itinerary_list_item, data.get(0));
        lv.setAdapter(adapter);
        for(int i = 0; i < daysCount; i++)
        {
            btn = new Button(this);
            btn.setPadding(0, 0, 0, 0);
            btn.setTextSize(12);
            btn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            btn.setText("Day " + (i + 1));
            btn.setTypeface(tf);
            btn.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("ASD",Integer.parseInt(((Button)view).getText().toString().split(" ")[1])-1+" ");
                    changeAdapter(Integer.parseInt(((Button)view).getText().toString().split(" ")[1])-1);
                    //lv.setAdapter(adapters.get(Integer.parseInt(((Button)view).getText().toString().split(" ")[1])-1));
                }
            });
            ll.addView(btn);
        }

        //Json Settings

    }
    private String getItemName(int i)
    {
        return "day_" + day[i];
    }

    private void changeAdapter(int i)
    {
        lv.setAdapter(adapters.get(i));
    }
    private void genData()
    {
        data = new ArrayList<Point[]>();
        dat = new Point[4];
        dat[0]= new Point("Registan","39.65219, 66.94716","ADAS");
        dat[1]= new Point("Guri Emir","39.65552, 66.95680","ADAS");
        dat[2]= new Point("Rukhabad","39.66166, 66.97904","ADAS");
        dat[3]= new Point("Afrasiab Hotel","39.66607, 66.97932","ADAS");
        data.add(dat);
        dat = new Point[4];
        dat[2]= new Point("Siyob Bazar","39.65219, 66.94716","ADAS");
        dat[1]= new Point("Afrasiab","39.65552, 66.95680","ADAS");
        dat[0]= new Point("Shakhizinda","39.66166, 66.97904","ADAS");
        data.add(dat);
        adapters = new ArrayList<SugItineraryAdapter>();
        for(int i = 0; i < data.size(); i++)
        {
            adapter = new SugItineraryAdapter(this,R.layout.itinerary_list_item, data.get(0));
            adapters.add(adapter);
        }
    }

    public String loadJSONFromAsset(String fileName) {
        String json = null;
        try {

            InputStream is = getAssets().open(fileName);

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

