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
        ITINERARYWIZARD(0),
        ITINERARY(1),
        HOTEL(2),
        FOODNDRINK(3),
        ATTRACTION(4),
        SHOPPING(5),
        TRAINTIME(6),
        SETTING(7);

        private int num;
        MainMenu(int arg){
            this.num = arg;
        }
    };

    public MenuItems(int id, String title, String imageSrc, MainMenu mainMenu)
    {
        this.id = id;
        this.title = title;
        this.imageSrc = imageSrc;
        this.mainMenu = mainMenu;
    }
}
