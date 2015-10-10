package uz.samtuit.samapp.main;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Bakhrom Rakhmonov on 10.10.2015.
 */
public class ConfigurePropertiesDB{
    private SQLiteDatabase APP_DB;
    public ConfigurePropertiesDB(SQLiteDatabase APP_DB)
    {
        this.APP_DB = APP_DB;
    }
    public void RepairDB()
    {
        APP_DB.execSQL("CREATE TABLE IF NOT EXISTS app_properties(app_name VARCHAR, app_ver VARCHAR, app_lang VARCHAR, app_first_launch VARCHAR);");
        Cursor data = APP_DB.rawQuery("Select * from app_properties",null);
        data.moveToFirst();
        if(data.getCount()==0)
        {
            APP_DB.execSQL("INSERT INTO app_properties VALUES('Samapp', '0.01', 'en', 'true');");
        }
    }
}
