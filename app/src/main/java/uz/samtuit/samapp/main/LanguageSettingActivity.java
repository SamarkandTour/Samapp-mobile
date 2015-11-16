package uz.samtuit.samapp.main;

import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.Locale;

import uz.samtuit.samapp.util.GlobalsClass;
import uz.samtuit.samapp.util.SystemSetting;
import uz.samtuit.samapp.util.TourFeatureList;
import uz.samtuit.sammap.main.R;

public class LanguageSettingActivity extends AppCompatActivity {

    private ImageButton UZ_BTN,RU_BTN,EN_BTN;
    private SQLiteDatabase APP_DB;
    private GlobalsClass globals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_setting);

        globals = (GlobalsClass)getApplicationContext();
        APP_DB = openOrCreateDatabase("SamTour_data",MODE_PRIVATE,null);
        ConfigurePropertiesDB configurePropertiesDB = new ConfigurePropertiesDB(APP_DB);
        configurePropertiesDB.RepairDB();
        Cursor APP_PROPERTIES = APP_DB.rawQuery("Select `app_first_launch` from app_properties", null);
        APP_PROPERTIES.moveToFirst();
        boolean AP_FIRSTLAUNCH = Boolean.parseBoolean(APP_PROPERTIES.getString(0));
        Log.e("FL", AP_FIRSTLAUNCH + "");

        if (AP_FIRSTLAUNCH) {
            // Set App name and version
            APP_PROPERTIES = APP_DB.rawQuery("Select * from app_properties",null);
            APP_PROPERTIES.moveToFirst();
            globals.setApplicationName(APP_PROPERTIES.getString(0));
            globals.setApplicationVersion(APP_PROPERTIES.getString(1));

            // All downloaded GeoJSON files from server will be located in ExternalDir
            // So working directory is ExternalDir, all files in the asset should be copied to ExternalDir at first launch
            if (TourFeatureList.CopyLocalGeoJSONFilesToExternalDir(this)) {
                Toast.makeText(this, R.string.Err_file_not_found, Toast.LENGTH_SHORT).show();
            }

            // IF the system locale is same as supported language, set as it
            String systemLocale = SystemSetting.checkSystemLocale();
            if (systemLocale.equals("en") || systemLocale.equals("uz") || systemLocale.equals("ru")) {
                APP_DB.execSQL("UPDATE app_properties SET `app_lang`='" + systemLocale + "', `app_first_launch`='false' WHERE `app_name`='Samapp';");
                APP_DB.close();
                setUserLanguage(systemLocale);
                finish();
            }

            UZ_BTN = (ImageButton) findViewById(R.id.lang_uz);
            RU_BTN = (ImageButton) findViewById(R.id.lang_ru);
            EN_BTN = (ImageButton) findViewById(R.id.lang_en);

            ImageButton.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String selectLang = view.getTag().toString();
                    APP_DB.execSQL("UPDATE app_properties SET `app_lang`='" + selectLang + "', `app_first_launch`='false' WHERE `app_name`='Samapp';");
                    APP_DB.close();
                    setUserLanguage(selectLang);
                    finish();
                    overridePendingTransition(R.anim.slide_content, R.anim.slide_in);
                }
            };

            UZ_BTN.setOnClickListener(onClickListener);
            RU_BTN.setOnClickListener(onClickListener);
            EN_BTN.setOnClickListener(onClickListener);
        } else { // Load saved language setting
            APP_PROPERTIES = APP_DB.rawQuery("Select * from app_properties",null);
            APP_PROPERTIES.moveToFirst();
            setUserLanguage(APP_PROPERTIES.getString(2));
            APP_DB.close();
            finish();
        }
    }

    private void setUserLanguage(String currentLang) {
        globals = (GlobalsClass)getApplicationContext();
        globals.setApplicationLanguage(currentLang);
        Log.e("LANG", globals.getApplicationLanguage());

        // Set Display UI locale
        Locale locale = new Locale(currentLang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, R.string.select_your_language, Toast.LENGTH_SHORT).show();
    }
}
