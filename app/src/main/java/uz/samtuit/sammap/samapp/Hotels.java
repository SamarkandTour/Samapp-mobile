package uz.samtuit.sammap.samapp;

import com.mapbox.mapboxsdk.geometry.LatLng;

/**
 * Created by sammap on 7/8/15.
 */
public class Hotels {
    public String Name;
    public int Reviews;
    public int Rating;
    public String Telephone;
    public String Address;
    public double Latitude,Longitude;
    public Hotels(String Name, int Reviews, int Rating, String Telephone, String Address, double Latitude,double Longitude)
    {
        this.Name = Name;
        this.Reviews = Reviews;
        this.Rating = Rating;
        this.Telephone = Telephone;
        this.Address = Address;
        this.Latitude = Latitude;
        this.Longitude = Longitude;
    }
}
