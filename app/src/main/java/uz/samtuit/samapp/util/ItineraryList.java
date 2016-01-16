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
import uz.samtuit.samapp.main.SuggestedItineraryActivity;

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
        mItineraryList.clear();
        mItineraryList = list;
    }

    public void sendToAnotherDay(Context context, String ItineraryFeatureName,int day, int index, int inc){
        int last = mItineraryList.size() - 1;
        int pos = 0;
        int newInd = 0;
        for (int i = last; i >= 0; i--) {
            TourFeature item = mItineraryList.get(i);
            if(item.getDay() == day){
                if(Integer.parseInt(item.getString("index"))>index){
                    mItineraryList.get(i).setStringHashMap("index",(Integer.parseInt(item.getString("index"))-1)+"");
                }
            }
            if(item.getDay() == day + inc){
                newInd = Math.max(newInd,Integer.parseInt(item.getString("index")));
            }
            if (mItineraryList.get(i).getString("name")==ItineraryFeatureName) {
                pos = i;
            }
        }
        mItineraryList.get(pos).setDay(day+inc);
        mItineraryList.get(pos).setStringHashMap("index",(++index)+"");

        GlobalsClass globalsClass = (GlobalsClass)context.getApplicationContext();
        globalsClass.setItineraryFeatures(mItineraryList);
        SuggestedItineraryActivity suggestedItineraryActivity = SuggestedItineraryActivity.getInstance();
        suggestedItineraryActivity.InitItineraryListArray(globalsClass);
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

        tourFeatures = globalVariables.getTourFeatures(GlobalsClass.FeatureType.SHOPPING);
        for (TourFeature v:tourFeatures) {
            if (v.getString("name").equals(name)) {
                v.setStringHashMap("category", "shopping");
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

        tourFeatures = globalVariables.getTourFeatures(GlobalsClass.FeatureType.HOTEL);
        for (TourFeature v:tourFeatures) {
            if (v.getString("name").equals(name)) {
                v.setStringHashMap("category", "hotel");
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

    public static void initTourday() {
        tourDay = 0;
    }

    public LinkedList<TourFeature> mergeCoursesFromGeoJSONFileToItineraryList(Context context, String fileName) {
        String prevAmPm = null, preDay = null;
        int index = 0;
        try {
            index = mItineraryList.size();
        }catch (Exception ex){
            ex.printStackTrace();
        }

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
                    Toast.makeText(context, R.string.Err_wrong_itinerary_file, Toast.LENGTH_LONG).show();
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
                        itineraryElement.setStringHashMap("index",(++index)+"");
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
        int index = 0;
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
                    Toast.makeText(context, R.string.Err_wrong_itinerary_file, Toast.LENGTH_LONG).show();
                } else {
                    itineraryElement.setStringHashMap("index",(++index)+"");
                    itineraryElement.setDay(v.getProperties().getInt("day"));
                    mItineraryList.add(itineraryElement);
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
