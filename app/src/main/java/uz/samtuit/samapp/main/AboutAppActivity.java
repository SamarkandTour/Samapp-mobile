package uz.samtuit.samapp.main;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

import uz.samtuit.samapp.util.GlobalsClass;
import uz.samtuit.samapp.util.TourFeature;
import uz.samtuit.samapp.util.TourFeatureList;


public class AboutAppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            Log.e("AboutAppActivity", "onRestore()");

            GlobalsClass globalVariables = (GlobalsClass)getApplicationContext();
            ArrayList<TourFeature> featureList = globalVariables.getTourFeatures(GlobalsClass.FeatureType.HOTEL);

            // When Features are out of memory, App should need to restart from the start
            if (featureList == null) {
                Log.e("AboutAppActivity", "featureList=null");
                SharedPreferences pref = globalVariables.getApplicationContext().getSharedPreferences("SamTour_Pref", 0);
                String currentLang = pref.getString("app_lang", null);
                TourFeatureList.loadAllFeaturesToMemory(this, currentLang);
            }
        }

        setContentView(R.layout.activity_about_app);

        String version = this.getSharedPreferences("SamTour_Pref", 0).getString("app_version", "0");
        TextView textView = (TextView)findViewById(R.id.version);
        textView.setText("Ver. " + version);
    }
}
