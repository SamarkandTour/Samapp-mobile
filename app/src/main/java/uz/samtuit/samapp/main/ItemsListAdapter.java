package uz.samtuit.samapp.main;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import uz.samtuit.samapp.util.BitmapUtil;
import uz.samtuit.samapp.util.FileUtil;
import uz.samtuit.samapp.util.TourFeature;

class ItemsListAdapter extends ArrayAdapter<TourFeature> {

    Context context;
    ArrayList<TourFeature> data = null;
    private int layoutResourceId;

    public ItemsListAdapter(Context context, int layoutResourceId, ArrayList<TourFeature> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
        {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
        }

        LinearLayout holderLayout = (LinearLayout)convertView.findViewById(R.id.holder_layout);
        TextView name = (TextView) convertView.findViewById(R.id.title);
        TextView revs = (TextView) convertView.findViewById(R.id.reviewsCount);
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Thin.ttf");
        ImageView star1 = (ImageView)convertView.findViewById(R.id.star1);
        ImageView star2 = (ImageView)convertView.findViewById(R.id.star2);
        ImageView star3 = (ImageView)convertView.findViewById(R.id.star3);
        ImageView star4 = (ImageView)convertView.findViewById(R.id.star4);
        ImageView star5 = (ImageView)convertView.findViewById(R.id.star5);

        String fileName = data.get(position).getPhoto();
        try {
            if (fileName != null) {
                ImageView mainImage = (ImageView) convertView.findViewById(R.id.listViewThumbnail);
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
        StringBuilder sb = new StringBuilder();
        sb.append("");
        //sb.append(hotel.Reviews);
        revs.setText(sb.toString());
        revs.setTag("revs");
        name.setTag(item.getString("name"));
        name.setTypeface(tf);
        return convertView;
    }
}
