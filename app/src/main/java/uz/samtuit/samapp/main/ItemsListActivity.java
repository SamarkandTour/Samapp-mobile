package uz.samtuit.samapp.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import uz.samtuit.samapp.util.GlobalsClass;
import uz.samtuit.samapp.util.GlobalsClass.FeatureType;
import uz.samtuit.samapp.util.TourFeature;
import uz.samtuit.sammap.main.R;


public class ItemsListActivity extends ActionBarActivity {
    private ArrayList<TourFeature> items;
    ListView list;
    private EditText search_text;
    private MenuItem mActionSearch;
    private ItemsListAdapter adapter;
    private boolean isSearchOpen = false;
    private RelativeLayout RelLayout;
    private int PRIMARY_COLOR;
    private int TOOLBAR_COLOR;
    private String TITLE;
    FeatureType S_ACTIVITY_NAME;
    android.support.v7.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_list);

    //Include Global Variables
        GlobalsClass globalVariables = (GlobalsClass)getApplicationContext();

    //Configure views and variables
        RelLayout = (RelativeLayout)findViewById(R.id.hotelsMain);
        toolbar = (Toolbar)findViewById(R.id.tool_bar);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/segoeui.ttf");
        list = (ListView)findViewById(R.id.hotelsListView);

    //Configure Common data
        Bundle extras = getIntent().getExtras();
        S_ACTIVITY_NAME = FeatureType.valueOf(extras.getString("action"));

        PRIMARY_COLOR = getPrimaryColorId(S_ACTIVITY_NAME);
        TOOLBAR_COLOR = getToolbarColorId(S_ACTIVITY_NAME);
        TITLE = getResources().getString(getTitle(S_ACTIVITY_NAME));
        items = globalVariables.getTourFeatures(S_ACTIVITY_NAME);

    //ActionBar TOOLBAR
        RelLayout.setBackground(getResources().getDrawable(PRIMARY_COLOR));
        toolbar.setBackgroundColor(getResources().getColor(TOOLBAR_COLOR));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_content, R.anim.slide_in);
            }
        });

        SpannableString s = new SpannableString(TITLE);
        s.setSpan(new CustomTypefaceSpan("", tf), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);
        toolbar.setTitle(s);
    //end Action Bar

        adapter = new ItemsListAdapter(this,R.layout.list_item, items);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ItemsListActivity.this, ListItemActivity.class);
                intent.putExtra("featureType", S_ACTIVITY_NAME.toString());
                intent.putExtra("photo",items.get(position).getPhoto());
                intent.putExtra("rating",items.get(position).getRating());
                intent.putExtra("name",items.get(position).getString("name"));
                intent.putExtra("desc",items.get(position).getString("desc"));
                intent.putExtra("type",items.get(position).getString("type"));
                intent.putExtra("price",items.get(position).getString("price"));
                intent.putExtra("wifi",items.get(position).getString("wifi"));
                intent.putExtra("open",items.get(position).getString("open"));
                intent.putExtra("addr",items.get(position).getString("addr"));
                intent.putExtra("tel", items.get(position).getString("tel"));
                intent.putExtra("url",items.get(position).getString("url"));
                intent.putExtra("long",items.get(position).getLongitude());
                intent.putExtra("lat",items.get(position).getLatitude());
                intent.putExtra("primaryColorId",PRIMARY_COLOR);
                intent.putExtra("toolbarColorId",TOOLBAR_COLOR);
                startActivity(intent);
            }
        });
        extras.clear();
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
        getMenuInflater().inflate(R.menu.menu_hotels, menu);
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
            action.setDisplayUseLogoEnabled(false);

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
        ArrayList<TourFeature> found_items = new ArrayList<TourFeature>();
        for(int i = 0; i < items.size(); i++)
        {
            if(items.get(i).getString("name").toLowerCase().contains(sequence.toLowerCase()))
            {
                found_items.add(items.get(i));
            }
        }
        ItemsListAdapter adapter = new ItemsListAdapter(this, R.layout.list_item, found_items);
        list.setAdapter(adapter);
    }

    private int getPrimaryColorId(FeatureType type)
    {
        int id = 0;

        switch (type)
        {
            case HOTEL:
                id = R.color.hotel_primary;
                break;
            case FOODNDRINK:
                id = R.color.foodanddrink_primary;
                break;
            case ATTRACTION:
                id = R.color.attraction_primary;
                break;
            case SHOPPING:
                id = R.color.shop_primary;
                break;
        }
        return id;
    }

    private int getToolbarColorId(FeatureType type)
    {
        int id = 0;

        switch (type)
        {
            case HOTEL:
                id = R.color.hotel_tool;
                break;
            case FOODNDRINK:
                id = R.color.foodanddrink_tool;
                break;
            case ATTRACTION:
                id = R.color.attraction_tool;
                break;
            case SHOPPING:
                id = R.color.shop_tool;
                break;
        }
        return id;
    }

    private int getTitle(FeatureType type)
    {
        int id = 0;

        switch (type)
        {
            case HOTEL:
                id = R.string.hotels;
                break;
            case FOODNDRINK:
                id = R.string.foodanddrinks;
                break;
            case ATTRACTION:
                id = R.string.attractions;
                break;
            case SHOPPING:
                id = R.string.shopping;
                break;
        }
        return id;
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
            case R.id.action_sort:
                Collections.sort(items, new CustomComparator());
                adapter = new ItemsListAdapter(this, R.layout.list_item, items);
                list.setAdapter(adapter);
                break;
            case R.id.action_search:
                handleMenuSearch();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class CustomComparator implements Comparator<TourFeature> {
        @Override
        public int compare(TourFeature o1, TourFeature o2) {
            return o1.getString("name").compareTo(o2.getString("name"));
        }
    }
}
