package uz.samtuit.samapp.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.cocoahero.android.geojson.Feature;
import com.cocoahero.android.geojson.FeatureCollection;
import com.mapbox.mapboxsdk.util.DataLoadingUtils;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MBP on 2015. 10. 14..
 */
public class TourFeatureList {
    private ArrayList<TourFeature> tourFeatureList;

    public TourFeatureList() {
        tourFeatureList = new ArrayList<TourFeature>();
    }

    public ArrayList<TourFeature> getTourFeatureList(Context context, String fileName) {
        try {
            FeatureCollection featureCollection = DataLoadingUtils.loadGeoJSONFromAssets(context, fileName);
            List<Feature> featuresList = featureCollection.getFeatures();

            for (Feature v:featuresList) {
                TourFeature tourFeature = new TourFeature();

                if (v.getProperties().isNull("photo")) {
                    tourFeature.setPhoto(null);
                } else {
                    tourFeature.setPhoto(v.getProperties().getString("photo"));
                }

                tourFeature.setRating(v.getProperties().getInt("rating"));

                if (v.getProperties().isNull("name")) {
                    tourFeature.setStringHashMap("name", null);
                } else {
                    tourFeature.setStringHashMap("name", v.getProperties().getString("name"));
                }

                if (v.getProperties().isNull("description")) {
                    tourFeature.setStringHashMap("description", null);
                } else {
                    tourFeature.setStringHashMap("description", v.getProperties().getString("description"));
                }

                if (v.getProperties().isNull("type")) {
                    tourFeature.setStringHashMap("type", null);
                } else {
                    tourFeature.setStringHashMap("type", v.getProperties().getString("type"));
                }

                if (v.getProperties().isNull("price")) {
                    tourFeature.setStringHashMap("price", null);
                } else {
                    tourFeature.setStringHashMap("price", v.getProperties().getString("price"));
                }

                if (v.getProperties().isNull("wifi")) {
                    tourFeature.setStringHashMap("wifi", null);
                } else {
                    tourFeature.setStringHashMap("wifi", v.getProperties().getString("wifi"));
                }

                if (v.getProperties().isNull("open")) {
                    tourFeature.setStringHashMap("open", null);
                } else {
                    tourFeature.setStringHashMap("open", v.getProperties().getString("open"));
                }

                if (v.getProperties().isNull("address")) {
                    tourFeature.setStringHashMap("address", null);
                } else {
                    tourFeature.setStringHashMap("address", v.getProperties().getString("address"));
                }

                if (v.getProperties().isNull("url")) {
                    tourFeature.setStringHashMap("url", null);
                } else {
                    tourFeature.setStringHashMap("url", v.getProperties().getString("url"));
                }

                tourFeature.setLongitude(v.getGeometry().toJSON().getJSONArray("coordinates").getDouble(0));
                tourFeature.setLatitude(v.getGeometry().toJSON().getJSONArray("coordinates").getDouble(1));

                tourFeatureList.add(tourFeature);
            }

        } catch (IOException e) {
            // File not found, Try redownload
            e.printStackTrace();
        } catch (JSONException e) {
            // JSON Format is malformed
            e.printStackTrace();
        }

        return tourFeatureList;
    }

    private Bitmap base64ToBitmap(String b64) {
        byte[] imageAsBytes = Base64.decode(b64.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }
}
