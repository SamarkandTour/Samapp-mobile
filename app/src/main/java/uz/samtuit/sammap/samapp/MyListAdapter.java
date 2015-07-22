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

class MyListAdapter extends ArrayAdapter<Hotels> {

    Context context;
    Hotels[] data = null;
    String reviews;
    int rating;
    private int layoutResourceId;

    public MyListAdapter(Context context,int layoutResourceId, Hotels[] data) {
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
        Hotels hotel = data[position];
        TextView name = (TextView) convertView.findViewById(R.id.title);
        TextView revs = (TextView) convertView.findViewById(R.id.reviewsCount);
        String Act = "drawable/star2",mainImg = "drawable/rasm";
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Thin.ttf");
        ImageView mainImage = (ImageView)convertView.findViewById(R.id.image);
        ImageView star1 = (ImageView)convertView.findViewById(R.id.star1);
        ImageView star2 = (ImageView)convertView.findViewById(R.id.star2);
        ImageView star3 = (ImageView)convertView.findViewById(R.id.star3);
        ImageView star4 = (ImageView)convertView.findViewById(R.id.star4);
        ImageView star5 = (ImageView)convertView.findViewById(R.id.star5);
        int ActImageResource = context.getResources().getIdentifier(Act, null, context.getPackageName());
        int mainImgResource = context.getResources().getIdentifier(mainImg, null, context.getPackageName());
        Drawable imageAct = context.getResources().getDrawable(ActImageResource);
        Drawable imageMain = context.getResources().getDrawable(mainImgResource);
        mainImage.setImageDrawable(imageMain);
        if(hotel.Rating>4)
            star5.setImageDrawable(imageAct);
        if(hotel.Rating>3)
            star4.setImageDrawable(imageAct);
        if(hotel.Rating>2)
            star3.setImageDrawable(imageAct);
        if(hotel.Rating>1)
            star2.setImageDrawable(imageAct);
        if(hotel.Rating>0)
            star1.setImageDrawable(imageAct);

        name.setText(hotel.Name);
        StringBuilder sb = new StringBuilder();
        sb.append("");
        sb.append(hotel.Reviews);
        revs.setText(sb.toString());
        revs.setTag("revs");
        name.setTag(hotel.Name);
        name.setTypeface(tf);
        return convertView;
    }
}