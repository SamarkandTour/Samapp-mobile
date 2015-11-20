package uz.samtuit.samapp.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.util.NetworkUtils;

import java.util.ArrayList;
import java.util.LinkedList;

import uz.samtuit.samapp.util.CustomDialog;
import uz.samtuit.samapp.util.GlobalsClass;
import uz.samtuit.samapp.util.SystemSetting;
import uz.samtuit.samapp.util.TourFeature;
import uz.samtuit.samapp.util.TourFeatureList;
import uz.samtuit.sammap.main.R;


public class LogoActivity extends ActionBarActivity {
    private boolean updateAvailable = false;
    private static ArrayList<TourFeature> Hotels;
    private static ArrayList<TourFeature> Shops;
    private static ArrayList<TourFeature> Attractions;
    private static ArrayList<TourFeature> Foods;
    private static LinkedList<TourFeature> Itinerary;
    private CustomDialog mUpdateAvalDialog;
    private String path;
    private TextView tvInfo;
    private GlobalsClass globals;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_logo);
        tvInfo = (TextView)findViewById(R.id.tv_info);

        pref = getApplicationContext().getSharedPreferences("SamTour_Pref", 0);
        if (pref.getBoolean("app_first_launch", true)) {
            // IF the system locale is same as one of supported languages, set as it
            String systemLocale = SystemSetting.checkSystemLocale();
            if (systemLocale.equals(GlobalsClass.supportedLanguages[0])
                    || systemLocale.equals(GlobalsClass.supportedLanguages[1])
                    || systemLocale.equals(GlobalsClass.supportedLanguages[2])) {
                SystemSetting.setUserLanguage(LogoActivity.this, systemLocale);

                SharedPreferences.Editor editor = pref.edit();
                editor.putString("app_lang", systemLocale); // Set App language
                editor.putString("app_version", "0.01"); // Set App version
                editor.commit();
            } else {
                // Go to language selection for UI, but set the delay for showing logo screen for a moment
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent first_launch_intent = new Intent(LogoActivity.this, LanguageSettingActivity.class);
                        startActivityForResult(first_launch_intent, 0);
                    }
                }, 500);
            }
        } else {
            continueInBackgroundTask();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        SharedPreferences.Editor editor = pref.edit();
        editor.putString("app_lang", data.getExtras().getString("sel_lang")); // Set App language
        editor.putString("app_version", "0.01");// Set App version
        editor.commit();

        continueInBackgroundTask();
    }

    private void continueInBackgroundTask(){
        LoadGeoJsonFilesInBackground loadGeoJsonFilesInBackground = new LoadGeoJsonFilesInBackground();
        loadGeoJsonFilesInBackground.execute();
    }

    private View.OnClickListener yesClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mUpdateAvalDialog.dismiss();

            // Start to download at Background
            // When the download is done, update photo files to new
            TourFeatureList.writeAllPhotosToFiles(LogoActivity.this);

            Intent mainMap = new Intent(LogoActivity.this, MainMap.class);
            startActivity(mainMap);
            finish();
        }
    };

    private View.OnClickListener noClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mUpdateAvalDialog.dismiss();

            Intent mainMap = new Intent(LogoActivity.this, MainMap.class);
            startActivity(mainMap);
            finish();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    class LoadGeoJsonFilesInBackground extends AsyncTask<Void, Pair<Integer, String>, Void>{
        private final int LOAD_START = 1;
        private final int LOAD_DONE = 2;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                // Do below, when first launch
                if (pref.getBoolean("app_first_launch", true)) {
                    // All downloaded GeoJSON files from server will be located in ExternalDir
                    // So working directory is ExternalDir, all files in the asset should be copied to ExternalDir at first launch
                    if (!TourFeatureList.CopyLocalGeoJSONFilesToExternalDir(LogoActivity.this)) {
                        Toast.makeText(LogoActivity.this, R.string.Err_file_not_found, Toast.LENGTH_SHORT).show();
                    }

                    TourFeatureList.writeAllPhotosToFiles(LogoActivity.this); // Make all photo data to files

                    pref.edit().putBoolean("app_first_launch", false).commit(); // Set first_launch to false
                }

                // Since language selection has finished, Set features lists
                String ChoosenLang = pref.getString("app_lang", null);

                publishProgress(new Pair<Integer, String>(LOAD_START, "hotels"));
                path = GlobalsClass.GeoJSONFileName[GlobalsClass.FeatureType.HOTEL.ordinal()];
                Hotels = new TourFeatureList().getTourFeatureListFromGeoJSONFile(getApplicationContext(), ChoosenLang + path);
                globals.setFeatures(GlobalsClass.FeatureType.HOTEL, Hotels);
                publishProgress(new Pair<Integer, String>(LOAD_DONE, ""));

    //            TimeUnit.MILLISECONDS.sleep(500);

                publishProgress(new Pair<Integer, String>(LOAD_START, "restourants"));
                path = GlobalsClass.GeoJSONFileName[GlobalsClass.FeatureType.FOODNDRINK.ordinal()];
                Foods = new TourFeatureList().getTourFeatureListFromGeoJSONFile(getApplicationContext(), ChoosenLang + path);
                globals.setFeatures(GlobalsClass.FeatureType.FOODNDRINK, Foods);
                publishProgress(new Pair<Integer, String>(LOAD_DONE, ""));

    //            TimeUnit.MILLISECONDS.sleep(500);

                publishProgress(new Pair<Integer, String>(LOAD_START, "attractions"));
                path = GlobalsClass.GeoJSONFileName[GlobalsClass.FeatureType.ATTRACTION.ordinal()];
                Attractions = new TourFeatureList().getTourFeatureListFromGeoJSONFile(getApplicationContext(), ChoosenLang + path);
                globals.setFeatures(GlobalsClass.FeatureType.ATTRACTION, Attractions);
                publishProgress(new Pair<Integer, String>(LOAD_DONE, ""));

    //            TimeUnit.MILLISECONDS.sleep(500);

                publishProgress(new Pair<Integer, String>(LOAD_START,"shops"));
                path = GlobalsClass.GeoJSONFileName[GlobalsClass.FeatureType.SHOPPING.ordinal()];
                Shops = new TourFeatureList().getTourFeatureListFromGeoJSONFile(getApplicationContext(), ChoosenLang + path);
                globals.setFeatures(GlobalsClass.FeatureType.SHOPPING, Shops);
                publishProgress(new Pair<Integer, String>(LOAD_DONE,""));

    //            TimeUnit.MILLISECONDS.sleep(500);

                publishProgress(new Pair<Integer, String>(LOAD_DONE, "itinerary"));
                Itinerary = new TourFeatureList(GlobalsClass.FeatureType.ITINERARY).getItineraryFeatureList(getApplicationContext(), ChoosenLang + "_itinerary_mixed_1.5.geojson");
                globals.setItineraryFeatures(Itinerary);
                TourFeatureList.ItineraryWriteToGeoJSONFile(LogoActivity.this, Itinerary, ChoosenLang + "_MyItinerary.geojson");
                publishProgress(new Pair<Integer, String>(LOAD_DONE, ""));

    //            TimeUnit.MILLISECONDS.sleep(500);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Pair<Integer, String>... values) {
            super.onProgressUpdate(values);
            switch (values[0].first){
                case LOAD_START:
                    tvInfo.setText("Loading " + values[0].second + ". ");
                    break;
                case LOAD_DONE:
                    tvInfo.setText(tvInfo.getText() + "DONE");
                    break;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tvInfo.setText("Started loading data...");
            globals = (GlobalsClass)getApplicationContext();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            tvInfo.setText("OK. Opening main window...");
            if (updateAvailable && NetworkUtils.isNetworkAvailable(LogoActivity.this)) {
                mUpdateAvalDialog = new CustomDialog(LogoActivity.this,
                        R.string.title_dialog_update_available,
                        R.string.dialog_update_available,
                        R.string.yes,
                        R.string.no,
                        yesClickListener,
                        noClickListener);
                mUpdateAvalDialog.show();
            } else {
//            Log.e("NEW LOG", "ENTER TO MAIN");

                Intent mainMap = new Intent(LogoActivity.this, MainMap.class);
                startActivity(mainMap);
                finish();

//            Log.e("NEW LOG","OUT FROM MAIN");
            }
        }
    }

}