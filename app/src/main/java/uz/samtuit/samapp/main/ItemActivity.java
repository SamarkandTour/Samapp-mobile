package uz.samtuit.samapp.main;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import uz.samtuit.samapp.util.BitmapUtil;
import uz.samtuit.samapp.util.FileUtil;


public class ItemActivity extends ActionBarActivity {

    private MenuItem mActionNavigate;
    private double latitude, longitude;
    private String name;
    private RelativeLayout relLayout;
    private ImageView imageView;
    private ImageButton callBtn, linkBtn;
    private String featureType;
    private String url, wifi, telNum;

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
        final Bundle extras = getIntent().getExtras();

        //ActionBar setting
        Toolbar toolbar = (Toolbar)findViewById(R.id.hotel_tool_bar);
        relLayout = (RelativeLayout)findViewById(R.id.itemRelLayout);
        toolbar.setBackgroundColor(getResources().getColor(R.color.list_item_tool));
        setSupportActionBar(toolbar);
        relLayout.setBackground(getResources().getDrawable(extras.getInt("primaryColorId")));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/segoeui.ttf");
        name = extras.getString("name");
        SpannableString s = new SpannableString(name);
        s.setSpan(new CustomTypefaceSpan("", tf), 0, s.length(),
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
        latitude = extras.getDouble("lat");
        longitude = extras.getDouble("long");

        // Photo
        imageView = (ImageView)findViewById(R.id.hotel_image);
        LoadImageFromExternalStorage loadImageFromExternalStorage = new LoadImageFromExternalStorage();
        loadImageFromExternalStorage.execute(extras.getString("photo"));

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
        TextView titleSmall = (TextView)findViewById(R.id.hotel_title_small);
        titleSmall.setText(extras.getString("name"));

        // Description
        TextView description = (TextView)findViewById(R.id.hotel_desc);
        description.setText(extras.getString("desc"));

        // Addr
        TextView address = (TextView)findViewById(R.id.hotel_address);
        address.setText(extras.getString("addr"));

        // Price
        TextView price = (TextView)findViewById(R.id.price);
        price.setText(extras.getString("price"));

        // Open
        TextView open = (TextView)findViewById(R.id.open);
        open.setText(extras.getString("open"));

        extras.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_item, menu);
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mActionNavigate = menu.findItem(R.id.action_navigate);
        mActionNavigate.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent intent = new Intent(ItemActivity.this, MainMap.class);
                intent.putExtra("type", "feature");
                intent.putExtra("lat", latitude);
                intent.putExtra("long", longitude);
                Log.e(latitude + "", longitude + "");
                intent.putExtra("name", name);
                intent.putExtra("featureType",featureType);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return false;
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class LoadImageFromExternalStorage extends AsyncTask<String,String,Void>{

        @Override
        protected Void doInBackground(String... params) {
            if (params[0] != null) {
                String encodedBytes = FileUtil.fileReadFromExternalDir(ItemActivity.this, params[0]);
                publishProgress(encodedBytes);
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Drawable dr = new BitmapDrawable(BitmapUtil.decodeBase64Image(values[0]));
            imageView.setImageDrawable(dr);
        }
    }

}
