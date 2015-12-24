package uz.samtuit.samapp.util;

import android.graphics.drawable.Drawable;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.HashMap;

/**
 * Created by Bakhrom Rakhmonov on 11.12.2015.
 */

public class ItineraryItem {
    public ItineraryItem(){
        stringHashMap = new HashMap<String, String>();
    }

    private Drawable bitmap = null;
    private HashMap<String, String> stringHashMap; //name, car_time, walk_time, distance;


    private LatLng position;

    public LatLng getPosition(){
        return position;
    }

    public void setStringHashMap(String strKey, String strVal) {
        stringHashMap.put(strKey, strVal);
    }

    public String getString(String strKey) {
        return stringHashMap.get(strKey);
    }

    public void setPosition(LatLng position){
        this.position = position;
    }
    private boolean _is_last = false;

    public void setIsLast(Boolean _is_last){
        this._is_last=_is_last;
    }

    public Boolean isLast(){
        return _is_last;
    }


    public void setBitmap(Drawable bitmap){
        this.bitmap = bitmap;
    }

    public Drawable getBitmap(){
        return bitmap;
    }
}
