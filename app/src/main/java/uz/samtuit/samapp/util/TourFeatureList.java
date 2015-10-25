package uz.samtuit.samapp.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.cocoahero.android.geojson.Feature;
import com.cocoahero.android.geojson.FeatureCollection;
import com.mapbox.mapboxsdk.util.DataLoadingUtils;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import uz.samtuit.samapp.main.GlobalsClass;
import uz.samtuit.sammap.main.R;

/**
 * Create Tour Features List from GeoJSON file
 */
public class TourFeatureList {
    private ArrayList<TourFeature> tourFeatureList;
    private LinkedList<TourFeature> itineraryList;

    public TourFeatureList() {
        tourFeatureList = new ArrayList<TourFeature>();
        itineraryList = new LinkedList<TourFeature>();
    }

    public ArrayList<TourFeature> getTourFeatureList(Context context, String fileName) {
        try {
            FeatureCollection featureCollection = DataLoadingUtils.loadGeoJSONFromAssets(context, fileName);
            List<Feature> featuresList = featureCollection.getFeatures();
            Log.e("SIZE",featuresList.size()+"");

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

                if (v.getProperties().isNull("desc")) {
                    tourFeature.setStringHashMap("desc", null);
                } else {
                    tourFeature.setStringHashMap("desc", v.getProperties().getString("desc"));
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

                if (v.getProperties().isNull("addr")) {
                    tourFeature.setStringHashMap("addr", null);
                } else {
                    tourFeature.setStringHashMap("addr", v.getProperties().getString("addr"));
                }

                if (v.getProperties().isNull("url")) {
                    tourFeature.setStringHashMap("url", null);
                } else {
                    tourFeature.setStringHashMap("url", v.getProperties().getString("url"));
                }

                if (v.getProperties().isNull("review")) {
                    tourFeature.setStringHashMap("review", null);
                } else {
                    tourFeature.setStringHashMap("review", v.getProperties().getString("review"));
                }

                tourFeature.setLongitude(v.getGeometry().toJSON().getJSONArray("coordinates").getDouble(0));
                tourFeature.setLatitude(v.getGeometry().toJSON().getJSONArray("coordinates").getDouble(1));

                tourFeatureList.add(tourFeature);
            }

        } catch (IOException e) {
            // File not found, Try re-download
            Toast.makeText(context, R.string.Err_file_not_found, Toast.LENGTH_LONG);
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            // JSON Format is malformed
            Toast.makeText(context, R.string.Err_wrong_geojson_file, Toast.LENGTH_LONG);
            e.printStackTrace();
            return null;
        }

        return tourFeatureList;
    }

    /**
     * There is no Hotel or Food&Drink in the Itinerary GeoJSON file
     */
    private TourFeature findFeature(Context context, String name) {
        ArrayList<TourFeature> tourFeatures;

        GlobalsClass globalVariables = (GlobalsClass)context.getApplicationContext();

        tourFeatures = globalVariables.getTourFeatures(GlobalsClass.FeatureType.ATTRACTION);
        for (TourFeature v:tourFeatures) {
            if (v.getString("name").equals(name)) {
                v.setStringHashMap("category", "attraction");
                return v;
            }
        }

        tourFeatures = globalVariables.getTourFeatures(GlobalsClass.FeatureType.SHOPPING);
        for (TourFeature v:tourFeatures) {
            if (v.getString("name").equals(name)) {
                v.setStringHashMap("category", "shopping");
                return v;
            }
        }

        return null;

    }

    public LinkedList<TourFeature> getItineraryFeatureList(Context context, String fileName) {
        try {
            FeatureCollection featureCollection = DataLoadingUtils.loadGeoJSONFromAssets(context, fileName);
            List<Feature> featuresList = featureCollection.getFeatures();
            Log.e("SIZE", featuresList.size() + "");

            for (Feature v:featuresList) {
                TourFeature itineraryElement = findFeature(context, v.getProperties().getString("name"));
                if (itineraryElement == null) {
                    Toast.makeText(context, R.string.Err_wrong_itinerary_file, Toast.LENGTH_LONG);
                    return null;
                }
                itineraryList.add(itineraryElement);
            }
        } catch (IOException e) {
            // File not found, Try re-download
            Toast.makeText(context, R.string.Err_file_not_found, Toast.LENGTH_LONG);
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            // JSON Format is malformed
            Toast.makeText(context, R.string.Err_wrong_geojson_file, Toast.LENGTH_LONG);
            e.printStackTrace();
            return null;
        }

        return itineraryList;
    }
}
