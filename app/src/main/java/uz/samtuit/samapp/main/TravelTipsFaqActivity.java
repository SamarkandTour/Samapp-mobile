package uz.samtuit.samapp.main;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;

import java.util.ArrayList;

import uz.samtuit.samapp.util.GlobalsClass;
import uz.samtuit.samapp.util.TourFeature;
import uz.samtuit.samapp.util.TourFeatureList;


public class TravelTipsFaqActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            Log.e("WizardCourseActivity", "onRestore()");

            GlobalsClass globalVariables = (GlobalsClass)getApplicationContext();
            ArrayList<TourFeature> featureList = globalVariables.getTourFeatures(GlobalsClass.FeatureType.HOTEL);

            // When Features are out of memory, App should need to restart from the start
            if (featureList == null) {
                Log.e("WizardCourseActivity", "featureList=null");
                SharedPreferences pref = globalVariables.getApplicationContext().getSharedPreferences("SamTour_Pref", 0);
                String currentLang = pref.getString("app_lang", null);
                TourFeatureList.loadAllFeaturesToMemory(this, currentLang);
            }
        }

        setContentView(R.layout.activity_traveltips_faq);

        webView = (WebView)findViewById(R.id.webView);
        webView.loadUrl("file:///android_asset/html/en_traveltips_faq.html");
    }
}
