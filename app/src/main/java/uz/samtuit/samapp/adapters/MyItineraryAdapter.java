package uz.samtuit.samapp.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import uz.samtuit.samapp.fragments.SuggestedItineraryFragment;
import uz.samtuit.samapp.fragments.TourFeaturesDialogFragmentWindow;
import uz.samtuit.samapp.helpers.ItineraryHelper;
import uz.samtuit.samapp.main.ItemActivity;
import uz.samtuit.samapp.main.R;
import uz.samtuit.samapp.util.BitmapUtil;
import uz.samtuit.samapp.util.GlobalsClass;
import uz.samtuit.samapp.util.ItineraryItem;
import uz.samtuit.samapp.util.ItineraryList;
import uz.samtuit.samapp.util.TourFeature;
import uz.samtuit.samapp.util.TourFeatureList;
import uz.samtuit.samapp.util.TypefaceHelper;

public class MyItineraryAdapter extends RecyclerView.Adapter<MyItineraryAdapter.ViewHolder> {
    private ArrayList<TourFeature> mDataset;
    private Context context;

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
    private FragmentManager fm;
    private SuggestedItineraryFragment mFragment;

    private static int[] dataSizeByDay = new int[ItineraryList.MAX_ITINERARY_DAYS];

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mName;
        public TextView mCarTime;
        public TextView mWalkTime;
        public ImageView mItemImage;
        public ImageButton mActionBtn;
        public TextView mDistance;
        public ImageButton addNewItem;
        public View mLayoutBetweenItems;
        public View container;
        public TextView mIdTV;

