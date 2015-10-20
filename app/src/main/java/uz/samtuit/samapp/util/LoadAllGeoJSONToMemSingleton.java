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
 * Singleton class for global access of GeoJSON features parsed from files
 */
public class LoadAllGeoJSONToMemSingleton {
    private static LoadAllGeoJSONToMemSingleton uniqueInstance;

    public static ArrayList<TourFeature> hotelFeatureList;
    public static ArrayList<TourFeature> foodndrinkFeatureList;
    public static ArrayList<TourFeature> attractionFeatureList;
    public static ArrayList<TourFeature> shoppingFeatureList;

    public LoadAllGeoJSONToMemSingleton() {}

    public static LoadAllGeoJSONToMemSingleton getInstance(Context context, String[] files) {
        if(uniqueInstance == null) {
            uniqueInstance = new LoadAllGeoJSONToMemSingleton();

            hotelFeatureList = new ArrayList<TourFeature>();
            foodndrinkFeatureList = new ArrayList<TourFeature>();
            attractionFeatureList = new ArrayList<TourFeature>();
            shoppingFeatureList = new ArrayList<TourFeature>();

            uniqueInstance.getTourFeatureList(context, hotelFeatureList, files[0]);
            uniqueInstance.getTourFeatureList(context, foodndrinkFeatureList, files[1]);
            uniqueInstance.getTourFeatureList(context, attractionFeatureList, files[2]);
            uniqueInstance.getTourFeatureList(context, shoppingFeatureList, files[3]);
        }

        return uniqueInstance;
    }

    public static void releaseInstance() {
        uniqueInstance = null;

        hotelFeatureList = null;
        foodndrinkFeatureList = null;
        attractionFeatureList = null;
        shoppingFeatureList = null;
    }

    public void getTourFeatureList(Context context, ArrayList<TourFeature> tourFeatureList, String fileName) {
        try {
            FeatureCollection featureCollection = DataLoadingUtils.loadGeoJSONFromAssets(context, fileName);
            List<Feature> featuresList = featureCollection.getFeatures();

            for (Feature v:featuresList) {
                TourFeature tourFeature = new TourFeature();

                //tourFeature.setPhoto(base64ToBitmap(v.getProperties().getString("photo")));
                tourFeature.setPhoto(v.getProperties().getString("photo"));
                tourFeature.setRating(v.getProperties().getInt("rating"));
                tourFeature.setStringHashMap("name", v.getProperties().getString("name"));
                tourFeature.setStringHashMap("description", v.getProperties().getString("description"));
                tourFeature.setStringHashMap("type", v.getProperties().getString("type"));
                tourFeature.setStringHashMap("price", v.getProperties().getString("price"));
                tourFeature.setStringHashMap("wifi", v.getProperties().getString("wifi"));
                tourFeature.setStringHashMap("open", v.getProperties().getString("open"));
                tourFeature.setStringHashMap("address", v.getProperties().getString("address"));
                tourFeature.setStringHashMap("url", v.getProperties().getString("url"));
                tourFeature.setLongitude(v.getProperties().getJSONArray("coordinates").getDouble(0));
                tourFeature.setLatitude(v.getProperties().getJSONArray("coordinates").getDouble(1));

                tourFeatureList.add(tourFeature);
            }

        } catch (IOException e) {
            // File not found, Try redownload
            e.printStackTrace();
        } catch (JSONException e) {
            // JSON Format is malformed
            e.printStackTrace();
        }
    }

    private Bitmap base64ToBitmap(String b64) {
        byte[] imageAsBytes = Base64.decode(b64.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }
}
