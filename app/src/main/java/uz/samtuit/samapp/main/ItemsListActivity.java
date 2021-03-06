package uz.samtuit.samapp.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import uz.samtuit.samapp.adapters.TourFeatureItemsAdapter;
import uz.samtuit.samapp.util.CustomTypefaceSpan;
import uz.samtuit.samapp.util.GlobalsClass;
import uz.samtuit.samapp.util.GlobalsClass.FeatureType;
import uz.samtuit.samapp.util.TourFeature;
import uz.samtuit.samapp.util.TourFeatureList;


public class ItemsListActivity extends ActionBarActivity {
    public static SortBy sortBy = SortBy.NAME;
    public enum SortBy {NAME, LOCATION};

    private int PRIMARY_COLOR;
    private int TOOLBAR_COLOR;
    private ArrayList<TourFeature> items;
    private FeatureType S_ACTIVITY_NAME;
    private RecyclerView list;
    private EditText mSearchEditText;
    private MenuItem mActionSearch;
    private MenuItem mActionSort;
    private MenuItem mActionSortML;
    private MenuItem mActionShowOnMap;
    private TourFeatureItemsAdapter adapter;
    private boolean isSearchOpen = false;
    private RelativeLayout RelLayout;
    private int[] adapterLayouts;
    private String TITLE;
    private android.support.v7.widget.Toolbar toolbar;
    private RecyclerView.LayoutManager mLayoutManager;
    private int list_type = 0;
    private Location currentLoc;
    private boolean fromItinerary = false;
    private GlobalsClass globalVariables;
    private int selectedDay = 0;
    private int indexToAssign = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            Log.e("ItemsListActivity", "onRestore()");

            GlobalsClass globalVariables = (GlobalsClass)getApplicationContext();
            ArrayList<TourFeature> featureList = globalVariables.getTourFeatures(FeatureType.HOTEL);