        public ViewHolder(View v) {
            super(v);

            mName = (TextView)v.findViewById(R.id.info_text);
            mCarTime = (TextView)v.findViewById(R.id.it_car_time);
            mWalkTime = (TextView)v.findViewById(R.id.it_walk_time);
            mItemImage = (ImageView)v.findViewById(R.id.it_image);
            mActionBtn = (ImageButton) v.findViewById(R.id.action_btn);
            mDistance = (TextView)v.findViewById(R.id.it_distance);
            mLayoutBetweenItems = v.findViewById(R.id.layout_between_items);
            addNewItem = (ImageButton) v.findViewById(R.id.add_new_itinerary_item);
            container = v.findViewById(R.id.card_container);
            mIdTV = (TextView) v.findViewById(R.id.it_ord_num);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyItineraryAdapter(Context context, int day, boolean mod, boolean animate, FragmentManager fragmentManager, SuggestedItineraryFragment fragment) {
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
        this.fm = fragmentManager;
        this.mFragment = fragment;
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
        final int indexInItineraryList = orderNumInTab - 1; // the index in list starts from 0
        mLayoutBetweenItems = holder.mLayoutBetweenItems;

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        String mTitle = mDataset.get(position).getString("name");
        holder.mName.setText(mTitle);
        holder.mIdTV.setText(Integer.toString(orderNumInTab));

        if (position < dataSize-1 && !isMod) { //if last and is not modifing show layout between items
            mLayoutBetweenItems.setVisibility(View.VISIBLE);
        } else {
            mLayoutBetweenItems.setVisibility(View.GONE);
        }

        switch (mDataset.get(position).getString("category")) {
            case "hotel":
                holder.mName.setTextColor(context.getResources().getColor(R.color.hotel_primary));
                holder.mIdTV.setBackgroundResource(R.drawable.hotel_round_bg);
                break;
            case "shopping":
                holder.mName.setTextColor(context.getResources().getColor(R.color.shop_primary));
                holder.mIdTV.setBackgroundResource(R.drawable.shop_round_bg);
                break;
            case "foodndrink":
                holder.mName.setTextColor(context.getResources().getColor(R.color.foodanddrink_primary));
                holder.mIdTV.setBackgroundResource(R.drawable.food_round_bg);
                break;
            case "attraction":
                holder.mName.setTextColor(context.getResources().getColor(R.color.attraction_primary));
                holder.mIdTV.setBackgroundResource(R.drawable.attraction_round_bg);
                break;
        }

        if (isMod) {
            holder.addNewItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ItineraryHelper.addNewItemFromItinerary(fm, selectedArrayDay, orderNumInTab);
                }
            });

        } else {
            holder.container.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final Context context = v.getContext();
                    makeQuickAction(indexInItineraryList, position, mDataset.get(position));
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
        holder.mActionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeQuickAction(indexInItineraryList, position, mDataset.get(position));
            }
        });

        BitmapUtil.setRoundImageFromFileToView(context, mDataset.get(position).getPhoto(), holder.mItemImage);
        CalculateDistanceTimeAsync calc = new CalculateDistanceTimeAsync(position, holder);
        calc.execute();
    }

    private Intent getTourFeatureIntent(Context vContext, TourFeature tf) {
        Intent intent = new Intent(vContext, ItemActivity.class);
        GlobalsClass.FeatureType featureType = TourFeatureList.findFeatureTypeByName(context, tf.getString("name"));
        intent.putExtra("featureType", featureType.toString());
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
        intent.putExtra("booking", tf.getString("booking"));
        intent.putExtra("long", tf.getLongitude());
        intent.putExtra("lat", tf.getLatitude());
        intent.putExtra("primaryColorId", GlobalsClass.getPrimaryColorId(featureType));
        intent.putExtra("toolbarColorId", GlobalsClass.getToolbarColorId(featureType));
        return intent;
    }

    private void makeQuickAction(final int indexInItineraryList, int position,final TourFeature tf) {
        Log.e("indexInItineraryList", indexInItineraryList + " " + position+ " " + tf.getString("name") + " " + tf.getDay());
        View view = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.itinerary_bottom_sheet, null);
        final Dialog mBottomSheet = new Dialog(context, R.style.MaterialDialogSheet);
        Typeface roboto = TypefaceHelper.getTypeface(context, "Roboto-Regular");
        mBottomSheet.setContentView(view);
        mBottomSheet.setCancelable(true);

        if ( (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            mBottomSheet.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        } else {
            mBottomSheet.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }

        mBottomSheet.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheet.show();

        TextView actionUp = (TextView) view.findViewById(R.id.action_up);
        TextView actionDayBack = (TextView) view.findViewById(R.id.action_day_backward);
        TextView actionDelete = (TextView) view.findViewById(R.id.action_delete);
        TextView actionDayForward = (TextView) view.findViewById(R.id.action_day_forward);
        TextView actionDown = (TextView) view.findViewById(R.id.action_down);
        TextView itTitle = (TextView) view.findViewById(R.id.it_title);

        ImageView image = (ImageView) view.findViewById(R.id.image_holder);
        View holder = view.findViewById(R.id.info_layout_holder);
        itTitle.setTypeface(roboto);
        itTitle.setText(mDataset.get(position).getString("name"));
        BitmapUtil.setRoundImageFromFileToView(context, mDataset.get(position).getPhoto(), image);
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        if (tf.getDay() != 1) { // Prev
            actionDayBack.setVisibility(View.VISIBLE);
            actionDayBack.setTypeface(roboto);
            actionDayBack.setText(context.getResources().getString(R.string.action_back));
        }

        if (position != 0) { // Up
            actionUp.setVisibility(View.VISIBLE);
            actionUp.setTypeface(roboto);
            actionUp.setText(context.getResources().getString(R.string.action_up));
        }

        actionDelete.setTypeface(roboto);
        actionDelete.setText(context.getResources().getString(R.string.action_delete));

        if (position < mDataset.size()-1) { //Down
            actionDown.setVisibility(View.VISIBLE);
            actionDown.setTypeface(roboto);
            actionDown.setText(context.getResources().getString(R.string.action_down));
        }

        if (mDataset.get(position).getDay() < ItineraryList.MAX_ITINERARY_DAYS) { // Next
            actionDayForward.setVisibility(View.VISIBLE);
            actionDayForward.setTypeface(roboto);
            actionDayForward.setText(context.getResources().getString(R.string.action_forward));
        }

        View.OnClickListener actionOnClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.action_day_backward:
                        changeDay(context, indexInItineraryList, -1);
                        break;
                    case R.id.action_up:
                        changeOrder(context, indexInItineraryList, tf, UP_ITEM_ID);
                        break;
                    case R.id.action_delete:
                        changeOrder(context, indexInItineraryList, tf, DELETE_ITEM_ID);
                        break;
                    case R.id.action_down:
                        changeOrder(context, indexInItineraryList, tf, DOWN_ITEM_ID);
                        break;
                    case R.id.action_day_forward:
                        changeDay(context, indexInItineraryList, 1);
                        break;
                }
                mFragment.showAddButton(mDataset.size()==0, selectedArrayDay, getStartIndexOfDay());
                mBottomSheet.dismiss();
            }
        };

        actionDayBack.setOnClickListener(actionOnClick);
        actionDayForward.setOnClickListener(actionOnClick);
        actionDelete.setOnClickListener(actionOnClick);
        actionDown.setOnClickListener(actionOnClick);
        actionUp.setOnClickListener(actionOnClick);
    }

    private void changeOrder(Context context, int index, TourFeature tf, int cmd) {
        GlobalsClass globalsClass = (GlobalsClass)context.getApplicationContext();
        LinkedList<TourFeature> itineraryList = globalsClass.getItineraryFeatures();
        String toastMag = null;

        TourFeature tourFeature = itineraryList.get(index);
        Log.e("Log", tourFeature.getString("name") + " " + tourFeature.getDay() + " " + index);
        itineraryList.remove(tf);

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

        ItineraryList.itineraryWriteToGeoJSONFile(context, context.getSharedPreferences("SamTour_Pref", Context.MODE_PRIVATE).getString("app_lang", null));

        mDataset = getItineraryByDay(context, selectedRealDay);
        dataSize = mDataset.size();
        dataSizeByDay[selectedArrayDay] = dataSize;
        notifyDataSetChanged();

        Toast.makeText(context, toastMag, Toast.LENGTH_SHORT).show();
    }

    private void changeDay(Context context, int index, int inc) {
        ItineraryHelper.changeDay(context,selectedRealDay,index, inc);

        mDataset = getItineraryByDay(context, selectedRealDay);
        dataSize = mDataset.size();
        dataSizeByDay[selectedArrayDay] = dataSize;
        notifyDataSetChanged();

        Toast.makeText(context, context.getString(R.string.itinerary_item_transfer) +
                ((inc == -1) ? context.getString(R.string.backward) : context.getString(R.string.forward)), Toast.LENGTH_SHORT).show();
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