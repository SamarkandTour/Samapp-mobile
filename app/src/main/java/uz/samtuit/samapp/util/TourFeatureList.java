package uz.samtuit.samapp.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.cocoahero.android.geojson.Feature;
import com.cocoahero.android.geojson.FeatureCollection;
import com.cocoahero.android.geojson.GeoJSON;
import com.cocoahero.android.geojson.Point;
import com.mapbox.mapboxsdk.util.DataLoadingUtils;
import com.mapbox.mapboxsdk.util.constants.UtilConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import uz.samtuit.sammap.main.R;

/**
 * Create Tour Features List from GeoJSON file
 */
public class TourFeatureList {
    private ArrayList<TourFeature> tourFeatureList;
    private LinkedList<TourFeature> itineraryList;

    public TourFeatureList() {
        tourFeatureList = new ArrayList<TourFeature>();
    }

    public TourFeatureList(GlobalsClass.FeatureType featureType) {
        if (featureType == GlobalsClass.FeatureType.ITINERARY) {
            itineraryList = new LinkedList<TourFeature>();
        }
    }

    // Do this when first launch
    public static boolean writeAllPhotosToFiles(Context context) {
        try {
            for ( String lang : GlobalsClass.supportedLanguages) {
                for (int i = 0; i < 4; i++) {
                    FeatureCollection featureCollection = loadGeoJSONFromExternalFilesDir(context, lang + GlobalsClass.GeoJSONFileName[i]);
                    List<Feature> featuresList = featureCollection.getFeatures();
                    Log.e("SIZE", featuresList.size() + "");

                    for (Feature v : featuresList) {
                        if (!v.getProperties().isNull("photo")) {
                            FileUtil.fileWriteToExternalDir(context, v.getProperties().getString("name"), v.getProperties().getString("photo"));
                        }
                    }
                }
            }
        } catch (IOException e) {
            // File not found, Try re-download
            Toast.makeText(context, R.string.Err_file_not_found, Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return false;
        } catch (JSONException e) {
            // JSON Format is malformed
            Toast.makeText(context, R.string.Err_wrong_geojson_file, Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return false;
        }

        return true;
    }


    public ArrayList<TourFeature> getTourFeatureListFromGeoJSONFile(Context context, String fileName) {
        try {
            FeatureCollection featureCollection = loadGeoJSONFromExternalFilesDir(context, fileName);
            List<Feature> featuresList = featureCollection.getFeatures();
            Log.e("SIZE",featuresList.size()+"");

            for (Feature v:featuresList) {
                TourFeature tourFeature = new TourFeature();

                if (v.getProperties().isNull("photo")) {
                    tourFeature.setPhoto(null);
                } else {
                    // Changed the location of physical photo to a file to reduce on-memory size
                    // Just leave the name of the file here
                    tourFeature.setPhoto(v.getProperties().getString("name"));
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

                Point point = (Point) v.getGeometry();
                tourFeature.setLongitude(point.getPosition().getLongitude());
                tourFeature.setLatitude(point.getPosition().getLatitude());

                tourFeatureList.add(tourFeature);
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

    public LinkedList<TourFeature> getItineraryFeatureListFromGeoJSONFile(Context context, String fileName) {
        try {
            FeatureCollection featureCollection = loadGeoJSONFromExternalFilesDir(context, fileName);
            List<Feature> featuresList = featureCollection.getFeatures();
            Log.e("SIZE", featuresList.size() + "");

            for (Feature v:featuresList) {
                TourFeature itineraryElement = findFeature(context, v.getProperties().getString("name"));
                if (itineraryElement == null) {
                    Toast.makeText(context, R.string.Err_wrong_itinerary_file, Toast.LENGTH_LONG).show();
                    return null;
                }
                itineraryElement.setDay(v.getProperties().getInt("day"));
                itineraryList.add(itineraryElement);
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

        return itineraryList;
    }

    public static boolean ItineraryWriteToGeoJSONFile(Context context, LinkedList<TourFeature> itineraryList, String fileName) {
        FeatureCollection featureCollection = new FeatureCollection();
        for (TourFeature v : itineraryList) {
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
            FileUtil.fileWriteToExternalDir(context, fileName, ItineraryGeoJSON);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return true;
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
        }

        return true;
    }

    public static FeatureCollection loadGeoJSONFromExternalFilesDir(final Context context, final String fileName)  throws IOException, JSONException {
        if (TextUtils.isEmpty(fileName)) {
            throw new NullPointerException("No GeoJSON File Name passed in.");
        }

        if (UtilConstants.DEBUGMODE) {
            Log.d(DataLoadingUtils.class.getCanonicalName(), "Mapbox SDK loading GeoJSON URL: " + fileName);
        }

        FileInputStream fis = new FileInputStream(context.getExternalFilesDir(null).getAbsolutePath() + "/" + fileName);
        BufferedReader rd = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
        String jsonText = DataLoadingUtils.readAll(rd);

        FeatureCollection parsed = (FeatureCollection) GeoJSON.parse(jsonText);
        if (UtilConstants.DEBUGMODE) {
            Log.d(DataLoadingUtils.class.getCanonicalName(), "Parsed GeoJSON with " + parsed.getFeatures().size() + " features.");
        }

        return parsed;
    }
}
