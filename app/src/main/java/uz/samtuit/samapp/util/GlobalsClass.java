package uz.samtuit.samapp.util;

import android.app.Application;
import android.location.Location;

import java.util.ArrayList;
import java.util.LinkedList;

import uz.samtuit.samapp.main.R;

/**
 * Globally shared valuables
 */
public class GlobalsClass extends Application {
    static final public String featuresDownloadURL = "http://download.samarkandtour.uz/tour_database.zip";
    static final public String mapDownloadURL = "http://download.samarkandtour.uz/samarkand.mbtiles";
    static final public String mapFileName = "samarkand.mbtiles";

    private boolean itineraryModifyMode = false;

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

    private Location currentLoc;

    public ArrayList<TourFeature> getTourFeatures(FeatureType featureType) {
        switch(featureType) {
            case HOTEL:
                return Hotels;

            case FOODNDRINK:
                return FoodAndDrinks;

            case ATTRACTION:
                return Attractions;

            case SHOPPING:
                return Shops;
        }

        return null;
    }

    public LinkedList<TourFeature> getItineraryFeatures() {
        return Itinerary;
    }

    public static String ParseCellPhoneNumber( String CellPhoneString ) {
        String result = "+";
        int counter = 0;
        for(int i = 0; i < CellPhoneString.length() && counter < 12; i++) {
            if(Character.isDigit(CellPhoneString.charAt(i))) {
                result += CellPhoneString.charAt(i);
                counter++;
            } else {
                continue;
            }
        }
        return result;
    }

    public void setFeatures(FeatureType featureType, ArrayList<TourFeature> featureArrayList) {
        switch(featureType) {
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
    }

    public void clearCurrnetLoc() {
        if (currentLoc != null) {
            currentLoc.reset();
        }
    }

    public void setCurrentLoc(Location loc) {
        currentLoc = loc;
    }

    public Location getCurrentLoc() {
        return currentLoc;
    }

    public static int getPrimaryColorId(GlobalsClass.FeatureType type) {
        int id = 0;

        switch (type) {
            case HOTEL:
                id = R.color.hotel_primary;
                break;
            case FOODNDRINK:
                id = R.color.foodanddrink_primary;
                break;
            case ATTRACTION:
                id = R.color.attraction_primary;
                break;
            case SHOPPING:
                id = R.color.shop_primary;
                break;
        }

        return id;
    }

    public static int getToolbarColorId(GlobalsClass.FeatureType type) {
        int id = 0;

        switch (type) {
            case HOTEL:
                id = R.color.hotel_tool;
                break;
            case FOODNDRINK:
                id = R.color.foodanddrink_tool;
                break;
            case ATTRACTION:
                id = R.color.attraction_tool;
                break;
            case SHOPPING:
                id = R.color.shop_tool;
                break;
        }

        return id;
    }
}
