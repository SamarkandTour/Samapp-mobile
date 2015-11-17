package uz.samtuit.samapp.util;

import android.app.Application;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Globally shared valuables
 */
public class GlobalsClass extends Application {
    // Don't change the order of the file, it's synced with FeatureType
    static final public String[] GeoJSONFileName = {
            "_hotels.geojson",
            "_foodndrinks.geojson",
            "_attractions.geojson",
            "_shopping.geojson",
            "_MyItinerary.geojson"
    };

    public enum FeatureType {
        HOTEL(0),
        FOODNDRINK(1),
        ATTRACTION(2),
        SHOPPING(3),
        ITINERARY(4),
        PATHOVERLAY(5);

        private int num;
        FeatureType(int arg){
            this.num = arg;
        }
    };

    private String AP_NAME;
    private String AP_LANG;
    private ArrayList<TourFeature> Hotels;
    private ArrayList<TourFeature> FoodAndDrinks;
    private ArrayList<TourFeature> Shops;
    private ArrayList<TourFeature> Attractions;
    private LinkedList<TourFeature> Itinerary;
    private String AP_VER;

    public String getApplicationName() {
        return AP_NAME;
    }
    public String getApplicationLanguage(){
        return AP_LANG;
    }
    public String getApplicationVersion(){
        return AP_VER;
    }

    public ArrayList<TourFeature> getTourFeatures(FeatureType featureType)
    {
        ArrayList<TourFeature> tourFeatures = null;
        switch(featureType)
        {
            case HOTEL:
//                Log.e("GET","HOT");
                tourFeatures = Hotels;
                break;

            case FOODNDRINK:
//                Log.e("GET","FOOD");
                tourFeatures = FoodAndDrinks;
                break;

            case ATTRACTION:
//                Log.e("GET","ATTR");
                tourFeatures = Attractions;
                break;

            case SHOPPING:
//                Log.e("GET","SHOP");
                tourFeatures = Shops;
                break;
        }

        return tourFeatures;

    }

    public LinkedList<TourFeature> getItineraryFeatures()
    {
        return Itinerary;
    }

    public void setApplicationName(String Name){
        AP_NAME = Name;
    }
    public void setApplicationVersion(String Version){
        AP_VER = Version;
    }
    public void setApplicationLanguage(String Language){
        AP_LANG = Language;
    }

    public void setFeatures(FeatureType featureType, ArrayList<TourFeature> FeaturesArrayList)
    {
        switch(featureType)
        {
            case HOTEL:
//                Log.e("SET","HOTEL");
                this.Hotels = FeaturesArrayList;
                break;
            case FOODNDRINK:
//                Log.e("SET","FOODDRINK");
                this.FoodAndDrinks = FeaturesArrayList;
                break;
            case ATTRACTION:
//                Log.e("SET","ATTRACTION");
                this.Attractions = FeaturesArrayList;
                break;
            case SHOPPING:
//                Log.e("SET","SHOP");
                this.Shops = FeaturesArrayList;
                break;
        }
    }

    public void  setItineraryFeatures(LinkedList FeaturesLinkedList) {
        this.Itinerary = FeaturesLinkedList;
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("app_properties.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
