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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import uz.samtuit.samapp.util.BitmapUtil;
import uz.samtuit.samapp.util.CustomTypefaceSpan;
import uz.samtuit.samapp.util.FileUtil;
import uz.samtuit.samapp.util.GlobalsClass;
import uz.samtuit.samapp.util.ItineraryList;
import uz.samtuit.samapp.util.TourFeature;
import uz.samtuit.samapp.util.TourFeatureList;

public class ItemActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener {
    private MenuItem mActionNavigate;
    private String name;
    private View mAppBarLayout;
    private ImageButton callBtn, linkBtn, bookingBtn;
    private int selectedDay = 1;
    private String featureType;
    private String wifi, telNum, url, bookingUrl;
    private String photoSrc = "";
    private TextView mInfo;
    private TextView mAddress;
    private SpannableString s;
    private TextView mPrice;
    private TextView mOpen;
    boolean fromItinerary = false;
    @InjectView(R.id.toolbar) Toolbar toolbar;
    @InjectView(R.id.fab)   FloatingActionButton fab;
    @InjectView(R.id.star1) ImageView star1;
    @InjectView(R.id.star2) ImageView star2;
    @InjectView(R.id.star3) ImageView star3;
    @InjectView(R.id.star4) ImageView star4;
    @InjectView(R.id.star5)  ImageView star5;

    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
    private int mShortAnimationDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            Log.e("TourFeatureActivity", "onRestore()");

            GlobalsClass globalVariables = (GlobalsClass)getApplicationContext();
            ArrayList<TourFeature> featureList = globalVariables.getTourFeatures(GlobalsClass.FeatureType.HOTEL);

            // When Features are out of memory, App should need to restart from the start
            if (featureList == null) {
                Log.e("TourFeatureActivity", "featureList=null");
                SharedPreferences pref = globalVariables.getApplicationContext().getSharedPreferences("SamTour_Pref", 0);
                String currentLang = pref.getString("app_lang", null);
                TourFeatureList.loadAllFeaturesToMemory(this, currentLang);
            }
        }

        setContentView(R.layout.activity_item);
        ButterKnife.inject(this);

        final Bundle extras = getIntent().getExtras();
        fromItinerary = extras.getBoolean("from_itinerary");
        setSupportActionBar(toolbar);
        name = extras.getString("name");
        s = new SpannableString(name);
        Typeface tf = Typeface.createFromAsset(getAssets(), "font/Roboto-Thin.ttf");
        s.setSpan(new CustomTypefaceSpan("", tf), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);
        getWindow().setBackgroundDrawable(getResources().getDrawable(extras.getInt("primaryColorId")));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(s);
        toolBarLayout.setContentScrimColor(getResources().getColor(extras.getInt("primaryColorId")));
        fab.setColorFilter(getResources().getColor(extras.getInt("primaryColorId")));
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

        ImageView wifiIcon = (ImageView) findViewById(R.id.wifi);
        callBtn = (ImageButton) findViewById(R.id.call_btn);
        linkBtn = (ImageButton) findViewById(R.id.url_btn);
        bookingBtn = (ImageButton) findViewById(R.id.booking_btn);
        wifiIcon.setColorFilter(Color.BLACK);
        callBtn.setColorFilter(Color.BLACK);
        linkBtn.setColorFilter(Color.BLACK);
        bookingBtn.setColorFilter(Color.BLACK);

        // Floating action button
        GlobalsClass globals = (GlobalsClass)getApplicationContext();
        ItineraryList list = ItineraryList.getInstance();
        TourFeature feature = list.findFeature(getApplicationContext(), extras.getString("name"));
        LinkedList<TourFeature> itineraryItems = globals.getItineraryFeatures();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddToMyItinerary(view);
            }
        });
        if(itineraryItems.contains(feature)) {
            alrearyInItinerary();
        }
        try {
            photoSrc = extras.getString("photo");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Photo
        LoadImageFromExternalStorage loadImageFromExternalStorage = new LoadImageFromExternalStorage();
        loadImageFromExternalStorage.execute(photoSrc);

        // Loc
        featureType = extras.getString("featureType");

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
        if (wifi.length() != 0 && wifi.equalsIgnoreCase("Yes")) {
            wifiIcon.clearColorFilter();
        }

        // Tel
        if ((telNum = extras.getString("tel")).length() != 0) {
            callBtn.clearColorFilter();
            callBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + GlobalsClass.ParseCellPhoneNumber(telNum)));
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

        // Booking URL
        if ((bookingUrl = extras.getString("booking")).length() != 0) {
            bookingBtn.clearColorFilter();
            bookingBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (!bookingUrl.startsWith("https://") && !bookingUrl.startsWith("http://")) {
                            bookingUrl = "http://" + bookingUrl;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(bookingUrl));
                    startActivity(i);
                }
            });
        }

        // Description
        mInfo.setText(extras.getString("desc"));
        mAddress.setText(extras.getString("addr"));
        mPrice.setText(extras.getString("price"));
        mOpen.setText(extras.getString("open"));

        extras.clear();
    }

    private void alrearyInItinerary() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, getString(R.string.itinerary_already_exist),Snackbar.LENGTH_SHORT).show();
            }
        });
        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_white_24dp));
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

        if(fromItinerary) {
            final Bundle extras = getIntent().getExtras();
            final ItineraryList list = ItineraryList.getInstance();
            final TourFeature feature = list.findFeature(getApplicationContext(), extras.getString("name"));
            LinkedList<TourFeature> itineraryItems = globals.getItineraryFeatures();
            if (itineraryItems.contains(feature)) {
                Snackbar.make(view, getString(R.string.itinerary_already_exist), Snackbar.LENGTH_SHORT).show();
            } else {
                int selectedIndex = extras.getInt("index");
                feature.setDay(extras.getInt("selected_day") + 1);
                list.addNewFeatureToItineraryList(feature, selectedIndex);
                list.itineraryWriteToGeoJSONFile(getApplicationContext(), ItemActivity.this.getSharedPreferences("SamTour_Pref", 0).getString("app_lang", null));
                Toast.makeText(ItemActivity.this, getString(R.string.itinerary_added_successfully),Toast.LENGTH_SHORT).show();

                finish();
                overridePendingTransition(R.anim.slide_content, R.anim.slide_in);
            }
        } else {
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
                    feature.setDay(selectedDay);
                    list.addNewFeatureToItineraryList(feature, extras.getInt("index"));
                    list.itineraryWriteToGeoJSONFile(getApplicationContext(), ItemActivity.this.getSharedPreferences("SamTour_Pref", 0).getString("app_lang", null));
                    Snackbar.make(view, getString(R.string.itinerary_added_successfully), Snackbar.LENGTH_LONG).show();
                    alrearyInItinerary();
                    d.dismiss();
                }
            });
            b2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    d.dismiss();
                }
            });
            d.show();
        }

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
                String encodedBytes = FileUtil.fileReadFromExternalDir(ItemActivity.this, params[0]);
                dr = new BitmapDrawable(BitmapUtil.decodeBase64Image(encodedBytes));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mAppBarLayout.setBackground(dr);
            mAppBarLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (photoSrc == "" || photoSrc == null) {
                    } else {
                        Intent intent = new Intent(ItemActivity.this, ImageViewingActivity.class);
                        intent.putExtra("photo", photoSrc);
                        intent.putExtra("name", name);
                        startActivity(intent);
                    }
                }
            });

            mShortAnimationDuration = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);
        }
    }
}
