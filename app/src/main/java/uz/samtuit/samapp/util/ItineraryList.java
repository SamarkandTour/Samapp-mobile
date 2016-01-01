package uz.samtuit.samapp.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.cocoahero.android.geojson.Feature;
import com.cocoahero.android.geojson.FeatureCollection;
import com.cocoahero.android.geojson.Point;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import uz.samtuit.samapp.main.R;

/**
 * Itinerary Class
 */
public class ItineraryList {
    public static final String myItineraryDirectory = "my_itinerary/"; // my customized itinerary file
    public static final String myItineraryGeoJSONFileName = "_my_itinerary.geojson"; // my customized itinerary file
    public static final int MAX_ITINERARY_DAYS = 5;
    public static int MAX_ITINERARY_COURSES;

    private static HashMap<String , Float> mCourseWithDayHashMap;
    private static HashMap<String , String> mCourseWithUiNameHashMap;
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
    public void setNewItinearyFeaturesToGlobal(Context context, LinkedList<TourFeature> list) {
        GlobalsClass globalsClass = (GlobalsClass)context.getApplicationContext();
        globalsClass.setItineraryFeatures(list);
    }

    public void sendToAnotherDay(Context context, String ItineraryFeatureName, int inc){
        int last = mItineraryList.size() - 1;
        for(int i = last; i >= 0; i--){
            if(mItineraryList.get(i).getString("name")==ItineraryFeatureName){
                mItineraryList.get(i).setDay(mItineraryList.get(i).getDay()+inc);
                break;
            }
        }
        GlobalsClass globalsClass = (GlobalsClass)context.getApplicationContext();
        globalsClass.setItineraryFeatures(mItineraryList);
        itineraryWriteToGeoJSONFile(context, context.getSharedPreferences("SamTour_Pref", 0).getString("app_lang", null));
    }
    /**
     * There is no Hotel or Food&Drink in the Itinerary GeoJSON file
     */
    public TourFeature findFeatureInAttractionNShoppingList(Context context, String name) {
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

    public boolean addNewFeatureToItineraryList(TourFeature tourFeature){
        if(tourFeature!=null){
            mItineraryList.add(tourFeature);
            return true;
        }
        return false;
    }

    public void clearItineraryFeatureList() {
        mItineraryList.clear();
    }

    public LinkedList<TourFeature> getItineraryFeatureListFromGeoJSONFile(Context context, String fileName) {
        try {
            FeatureCollection featureCollection = FileUtil.loadFeatureCollectionFromExternalGeoJSONFile(context, fileName);
            if (featureCollection == null) {
                return mItineraryList;
            }

            List<Feature> featuresList = featureCollection.getFeatures();
            Log.e("SIZE", featuresList.size() + "");

            for (Feature v:featuresList) {
                TourFeature itineraryElement = findFeatureInAttractionNShoppingList(context, v.getProperties().getString("name"));

                if (itineraryElement == null) {
                    Toast.makeText(context, R.string.Err_wrong_itinerary_file, Toast.LENGTH_LONG).show();
                } else {
                    itineraryElement.setDay(v.getProperties().getInt("day"));
                    int last = mItineraryList.size();
                    boolean found = false;
                    for (int i = last-1; i >= 0; i--) {
                        if(mItineraryList.get(i).getString("name").equals(itineraryElement.getString("name"))){
                            found = true;
                            if(mItineraryList.get(i).getDay()>itineraryElement.getDay())
                            {
                                mItineraryList.get(i).setDay(itineraryElement.getDay());
                            }
                            break;
                        }
                    }
                    if(!found){
                        mItineraryList.add(itineraryElement);
                    }
                }
            }
        } catch (JSONException e) {
            // JSON Format is malformed
            Toast.makeText(context, R.string.Err_wrong_geojson_file, Toast.LENGTH_LONG).show();
            e.printStackTrace();
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
                return false;
            }
            featureCollection.addFeature(feature);
        }

        try {
            FileUtil.createDirectoryInExternalDir(context, myItineraryDirectory);
            String fileName = myItineraryDirectory + lang + myItineraryGeoJSONFileName;
            String ItineraryGeoJSON = featureCollection.toJSON().toString();
            FileUtil.fileWriteToExternalDir(context, fileName, ItineraryGeoJSON);

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private String courseNameToUiName(Context context, String key) {
        int resID = context.getResources().getIdentifier("itinerary_" + key, "string", context.getPackageName());
        return context.getString(resID);
    }

    public void categorizeItineraryWithDays(Context context, String lang) {
        String filter = new String(lang + "_itinerary");
        String[] stringArray = FileUtil.getFileNameListWithFilter(context, filter);

        mCourseWithDayHashMap = new HashMap<String, Float>();
        mCourseWithUiNameHashMap = new HashMap<String, String>();

        // Make HashMap with a pair of both course name and the tour day
        for (String fileName : stringArray) {
            String[] splits1 = fileName.split("_");
            float day = Float.valueOf(splits1[2]);
            String[] splits2 = splits1[3].split("\\.");
            String courseName = splits2[0];

            mCourseWithDayHashMap.put(courseName, day);
            mCourseWithUiNameHashMap.put(courseName, courseNameToUiName(context, courseName));
        }

        MAX_ITINERARY_COURSES = mCourseWithDayHashMap.size();
    }

    public static float getCourseDayFromHashMap(String key) {
        return mCourseWithDayHashMap.get(key);
    }

    public static Set<String> getCourseHashMap() {
        return mCourseWithDayHashMap.keySet();
    }

    public static String getUiNameFromHashMap(String key) {
        return mCourseWithUiNameHashMap.get(key);
    }

    public static String UiCourseNameToCourseName(String value) {
        for (String key : mCourseWithUiNameHashMap.keySet()) {
            if (mCourseWithUiNameHashMap.get(key).equals(value)) {
                return key;
            }
        }

        return null;
    }
}
