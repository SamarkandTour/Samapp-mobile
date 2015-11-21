package uz.samtuit.samapp.util;

import android.app.Application;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Globally shared valuables
 */
public class GlobalsClass extends Application {
    static final public String[] supportedLanguages = {
            "en",
            "uz",
            "ru"
    };

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
        NONE(5);

        private int num;
        FeatureType(int arg){
            this.num = arg;
        }
    };

    private ArrayList<TourFeature> Hotels;
    private ArrayList<TourFeature> FoodAndDrinks;
    private ArrayList<TourFeature> Shops;
    private ArrayList<TourFeature> Attractions;
    private LinkedList<TourFeature> Itinerary;

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

    public void setFeatures(FeatureType featureType, ArrayList<TourFeature> featureArrayList)
    {
        switch(featureType)
        {
            case HOTEL:
//                Log.e("SET","HOTEL");
                this.Hotels = featureArrayList;
                break;
            case FOODNDRINK:
//                Log.e("SET","FOODDRINK");
                this.FoodAndDrinks = featureArrayList;
                break;
            case ATTRACTION:
//                Log.e("SET","ATTRACTION");
                this.Attractions = featureArrayList;
                break;
            case SHOPPING:
//                Log.e("SET","SHOP");
                this.Shops = featureArrayList;
                break;
        }
    }

    public void  setItineraryFeatures(LinkedList featureLinkedList) {
        this.Itinerary = featureLinkedList;
    }
}
