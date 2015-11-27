package uz.samtuit.samapp.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.cocoahero.android.geojson.Feature;
import com.cocoahero.android.geojson.FeatureCollection;
import com.cocoahero.android.geojson.Point;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import uz.samtuit.sammap.main.R;

/**
 * Itinerary Class
 */
public class ItineraryList {
    static final public String myItineraryGeoJSONFileName = "_MyItinerary.geojson"; // my customized itinerary file

    public static final int MAX_ITINERARY_DAYS = 5;
    private static HashMap<String , Float> mCourseHashMap;
    private static LinkedList<TourFeature> mItineraryList;
    private static ItineraryList ourInstance = new ItineraryList(); // Singleton

    public ItineraryList() {
        mItineraryList = new LinkedList<TourFeature>();
    }

    // Singleton
    public static ItineraryList getInstance() {
        return ourInstance;
    }

    public ItineraryList(LinkedList<TourFeature> itineraryList) {
        mItineraryList = itineraryList;
    }

    public void setItinearyFeaturesToGlobal(Context context) {
        GlobalsClass globalsClass = (GlobalsClass)context.getApplicationContext();
        globalsClass.setItineraryFeatures(mItineraryList);
    }

    /**
     * There is no Hotel or Food&Drink in the Itinerary GeoJSON file
     */
    private TourFeature findFeatureInAttractionNShoppingList(Context context, String name) {
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

    public void clearItineraryFeatureList() {
        mItineraryList.clear();
    }

    public LinkedList<TourFeature> getItineraryFeatureListFromGeoJSONFile(Context context, String fileName) {
        try {
            FeatureCollection featureCollection = FileUtil.loadFeatureCollectionfromExternalGeoJSONFile(context, fileName);
            List<Feature> featuresList = featureCollection.getFeatures();
            Log.e("SIZE", featuresList.size() + "");

            for (Feature v:featuresList) {
                TourFeature itineraryElement = findFeatureInAttractionNShoppingList(context, v.getProperties().getString("name"));

                if (itineraryElement == null) {
                    Toast.makeText(context, R.string.Err_wrong_itinerary_file, Toast.LENGTH_LONG).show();
                } else {
                    itineraryElement.setDay(v.getProperties().getInt("day"));
                    mItineraryList.add(itineraryElement);
                }
            }
        } catch (IOException e) {
            // File not found, Try re-download
            Toast.makeText(context, R.string.Err_file_not_found, Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            // JSON Format is malformed
            Toast.makeText(context, R.string.Err_wrong_geojson_file, Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return null;
        }

        return mItineraryList;
    }

    public static boolean itineraryWriteToGeoJSONFile(Context context, String lang) {
        FeatureCollection featureCollection = new FeatureCollection();
        for (TourFeature v : mItineraryList) {
            Feature feature = new Feature();

            try {
                Point coordinates = new Point(v.getLatitude(), v.getLongitude()); // Caution! the order of coordinates
                feature.setGeometry(coordinates);

                JSONObject properties = new JSONObject();
                properties.put("photo", v.getPhoto());
                properties.put("rating", v.getRating());
                properties.put("name", v.getString("name"));
                properties.put("day", v.getDay());
                properties.put("comment", "");
                feature.setProperties(properties);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            featureCollection.addFeature(feature);
        }

        try {
            String ItineraryGeoJSON = featureCollection.toJSON().toString();
            String path = lang + myItineraryGeoJSONFileName;
            FileUtil.fileWriteToExternalDir(context, path, ItineraryGeoJSON);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static void categorizeItineraryWithDays(Context context, String lang) {
        String filter = new String(lang + "_itinerary");
        String[] stringArray = FileUtil.getFileNameListWithFilter(context, filter);

        mCourseHashMap = new HashMap<String, Float>();

        for (String fileName : stringArray) {
            String[] splits1 = fileName.split("_");
            float day = Float.valueOf(splits1[2]);
            String[] splits2 = splits1[3].split("\\.");
            String course = splits2[0];

            mCourseHashMap.put(course, day);
        }
    }

    public static HashMap<String , Float> getCourseHashMap() {
        return mCourseHashMap;
    }
}
