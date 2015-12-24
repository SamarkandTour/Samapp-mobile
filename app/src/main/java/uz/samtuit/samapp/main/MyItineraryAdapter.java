package uz.samtuit.samapp.main;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;

import java.util.LinkedList;
import java.util.Objects;

import uz.samtuit.samapp.util.ActionItem;
import uz.samtuit.samapp.util.BitmapUtil;
import uz.samtuit.samapp.util.FileUtil;
import uz.samtuit.samapp.util.ItineraryItem;
import uz.samtuit.samapp.util.ItineraryList;
import uz.samtuit.samapp.util.QuickAction;
import uz.samtuit.samapp.util.TourFeature;

public class MyItineraryAdapter extends RecyclerView.Adapter<MyItineraryAdapter.ViewHolder> {
    private LinkedList<TourFeature> mDataset;
    private int dataSize = 0;
    private int lastPosition = -1;
    private Context context;
    private float imageSize;
    public TextView mCarTime;
    public TextView mWalkTime;
    final private int PREVIOUSDAY_ITEM_ID = 1000;
    final private int NEXTDAY_ITEM_ID = 1001;
    final private int UP_ITEM_ID = 1002;
    final private int DOWN_ITEM_ID = 1003;
    final private int DELETE_ITEM_ID = 1004;
    public ImageView mItemImage;
    public TextView mDistance;
    public View mLayoutBetweenItems;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mName;
        public TextView mCarTime;
        public TextView mWalkTime;
        public ImageView mItemImage;
        public TextView mOrderNum;
        public TextView mDistance;
        public View mLayoutBetweenItems;
        public LinearLayout container;
        public ViewHolder(View v) {
            super(v);
            mName = (TextView)v.findViewById(R.id.info_text);
            mCarTime = (TextView)v.findViewById(R.id.it_car_time);
            mWalkTime = (TextView)v.findViewById(R.id.it_walk_time);
            mOrderNum = (TextView)v.findViewById(R.id.it_ord_num);
            mItemImage = (ImageView)v.findViewById(R.id.it_image);
            mDistance = (TextView)v.findViewById(R.id.it_distance);
            mLayoutBetweenItems = v.findViewById(R.id.layout_between_items);
            container = (LinearLayout)v.findViewById(R.id.container);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyItineraryAdapter(LinkedList myDataset) {
        mDataset = myDataset;
        if(mDataset!=null)
            dataSize = mDataset.size();
        this.imageSize = imageSize;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyItineraryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_text_view, parent, false);
        context = parent.getContext();
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        mLayoutBetweenItems = holder.mLayoutBetweenItems;
        GetBitmapAsync getBitmapAsync = new GetBitmapAsync(position,holder,holder.container.getHeight()*context.getResources().getDisplayMetrics().density);
        getBitmapAsync.execute();

        if(position<dataSize-1){
            mLayoutBetweenItems.setVisibility(View.VISIBLE);
        }
        else
        {
            mLayoutBetweenItems.setVisibility(View.GONE);
        }
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mName.setText(mDataset.get(position).getString("name"));
        holder.mOrderNum.setText((position + 1) + "");

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Context context = v.getContext();

                QuickAction quickAction = new QuickAction(context);
                ActionItem _item1 = new ActionItem(PREVIOUSDAY_ITEM_ID,"P.DAY",context.getResources().getDrawable(R.drawable.skip_previous));
                ActionItem _item2 = new ActionItem(UP_ITEM_ID,"UP",context.getResources().getDrawable(R.drawable.arrow_up));
                ActionItem _item3 = new ActionItem(DELETE_ITEM_ID,"DELETE",context.getResources().getDrawable(R.drawable.delete));
                ActionItem _item4 = new ActionItem(DOWN_ITEM_ID,"DOWN",context.getResources().getDrawable(R.drawable.arrow_down));
                ActionItem _item5 = new ActionItem(NEXTDAY_ITEM_ID,"N.DAY",context.getResources().getDrawable(R.drawable.skip_next));

                if(mDataset.get(position).getDay()!=0)
                    quickAction.addActionItem(_item1);
                if(position!=0)
                    quickAction.addActionItem(_item2);
                quickAction.addActionItem(_item3);
                if(position<mDataset.size()-1)
                    quickAction.addActionItem(_item4);
                if(mDataset.get(position).getDay()< ItineraryList.MAX_ITINERARY_DAYS)
                    quickAction.addActionItem(_item5);
                _item3.setSticky(true);

                quickAction.show(v);
                quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
                    @Override
                    public void onItemClick(QuickAction source, int pos, int actionId) {
                        switch (actionId){
                            case PREVIOUSDAY_ITEM_ID:

                        }
                    }
                });
                quickAction.setAnimStyle(QuickAction.ANIM_GROW_FROM_LEFT);

