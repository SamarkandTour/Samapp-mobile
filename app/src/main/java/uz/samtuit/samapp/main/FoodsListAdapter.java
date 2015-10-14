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

import uz.samtuit.sammap.main.R;

class FoodsListAdapter extends ArrayAdapter<Foods> {

    Context context;
    ArrayList<Foods> data = null;
    private int layoutResourceId;

    public FoodsListAdapter(Context context,int layoutResourceId, ArrayList<Foods> data) {
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
        Foods food = data.get(position);
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
        if(food.Rating>4)
            star5.setImageResource(R.drawable.ic_star_rate_white_18dp);
        if(food.Rating>3)
            star4.setImageResource(R.drawable.ic_star_rate_white_18dp);
        if(food.Rating>2)
            star3.setImageResource(R.drawable.ic_star_rate_white_18dp);
        if(food.Rating>1)
            star2.setImageResource(R.drawable.ic_star_rate_white_18dp);
        if(food.Rating>0)
            star1.setImageResource(R.drawable.ic_star_rate_white_18dp);

        name.setText(food.Name);
        StringBuilder sb = new StringBuilder();
        sb.append("");
        sb.append(food.Reviews);
        revs.setText(sb.toString());
        revs.setTag("revs");
        name.setTag(food.Name);
        name.setTypeface(tf);
        return convertView;
    }
}