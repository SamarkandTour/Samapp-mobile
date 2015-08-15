package uz.samtuit.sammap.samapp;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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


public class SuggestedItinerary extends Activity {

    Button btn;
    LinearLayout ll;
    FrameLayout fl;
    TextView tv;
    String day[] = {"one","two","three","five","six","seven","eight","nine","ten"};



    private final static int daysCount = 3;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary_wizard);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");
        tv = (TextView)findViewById(R.id.suggestedItineraryTitle);
        tv.setTypeface(tf);
        ll = (LinearLayout)findViewById(R.id.tabButtonsLayout);
        for(int i = 0; i < daysCount; i++)
        {
            btn = new Button(this);
            btn.setPadding(0,0,0,0);
            btn.setTextSize(12);
            btn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            btn.setText("Day " + (i+1));
            btn.setTypeface(tf);
            ll.addView(btn);
        }

        //Json Settings
        try
        {
            String sug_it = loadJSONFromAsset("itinerary.json");
            GeoJSONObject obj = GeoJSON.parse(sug_it);
            JSONObject ob = obj.toJSON();
            JSONArray jItinerary = ob.getJSONArray("sug_itinerary");
            for(int i = 0; i < jItinerary.length(); i++)
            {
                JSONObject c = jItinerary.getJSONObject(i);
                JSONArray jsonArray = c.getJSONArray(getItemName(i));

            }
        }
        catch(JSONException e)
        {

        }

    }
    private String getItemName(int i)
    {
        return "day_" + day[i];
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

