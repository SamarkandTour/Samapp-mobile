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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import uz.samtuit.samapp.main.R;

/**
 * Itinerary Class
 */
public class ItineraryList {
    public static final String myItineraryDirectory = "my_itinerary/";
    public static final String myItineraryGeoJSONFileName = "_my_itinerary.geojson"; // my customized itinerary file
    public static final int MAX_ITINERARY_DAYS = 5;
    public static int MAX_ITINERARY_COURSES;
    private static float tourDay;

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

    public void setItinearyFeaturesToGlobal(Context context) {
        GlobalsClass globalsClass = (GlobalsClass)context.getApplicationContext();
        globalsClass.setItineraryFeatures(mItineraryList);
    }

    static public void sortItineraryList() {
        Collections.sort(mItineraryList);
    }

    public TourFeature findFeature(Context context, String name) {
        ArrayList<TourFeature> tourFeatures;

        GlobalsClass globalVariables = (GlobalsClass)context.getApplicationContext();

        tourFeatures = globalVariables.getTourFeatures(GlobalsClass.FeatureType.ATTRACTION);
        for (TourFeature v:tourFeatures) {
            if (v.getString("name").equals(name)) {
                v.setStringHashMap("category", "attraction");
                return v;
            }
        }

        tourFeatures = globalVariables.getTourFeatures(GlobalsClass.FeatureType.FOODNDRINK);
        for (TourFeature v:tourFeatures) {
            if (v.getString("name").equals(name)) {
                v.setStringHashMap("category", "foodndrink");
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

        tourFeatures = globalVariables.getTourFeatures(GlobalsClass.FeatureType.HOTEL);
        for (TourFeature v:tourFeatures) {
            if (v.getString("name").equals(name)) {
                v.setStringHashMap("category", "hotel");
                return v;
            }
        }

        return null;
    }

    public boolean addNewFeatureToItineraryList(TourFeature tourFeature, int index){
        if(tourFeature!=null){
            Log.e("day & index",  index + "");
            try {
                mItineraryList.add(index, tourFeature);
            } catch (Exception ex) {
                ex.printStackTrace();
                mItineraryList.add(tourFeature);
            }

//            boolean added = false;
//            int counter = 0;
//            int listSize = mItineraryList.size();
//            for(int i = 0;  i < listSize; i++) {
//                if(mItineraryList.get(i).getDay() == tourFeature.getDay()) {
//                    if(counter==index) { //if counter is in index position add tourfeature
//                        added = true;
//                        Log.e("ADDED INDEX" , i + " " );
//                        mItineraryList.add(i, tourFeature);
//                        break;
//                    } else {
//                        counter++;
//                    }
//                }
//            }
//            if(!added)
//                mItineraryList.add(tourFeature);
////            mItineraryList.add(tourFeature);
            return true;
        }
        return false;
    }

    public void clearItineraryFeatureList() {
        mItineraryList.clear();
    }

    public static void initTourday() {
        tourDay = 0;
    }

    public LinkedList<TourFeature> mergeCoursesFromGeoJSONFileToItineraryList(Context context, String fileName) {
        String prevAmPm = null, preDay = null;

        try {
            FeatureCollection featureCollection = FileUtil.loadFeatureCollectionFromExternalGeoJSONFile(context, fileName);
            if (featureCollection == null) {
                return mItineraryList;
            }

            List<Feature> featuresList = featureCollection.getFeatures();
            Log.e("SIZE", featuresList.size() + "");

            for (Feature v:featuresList) {
                TourFeature itineraryElement = findFeature(context, v.getProperties().getString("name"));

                if (itineraryElement == null) {
                    Log.e("ItineraryList", "Wrong Feature name=" + v.getProperties().getString("name"));
                } else {
                    boolean found = false;
                    String curAmPm = v.getProperties().getString("ampm");
                    String curDay = v.getProperties().getString("day");

                    // Whenever change between AM and PM, increase tour day by 0.5day
                    if (!curAmPm.equals(prevAmPm) || !curDay.equals(preDay)) {
                        tourDay += 0.5F;
                    }

                    prevAmPm = curAmPm;
                    preDay = curDay;

                    // Remove duplication
                    for (TourFeature feature : mItineraryList) {
                        if (feature.getString("name").equals(itineraryElement.getString("name"))) {
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        itineraryElement.setDay(Math.round(tourDay)); // Round up
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

    public LinkedList<TourFeature> getItineraryFeatureListFromGeoJSONFile(Context context, String fileName) {
        clearItineraryFeatureList(); // Init memory

        try {
            FeatureCollection featureCollection = FileUtil.loadFeatureCollectionFromExternalGeoJSONFile(context, fileName);
            if (featureCollection == null) {
                return mItineraryList;
            }

            List<Feature> featuresList = featureCollection.getFeatures();
            for (Feature v:featuresList) {
                TourFeature itineraryElement = findFeature(context, v.getProperties().getString("name"));

                if (itineraryElement == null) {
                    // If the feature's name  in the file is not same as new name since Wiki is updated, sync will be broken.
                    Log.e("ItineraryList", "Wrong feature in itinerary file, feature name=" + v.getProperties().getString("name"));
                } else {
                    itineraryElement.setDay(v.getProperties().getInt("day"));
                    mItineraryList.add(itineraryElement);
                }
            }
        } catch (JSONException e) {
            // JSON Format is malformed
            Toast.makeText(context, R.string.Err_wrong_geojson_file, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        Log.e("ItineraryList SIZE", mItineraryList.size() + "");
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
