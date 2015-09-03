package uz.samtuit.sammap.samapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class HotelActivity extends ActionBarActivity {

    private MenuItem mActionNavigate;
    private TextView address,titleSmall,description;
    private double lat, longt;
    private String name;
    private ImageView imageView;
    private ImageButton call,link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel);
        final Bundle extras = getIntent().getExtras();

        //ActionBar setting
        Toolbar toolbar = (Toolbar)findViewById(R.id.hotel_tool_bar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/segoeui.ttf");
        SpannableString s = new SpannableString(extras.getString("name"));
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
        tf = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");

        imageView = (ImageView)findViewById(R.id.hotel_image);
        address = (TextView)findViewById(R.id.hotel_address);
        address.setText(extras.getString("address"));
        link = (ImageButton)findViewById(R.id.hotel_link_btn);
        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = extras.getString("url");
                if (!url.startsWith("https://") && !url.startsWith("http://")){
                    url = "http://" + url;
                }
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        description = (TextView)findViewById(R.id.hotel_desc);
        titleSmall = (TextView)findViewById(R.id.hotel_title_small);
        ImageView star1 = (ImageView)findViewById(R.id.star1);
        ImageView star2 = (ImageView)findViewById(R.id.star2);
        ImageView star3 = (ImageView)findViewById(R.id.star3);
        ImageView star4 = (ImageView)findViewById(R.id.star4);
        ImageView star5 = (ImageView)findViewById(R.id.star5);
        lat = extras.getDouble("lat");
        longt = extras.getDouble("long");
        name = extras.getString("name");

        call = (ImageButton)findViewById(R.id.call_button);

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + extras.getString("telephone")));
                startActivity(intent);
            }
        });

        byte[] decodedString = Base64.decode(extras.getString("photo"), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        Drawable dr = new BitmapDrawable(decodedByte);
        imageView.setImageDrawable(dr);
        titleSmall.setText(extras.getString("name"));
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

        description.setText(extras.getString("desc"));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_food, menu);
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mActionNavigate = menu.findItem(R.id.action_navigate);
        mActionNavigate.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent intent = new Intent(HotelActivity.this, MainMap.class);
                intent.putExtra("lat", lat);
                intent.putExtra("long", longt);
                intent.putExtra("name", name);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
