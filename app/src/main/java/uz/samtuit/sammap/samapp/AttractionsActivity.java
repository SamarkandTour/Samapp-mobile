package uz.samtuit.sammap.samapp;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cocoahero.android.geojson.Feature;
import com.cocoahero.android.geojson.GeoJSON;
import com.cocoahero.android.geojson.GeoJSONObject;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;


public class AttractionsActivity extends ActionBarActivity {
    private static Attractions[] item;
    ListView list;
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attractions);
        //Json
        JSONArray jhotel = null;
        String hotel = loadJSONFromAsset("attractions_0_001.geojson");

        try {

            GeoJSONObject ob = GeoJSON.parse(hotel);
            JSONObject obj = ob.toJSON();
            jhotel = obj.getJSONArray("features");

            item = new Attractions[jhotel.length()];
            Log.e("HOTELS COUNT", jhotel.length() + " S");
            // looping through All Contacts
            for (int i = 0; i < jhotel.length() ; i++) {
                item[i] = new Attractions();
                JSONObject j = jhotel.getJSONObject(i);
                Feature feature = new Feature(j);
                JSONObject g = feature.getGeometry().toJSON();
                JSONObject c = feature.getProperties();
                Log.e("COUNT", c.length() + " s");
                item[i].Name = c.getString("Name");
                Log.e("NAME", c.getInt("Rating") + " S");
                item[i].Latitude = g.getJSONArray("coordinates").getDouble(1);
                item[i].Longitude = g.getJSONArray("coordinates").getDouble(0);
                item[i].Address = c.getString("Address");
                item[i].Telephone = c.getString("Tel");
                item[i].WiFi = c.getBoolean("Wi-Fi");
                item[i].Rating = c.getInt("Rating");
                item[i].URL = c.getString("URL");
                item[i].Description = c.getString("description");
                item[i].Open = c.getString("Open");
                item[i].Type = c.getString("Type");
                item[i].Photo = c.getString("Photo");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //
        list = (ListView)findViewById(R.id.attractionsListView);
        tv=(TextView)findViewById(R.id.attractionsTitle);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Baskerville.ttf");
        tv.setTypeface(tf);
        AttractionsListAdapter adapter = new AttractionsListAdapter(this,R.layout.list_item, item);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
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
