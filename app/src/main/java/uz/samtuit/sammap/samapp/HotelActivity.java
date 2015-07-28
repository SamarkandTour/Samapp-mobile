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
    public Hotels data[];
    TextView title,titleSmall,description;
    LinearLayout imageLayout;
    ImageView image,star1,star2,star3,star4,star5;
    ImageButton call,location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel);
        final Bundle extras = getIntent().getExtras();
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");


        title = (TextView)findViewById(R.id.HotelTitle);
        title.setTypeface(tf);
        imageLayout = (LinearLayout)findViewById(R.id.imageLayout);
        description = (TextView)findViewById(R.id.hotelDesc);
        titleSmall = (TextView)findViewById(R.id.hotelTitleSmall);
        int s = 0;
        ImageView star1 = (ImageView)findViewById(R.id.star1);
        ImageView star2 = (ImageView)findViewById(R.id.star2);
        ImageView star3 = (ImageView)findViewById(R.id.star3);
        ImageView star4 = (ImageView)findViewById(R.id.star4);
        ImageView star5 = (ImageView)findViewById(R.id.star5);
        call = (ImageButton)findViewById(R.id.callBtn);

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:"+extras.getString("telephone")));
                startActivity(intent);
            }
        });

        ImageButton locate = (ImageButton)findViewById(R.id.hotelLocation);
        locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HotelActivity.this,MainMap.class);
                intent.putExtra("lat",extras.getDouble("lat"));
                intent.putExtra("long",extras.getDouble("long"));
                intent.putExtra("name",extras.getString("name"));
                startActivity(intent);
            }
        });

        titleSmall.setText(extras.getString("name"));
        title.setText(extras.getString("name"));
        Log.e("SDAS",extras.getString("name"));
        int Rating = extras.getInt("rating");
        if(Rating>4)
                star5.setImageResource(R.drawable.star_yellow_icon);
        if(Rating>3)
                star4.setImageResource(R.drawable.star_yellow_icon);
        if(Rating>2)
                star3.setImageResource(R.drawable.star_yellow_icon);
        if(Rating>1)
                star2.setImageResource(R.drawable.star_yellow_icon);
        if(Rating>0)
                star1.setImageResource(R.drawable.star_yellow_icon);

        description.setText(extras.getString("desc"));
    }
}
