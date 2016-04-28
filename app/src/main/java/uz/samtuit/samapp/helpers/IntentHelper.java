package uz.samtuit.samapp.helpers;

import android.content.Context;
import android.content.Intent;

import uz.samtuit.samapp.main.ItemActivity;
import uz.samtuit.samapp.util.GlobalsClass;
import uz.samtuit.samapp.util.TourFeature;
import uz.samtuit.samapp.util.TourFeatureList;

public class IntentHelper {
    public static void startItemActivity(Context context, GlobalsClass.FeatureType featureType, TourFeature feature) {
        Intent intent = new Intent(context, ItemActivity.class);

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
        intent.putExtra("booking", feature.getString("booking"));
        intent.putExtra("long", feature.getLongitude());
        intent.putExtra("lat", feature.getLatitude());
        intent.putExtra("primaryColorId", GlobalsClass.getPrimaryColorId(featureType));
        intent.putExtra("toolbarColorId", GlobalsClass.getToolbarColorId(featureType));

        context.startActivity(intent);
    }
}
