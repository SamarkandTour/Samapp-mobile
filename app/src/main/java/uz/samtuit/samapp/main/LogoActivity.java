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

import uz.samtuit.samapp.util.CustomDialog;
import uz.samtuit.samapp.util.GlobalsClass;
import uz.samtuit.samapp.util.ItineraryList;
import uz.samtuit.samapp.util.SystemSetting;
import uz.samtuit.samapp.util.TourFeature;
import uz.samtuit.samapp.util.TourFeatureList;
import uz.samtuit.sammap.main.R;


public class LogoActivity extends ActionBarActivity {
    private static ArrayList<TourFeature> Hotels;
    private static ArrayList<TourFeature> Shops;
    private static ArrayList<TourFeature> Attractions;
    private static ArrayList<TourFeature> Foods;

    private CustomDialog mUpdateAvalDialog;
    private String path;
    private TextView tvInfo;
    private GlobalsClass globals;
    private SharedPreferences pref;
    private boolean updateAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_logo);
        tvInfo = (TextView)findViewById(R.id.tv_info);

        pref = LogoActivity.this.getSharedPreferences("SamTour_Pref", 0);
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

                continueInBackgroundTask();
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
                // Since language selection has finished, Set features lists
                String chosenLang = pref.getString("app_lang", null);


                // Do below, when first launch
                if (pref.getBoolean("app_first_launch", true)) {
                    // All downloaded GeoJSON files from server will be located in ExternalDir
                    // So working directory is ExternalDir, all files in the asset should be copied to ExternalDir at first launch
                    if (!TourFeatureList.CopyLocalGeoJSONFilesToExternalDir(LogoActivity.this)) {
                        Toast.makeText(LogoActivity.this, R.string.Err_file_not_found, Toast.LENGTH_SHORT).show();
                    }

                    TourFeatureList.writeAllPhotosToFiles(LogoActivity.this); // Make all photo data to files
                }

                publishProgress(new Pair<Integer, String>(LOAD_START, "hotels"));
                path = GlobalsClass.featuresGeoJSONFileName[GlobalsClass.FeatureType.HOTEL.ordinal()];
                Hotels = new TourFeatureList().getTourFeatureListFromGeoJSONFile(LogoActivity.this, chosenLang + path);
                globals.setFeatures(GlobalsClass.FeatureType.HOTEL, Hotels);
                publishProgress(new Pair<Integer, String>(LOAD_DONE, ""));

    //            TimeUnit.MILLISECONDS.sleep(500);

                publishProgress(new Pair<Integer, String>(LOAD_START, "restaurant"));
                path = GlobalsClass.featuresGeoJSONFileName[GlobalsClass.FeatureType.FOODNDRINK.ordinal()];
                Foods = new TourFeatureList().getTourFeatureListFromGeoJSONFile(LogoActivity.this, chosenLang + path);
                globals.setFeatures(GlobalsClass.FeatureType.FOODNDRINK, Foods);
                publishProgress(new Pair<Integer, String>(LOAD_DONE, ""));

    //            TimeUnit.MILLISECONDS.sleep(500);

                publishProgress(new Pair<Integer, String>(LOAD_START, "attractions"));
                path = GlobalsClass.featuresGeoJSONFileName[GlobalsClass.FeatureType.ATTRACTION.ordinal()];
                Attractions = new TourFeatureList().getTourFeatureListFromGeoJSONFile(LogoActivity.this, chosenLang + path);
                globals.setFeatures(GlobalsClass.FeatureType.ATTRACTION, Attractions);
                publishProgress(new Pair<Integer, String>(LOAD_DONE, ""));

    //            TimeUnit.MILLISECONDS.sleep(500);

                publishProgress(new Pair<Integer, String>(LOAD_START,"shops"));
                path = GlobalsClass.featuresGeoJSONFileName[GlobalsClass.FeatureType.SHOPPING.ordinal()];
                Shops = new TourFeatureList().getTourFeatureListFromGeoJSONFile(LogoActivity.this, chosenLang + path);
                globals.setFeatures(GlobalsClass.FeatureType.SHOPPING, Shops);
                publishProgress(new Pair<Integer, String>(LOAD_DONE,""));

    //            TimeUnit.MILLISECONDS.sleep(500);

                publishProgress(new Pair<Integer, String>(LOAD_START, "itinerary"));
                if (!pref.getBoolean("app_first_launch", true)) { // At first launch, there is no my itinerary
                    path = ItineraryList.myItineraryGeoJSONFileName;
                    ItineraryList.getInstance().getItineraryFeatureListFromGeoJSONFile(LogoActivity.this, chosenLang + path);
                    ItineraryList.getInstance().setItinearyFeaturesToGlobal(LogoActivity.this);
                }
                ItineraryList.categorizeItineraryWithDays(LogoActivity.this, chosenLang); // Categorize the name of itinerary course with days
                publishProgress(new Pair<Integer, String>(LOAD_DONE, ""));

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
                if (pref.getBoolean("app_first_launch", true)) {
                    pref.edit().putBoolean("app_first_launch", false).commit(); // Don't forget, Set first_launch to false

                    Intent intent = new Intent(LogoActivity.this, WizardDaySelectActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(LogoActivity.this, MainMap.class);
                    intent.putExtra("type", "features");
                    intent.putExtra("featureType", GlobalsClass.FeatureType.ITINERARY.toString());
                    startActivity(intent);
                    finish();
                }

//            Log.e("NEW LOG","OUT FROM MAIN");
            }
        }
    }

}