            // When Features are out of memory, App should need to restart from the start
            if (featureList == null) {
                Log.e("ItemsListActivity", "featureList=null");
                SharedPreferences pref = globalVariables.getApplicationContext().getSharedPreferences("SamTour_Pref", 0);
                String currentLang = pref.getString("app_lang", null);
                TourFeatureList.loadAllFeaturesToMemory(this, currentLang);
            }
        }

        setContentView(R.layout.activity_items_list);

        //Include Global Variables
        globalVariables = (GlobalsClass)getApplicationContext();
        SharedPreferences sharedPreferences = getPreferences(0);
        adapterLayouts =  new int[] {R.layout.items_list_adapter, R.layout.items_list_adapter_grid_card};

        //Configure views and variables
        RelLayout = (RelativeLayout)findViewById(R.id.hotelsMain);
        toolbar = (Toolbar)findViewById(R.id.tool_bar);
        Typeface tf = Typeface.createFromAsset(getAssets(), "font/segoeui.ttf");
        list = (RecyclerView) findViewById(R.id.itemsRecycler);
        list.setHasFixedSize(true);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);
        list.setItemAnimator(itemAnimator);

        mLayoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(mLayoutManager);

        //Configure Common data
        Bundle extras = getIntent().getExtras();
        S_ACTIVITY_NAME = FeatureType.valueOf(extras.getString("action"));
        fromItinerary = extras.getBoolean("from_itinerary");
        selectedDay = extras.getInt("selected_day");
        indexToAssign = extras.getInt("index");

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

        adapter = new TourFeatureItemsAdapter(this, S_ACTIVITY_NAME, items, adapterLayouts[list_type],fromItinerary, selectedDay, indexToAssign);
        list.setAdapter(adapter);
        extras.clear();

        if (sortBy.name().equals("NAME")) {
            sortByName();
        } else if (sortBy.name().equals("LOCATION")) {
            sortByLocation();
        }
    }

    private static int getPrimaryColorId(FeatureType type) {
        int id = 0;

        switch (type) {
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

    private static int getToolbarColorId(FeatureType type) {
        int id = 0;

        switch (type) {
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

    private int getTitle(FeatureType type) {
        int id = 0;

        switch (type) {
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
    public void onBackPressed() {
        if (isSearchOpen) {
            handleMenuSearch();
            return;
        }
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_content, R.anim.slide_in);

        if (!fromItinerary) {
            Intent intent = new Intent(this, MainMap.class);
            intent.putExtra("type", "features");
            intent.putExtra("featureType", GlobalsClass.FeatureType.ITINERARY.toString());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
        finish();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mActionSort = menu.findItem(R.id.action_sort_by_title);
        mActionSortML = menu.findItem(R.id.action_sort_by_mylocation);
        mActionSearch = menu.findItem(R.id.action_search);
        mActionShowOnMap = menu.findItem(R.id.action_show_markers);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_items_list, menu);

        return true;
    }

    protected void handleMenuSearch(){
        ActionBar action = getSupportActionBar(); //get the actionbar

        if (isSearchOpen) { //test if the search is open
            action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
            action.setDisplayShowTitleEnabled(true); //show the title in the action bar
            mActionShowOnMap.setVisible(true);
            mActionSort.setVisible(true);

            //hides the keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mSearchEditText.getWindowToken(), 0);
            list.setAdapter(adapter);

            //add the search icon in the action bar
            mActionSearch.setIcon(getResources().getDrawable(R.drawable.ic_search_white_24dp));

            isSearchOpen = false;
        } else { //open the search entry
            mActionSort.setVisible(false);
            mActionShowOnMap.setVisible(false);
            action.setDisplayShowCustomEnabled(true); //enable it to display a custom view in the action bar

            action.setCustomView(R.layout.search_bar);//add the custom view
            action.getCustomView().setLayoutParams(new Toolbar.LayoutParams(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT));
            action.setDisplayShowTitleEnabled(false); //hide the title
            action.setDisplayUseLogoEnabled(false);

            mSearchEditText = (EditText)action.getCustomView().findViewById(R.id.edtSearch); //the text editor

            mSearchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    doSearch(mSearchEditText.getText().toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            //this is a listener to do a search when the user clicks on search button
            mSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        doSearch(mSearchEditText.getText().toString());
                        return true;
                }
                    return false;
                }
            });

            mSearchEditText.requestFocus();

            //open the keyboard focused in the edtSearch
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(mSearchEditText, InputMethodManager.SHOW_IMPLICIT);

            //add the close icon
            mActionSearch.setIcon(getResources().getDrawable(R.drawable.ic_search_black_24dp));

            isSearchOpen = true;
        }
    }

    private void doSearch(String sequence) {
        ArrayList<TourFeature> found_items = new ArrayList<TourFeature>();
        for (int i = 0; i < items.size(); i++) {
            if(items.get(i).getString("name").toLowerCase().contains(sequence.toLowerCase())) {
                found_items.add(items.get(i));
            }
        }
        TourFeatureItemsAdapter adapter = new TourFeatureItemsAdapter(this, S_ACTIVITY_NAME, found_items, adapterLayouts[list_type],fromItinerary, selectedDay, indexToAssign);
        list.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_sort_by_title:
                Collections.sort(items, new CustomComparator());
                TourFeatureItemsAdapter adapter = new TourFeatureItemsAdapter(this, S_ACTIVITY_NAME, items,adapterLayouts[list_type],fromItinerary, selectedDay, indexToAssign);
                list.setAdapter(adapter);
                sortByName();
                break;
            case R.id.action_sort_by_mylocation:
                sortByLocation();
                break;
            case R.id.action_search:
                handleMenuSearch();
                break;
            case R.id.action_show_markers:
                Intent intent = new Intent(ItemsListActivity.this, MainMap.class);
                intent.putExtra("type", "features");
                intent.putExtra("featureType", S_ACTIVITY_NAME.toString());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;
            /* For first official version release, temporarily blocked this menu
            case R.id.action_reshape:
                if(list_type == 0){
                    list.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
                    item.setTitle(getString(R.string.items_list_as_row));
                } else {
                    list.setLayoutManager(mLayoutManager);
                    item.setTitle(getString(R.string.items_list_as_grid));
                }
                list_type = 1 - list_type;
                adapter = new TourFeatureItemsAdapter(this, S_ACTIVITY_NAME, items, adapterLayouts[list_type],fromItinerary, selectedDay, indexToAssign);
                list.setAdapter(adapter);
                break;
            */

        }
        return super.onOptionsItemSelected(item);
    }

    private void sortByName() {
        Collections.sort(items, new CustomComparator());
        adapter = new TourFeatureItemsAdapter(this, S_ACTIVITY_NAME, items, adapterLayouts[list_type],fromItinerary, selectedDay, indexToAssign);
        list.setAdapter(adapter);
        sortBy = SortBy.NAME;
    }

    private void sortByLocation() {
        if(globalVariables.getCurrentLoc()==null)
            currentLoc = new Location("service Provider");
        else
            currentLoc = globalVariables.getCurrentLoc();

        if (currentLoc == null || currentLoc.getLatitude() == 0 || currentLoc.getLongitude() == 0) {
            Toast.makeText(this, R.string.toast_no_current_position, Toast.LENGTH_LONG).show();
            return;
        }
        Collections.sort(items, new LocationComparator());
        adapter = new TourFeatureItemsAdapter(this, S_ACTIVITY_NAME, items, adapterLayouts[list_type],fromItinerary, selectedDay, indexToAssign);
        list.setAdapter(adapter);
        sortBy = SortBy.LOCATION;
    }

    public class CustomComparator implements Comparator<TourFeature> {
        @Override
        public int compare(TourFeature o1, TourFeature o2) {
            return o1.getString("name").compareTo(o2.getString("name"));
        }
    }

    public class LocationComparator implements Comparator<TourFeature> {
        @Override
        public int compare(TourFeature o1, TourFeature o2) {
            float[] distance1 = new float[1];
            float[] distance2 = new float[1];

            android.location.Location.distanceBetween(currentLoc.getLatitude(), currentLoc.getLongitude(), o1.getLatitude(), o1.getLongitude(), distance1);
            android.location.Location.distanceBetween(currentLoc.getLatitude(), currentLoc.getLongitude(), o2.getLatitude(), o2.getLongitude(), distance2);

            return (int)distance1[0] - (int)distance2[0];
        }
    }
}