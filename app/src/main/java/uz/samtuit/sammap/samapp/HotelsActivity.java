package uz.samtuit.sammap.samapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class HotelsActivity extends Activity {
    final Hotels[] item = new Hotels[6];
    ListView list;
    TextView tv;
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotels);
        item[0] = new Hotels("emirxan",123,3);
        item[1] = new Hotels("grand Samarkand",5,4);
        item[2] = new Hotels("emirxan",123,3);
        item[3] = new Hotels("grand Samarkand",5,4);
        item[4] = new Hotels("emirxan",123,3);
        item[5] = new Hotels("grand Samarkand",5,4);
        
        //Json Parser
        JSONArray jhotel = null;
        String hotel = loadJSONFromAsset();
        String name = null;

        try {
            JSONObject obj = new JSONObject(hotel);

            jhotel = obj.getJSONArray("Hotel");
            // looping through All Contacts
            for (int i = 0; i < hotel.length(); i++) {
                JSONObject c = jhotel.getJSONObject(i);

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
        ///
        
        
        list = (ListView)findViewById(R.id.hotelsListView);
        tv=(TextView)findViewById(R.id.hotelsTitle);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Baskerville.ttf");
        tv.setTypeface(tf);
        MyListAdapter adapter = new MyListAdapter(this,R.layout.list_item, item);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(HotelsActivity.this, HotelActivity.class);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hotels, menu);
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
