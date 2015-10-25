package uz.samtuit.samapp.main;

/**
 * Data structure of MenuItems
 */
public class MenuItems {
    public int id;
    public String title;
    public String imageSrc;
    public MainMenu mainMenu;

    public enum MainMenu {
        ITINERARYWIZARD(5),
        ITINERARY(6),
        HOTEL(1),
        FOODNDRINK(2),
        ATTRACTION(3),
        SHOPPING(4),
        SETTING(7);

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
