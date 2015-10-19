package uz.samtuit.samapp.main;

/**
 * Created by sammap on 7/10/15.
 */
public class MenuItems {
    public int id;
    public String Title;
    public String imageSrc;
    public String tag;
    public MenuItems(int id, String Title, String imageSrc, String tag)
    {
        this.id = id;
        this.Title =Title;
        this.imageSrc = imageSrc;
        this.tag = tag;
    }
}
