package uz.samtuit.samapp.main;

import android.app.Application;
import android.util.Log;

import java.util.ArrayList;

import uz.samtuit.samapp.util.TourFeature;

/**
 * Created by Bakhrom Rakhmonov on 09.10.2015.
 */
public class GlobalsClass extends Application {
    private String AP_NAME;
    private String AP_LANG;
    private static ArrayList<TourFeature> Hotels;
    private static ArrayList<TourFeature> FoodAndDrinks;
    private static ArrayList<TourFeature> Shops;
    private static ArrayList<TourFeature> Attractions;
    private String AP_VER;

    public String getApplicationName() {
        return AP_NAME;
    }
    public ArrayList<TourFeature> getTourFeatures(String FeatureType)
    {
        ArrayList<TourFeature> T = null;
        switch(FeatureType)
        {
            case "foodndrink":
                Log.e("GET","FOOD");
                return FoodAndDrinks;
            case "shopping":
                Log.e("GET","SHOP");
                return Shops;
            case "attraction":
                Log.e("GET","ATTR");
               return Attractions;
        }
        Log.e("GET","HOT");
        return Hotels;
    }

    public String getApplicationLanguage(){
        return AP_LANG;
    }

    public String getApplicationVersion(){
        return AP_VER;
    }

    public void setFeatures(String FeatureType, ArrayList<TourFeature> FeaturesArrayList)
    {
        switch(FeatureType)
        {
            case "hotel":
                Log.e("SET","HOTEL");
                this.Hotels = FeaturesArrayList;
                break;
            case "foodndrink":
                Log.e("SET","FOODDRINK");
                this.FoodAndDrinks = FeaturesArrayList;
                break;
            case "shopping":
                Log.e("SET","SHOP");
                this.Shops = FeaturesArrayList;
                break;
            case "attraction":
                Log.e("SET","ATTRACTION");
                this.Attractions = FeaturesArrayList;
                break;
        }
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
}
