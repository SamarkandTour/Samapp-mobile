package uz.samtuit.samapp.main;

import android.app.Application;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;

import uz.samtuit.samapp.util.TourFeature;

/**
 * Globally shared valuables
 */
public class GlobalsClass extends Application {
    private String AP_NAME;
    private String AP_LANG;
    private ArrayList<TourFeature> Hotels;
    private ArrayList<TourFeature> FoodAndDrinks;
    private ArrayList<TourFeature> Shops;
    private ArrayList<TourFeature> Attractions;
    private LinkedList<TourFeature> Itinerary;
    private String AP_VER;

    public enum FeatureType {
        NONE(0),
        HOTEL(1),
        FOODNDRINK(2),
        ATTRACTION(3),
        SHOPPING(4);

        private int num;
        FeatureType(int arg){
            this.num = arg;
        }
    };

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
                Log.e("GET","HOT");
                tourFeatures = Hotels;
                break;

            case FOODNDRINK:
                Log.e("GET","FOOD");
                tourFeatures = FoodAndDrinks;
                break;

            case ATTRACTION:
                Log.e("GET","ATTR");
                tourFeatures = Attractions;
                break;

            case SHOPPING:
                Log.e("GET","SHOP");
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
                Log.e("SET","HOTEL");
                this.Hotels = FeaturesArrayList;
                break;
            case FOODNDRINK:
                Log.e("SET","FOODDRINK");
                this.FoodAndDrinks = FeaturesArrayList;
                break;
            case ATTRACTION:
                Log.e("SET","ATTRACTION");
                this.Attractions = FeaturesArrayList;
                break;
            case SHOPPING:
                Log.e("SET","SHOP");
                this.Shops = FeaturesArrayList;
                break;
        }
    }

    public void  setItineraryFeatures(LinkedList FeaturesLinkedList) {
        this.Itinerary = FeaturesLinkedList;
    }
}
