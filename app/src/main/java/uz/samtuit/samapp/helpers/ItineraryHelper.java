package uz.samtuit.samapp.helpers;

import android.content.Context;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import uz.samtuit.samapp.util.GlobalsClass;
import uz.samtuit.samapp.util.ItineraryList;
import uz.samtuit.samapp.util.TourFeature;

/**
 * Created by Bakha on 14.03.2016.
 */
public class ItineraryHelper {
    public static void changeDay(Context context, int currentDay, int index, int inc) {
        GlobalsClass globalsClass = (GlobalsClass)context.getApplicationContext();
        List<TourFeature> itineraryList = globalsClass.getItineraryFeatures();

        TourFeature tourFeature = itineraryList.get(index);
        tourFeature.setDay(currentDay + inc);
        itineraryList.remove(index);
        itineraryList.add(tourFeature);

        Log.e("LAST DAY", itineraryList.get(itineraryList.size()-1).getDay() + " " + itineraryList.get(itineraryList.size()-1).getString("name"));

        ItineraryList.sortItineraryList();
        ItineraryList.itineraryWriteToGeoJSONFile(context, context.getSharedPreferences("SamTour_Pref", Context.MODE_PRIVATE).getString("app_lang", null));
    }
}
