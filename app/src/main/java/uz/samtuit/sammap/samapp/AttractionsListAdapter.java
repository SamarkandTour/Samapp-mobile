package uz.samtuit.sammap.samapp;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

class AttractionsListAdapter extends ArrayAdapter<Attractions> {

    Context context;
    ArrayList<Attractions> data = null;
    private int layoutResourceId;

    public AttractionsListAdapter(Context context,int layoutResourceId, ArrayList<Attractions> data) {
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
        Attractions attraction = data.get(position);
        TextView name = (TextView) convertView.findViewById(R.id.title);
        TextView revs = (TextView) convertView.findViewById(R.id.reviewsCount);
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Thin.ttf");
        ImageView mainImage = (ImageView)convertView.findViewById(R.id.image);
        ImageView star1 = (ImageView)convertView.findViewById(R.id.star1);
        ImageView star2 = (ImageView)convertView.findViewById(R.id.star2);
        ImageView star3 = (ImageView)convertView.findViewById(R.id.star3);
        ImageView star4 = (ImageView)convertView.findViewById(R.id.star4);
        ImageView star5 = (ImageView)convertView.findViewById(R.id.star5);
        mainImage.setImageResource(R.drawable.round);
        if(attraction.Rating>4)
            star5.setImageResource(R.drawable.ic_star_rate_white_18dp);
        if(attraction.Rating>3)
            star4.setImageResource(R.drawable.ic_star_rate_white_18dp);
        if(attraction.Rating>2)
            star3.setImageResource(R.drawable.ic_star_rate_white_18dp);
        if(attraction.Rating>1)
            star2.setImageResource(R.drawable.ic_star_rate_white_18dp);
        if(attraction.Rating>0)
            star1.setImageResource(R.drawable.ic_star_rate_white_18dp);


        name.setText(attraction.Name);
        StringBuilder sb = new StringBuilder();
        sb.append("");
        sb.append(attraction.Reviews);
        revs.setText(sb.toString());
        revs.setTag("revs");
        name.setTag(attraction.Name);
        name.setTypeface(tf);
        return convertView;
    }
}