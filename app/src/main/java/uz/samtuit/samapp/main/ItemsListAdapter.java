package uz.samtuit.samapp.main;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import uz.samtuit.samapp.util.TourFeature;
import uz.samtuit.sammap.main.R;

class ItemsListAdapter extends ArrayAdapter<TourFeature> {

    Context context;
    ArrayList<TourFeature> data = null;
    private int layoutResourceId;

    public ItemsListAdapter(Context context, int layoutResourceId, ArrayList<TourFeature> data) {
        // TODO Auto-generated constructor stub
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null)
        {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
        }
        TourFeature hotel = data.get(position);
        TextView name = (TextView) convertView.findViewById(R.id.title);
        TextView revs = (TextView) convertView.findViewById(R.id.reviewsCount);
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Thin.ttf");
        ImageView mainImage = (ImageView)convertView.findViewById(R.id.image);
        ImageView star1 = (ImageView)convertView.findViewById(R.id.star1);
        ImageView star2 = (ImageView)convertView.findViewById(R.id.star2);
        ImageView star3 = (ImageView)convertView.findViewById(R.id.star3);
        ImageView star4 = (ImageView)convertView.findViewById(R.id.star4);
        ImageView star5 = (ImageView)convertView.findViewById(R.id.star5);
        mainImage.setImageResource(R.drawable.rasm);
        int Rating = hotel.getRating();
        if(Rating>4)
            star5.setImageResource(R.drawable.ic_star_rate_white_18dp);
        if(Rating>3)
            star4.setImageResource(R.drawable.ic_star_rate_white_18dp);
        if(Rating>2)
            star3.setImageResource(R.drawable.ic_star_rate_white_18dp);
        if(Rating >1)
            star2.setImageResource(R.drawable.ic_star_rate_white_18dp);
        if(Rating> 0)
            star1.setImageResource(R.drawable.ic_star_rate_white_18dp);
        name.setText(hotel.getString("name"));
        StringBuilder sb = new StringBuilder();
        sb.append("");
        //sb.append(hotel.Reviews);
        revs.setText(sb.toString());
        revs.setTag("revs");
        name.setTag(hotel.getString("name"));
        name.setTypeface(tf);
        return convertView;
    }
}