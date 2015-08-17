package uz.samtuit.sammap.samapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.cocoahero.android.geojson.Feature;
import com.cocoahero.android.geojson.GeoJSON;
import com.cocoahero.android.geojson.GeoJSONObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;


public class HotelsActivity extends ActionBarActivity {
    private static Hotels[] item;
    ListView list;
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotels);
        //Json
        JSONArray jhotel = null;
        String hotel = loadJSONFromAsset("hotels_0_001.geojson");

        try {

            GeoJSONObject ob = GeoJSON.parse(hotel);
            JSONObject obj = ob.toJSON();
            jhotel = obj.getJSONArray("features");

            item = new Hotels[jhotel.length()];
            Log.e("HOTELS COUNT",jhotel.length()+" S");
            // looping through All Contacts
            for (int i = 0; i < jhotel.length() ; i++) {
                item[i] = new Hotels();
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

//        int d = item.length/2;
//        while(d>0)
//        {
//            for(int i = 0; i < item.length - d - 1; i++)
//            {
//                int j = i;
//                while(j>=0 && item[j].Name > item[j+d].Name)
//            }
//        }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
