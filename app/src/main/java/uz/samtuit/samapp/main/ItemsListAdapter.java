package uz.samtuit.samapp.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

import uz.samtuit.samapp.util.BitmapUtil;
import uz.samtuit.samapp.util.FileUtil;
import uz.samtuit.samapp.util.GlobalsClass;
import uz.samtuit.samapp.util.TourFeature;

class ItemsListAdapter extends RecyclerView.Adapter<ItemsListAdapter.ViewHolder> {

    Context context;
    private GlobalsClass.FeatureType S_ACTIVITY_NAME;
    private int layoutId;
    ArrayList<TourFeature> data = null;
    private int layoutResourceId;
    private GlobalsClass globalVariables;
    private Location currentLoc;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTV;
        public ImageView mainImage;
        public RatingBar rb;
        public LinearLayout holderLayout;

        public ViewHolder(View v){
            super(v);
            nameTV = (TextView)v.findViewById(R.id.tf_title);
            mainImage = (ImageView)v.findViewById(R.id.listViewThumbnail);
            rb = (RatingBar)v.findViewById(R.id.ratingBar);
            holderLayout = (LinearLayout)v.findViewById(R.id.holder_layout);
        }
    }

    public ItemsListAdapter(Context context, GlobalsClass.FeatureType S_ACTIVITY_NAME, ArrayList<TourFeature> data, int layoutID) {
        this.context = context;
        this.data = data;
        this.S_ACTIVITY_NAME = S_ACTIVITY_NAME;
        this.layoutId = layoutID;
        globalVariables = (GlobalsClass)context.getApplicationContext();
        currentLoc = globalVariables.getCurrentLoc();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(layoutId, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }



    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.nameTV.setText(data.get(position).getString("name"));
        String fileName = data.get(position).getPhoto();
        try {
            if (fileName != null) {
                String encodedBytes = FileUtil.fileReadFromExternalDir(context, fileName);
                Bitmap decodedBytes = BitmapUtil.decodeBase64Bitmap(encodedBytes, holder.mainImage.getLayoutParams().height , holder.mainImage.getLayoutParams().width);
                BitmapUtil.RoundedDrawable roundedDrawable = new BitmapUtil.RoundedDrawable(decodedBytes, false);
                holder.mainImage.setImageDrawable(roundedDrawable);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        holder.rb.setRating(data.get(position).getRating());
        holder.holderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemsListActivity.startItemActivity(context, S_ACTIVITY_NAME, data.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d("TAG", "getItemCount: " + data.size());
         return data.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);

    }

}