package uz.samtuit.samapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import uz.samtuit.samapp.main.R;
import uz.samtuit.samapp.main.TourFeatureActivity;
import uz.samtuit.samapp.util.ActionItem;
import uz.samtuit.samapp.util.BitmapUtil;
import uz.samtuit.samapp.util.FileUtil;
import uz.samtuit.samapp.util.GlobalsClass;
import uz.samtuit.samapp.util.ItineraryItem;
import uz.samtuit.samapp.util.ItineraryList;
import uz.samtuit.samapp.util.QuickAction;
import uz.samtuit.samapp.util.TourFeature;

public class MyItineraryAdapter extends RecyclerView.Adapter<MyItineraryAdapter.ViewHolder> {
    private ArrayList<TourFeature> mDataset;
    private Context context;

    final private int PREVIOUSDAY_ITEM_ID = 1000;
    final private int NEXTDAY_ITEM_ID = 1001;
    final private int UP_ITEM_ID = 1002;
    final private int DOWN_ITEM_ID = 1003;
    final private int DELETE_ITEM_ID = 1004;

    private int dataSize = 0;
    private int lastPosition = -1;

    private View mLayoutBetweenItems;
    int layoutResId = R.layout.itinerary_card;

    private ArrayList<TourFeature> itineraryByDay = new ArrayList<TourFeature>();
    private int selectedArrayDay, selectedRealDay;
    private List<Integer> checkedItems; //checked items in modify mode

    boolean animate = false; //animate items or not
    boolean isMod = false; //toggle for modify mode

    private static int[] dataSizeByDay = new int[ItineraryList.MAX_ITINERARY_DAYS];

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mName;
        public TextView mCarTime;
        public TextView mWalkTime;
        public ImageView mItemImage;
        public TextView mOrderNum;
        public TextView mDistance;
        public ImageButton addNewItem;
        public View mLayoutBetweenItems;
        public View container;

