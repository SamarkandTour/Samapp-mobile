package uz.samtuit.samapp.helpers;

import android.content.Context;

import java.util.LinkedList;

import uz.samtuit.samapp.util.GlobalsClass;
import uz.samtuit.samapp.util.ItineraryList;
import uz.samtuit.samapp.util.TourFeature;

/**
 * Created by Bakha on 14.03.2016.
 */
public class ItineraryHelper {
    public static void changeDay(Context context, int currentDay, int index, int inc) {
        GlobalsClass globalsClass = (GlobalsClass)context.getApplicationContext();
        LinkedList<TourFeature> itineraryList = globalsClass.getItineraryFeatures();

        TourFeature tourFeature = itineraryList.get(index);
        tourFeature.setDay(currentDay + inc);
        itineraryList.add(tourFeature);
        itineraryList.remove(index);

        ItineraryList.sortItineraryList();
        ItineraryList.itineraryWriteToGeoJSONFile(context, context.getSharedPreferences("SamTour_Pref", Context.MODE_PRIVATE).getString("app_lang", null));
    }
}
