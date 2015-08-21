package uz.samtuit.sammap.samapp;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
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
    private EditText search_text;
    private MenuItem mActionSearch;
    private boolean isSearchOpen = false;
    TextView tv;
    private android.support.v7.widget.Toolbar toolbar;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotels);
        //Window window = this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");
        SpannableString s = new SpannableString(getResources().getString(R.string.hotels));
        s.setSpan(new CustomTypefaceSpan("",tf), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

// Update the action bar title with the TypefaceSpan instance
        toolbar.setLogo(getResources().getDrawable(R.drawable.hotel_marker));
        getSupportActionBar().setTitle(s);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        toolbar.setTitle(s);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_content, R.anim.slide_in);
            }
        });

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

    private void runFadeInAnimation() {
        Animation a = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        a.reset();
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.hotelsMain);
        rl.clearAnimation();
        rl.startAnimation(a);
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        mActionSearch = menu.findItem(R.id.action_search);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hotels, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
//        searchView.setSearchableInfo(
//                searchManager.getSearchableInfo(getComponentName()));

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
        if(id == R.id.action_search)
        {

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
