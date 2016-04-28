package uz.samtuit.samapp.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.cocoahero.android.geojson.Feature;
import com.cocoahero.android.geojson.FeatureCollection;
import com.cocoahero.android.geojson.Point;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uz.samtuit.samapp.main.R;

/**
 * Create Tour Features List from GeoJSON file
 */
public class TourFeatureList {
    public static final String photoDirectory = "photo/";

    private ArrayList<TourFeature> tourFeatureList;

    public TourFeatureList() {
        tourFeatureList = new ArrayList<TourFeature>();
    }

    public void setTourFeaturesToGlobal(Context context, GlobalsClass.FeatureType featureType, ArrayList<TourFeature> tourFeatureList) {
        GlobalsClass globalsClass = (GlobalsClass)context.getApplicationContext();
        globalsClass.setFeatures(featureType, tourFeatureList);
    }

    public ArrayList<TourFeature> getTourFeatureListFromGeoJSONFile(Context context, String fileName) {
        try {
            FeatureCollection featureCollection = FileUtil.loadFeatureCollectionFromExternalGeoJSONFile(context, fileName);
            if (featureCollection == null) {
                return tourFeatureList;
            }

            List<Feature> featuresList = featureCollection.getFeatures();
            for (Feature v:featuresList) {
                TourFeature tourFeature = new TourFeature();

                if (v.getProperties().isNull("photo")) {
                    tourFeature.setPhoto(null);
                } else {
                    // Changed the location of physical photo to a file to reduce on-memory size
                    // Just leave the name of the file here
                    tourFeature.setPhoto(photoDirectory + v.getProperties().getString("name"));
                }

                tourFeature.setRating(v.getProperties().getInt("rating"));

                if (v.getProperties().isNull("name")) {
                    tourFeature.setStringHashMap("name", null);
                } else {
                    tourFeature.setStringHashMap("name", v.getProperties().getString("name"));
                }

                if (v.getProperties().isNull("booking")) {
                    // Leave a blank space, not NULL, because it's possible not to have a booking property in the downloaded file
                    tourFeature.setStringHashMap("booking", "");
                } else {
                    tourFeature.setStringHashMap("booking", v.getProperties().getString("booking"));
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

                if (v.getProperties().isNull("tel")) {
                    tourFeature.setStringHashMap("tel", null);
                } else {
                    tourFeature.setStringHashMap("tel", v.getProperties().getString("tel"));
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

                Point point = (Point) v.getGeometry();
                tourFeature.setLongitude(point.getPosition().getLongitude());
                tourFeature.setLatitude(point.getPosition().getLatitude());

                tourFeatureList.add(tourFeature);
            }
        } catch (JSONException e) {
            // JSON Format is malformed
            Toast.makeText(context, R.string.Err_wrong_geojson_file, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        Log.e("TourFeatureList SIZE", tourFeatureList.size() + "");
        return tourFeatureList;
    }

    // Do this when first launch or since new update has been downloaded
    public static boolean writeAllPhotosToFiles(Context context) {
        Log.e("BBBBB","BEEEP");
        try {
            FileUtil.createDirectoryInExternalDir(context, photoDirectory); // If photo directory is not

            for ( String lang : GlobalsClass.supportedLanguages) {
                for (int i = 0; i < GlobalsClass.featuresGeoJSONFileName.length; i++) {
                    FeatureCollection featureCollection = FileUtil.loadFeatureCollectionFromExternalGeoJSONFile(context, lang + GlobalsClass.featuresGeoJSONFileName[i]);
                    if (featureCollection == null) { // If there is no file, skip this file
                        continue;
                    }

                    List<Feature> featuresList = featureCollection.getFeatures();
                    Log.e("SIZE", featuresList.size() + "");

                    for (Feature v : featuresList) {
                        if (!v.getProperties().isNull("photo")) {
                            FileUtil.fileWriteToExternalDir(context, photoDirectory + v.getProperties().getString("name"),  v.getProperties().getString("photo"));
                        }
                    }
                }
            }
        } catch (JSONException e) {
            // JSON Format is malformed
            Toast.makeText(context, R.string.Err_wrong_geojson_file, Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static GlobalsClass.FeatureType findFeatureTypeByName(Context context, String name) {
        ArrayList<TourFeature> tourFeatures;

        GlobalsClass globalVariables = (GlobalsClass)context.getApplicationContext();

        for (GlobalsClass.FeatureType featuretype : GlobalsClass.FeatureType.values()) {
            tourFeatures = globalVariables.getTourFeatures(featuretype);
            for (TourFeature v:tourFeatures) {
                if (v.getString("name").equals(name)) {
                    return featuretype;
                }
            }
        }

        return null;

    }

    public static TourFeature findFeatureByName(Context context, String name) {
        ArrayList<TourFeature> tourFeatures;

        GlobalsClass globalVariables = (GlobalsClass)context.getApplicationContext();

        for (GlobalsClass.FeatureType featuretype : GlobalsClass.FeatureType.values()) {
            tourFeatures = globalVariables.getTourFeatures(featuretype);
            for (TourFeature v:tourFeatures) {
                if (v.getString("name").equals(name)) {
                    return v;
                }
            }
        }

        return null;

    }

    // All downloaded GeoJSON files from server should be located in ExternalDir
    // So working directory is ExternalDir, all files in the asset should be copied to ExternalDir at first launch
    public static boolean CopyLocalGeoJSONFilesToExternalDir(Context context) {
        try {
            String[] filelist = context.getAssets().list("data"); // Files in the assets/data

            if (!FileUtil.CopyFilesFromAssetToExternalDir(context, filelist)) {
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static void loadAllFeaturesToMemory(Context context, String chosenLang) {
        String path = null;

        for (GlobalsClass.FeatureType featureType : GlobalsClass.FeatureType.values()){
            if (featureType == GlobalsClass.FeatureType.ITINERARY) {
                path = ItineraryList.myItineraryDirectory + chosenLang + ItineraryList.myItineraryGeoJSONFileName;

                ItineraryList.getInstance().clearItineraryFeatureList(); // Clear itinerary list
                ItineraryList.getInstance().getItineraryFeatureListFromGeoJSONFile(context, path);
                ItineraryList.getInstance().setItinearyFeaturesToGlobal(context);
                ItineraryList.getInstance().categorizeItineraryWithDays(context, chosenLang); // Categorize with days from the name of itinerary files
            } else {
                Log.e("TAG",featureType.toString());
                path = GlobalsClass.featuresGeoJSONFileName[featureType.ordinal()];

                TourFeatureList tourFeatureList = new TourFeatureList();
                ArrayList<TourFeature> tourFeatureArrayList = tourFeatureList.getTourFeatureListFromGeoJSONFile(context, chosenLang + path);
                tourFeatureList.setTourFeaturesToGlobal(context, featureType, tourFeatureArrayList);
            }
        }
    }
}
