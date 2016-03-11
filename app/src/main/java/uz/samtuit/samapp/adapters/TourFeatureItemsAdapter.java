package uz.samtuit.samapp.adapters;

import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.textservice.TextInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.io.File;
import java.util.ArrayList;

import uz.samtuit.samapp.main.ItemsListActivity;
import uz.samtuit.samapp.main.LogoActivity;
import uz.samtuit.samapp.main.R;
import uz.samtuit.samapp.util.BitmapUtil;
import uz.samtuit.samapp.util.FileUtil;
import uz.samtuit.samapp.util.GlobalsClass;
import uz.samtuit.samapp.util.TourFeature;

/**
 * Created by Bakha on 25.02.2016.
 */
public class TourFeatureItemsAdapter extends RecyclerView.Adapter<TourFeatureItemsAdapter.ViewHolder> {

    Context context;
    ArrayList<TourFeature> data;
    private GlobalsClass.FeatureType featureType;
    private int layoutID;

    public TourFeatureItemsAdapter(Context context, GlobalsClass.FeatureType featureType, ArrayList<TourFeature> data, int LayoutID){
        this.context = context;
        this.featureType = featureType;
        this.data = data;
        this.layoutID = LayoutID;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(layoutID, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,final int position) {
        holder.TF_RATING.setRating(data.get(position).getRating());
        holder.TF_TITLE.setText(data.get(position).getString("name"));
        String fileName = data.get(position).getPhoto();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        if(layoutID==R.layout.items_list_adapter){

        } else {
            holder.TF_TITLE.setTextSize(13);
        }
        //Log.e("TEGA","PPEPP");


        try{
            String encodedBytes = FileUtil.fileReadFromExternalDir(context, fileName);
            BitmapUtil.setRoundImageFromFileToView(context, encodedBytes, holder.TF_IMAGE, AnimationUtils.loadAnimation(context, android.R.anim.fade_in));
//            final ImageView imageView = holder.TF_IMAGE;
//            if(layoutID==R.layout.items_list_adapter)
//            {
//                Glide.with(context).load(Base64.decode(encodedBytes, Base64.DEFAULT)).asBitmap().into(new BitmapImageViewTarget(imageView) {
//                    @Override
//                    protected void setResource(Bitmap resource) {
//                        super.setResource(resource);
//                        RoundedBitmapDrawable circularBitmapDrawable =
//                                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
//                        circularBitmapDrawable.setCircular(true);
//                        imageView.setImageDrawable(circularBitmapDrawable);
//                        imageView.setPadding(6,6,6,6);
//                    }
//                });
//            } else {
//                Glide.with(context).load(Base64.decode(encodedBytes, Base64.DEFAULT)).asBitmap().placeholder(R.drawable.no_image).into(holder.TF_IMAGE);
//                holder.TF_IMAGE.getLayoutParams().height = display.getWidth() / 2 - 80;
//            }

        }catch (Exception ex){
//            Glide.with(context).load(R.drawable.no_image).into(holder.TF_IMAGE);
//            if(layoutID == R.layout.items_list_adapter_grid_card)
//                holder.TF_IMAGE.getLayoutParams().height = display.getWidth() / 2 - 80;
            ex.printStackTrace();
        }





//        try {
//            if (fileName != null) {
//
//                Bitmap decodedBytes = BitmapUtil.decodeBase64Bitmap(encodedBytes, holder.TF_IMAGE.getLayoutParams().height , holder.TF_IMAGE.getLayoutParams().width);
//                BitmapUtil.RoundedDrawable roundedDrawable = new BitmapUtil.RoundedDrawable(decodedBytes, false);
//                holder.TF_IMAGE.setImageDrawable(roundedDrawable);
//            }
//        }catch (Exception ex){
//            ex.printStackTrace();
//        }
        holder.TF_HOLDER.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemsListActivity.startItemActivity(context, featureType, data.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView TF_TITLE;
        public ImageView TF_IMAGE;
        public RatingBar TF_RATING;
        public View TF_HOLDER;

        public ViewHolder(View v) {
            super(v);
            TF_TITLE = (TextView)v.findViewById(R.id.tf_title);
            TF_IMAGE = (ImageView)v.findViewById(R.id.listViewThumbnail);
            TF_RATING = (RatingBar)v.findViewById(R.id.ratingBar);
            TF_HOLDER = v.findViewById(R.id.holder_layout);
        }
    }
}
