package uz.samtuit.sammap.samapp;


public class Hotels {
    public String Name,Telephone,Address,Open,URL,Description,Photo,Type;
    public int Reviews,Price,Rating;
    public Boolean WiFi;
    public double Latitude,Longitude;
    public Hotels(String Name, int Reviews, int Rating, String Telephone, String Address, double Latitude,double Longitude,Boolean WiFi, int Price, String Open, String URL, String Description, String Photo, String Type)
    {
        this.Name = Name;
        this.Reviews = Reviews;
        this.Rating = Rating;
        this.Telephone = Telephone;
        this.Address = Address;
        this.Latitude = Latitude;
        this.Longitude = Longitude;
        this.Open = Open;
        this.URL = URL;
        this.Description = Description;
        this.Photo = Photo;
        this.Price = Price;
        this.WiFi = WiFi;
        this.Type = Type;
    }
    public Hotels()
    {

    }
}
