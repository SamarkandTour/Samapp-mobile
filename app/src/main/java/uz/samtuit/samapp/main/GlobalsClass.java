package uz.samtuit.samapp.main;

import android.app.Application;

/**
 * Created by Bakhrom Rakhmonov on 09.10.2015.
 */
public class GlobalsClass extends Application {
    private String AP_NAME;
    private String AP_LANG;
    private String AP_VER;

    public String getApplicationName() {
        return AP_NAME;
    }

    public String getApplicationLanguage(){
        return AP_LANG;
    }

    public String getApplicationVersion(){
        return AP_VER;
    }
    public void setApplicationName(String Name){
        AP_NAME = Name;
    }
    public void setApplicationVersion(String Version){
        AP_VER = Version;
    }
    public void setApplicationLanguage(String Language){
        AP_LANG = Language;
    }
}
