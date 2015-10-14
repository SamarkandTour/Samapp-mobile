package uz.samtuit.samapp.util;

import android.graphics.Bitmap;

/**
 * Data type of GeoJSON feature for Tour Information
 */
public class TourFeature {
    public Bitmap photo;
    public String name, description, type, price, wifi, open, address, tel, url;
    public int rating;
    public double longitude, latitude;

    public TourFeature(){}
}
