package uz.samtuit.samapp.main;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.mapbox.mapboxsdk.util.NetworkUtils;

import java.util.ArrayList;
import java.util.LinkedList;

import uz.samtuit.samapp.util.CustomDialog;
import uz.samtuit.samapp.util.GlobalsClass;
import uz.samtuit.samapp.util.TourFeature;
import uz.samtuit.samapp.util.TourFeatureList;
import uz.samtuit.sammap.main.R;


public class LogoActivity extends ActionBarActivity {
    private boolean updateAvailable = true;
    private static ArrayList<TourFeature> Hotels;
    private static ArrayList<TourFeature> Shops;
    private static ArrayList<TourFeature> Attractions;
    private static ArrayList<TourFeature> Foods;
    private static LinkedList<TourFeature> Itinerary;
    private CustomDialog mUpdateAvalDialog;
    private String path;
    private TextView tvInfo;
    private GlobalsClass globals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        tvInfo = (TextView)findViewById(R.id.tv_info);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Set language for UI
                Intent first_launch_intent = new Intent(LogoActivity.this, LanguageSettingActivity.class);
                startActivityForResult(first_launch_intent, 0);
            }
        }, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        globals = (GlobalsClass)getApplicationContext();
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
            publishProgress(new Pair<Integer, String>(LOAD_START, "hotels"));
            TourFeatureList tourFeatureList = new TourFeatureList();
            tourFeatureList.CopyLocalGeoJSONFilesToExternalDir(getApplicationContext());
            String ChoosenLang = globals.getApplicationLanguage();

            path = GlobalsClass.GeoJSONFileName[GlobalsClass.FeatureType.HOTEL.ordinal()];
            Hotels = tourFeatureList.getTourFeatureListFromGeoJSONFile(getApplicationContext(), ChoosenLang + path);
            globals.setFeatures(GlobalsClass.FeatureType.HOTEL, Hotels);
            publishProgress(new Pair<Integer, String>(LOAD_DONE, ""));

//            TimeUnit.MILLISECONDS.sleep(500);

            publishProgress(new Pair<Integer, String>(LOAD_START, "restourants"));
            path = GlobalsClass.GeoJSONFileName[GlobalsClass.FeatureType.FOODNDRINK.ordinal()];
            tourFeatureList = new TourFeatureList();
            Foods = tourFeatureList.getTourFeatureListFromGeoJSONFile(getApplicationContext(), ChoosenLang + path);
            globals.setFeatures(GlobalsClass.FeatureType.FOODNDRINK, Foods);
            publishProgress(new Pair<Integer, String>(LOAD_DONE, ""));

//            TimeUnit.MILLISECONDS.sleep(500);

            publishProgress(new Pair<Integer, String>(LOAD_START, "attractions"));
            path = GlobalsClass.GeoJSONFileName[GlobalsClass.FeatureType.ATTRACTION.ordinal()];
            tourFeatureList = new TourFeatureList();
            Attractions = tourFeatureList.getTourFeatureListFromGeoJSONFile(getApplicationContext(), ChoosenLang + path);
            globals.setFeatures(GlobalsClass.FeatureType.ATTRACTION, Attractions);
            publishProgress(new Pair<Integer, String>(LOAD_DONE, ""));

//            TimeUnit.MILLISECONDS.sleep(500);

            publishProgress(new Pair<Integer, String>(LOAD_START,"shops"));
            path = GlobalsClass.GeoJSONFileName[GlobalsClass.FeatureType.SHOPPING.ordinal()];
            tourFeatureList = new TourFeatureList();
            Shops = tourFeatureList.getTourFeatureListFromGeoJSONFile(getApplicationContext(), ChoosenLang + path);

            globals.setFeatures(GlobalsClass.FeatureType.SHOPPING, Shops);
            publishProgress(new Pair<Integer, String>(LOAD_DONE,""));
            TourFeatureList itineraryList = new TourFeatureList();

//            TimeUnit.MILLISECONDS.sleep(500);

            publishProgress(new Pair<Integer, String>(LOAD_DONE, "itinerary"));
            Itinerary = itineraryList.getItineraryFeatureList(getApplicationContext(), ChoosenLang + "_itinerary_mixed_nightlife_onenhalf_day.geojson");
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

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Set language for UI
                        Intent mainMap = new Intent(LogoActivity.this, MainMap.class);
                        startActivity(mainMap);
                        finish();
                    }
                }, 500);

//            Log.e("NEW LOG","OUT FROM MAIN");

            }
        }
    }

}