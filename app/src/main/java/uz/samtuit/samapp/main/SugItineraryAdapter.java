package uz.samtuit.samapp.main;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import uz.samtuit.sammap.main.R;

public class SugItineraryAdapter extends ArrayAdapter<NewPoint> {
    NewPoint[] data = null;
    Context context;
    int layoutResId;
    public SugItineraryAdapter(Context context, int layoutResId, NewPoint[] data)
    {
        super(context,layoutResId,data);
        this.context = context;
        this.data = data;
        this.layoutResId = layoutResId;
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
        TextView SI_BUS = (TextView)convertView.findViewById(R.id.si_bus);
        TextView SI_DISTANCE = (TextView)convertView.findViewById(R.id.it_distance);
        ImageView SI_IMAGE = (ImageView)convertView.findViewById(R.id.si_image);
        NewPoint item = data[position];
        double f_lat = Double.parseDouble(data[position].location.split(",")[0]), f_long = Double.parseDouble(data[position].location.split(",")[1]),l_lat,l_long;
        if(position<=0)
        {
            l_lat = 0;
            l_long = 0;
        }
        else
        {
            l_lat = Double.parseDouble(data[position - 1].location.split(",")[0]);
            l_long = Float.parseFloat(data[position-1].location.split(",")[1]);
        }
        float distance[] = new float[3];
        SI_NAME.setText(item.name);


        android.location.Location.distanceBetween(f_lat,f_long,l_lat,l_long,distance);
        if(distance[0]<900)
            SI_DISTANCE.setText((int)distance[0]+" m");
        else
            SI_DISTANCE.setText((int)((distance[0]+999)/1000)+" km");
//      SI_DISTANCE.setText(distance(Float.parseFloat(data[position].location.split(",")[0]),Float.parseFloat(data[position].location.split(",")[0]),f_lat,f_long)+"");
        SI_CAR.setText(getTimeToNext(distance[0], 5.555));
        SI_BUS.setText(getTimeToNext(distance[0],2.554));
        SI_WALK.setText(getTimeToNext(distance[0],0.5));
        return convertView;
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