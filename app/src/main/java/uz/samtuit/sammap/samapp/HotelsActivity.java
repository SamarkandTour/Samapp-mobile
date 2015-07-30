package uz.samtuit.sammap.samapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mapbox.mapboxsdk.geometry.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;


public class HotelsActivity extends Activity {
    private static Hotels[] item;
    ListView list;
    TextView tv;
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotels);
        //Json
        JSONArray jhotel = null;
        String hotel = loadJSONFromAsset();
        String name = null;

        try {
            JSONObject obj = new JSONObject(hotel);

            jhotel = obj.getJSONArray("features");
            item = new Hotels[jhotel.length()];
            Log.e("HOTELS COUNT",jhotel.length()+" S");
            // looping through All Contacts
            for (int i = 0; i < jhotel.length() ; i++) {
                item[i] = new Hotels();
                JSONObject j = jhotel.getJSONObject(i);
                JSONObject c = j.getJSONObject("properties");
                Log.e("COUNT", c.length()+" s");
                item[i].Name = c.getString("Name");
                Log.e("NAME", c.getInt("Rating")+ " S");
//                String Location = c.getString("Location");
//                item[i].Latitude = Double.parseDouble(Location.split(", ")[0]);
//                item[i].Longitude = Double.parseDouble(Location.split(", ")[1]);
                item[i].Address = c.getString("Address");
                item[i].Telephone = c.getString("Tel");
                item[i].WiFi = c.getBoolean("Wi-Fi");
                item[i].Rating = c.getInt("Rating");
                item[i].URL = c.getString("URL");
                item[i].Description = c.getString("Desc");
                item[i].Open = c.getString("Open");
                item[i].Type = c.getString("Type");
                item[i].Photo = c.getString("Photo");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //
        list = (ListView)findViewById(R.id.hotelsListView);
        tv=(TextView)findViewById(R.id.hotelsTitle);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Baskerville.ttf");
        tv.setTypeface(tf);
        HotelsListAdapter adapter = new HotelsListAdapter(this,R.layout.list_item, item);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(HotelsActivity.this, HotelActivity.class);
                intent.putExtra("name",item[position].Name);
                intent.putExtra("telephone",item[position].Telephone);
                intent.putExtra("address",item[position].Address);
                intent.putExtra("rating",item[position].Rating);
                intent.putExtra("lat",item[position].Latitude);
                intent.putExtra("long",item[position].Longitude);
                intent.putExtra("desc",item[position].Description);
                intent.putExtra("photo",item[position].Photo);
                intent.putExtra("wifi",item[position].WiFi);
                intent.putExtra("price",item[position].Price);
                startActivity(intent);
            }
        });



    }

    public String loadJSONFromAsset() {
        String json = null;
        try {

            InputStream is = getAssets().open("Hotel_20150728.geojson");

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
