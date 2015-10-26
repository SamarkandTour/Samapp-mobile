package uz.samtuit.samapp.util;

/**
 * Data structure of MenuItems
 */
public class MenuItems {
    public int id;
    public String title;
    public String imageSrc;
    public MainMenu mainMenu;

    public enum MainMenu {
        HOTEL(0),
        FOODNDRINK(1),
        ATTRACTION(2),
        SHOPPING(3),
        ITINERARYWIZARD(4),
        ITINERARY(5),
        SETTING(6);

        private int num;
        MainMenu(int arg){
            this.num = arg;
        }
    };

    public MenuItems(int id, String title, String imageSrc, MainMenu mainMenu)
    {
        this.id = id;
        this.title =title;
        this.imageSrc = imageSrc;
        this.mainMenu = mainMenu;
    }
}
