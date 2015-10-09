package uz.samtuit.samapp.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
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
import java.util.ArrayList;

import uz.samtuit.sammap.main.R;


public class ShoppingActivity extends ActionBarActivity {
    private static ArrayList<Shops> items;
    ListView list;
    private EditText search_text;
    private MenuItem mActionSearch;
    private ShopsListAdapter adapter;
    private boolean isSearchOpen = false;
    private Shops item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);

        //ActionBar setting
        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.shop_tool));
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_content, R.anim.slide_in);
            }
        });
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/segoeui.ttf");
        SpannableString s = new SpannableString(getResources().getString(R.string.shopping));
        s.setSpan(new CustomTypefaceSpan("", tf), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);
        toolbar.setTitle(s);

        //end ActionBar Setting

        //Json
        JSONArray jshop = null;
        String shop = loadJSONFromAsset("data/en/shops_0_001.geojson");

        try {

            GeoJSONObject ob = GeoJSON.parse(shop);
            JSONObject obj = ob.toJSON();
            jshop = obj.getJSONArray("features");

            items = new ArrayList<Shops>();

            // looping through All Contacts
            for (int i = 0; i < jshop.length() ; i++) {
                item = new Shops();
                JSONObject j = jshop.getJSONObject(i);
                Feature feature = new Feature(j);
                JSONObject g = feature.getGeometry().toJSON();
                JSONObject c = feature.getProperties();
                item.Name = c.getString("Name");
                item.Latitude = g.getJSONArray("coordinates").getDouble(1);
                item.Longitude = g.getJSONArray("coordinates").getDouble(0);
                item.Address = c.getString("Address");
                item.Telephone = c.getString("Tel");
                item.WiFi = c.getBoolean("Wi-Fi");
                item.Rating = c.getInt("Rating");
                item.URL = c.getString("URL");
                item.Description = c.getString("description");
                item.Open = c.getString("Open");
                item.Type = c.getString("Type");
                item.Photo = c.getString("Photo");
                items.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //

        list = (ListView)findViewById(R.id.shoppingListView);
        adapter = new ShopsListAdapter(this,R.layout.list_item, items);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ShoppingActivity.this, ShopActivity.class);
                intent.putExtra("name", items.get(position).Name);
                intent.putExtra("telephone", items.get(position).Telephone);
                intent.putExtra("address", items.get(position).Address);
                intent.putExtra("rating", items.get(position).Rating);
                intent.putExtra("lat", items.get(position).Latitude);
                intent.putExtra("long", items.get(position).Longitude);
                intent.putExtra("desc", items.get(position).Description);
                intent.putExtra("photo", items.get(position).Photo);
                intent.putExtra("wifi", items.get(position).WiFi);
                intent.putExtra("price", items.get(position).Price);
                intent.putExtra("url", items.get(position).URL);
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
    public void onBackPressed() {
        if(isSearchOpen)
        {
            handleMenuSearch();
            return;
        }
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_content, R.anim.slide_in);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mActionSearch = menu.findItem(R.id.action_search);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shopping, menu);
        // Associate searchable configuration with the SearchView

        return true;
    }

    protected void handleMenuSearch(){
        ActionBar action = getSupportActionBar(); //get the actionbar

        if(isSearchOpen){ //test if the search is open

            action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
            action.setDisplayShowTitleEnabled(true); //show the title in the action bar


            //hides the keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(search_text.getWindowToken(), 0);
            list.setAdapter(adapter);
            //add the search icon in the action bar
            mActionSearch.setIcon(getResources().getDrawable(R.drawable.ic_search_white_24dp));

            isSearchOpen = false;
        } else { //open the search entry

            action.setDisplayShowCustomEnabled(true); //enable it to display a
            // custom view in the action bar.
            action.setCustomView(R.layout.search_bar);//add the custom view
            action.setDisplayShowTitleEnabled(false); //hide the title

            search_text = (EditText)action.getCustomView().findViewById(R.id.edtSearch); //the text editor
            search_text.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    doSearch(search_text.getText().toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            //this is a listener to do a search when the user clicks on search button
            search_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        doSearch(search_text.getText().toString());
                        return true;
                    }
                    return false;
                }
            });

            search_text.requestFocus();

            //open the keyboard focused in the edtSearch
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(search_text, InputMethodManager.SHOW_IMPLICIT);

            //add the close icon
            mActionSearch.setIcon(getResources().getDrawable(R.drawable.ic_search_black_24dp));

            isSearchOpen = true;
        }
    }

    private void doSearch(String sequence)
    {
        ArrayList<Shops> found_items = new ArrayList<Shops>();
        for(int i = 0; i < items.size(); i++)
        {
            if(items.get(i).Name.toLowerCase().contains(sequence.toLowerCase()))
            {
                found_items.add(items.get(i));
            }
        }
        ShopsListAdapter adapter = new ShopsListAdapter(this, R.layout.list_item, found_items);
        list.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id)
        {
            case R.id.action_settings:
                return true;
            case R.id.action_search:
                handleMenuSearch();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
