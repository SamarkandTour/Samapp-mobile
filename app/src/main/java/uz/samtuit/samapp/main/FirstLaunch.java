package uz.samtuit.samapp.main;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import uz.samtuit.samapp.util.TourFeature;
import uz.samtuit.samapp.util.TourFeatureList;
import uz.samtuit.sammap.main.R;

public class FirstLaunch extends AppCompatActivity {

    ImageButton UZ_BTN,RU_BTN,EN_BTN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_launch);
        UZ_BTN = (ImageButton)findViewById(R.id.lang_uz);
        RU_BTN = (ImageButton)findViewById(R.id.lang_ru);
        EN_BTN = (ImageButton)findViewById(R.id.lang_en);
        ImageButton.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase APP_DB = openOrCreateDatabase("SamTour_data",MODE_PRIVATE,null);
                ConfigurePropertiesDB configurePropertiesDB = new ConfigurePropertiesDB(APP_DB);
                configurePropertiesDB.RepairDB();
                APP_DB.execSQL("UPDATE app_properties SET `app_lang`='"+view.getTag().toString()+"', `app_first_launch`='false' WHERE `app_name`='Samapp';");
                APP_DB.close();
                finish();
                overridePendingTransition(R.anim.slide_content, R.anim.slide_in);
            }
        };
        UZ_BTN.setOnClickListener(onClickListener);
        RU_BTN.setOnClickListener(onClickListener);
        EN_BTN.setOnClickListener(onClickListener);
    }
    public String loadJSONFromAsset(String FileName) {
        String json = null;
        try {
            InputStream is = getAssets().open(FileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