        public ViewHolder(View v) {
            super(v);

            mName = (TextView)v.findViewById(R.id.info_text);
            mCarTime = (TextView)v.findViewById(R.id.it_car_time);
            mWalkTime = (TextView)v.findViewById(R.id.it_walk_time);
            mOrderNum = (TextView)v.findViewById(R.id.it_ord_num);
            mItemImage = (ImageView)v.findViewById(R.id.it_image);
            mDistance = (TextView)v.findViewById(R.id.it_distance);
            mLayoutBetweenItems = v.findViewById(R.id.layout_between_items);
            addNewItem = (ImageButton)v.findViewById(R.id.add_new_itinerary_item);
            container = v.findViewById(R.id.card_container);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyItineraryAdapter(Context context, int day, boolean mod, boolean animate) {
        selectedArrayDay = day;
        selectedRealDay = selectedArrayDay + 1; // Because the day argument is the position of tabs, add 1 to change to real day
        mDataset = getItineraryByDay(context, selectedRealDay);
        this.animate = animate;
        this.isMod = mod;
        if(isMod)
        {
            layoutResId = R.layout.itinerary_card_mod;
            checkedItems = new ArrayList<Integer>();
        }

        if(mDataset != null) {
            dataSize = mDataset.size();
            dataSizeByDay[day] = dataSize;
        }
    }

    private ArrayList<TourFeature> getItineraryByDay(Context context, int itineraryDay) {
        GlobalsClass globalsClass = (GlobalsClass)context.getApplicationContext();
        LinkedList<TourFeature> itineraryFeatures = globalsClass.getItineraryFeatures();

        if (itineraryFeatures == null) {
            return null;
        }

        itineraryByDay.clear();
        for (TourFeature v : itineraryFeatures) {
            if (itineraryDay == v.getDay()) {
                itineraryByDay.add(v);
            }
        }

        return itineraryByDay;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyItineraryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(layoutResId, parent, false);

        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    private int getStartIndexOfDay() {
        int startOrderOfDay = 1; // the order in tab starts from 1

        for (int i = 0; i < selectedArrayDay; i++) {
            startOrderOfDay = startOrderOfDay + dataSizeByDay[i];
        }

        return startOrderOfDay;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final int orderNumInTab = getStartIndexOfDay() + position;
        final ImageView mItemImage = holder.mItemImage;
        final int indexInItineraryList = orderNumInTab - 1; // the index in list starts from 0
        mLayoutBetweenItems = holder.mLayoutBetweenItems;

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mName.setText(mDataset.get(position).getString("name"));
        holder.mOrderNum.setText(Integer.toString(orderNumInTab));
        if (position < dataSize-1 && !isMod) { //if last and is not modifing show layout between items
            mLayoutBetweenItems.setVisibility(View.VISIBLE);
        } else {
            mLayoutBetweenItems.setVisibility(View.GONE);
        }
        switch (mDataset.get(position).getString("category")) {
            case "hotel":
                holder.mOrderNum.setBackgroundResource(R.drawable.hotel_round_bg);
                break;
            case "shopping":
                holder.mOrderNum.setBackgroundResource(R.drawable.shop_round_bg);
                break;
            case "foodndrink":
                holder.mOrderNum.setBackgroundResource(R.drawable.food_round_bg);
                break;
            case "attraction":
                holder.mOrderNum.setBackgroundResource(R.drawable.attraction_round_bg);
                break;
        }

        if (isMod) {
            holder.addNewItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            final Animation animIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
            final Animation animOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkedItems.contains(position)) {
                        checkedItems.remove(checkedItems.indexOf(position));
                        BitmapUtil.setRoundImageFromFileToView(context, mDataset.get(position).getPhoto(), holder.mItemImage,animOut);
                        holder.mItemImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    } else {
                        checkedItems.add(position);
                        Glide.with(context).load(R.drawable.ic_done_white_24dp).animate(animIn).into(holder.mItemImage);
                        holder.mItemImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    }
                    Log.e("COUNT", checkedItems.size()+"");
                }
            });
        } else {
            holder.container.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final Context context = v.getContext();
                    makeQuickAction(context, v, indexInItineraryList, position, mDataset.get(position));
                    return false;
                }
            });
            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = getTourFeatureIntent(context, mDataset.get(position));

                    context.startActivity(intent);
                }
            });
        }

        BitmapUtil.setRoundImageFromFileToView(context, mDataset.get(position).getPhoto(), holder.mItemImage);
        CalculateDistanceTimeAsync calc = new CalculateDistanceTimeAsync(position, holder);
        calc.execute();
        setAnimation(holder.container,position);
    }

    private Intent getTourFeatureIntent(Context vContext, TourFeature tf) {
        Intent intent = new Intent(vContext, TourFeatureActivity.class);
        intent.putExtra("featureType", tf.getString("type"));
        intent.putExtra("photo", tf.getPhoto());
        intent.putExtra("rating", tf.getRating());
        intent.putExtra("name", tf.getString("name"));
        intent.putExtra("desc", tf.getString("desc"));
        intent.putExtra("type", tf.getString("type"));
        intent.putExtra("price", tf.getString("price"));
        intent.putExtra("wifi", tf.getString("wifi"));
        intent.putExtra("open", tf.getString("open"));
        intent.putExtra("addr", tf.getString("addr"));
        intent.putExtra("tel", tf.getString("tel"));
        intent.putExtra("url", tf.getString("url"));
        intent.putExtra("long", tf.getLongitude());
        intent.putExtra("lat", tf.getLatitude());
        intent.putExtra("primaryColorId", R.color.attraction_primary);
        intent.putExtra("toolbarColorId", R.color.attraction_primary);
        return intent;
    }

    private void makeQuickAction(Context vContext, View v, final int indexInItineraryList, int position, TourFeature tf) {
        QuickAction quickAction = new QuickAction(vContext);
        ActionItem _item1 = new ActionItem(PREVIOUSDAY_ITEM_ID,"P.DAY",context.getResources().getDrawable(R.drawable.skip_previous));
        ActionItem _item2 = new ActionItem(UP_ITEM_ID,"UP",context.getResources().getDrawable(R.drawable.arrow_up));
        ActionItem _item3 = new ActionItem(DELETE_ITEM_ID,"DELETE",context.getResources().getDrawable(R.drawable.delete));
        ActionItem _item4 = new ActionItem(DOWN_ITEM_ID,"DOWN",context.getResources().getDrawable(R.drawable.arrow_down));
        ActionItem _item5 = new ActionItem(NEXTDAY_ITEM_ID,"N.DAY",context.getResources().getDrawable(R.drawable.skip_next));

        if (tf.getDay() != 1) { // Prev
            quickAction.addActionItem(_item1);
        }

        if (position != 0) { // Up
            quickAction.addActionItem(_item2);
        }

        quickAction.addActionItem(_item3); // Delete

        if (position < mDataset.size()-1) { //Down
            quickAction.addActionItem(_item4);
        }

        if (mDataset.get(position).getDay() < ItineraryList.MAX_ITINERARY_DAYS) { // Next
            quickAction.addActionItem(_item5);
        }

        quickAction.show(v);
        quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
            @Override
            public void onItemClick(QuickAction source, int pos, int actionId) {
                switch (actionId) {
                    case PREVIOUSDAY_ITEM_ID:
                        changeDay(context, indexInItineraryList, -1);
                        break;
                    case NEXTDAY_ITEM_ID:
                        changeDay(context, indexInItineraryList, 1);
                        break;
                    case DELETE_ITEM_ID:
                        changeOrder(context, indexInItineraryList, DELETE_ITEM_ID);
                        break;
                    case DOWN_ITEM_ID:
                        changeOrder(context, indexInItineraryList, DOWN_ITEM_ID);
                        break;
                    case UP_ITEM_ID:
                        changeOrder(context, indexInItineraryList, UP_ITEM_ID);
                        break;
                }
            }
        });
        quickAction.setAnimStyle(QuickAction.ANIM_GROW_FROM_LEFT);
    }

    private void changeOrder(Context context, int index, int cmd) {
        GlobalsClass globalsClass = (GlobalsClass)context.getApplicationContext();
        LinkedList<TourFeature> itineraryList = globalsClass.getItineraryFeatures();
        String toastMag = null;

        TourFeature tourFeature = itineraryList.get(index);
        itineraryList.remove(index);

        switch (cmd) {
            case DOWN_ITEM_ID:
                itineraryList.add(index + 1, tourFeature);
                toastMag = context.getString(R.string.move);
                break;

            case UP_ITEM_ID:
                itineraryList.add(index - 1, tourFeature);
                toastMag = context.getString(R.string.move);
                break;

            case DELETE_ITEM_ID:
                toastMag = context.getString(R.string.delete);
                break;
        }

        ItineraryList.itineraryWriteToGeoJSONFile(context, context.getSharedPreferences("SamTour_Pref", 0).getString("app_lang", null));

        mDataset = getItineraryByDay(context, selectedRealDay);
        dataSize = mDataset.size();
        dataSizeByDay[selectedArrayDay] = dataSize;
        notifyDataSetChanged();

        Toast.makeText(context, toastMag, Toast.LENGTH_LONG).show();
    }

    private void changeDay(Context context, int index, int inc) {
        GlobalsClass globalsClass = (GlobalsClass)context.getApplicationContext();
        LinkedList<TourFeature> itineraryList = globalsClass.getItineraryFeatures();

        TourFeature tourFeature = itineraryList.get(index);
        tourFeature.setDay(selectedRealDay + inc);
        itineraryList.add(tourFeature);
        itineraryList.remove(index);

        ItineraryList.sortItineraryList();
        ItineraryList.itineraryWriteToGeoJSONFile(context, context.getSharedPreferences("SamTour_Pref", 0).getString("app_lang", null));

        mDataset = getItineraryByDay(context, selectedRealDay);
        dataSize = mDataset.size();
        dataSizeByDay[selectedArrayDay] = dataSize;
        notifyDataSetChanged();

        Toast.makeText(context, context.getString(R.string.itinerary_item_transfer) +
                ((inc == -1) ? context.getString(R.string.backward) : context.getString(R.string.forward)), Toast.LENGTH_LONG).show();
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition && animate) {
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
        return dataSize;
    }

    private class CalculateDistanceTimeAsync extends AsyncTask<Void, Void, ItineraryItem>{

        private float distance[] = new float[1];
        private int mPosition;
        private ViewHolder mHolder;

        public CalculateDistanceTimeAsync(int position, ViewHolder holder) {
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
                item.setStringHashMap("distance",(distance[0] > 1000) ? Math.round(distance[0]/1000 * 10.0) / 10.0 + " km" : (int) distance[0] + " m");
                item.setStringHashMap("car_time",getTimeToNext(distance[0], 5.555));
                item.setStringHashMap("walk_time",getTimeToNext(distance[0], 0.5));
            } else { // And don't draw images regarding distance
                item.setIsLast(true);
            }
            return item;
        }

        private String getTimeToNext(double dist, double speedFactor) {
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