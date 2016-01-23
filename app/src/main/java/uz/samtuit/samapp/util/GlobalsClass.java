package uz.samtuit.samapp.util;

import android.app.Application;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Globally shared valuables
 */
public class GlobalsClass extends Application {
    static final public String featuresDownloadURL = "http://download.samarkandtour.org/tour_database.zip";
    static final public String mapDownloadURL = "http://download.samarkandtour.org/samarkand.mbtiles";
    static final public String mapFileName = "samarkand.mbtiles";

    static final public String[] supportedLanguages = {
            "en",
            "uz",
            "ru"
    };

    // Don't change the order of the file, it's synced with FeatureType
    static final public String[] featuresGeoJSONFileName = {
            "_hotels.geojson",
            "_foodndrinks.geojson",
            "_attractions.geojson",
            "_shopping.geojson",
    };

    public enum FeatureType {
        HOTEL(0),
        FOODNDRINK(1),
        ATTRACTION(2),
        SHOPPING(3),
        ITINERARY(4);

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
                tourFeatures = Hotels;
                break;

            case FOODNDRINK:
                tourFeatures = FoodAndDrinks;
                break;

            case ATTRACTION:
                tourFeatures = Attractions;
                break;

            case SHOPPING:
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
                Hotels = featureArrayList;
                break;

            case FOODNDRINK:
                FoodAndDrinks = featureArrayList;
                break;

            case ATTRACTION:
                Attractions = featureArrayList;
                break;

            case SHOPPING:
                Shops = featureArrayList;
                break;
        }
    }

    public void setItineraryFeatures(LinkedList featureLinkedList) {
        Itinerary = featureLinkedList;
        /*
        Itinerary = new LinkedList<TourFeature>();

        for (int i = 0; i < featureLinkedList.size(); i++) {
            if (!Itinerary.contains(featureLinkedList.get(i))) {
                Itinerary.add((TourFeature)featureLinkedList.get(i));
            }
        }
        */
    }
}
