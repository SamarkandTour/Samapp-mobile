package uz.samtuit.samapp.main;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.LinkedList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import uz.samtuit.samapp.util.BitmapUtil;
import uz.samtuit.samapp.util.FileUtil;
import uz.samtuit.samapp.util.GlobalsClass;
import uz.samtuit.samapp.util.ItineraryList;
import uz.samtuit.samapp.util.TourFeature;
import uz.samtuit.samapp.util.TypefaceHelper;


public class ItemActivity extends ActionBarActivity implements NumberPicker.OnValueChangeListener{

    private MenuItem mActionNavigate;
    private double latitude, longitude;
    private String name;


    private ImageButton callBtn, linkBtn;
    private int selectedDay = 1;
    private ImageButton mAddToMyItinerary;
    private String featureType;
    private String url, wifi, telNum;
    private SharedPreferences sharedPreferences;
    private int index;
    @InjectView(R.id.star1) ImageView star1;
    @InjectView(R.id.star2) ImageView star2;
    @InjectView(R.id.star3) ImageView star3;
    @InjectView(R.id.star4) ImageView star4;
    @InjectView(R.id.star5)  ImageView star5;
    @InjectView(R.id.tool_bar) Toolbar toolbar;
    @InjectView(R.id.itemRelLayout)  RelativeLayout relLayout;
    @InjectView(R.id.hotel_title_small) TextView titleSmall;
    @InjectView(R.id.hotel_desc) TextView description;
    @InjectView(R.id.hotel_address) TextView address;
    @InjectView(R.id.price) TextView price;
    @InjectView(R.id.open) TextView open;
    @InjectView(R.id.fab) FloatingActionButton fab;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        ButterKnife.inject(this);
        final Bundle extras = getIntent().getExtras();

        index = extras.getInt("index");

        //ActionBar setting
        toolbar.setBackgroundColor(getResources().getColor(extras.getInt("primaryColorId")));
        fab.setColorFilter(getResources().getColor(extras.getInt("primaryColorId")));
        setSupportActionBar(toolbar);
        relLayout.setBackground(getResources().getDrawable(extras.getInt("primaryColorId")));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mAddToMyItinerary = (ImageButton)findViewById(R.id.add_to_my_itinerary);
        name = extras.getString("name");
        SpannableString s = new SpannableString(name);
        s.setSpan(new CustomTypefaceSpan("", TypefaceHelper.getTypeface(getApplicationContext(),"segoeui")), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);
        toolbar.setTitle(s);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_content, R.anim.slide_in);
            }
        });
        //end ActionBar Setting

        // Loc
        featureType = extras.getString("featureType");
        if(featureType.equals(GlobalsClass.FeatureType.HOTEL.toString())||featureType.equals(GlobalsClass.FeatureType.FOODNDRINK.toString())){
            mAddToMyItinerary.setVisibility(View.GONE);
        }
        latitude = extras.getDouble("lat");
        longitude = extras.getDouble("long");

        // Photo

        BitmapUtil.setImageFromFileToView(this, extras.getString("photo"), (ImageView)findViewById(R.id.hotel_image));


        // Rating
        int Rating = extras.getInt("rating");

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
        if (wifi.length() != 0 && wifi.equals("Yes")) {
            ImageView wifi = (ImageView) findViewById(R.id.wifi);
            wifi.setVisibility(View.VISIBLE);
        }

        // Tel
        if ((telNum = extras.getString("tel")).length() != 0) {
            callBtn = (ImageButton) findViewById(R.id.call_button);
            callBtn.setVisibility(View.VISIBLE);
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
            linkBtn = (ImageButton) findViewById(R.id.hotel_link_btn);
            linkBtn.setVisibility(View.VISIBLE);
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

        // Title

        titleSmall.setText(extras.getString("name"));

        // Description
        description.setText(extras.getString("desc"));

        // Addr
        address.setText(extras.getString("addr"));

        // Price
        price.setText(extras.getString("price"));

        // Open
        open.setText(extras.getString("open"));

        // Index
        index = extras.getInt("index");

        extras.clear();

        sharedPreferences = this.getSharedPreferences("SamTour_Pref",0);
    }

    public void AddToMyItinerary(View view){
        final GlobalsClass globals = (GlobalsClass)getApplicationContext();
        LinkedList<TourFeature> itineraryItems;
        final Dialog d = new Dialog(ItemActivity.this);
        d.setTitle(getString(R.string.itinerary_pick_day));
        d.setContentView(R.layout.day_dialog);
        Button b1 = (Button)d.findViewById(R.id.ok_btn);
        Button b2 = (Button)d.findViewById(R.id.cancel_btn);
        final NumberPicker numberPicker = (NumberPicker)d.findViewById(R.id.num_pckr);
        numberPicker.setMaxValue(5);
        numberPicker.setMinValue(1);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setOnValueChangedListener(this);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Bundle extras = getIntent().getExtras();
                ItineraryList list = ItineraryList.getInstance();
                TourFeature feature = list.findFeature(getApplicationContext(), extras.getString("name"));
                LinkedList<TourFeature> itineraryItems = globals.getItineraryFeatures();
                if(itineraryItems.contains(feature)){
                    Toast.makeText(ItemActivity.this, getString(R.string.itinerary_already_exist), Toast.LENGTH_SHORT).show();
                }
                else {
                    feature.setDay(selectedDay);
                    list.addNewFeatureToItineraryList(feature,0);
                    list.sortItineraryList();
                    list.itineraryWriteToGeoJSONFile(getApplicationContext(), ItemActivity.this.getSharedPreferences("SamTour_Pref", 0).getString("app_lang", null));
                    Log.e("QUERY", ItemActivity.this.getSharedPreferences("SamTour_Pref", 0).getString("app_lang", null));
                    Toast.makeText(ItemActivity.this, getString(R.string.itinerary_added_successfully), Toast.LENGTH_LONG).show();
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
                Intent intent = new Intent(ItemActivity.this, MainMap.class);
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
        if(oldVal==newVal)
            selectedDay=oldVal;
        else
            selectedDay=newVal;
    }
}