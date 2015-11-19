package uz.samtuit.samapp.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.views.InfoWindow;
import com.mapbox.mapboxsdk.views.MapView;

import uz.samtuit.samapp.util.GlobalsClass;
import uz.samtuit.samapp.util.RoundedDrawable;
import uz.samtuit.sammap.main.R;

/**
 * Custom Tooltip Window
 */
public class CustomInfoWindow extends InfoWindow {
    private int mLinkedListIndex;
    private Context mContext;

    public CustomInfoWindow(Context context, MapView mv, int index) {
        super(R.layout.marker_description, mv);

        mContext = context;
        mLinkedListIndex = index;

        // Add own OnTouchListener to customize handling InfoWindow touch events
        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Still close the InfoWindow though
                    close();
                }

                // Return true as we're done processing this event
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

        GlobalsClass globalVariables = (GlobalsClass)mContext.getApplicationContext();
        String photo = globalVariables.getItineraryFeatures().get(mLinkedListIndex).getPhoto();
        ImageView mainImage = (ImageView)mView.findViewById(R.id.tooltip_imageView);
        byte[] decodedString = Base64.decode(photo, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        RoundedDrawable roundedDrawable = new RoundedDrawable(decodedByte, true);
        mainImage.setImageDrawable(roundedDrawable);
    }
}

