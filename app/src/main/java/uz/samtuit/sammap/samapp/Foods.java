package uz.samtuit.sammap.samapp;

import com.mapbox.mapboxsdk.geometry.LatLng;

/**
 * Created by Bakhrom on 25.07.2015.
 */
public class Foods {
    public String Name;
    public int Reviews;
    public int Rating;
    public String Telephone;
    public String Address;
    public LatLng Location;
    public Foods(String Name, int Reviews, int Rating, String Telephone, String Address, LatLng location)
    {
        this.Name = Name;
        this.Reviews = Reviews;
        this.Rating = Rating;
        this.Telephone = Telephone;
        this.Address = Address;
        this.Location = Location;
    }
}