//                Intent intent = new Intent(context,ItemActivity.class);
//                intent.putExtra("featureType", mDataset.get(position).getString("type"));
//                Log.e("FEATURETYPE", mDataset.get(position).getString("type"));
//
//                intent.putExtra("photo", mDataset.get(position).getPhoto());
//                intent.putExtra("rating", mDataset.get(position).getRating());
//                intent.putExtra("name", mDataset.get(position).getString("name"));
//                intent.putExtra("desc", mDataset.get(position).getString("desc"));
//                intent.putExtra("type", mDataset.get(position).getString("type"));
//                intent.putExtra("price", mDataset.get(position).getString("price"));
//                intent.putExtra("wifi", mDataset.get(position).getString("wifi"));
//                intent.putExtra("open", mDataset.get(position).getString("open"));
//                intent.putExtra("addr", mDataset.get(position).getString("addr"));
//                intent.putExtra("tel", mDataset.get(position).getString("tel"));
//                intent.putExtra("url", mDataset.get(position).getString("url"));
//                intent.putExtra("long", mDataset.get(position).getLongitude());
//                intent.putExtra("lat", mDataset.get(position).getLatitude());
//                intent.putExtra("primaryColorId", R.color.attraction_primary);
//                intent.putExtra("toolbarColorId", R.color.attraction_primary);
//                context.startActivity(intent);
            }
        });

        CalculateDistanceTimeAsync calc = new CalculateDistanceTimeAsync(position,holder);
        calc.execute();
        setAnimation(holder.container,position);

    }

    private void setAnimation(View viewToAnimate, int position){
        if(position>lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private class GetBitmapAsync extends AsyncTask<Void, Void, Drawable>{
        private int mPosition;
        private float mImageSize;
        private ViewHolder mHolder;
        private BitmapUtil.RoundedDrawable roundedDrawable;

        public GetBitmapAsync(int position, ViewHolder holder, float imageSize){
            mPosition = position;
            mHolder = holder;
            mImageSize = imageSize;
        }

        @Override
        protected Drawable doInBackground(Void... params) {
            String fileName = mDataset.get(mPosition).getPhoto();
            if (fileName != null) {
                String encodedBytes = FileUtil.fileReadFromExternalDir(context, fileName);
                Bitmap decodedBytes = BitmapUtil.decodeBase64Bitmap(encodedBytes, imageSize, imageSize);
                BitmapUtil.RoundedDrawable roundedDrawable = new BitmapUtil.RoundedDrawable(decodedBytes, false);
            }
            Log.e("OKAY","DONE");
            return roundedDrawable;
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            mHolder.mItemImage.setImageDrawable(drawable);
        }
    }

    private class CalculateDistanceTimeAsync extends AsyncTask<Void, Void, ItineraryItem>{

        private float distance[] = new float[1];
        private int mPosition;
        private ViewHolder mHolder;

        public CalculateDistanceTimeAsync( int position, ViewHolder holder){
            mPosition = position;
            mHolder = holder;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ItineraryItem item) {
            super.onPostExecute(item);
            if(!item.isLast()) {
                mHolder.mDistance.setText(item.getString("distance"));
                mHolder.mCarTime.setText(item.getString("car_time"));
                mHolder.mWalkTime.setText(item.getString("walk_time"));
            }
        }

        @Override
        protected ItineraryItem doInBackground(Void... params) {
            ItineraryItem item = new ItineraryItem();
            if (mPosition < (getItemCount() - 1)) { // No need to calculate for last feature
                item.setIsLast(false);
                double f_lat = mDataset.get(mPosition).getLatitude();
                double f_long = mDataset.get(mPosition).getLongitude();
                double l_lat = mDataset.get(mPosition + 1).getLatitude();
                double l_long = mDataset.get(mPosition + 1).getLongitude();
                android.location.Location.distanceBetween(f_lat, f_long, l_lat, l_long, distance);
                item.setStringHashMap("distance",(distance[0] > 1000)?Math.round(distance[0]/1000 * 10.0) / 10.0 + " km":(int) distance[0] + " m");
                item.setStringHashMap("car_time",getTimeToNext(distance[0], 5.555));
                item.setStringHashMap("walk_time",getTimeToNext(distance[0], 0.5));
            } else { // And don't draw images regarding distance
                item.setIsLast(true);
            }
            return item;
        }
        private String getTimeToNext(double dist, double speedFactor)
        {
            String s,sf[]={" min"," hr", " day"," week"," year"};
            byte b = 0;
            int a = (int)(dist / speedFactor) / 60;

            if (a > 60) {
                a = (a + 59) / 60;
                b++;
            }

            if (a > 24 && b > 0) {
                a = (a + 23) / 24;
                b++;
            }

            if (a > 7 && b > 1) {
                a = (a + 6) / 7;
                b++;
            }

            s = a + sf[b];

            return s;
        }

    }


}