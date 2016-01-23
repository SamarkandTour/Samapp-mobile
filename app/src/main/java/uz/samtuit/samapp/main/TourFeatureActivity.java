package uz.samtuit.samapp.main;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.LinkedList;

import uz.samtuit.samapp.util.BitmapUtil;
import uz.samtuit.samapp.util.FileUtil;
import uz.samtuit.samapp.util.GlobalsClass;
import uz.samtuit.samapp.util.ItineraryList;
import uz.samtuit.samapp.util.TourFeature;

public class TourFeatureActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener {

    private MenuItem mActionNavigate;
    private String name;
    private View mAppBarLayout;


    private ImageButton callBtn, linkBtn;
    private int selectedDay = 1;
    private String featureType;
    private String url, wifi, telNum;
    private SharedPreferences sharedPreferences;
    private TextView mInfo;
    private TextView mAddress;
    private SpannableString s;
    private TextView mPrice;
    private TextView mOpen;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_feature);

        final Bundle extras = getIntent().getExtras();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        name = extras.getString("name");
        s = new SpannableString(name);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");
        s.setSpan(new CustomTypefaceSpan("", tf), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);
        getWindow().setBackgroundDrawable(getResources().getDrawable(extras.getInt("primaryColorId")));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(s);
        toolBarLayout.setContentScrimColor(getResources().getColor(extras.getInt("primaryColorId")));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_content, R.anim.slide_in);
            }
        });

        mInfo = (TextView)findViewById(R.id.tour_feature_info);
        mAddress = (TextView)findViewById(R.id.tour_feature_address);
        mPrice = (TextView)findViewById(R.id.tour_feature_price);
        mOpen = (TextView)findViewById(R.id.tour_feature_open);
        mAppBarLayout = findViewById(R.id.toolbar_layout);

        // Floating action bar
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddToMyItinerary(view);
            }
        });

        // Photo
        LoadImageFromExternalStorage loadImageFromExternalStorage = new LoadImageFromExternalStorage();
        loadImageFromExternalStorage.execute(extras.getString("photo"));

        // Loc
        featureType = extras.getString("featureType");

        // Rating
        int Rating = extras.getInt("rating");
        Log.e("Rating", Rating + "");

        ImageView star1 = (ImageView)findViewById(R.id.star1);
        ImageView star2 = (ImageView)findViewById(R.id.star2);
        ImageView star3 = (ImageView)findViewById(R.id.star3);
        ImageView star4 = (ImageView)findViewById(R.id.star4);
        ImageView star5 = (ImageView)findViewById(R.id.star5);

        if(Rating>4)
            star5.setImageResource(R.drawable.ic_star_rate_white_18dp);
        if(Rating>3)
            star4.setImageResource(R.drawable.ic_star_rate_white_18dp);
        if(Rating>2)
            star3.setImageResource(R.drawable.ic_star_rate_white_18dp);
        if(Rating>1)
            star2.setImageResource(R.drawable.ic_star_rate_white_18dp);
        if(Rating>0)
            star1.setImageResource(R.drawable.ic_star_rate_white_18dp);

        // Wifi
        wifi = extras.getString("wifi");

        ImageView wifiIcon = (ImageView) findViewById(R.id.wifi);
        callBtn = (ImageButton) findViewById(R.id.call_button);
        linkBtn = (ImageButton) findViewById(R.id.hotel_link_btn);
        wifiIcon.setColorFilter(Color.BLACK);
        callBtn.setColorFilter(Color.BLACK);
        linkBtn.setColorFilter(Color.BLACK);

        if (wifi.length() != 0 && wifi.equals("Yes")) {

            wifiIcon.clearColorFilter();
        }

        // Tel
        if ((telNum = extras.getString("tel")).length() != 0) {

            callBtn.clearColorFilter();
            callBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + telNum));
                    startActivity(intent);
                }
            });
        }

        // URL
        if ((url = extras.getString("url")).length() != 0) {

            linkBtn.clearColorFilter();
            linkBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (!url.startsWith("https://") && !url.startsWith("http://")) {
                            url = "http://" + url;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            });
        }

        // Description
        mInfo.setText(extras.getString("desc"));
        mAddress.setText(extras.getString("addr"));
        mPrice.setText(extras.getString("price"));
        mOpen.setText(extras.getString("open"));

        // Index
        index = extras.getInt("index");

        extras.clear();

        sharedPreferences = this.getSharedPreferences("SamTour_Pref",0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        overridePendingTransition(R.anim.slide_content, R.anim.slide_in);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_content, R.anim.slide_in);
    }

    public void AddToMyItinerary(final View view){
        final GlobalsClass globals = (GlobalsClass)getApplicationContext();
        final Dialog d = new Dialog(TourFeatureActivity.this);

        final NumberPicker numberPicker = (NumberPicker)d.findViewById(R.id.num_pckr);
        numberPicker.setMaxValue(5);
        numberPicker.setMinValue(1);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setOnValueChangedListener(this);

        d.setTitle(getString(R.string.itinerary_pick_day));
        d.setContentView(R.layout.day_dialog);
        Button b1 = (Button)d.findViewById(R.id.ok_btn);
        Button b2 = (Button)d.findViewById(R.id.cancel_btn);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Bundle extras = getIntent().getExtras();
                ItineraryList list = ItineraryList.getInstance();
                TourFeature feature = list.findFeature(getApplicationContext(), extras.getString("name"));
                LinkedList<TourFeature> itineraryItems = globals.getItineraryFeatures();

                if (itineraryItems.contains(feature)) {
                    Snackbar.make(view, getString(R.string.itinerary_already_exist), Snackbar.LENGTH_SHORT).show();
                } else {
                    feature.setDay(selectedDay);
                    list.addNewFeatureToItineraryList(feature);
                    //list.setNewItinearyFeaturesToGlobal(getApplicationContext(), itineraryItems);
                    Log.e("QUERY", TourFeatureActivity.this.getSharedPreferences("SamTour_Pref", 0).getString("app_lang", null));
                    list.itineraryWriteToGeoJSONFile(getApplicationContext(), TourFeatureActivity.this.getSharedPreferences("SamTour_Pref", 0).getString("app_lang", null));
                    Snackbar.make(view, getString(R.string.itinerary_added_successfully), Snackbar.LENGTH_LONG).show();
                    d.hide();
                }
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.hide();
            }
        });
        d.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mActionNavigate = menu.findItem(R.id.action_location);
        mActionNavigate.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent intent = new Intent(TourFeatureActivity.this, MainMap.class);
                intent.putExtra("type", "feature");
                intent.putExtra("name", name);
                intent.putExtra("featureType", featureType);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return false;
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        if (oldVal == newVal) {
            selectedDay = oldVal;
        } else {
            selectedDay = newVal;
        }
    }

    class LoadImageFromExternalStorage extends AsyncTask<String,String,Void>{
        Drawable dr = null;

        @Override
        protected Void doInBackground(String... params) {
            if (params[0] != null) {
                String encodedBytes = FileUtil.fileReadFromExternalDir(TourFeatureActivity.this, params[0]);
                dr = new BitmapDrawable(BitmapUtil.decodeBase64Image(encodedBytes));
                publishProgress(encodedBytes);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mAppBarLayout.setBackground(dr);
        }
    }
}
