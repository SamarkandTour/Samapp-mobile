package uz.samtuit.samapp.util;

import java.util.HashMap;

/**
 * Data type of GeoJSON feature for Tour Information
 */
public class TourFeature {
    //private Bitmap photo;
    private String photo;
    private HashMap<String, String> stringHashMap; //name, desc, type, price, wifi, open, addr, tel, url
    private int rating;
    private double longitude;
    private double latitude;

    public TourFeature(){
        stringHashMap = new HashMap<String, String>();
    }

    public void setStringHashMap(String strKey, String strVal) {
        stringHashMap.put(strKey, strVal);
    }

    public String getString(String strKey) {
        return stringHashMap.get(strKey);
    }

    public void setRating(int intVal) {
        rating = intVal;
    }

    public int getRating() {
        return rating;
    }

    public void setPhoto(String strVal) {
        photo = strVal;
    }

    public String getPhoto() {
        return photo;
    }

    public void setLongitude(double doubleVal) { longitude = doubleVal; }
    public double getLongitude() { return longitude; }

    public void setLatitude(double doubleVal) { latitude = doubleVal; }
    public double getLatitude() { return latitude; }
}
