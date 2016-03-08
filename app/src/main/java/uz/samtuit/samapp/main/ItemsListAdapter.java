package uz.samtuit.samapp.main;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import uz.samtuit.samapp.util.BitmapUtil;
import uz.samtuit.samapp.util.FileUtil;
import uz.samtuit.samapp.util.GlobalsClass;
import uz.samtuit.samapp.util.TourFeature;

class ItemsListAdapter extends ArrayAdapter<TourFeature> {

    Context context;
    ArrayList<TourFeature> data = null;
    private int layoutResourceId;
    private GlobalsClass globalVariables;
    private Location currentLoc;

    public ItemsListAdapter(Context context, int layoutResourceId, ArrayList<TourFeature> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;

        globalVariables = (GlobalsClass)context.getApplicationContext();
        currentLoc = globalVariables.getCurrentLoc();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
        {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.title);
        TextView revs = (TextView) convertView.findViewById(R.id.reviewsCount);
        TextView distanceView = (TextView) convertView.findViewById(R.id.distance);
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Thin.ttf");
        ImageView star1 = (ImageView)convertView.findViewById(R.id.star1);
        ImageView star2 = (ImageView)convertView.findViewById(R.id.star2);
        ImageView star3 = (ImageView)convertView.findViewById(R.id.star3);
        ImageView star4 = (ImageView)convertView.findViewById(R.id.star4);
        ImageView star5 = (ImageView)convertView.findViewById(R.id.star5);

        String fileName = data.get(position).getPhoto();
        ImageView mainImage = (ImageView) convertView.findViewById(R.id.listViewThumbnail);
        try {
            if (fileName != null) {
                String encodedBytes = FileUtil.fileReadFromExternalDir(context, fileName);
                Bitmap decodedBytes = BitmapUtil.decodeBase64Bitmap(encodedBytes, mainImage.getLayoutParams().height , mainImage.getLayoutParams().width);
                BitmapUtil.RoundedDrawable roundedDrawable = new BitmapUtil.RoundedDrawable(decodedBytes, false);
                mainImage.setImageDrawable(roundedDrawable);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

        TourFeature item = data.get(position);
        int Rating = item.getRating();
        star1.setImageResource(R.drawable.ic_star_rate_black_18dp);
        star2.setImageResource(R.drawable.ic_star_rate_black_18dp);
        star3.setImageResource(R.drawable.ic_star_rate_black_18dp);
        star4.setImageResource(R.drawable.ic_star_rate_black_18dp);
        star5.setImageResource(R.drawable.ic_star_rate_black_18dp);
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

        name.setText(item.getString("name"));
        name.setTag(item.getString("name"));

        // Distance
        if (currentLoc != null && (currentLoc.getLatitude() != 0 || currentLoc.getLongitude() != 0)
                && ItemsListActivity.sortBy == ItemsListActivity.SortBy.LOCATION) {
            float[] distance = new float[1];
            android.location.Location.distanceBetween(currentLoc.getLatitude(), currentLoc.getLongitude(), item.getLatitude(), item.getLongitude(), distance);
            distanceView.setVisibility(View.VISIBLE);
            distanceView.setText((distance[0] > 1000) ? Math.round(distance[0]/1000 * 10.0) / 10.0 + " km" : (int) distance[0] + " m");
        }

        // Reviews
        StringBuilder sb = new StringBuilder();
        sb.append("");
        revs.setText(sb.toString());
        revs.setTag("revs");

        name.setTypeface(tf);
        return convertView;
    }
}