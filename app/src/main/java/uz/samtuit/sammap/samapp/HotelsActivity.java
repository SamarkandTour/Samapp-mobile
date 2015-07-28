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

            jhotel = obj.getJSONArray("Hotel");
            item = new Hotels[hotel.length()];
            // looping through All Contacts
            for (int i = 0; i < hotel.length(); i++) {
                JSONObject c = jhotel.getJSONObject(i);
                item[i].Name = c.getString("Name");
                String Location = c.getString("Location");
                item[i].Latitude = Integer.parseInt(Location.split(", ")[0]);
                item[i].Longitude = Integer.parseInt(Location.split(", ")[1]);
                item[i].Address = c.getString("Address");
                item[i].Telephone = c.getString("")
                name = c.getString("Name");
                String loc = c.getString("Location");
                String addr = c.getString("Address");
                String type = c.getString("Type");
                String price = c.getString("Price");
                String wi_fi = c.getString("Wi-Fi");
                String open = c.getString("Open");
                String tel = c.getString("Tel");
                String url = c.getString("URL");
                String description = c.getString("Description");
                String rating = c.getString("Rating");
                String photo = c.getString("Photo");

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //


        item[0] = new Hotels("emirxan",123,3,"+998981234567","St.sadasd",39.66386,66.97060);
        item[1] = new Hotels("grand Samarkand",5,4,"+998981234567","St.sadasd",39.651977,66.9665084);
        item[2] = new Hotels("emirxan",123,3,"+998981234567","St.sadasd",39.651977,66.9665084);
        item[3] = new Hotels("grand Samarkand",5,4,"+998981234567","St.sadasd",39.651977,66.9665084);
        item[4] = new Hotels("emirxan",123,3,"+998981234567","St.sadasd",39.651977,66.9665084);
        item[5] = new Hotels("grand Samarkand",5,4,"+998981234567","St.sadasd",39.651977,66.9665084);
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
                startActivity(intent);
            }
        });



    }

    public String loadJSONFromAsset() {
        String json = null;
        try {

            InputStream is = getAssets().open("Hotel.json");

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
