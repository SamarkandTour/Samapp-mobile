package uz.samtuit.samapp.main;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.LinkedList;

import uz.samtuit.samapp.util.TourFeature;
import uz.samtuit.sammap.main.R;

public class SugItineraryAdapter extends ArrayAdapter<TourFeature> {
    LinkedList<TourFeature> data = null;
    Context context;
    int layoutResId;
    int dataSize;

    public SugItineraryAdapter(Context context, int layoutResId, LinkedList<TourFeature> data)
    {
        super(context,layoutResId, data);

        this.context = context;
        this.data = data;
        this.layoutResId = layoutResId;

        dataSize = data.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(layoutResId,parent,false);
        }
        TextView SI_NAME = (TextView)convertView.findViewById(R.id.si_name);
        TextView SI_WALK = (TextView)convertView.findViewById(R.id.si_walk);
        TextView SI_CAR = (TextView)convertView.findViewById(R.id.si_car);
        TextView SI_DISTANCE = (TextView)convertView.findViewById(R.id.it_distance);
        ImageView SI_IMAGE = (ImageView)convertView.findViewById(R.id.si_image);

        SI_NAME.setText(data.get(position).getString("name"));

        if (position != (dataSize - 1)) { // No need to calculate for last feature
            double f_lat = data.get(position).getLatitude();
            double f_long = data.get(position).getLongitude();
            double l_lat = data.get(position + 1).getLatitude();
            double l_long = data.get(position + 1).getLongitude();

            float distance[] = new float[1];
            android.location.Location.distanceBetween(f_lat, f_long, l_lat, l_long, distance);
            if(distance[0] > 1000) {
                SI_DISTANCE.setText(Math.round(distance[0]/1000 * 10.0) / 10.0 + " km"); // Round up to first decimal place
            } else {
                SI_DISTANCE.setText((int) distance[0] + " m");
            }

            SI_CAR.setText(getTimeToNext(distance[0], 5.555));
            SI_WALK.setText(getTimeToNext(distance[0], 0.5));
        } else { // And don't draw images regarding distance
            convertView.findViewById(R.id.distanceLinearLayout).setVisibility(LinearLayout.GONE);
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    // Add this method, not to recycle child view
    @Override
    public int getViewTypeCount() {

        return getCount();
    }

    // Add this method, not to recycle child view
    @Override
    public int getItemViewType(int position) {

        return position;
    }

    private String getTimeToNext(double dist, double vel)
    {
        String s,sf[]={" min"," hr", " day"," week"," year"};
        byte b=0;
        int a = (int)(dist / vel)/60;
        if(a>60){
            a=(a+59)/60;
            b++;
        }
        if(a>24&&b>0)
        {
            a=(a+23)/24;
            b++;
        }
        if(a>7&&b>1) {
            a = (a + 6) / 7;
            b++;
        }
        s = a + sf[b];
        return s;
    }
}