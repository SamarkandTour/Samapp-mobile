package uz.samtuit.samapp.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.views.InfoWindow;
import com.mapbox.mapboxsdk.views.MapView;


import uz.samtuit.samapp.helpers.IntentHelper;
import uz.samtuit.samapp.util.BitmapUtil;
import uz.samtuit.samapp.util.FileUtil;
import uz.samtuit.samapp.util.GlobalsClass;
import uz.samtuit.samapp.util.TourFeature;
import uz.samtuit.samapp.util.TypefaceHelper;

import uz.samtuit.samapp.helpers.IntentHelper.*;

/**
 * Custom Tooltip Window
 */
public class CustomInfoWindow extends InfoWindow {
    private TourFeature mFeature;
    private Context mContext;
    private GlobalsClass.FeatureType mFeatureType;

    public CustomInfoWindow(Context context, MapView mv, GlobalsClass.FeatureType featureType, TourFeature tourFeature) {
        super(R.layout.marker_custom_tooltip, mv);

        mContext = context;
        mFeatureType = featureType;
        mFeature = tourFeature;


        // Add own OnTouchListener to customize handling InfoWindow touch events
        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Still close the InfoWindow though
                    IntentHelper.startItemActivity(mContext, mFeatureType, mFeature);
                    close();
                }

                // Return true as we're done processing this event,,,,,
                return true;
            }
        });
    }

    /**
     * Dynamically set the content in the CustomInfoWindow
     * @param overlayItem The tapped Marker
     */
    @Override
    public void onOpen(Marker overlayItem) {

        String title = overlayItem.getTitle();

        ((TextView) mView.findViewById(R.id.customTooltip_title)).setText(title);
        ((TextView) mView.findViewById(R.id.customTooltip_title)).setTypeface(TypefaceHelper.getTypeface(mContext));

        String photoFileName = mFeature.getPhoto();
        if (photoFileName != null) {
            try{
                String encodedBytes = FileUtil.fileReadFromExternalDir(mContext, photoFileName);
                Bitmap decodedBytes = BitmapUtil.decodeBase64Image(encodedBytes);
                BitmapUtil.RoundedDrawable roundedDrawable = new BitmapUtil.RoundedDrawable(decodedBytes, true);
                ImageView mainImage = (ImageView) mView.findViewById(R.id.tooltip_imageView);
                mainImage.setImageDrawable(roundedDrawable);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

    }
}

