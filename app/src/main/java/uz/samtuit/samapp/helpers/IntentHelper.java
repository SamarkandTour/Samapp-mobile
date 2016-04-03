package uz.samtuit.samapp.helpers;

import android.content.Context;
import android.content.Intent;

import uz.samtuit.samapp.main.R;
import uz.samtuit.samapp.main.TourFeatureActivity;
import uz.samtuit.samapp.main.ItemsListActivity;
import uz.samtuit.samapp.util.GlobalsClass;
import uz.samtuit.samapp.util.TourFeature;
import uz.samtuit.samapp.util.TourFeatureList;
/**
 * Created by Bakha on 16.03.2016.
 */
public class IntentHelper {
    public static void startItemActivity(Context context, GlobalsClass.FeatureType featureType, TourFeature feature) {

        Intent intent = new Intent(context, TourFeatureActivity.class);

        if (featureType == GlobalsClass.FeatureType.ITINERARY) {
            featureType = TourFeatureList.findFeatureTypeByName(context, feature.getString("name"));
        }
        intent.putExtra("featureType", featureType.toString());
        intent.putExtra("photo", feature.getPhoto());
        intent.putExtra("rating", feature.getRating());
        intent.putExtra("name", feature.getString("name"));
        intent.putExtra("desc", feature.getString("desc"));
        intent.putExtra("type", feature.getString("type"));
        intent.putExtra("price", feature.getString("price"));
        intent.putExtra("wifi", feature.getString("wifi"));
        intent.putExtra("open", feature.getString("open"));
        intent.putExtra("addr", feature.getString("addr"));
        intent.putExtra("tel", feature.getString("tel"));
        intent.putExtra("url", feature.getString("url"));
        intent.putExtra("long", feature.getLongitude());
        intent.putExtra("lat", feature.getLatitude());
        intent.putExtra("primaryColorId", GlobalsClass.getPrimaryColorId(featureType));
        intent.putExtra("toolbarColorId", GlobalsClass.getToolbarColorId(featureType));

        context.startActivity(intent);
    }



}
