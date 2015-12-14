package uz.samtuit.samapp.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.util.NetworkUtils;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.ZipFile;

import uz.samtuit.samapp.util.CustomDialog;
import uz.samtuit.samapp.util.Downloader;
import uz.samtuit.samapp.util.FileUtil;
import uz.samtuit.samapp.util.GlobalsClass;
import uz.samtuit.samapp.util.ItineraryList;
import uz.samtuit.samapp.util.SystemSetting;
import uz.samtuit.samapp.util.TourFeature;
import uz.samtuit.samapp.util.TourFeatureList;
import uz.samtuit.samapp.util.ZipFileUtil;



public class LogoActivity extends ActionBarActivity {
    private CustomDialog mUpdateAvalDialog;
    private TextView tvInfo;
    private GlobalsClass globals;
    private SharedPreferences pref;
    private boolean isNeedFeaturesDownload;
    private boolean isNeedMapDownload;
    private boolean isFirstLaunch;
    private int countOfDownloaded;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_logo);
        tvInfo = (TextView)findViewById(R.id.tv_info);


        pref = LogoActivity.this.getSharedPreferences("SamTour_Pref", 0);
        if (isFirstLaunch = pref.getBoolean("app_first_launch", true)) {
            // IF the system locale is same as one of supported languages, set as it
            String systemLocale = SystemSetting.checkSystemLocale();
            if (systemLocale.equals(GlobalsClass.supportedLanguages[0])
                    || systemLocale.equals(GlobalsClass.supportedLanguages[1])
                    || systemLocale.equals(GlobalsClass.supportedLanguages[2])) {
                SystemSetting.setUserLanguage(LogoActivity.this, systemLocale);

                SharedPreferences.Editor editor = pref.edit();
                editor.putString("app_lang", systemLocale); // Set App language
                editor.putString("app_version", "0.01"); // Set App version
                editor.putLong("last_updated", new Date().getTime()); // Set installed date
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
        Date current = new Date();
        editor.putLong("last_updated", current.getTime()); // Set installed date
        editor.commit();

        continueInBackgroundTask();
    }

    private void continueInBackgroundTask(){
        LoadGeoJsonFilesInBackground loadGeoJsonFilesInBackground = new LoadGeoJsonFilesInBackground();
        loadGeoJsonFilesInBackground.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void loadFeaturesToMemory(Context context, String chosenLang, String path, GlobalsClass.FeatureType featureType) {
        if (featureType == GlobalsClass.FeatureType.ITINERARY) {
            ItineraryList.getInstance().getItineraryFeatureListFromGeoJSONFile(context, path);
            ItineraryList.getInstance().setItinearyFeaturesToGlobal(context);
            ItineraryList.getInstance().categorizeItineraryWithDays(context, chosenLang); // Categorize with days from the name of itinerary files
        } else {
            TourFeatureList tourFeatureList = new TourFeatureList();
            ArrayList<TourFeature> tourFeatureArrayList = tourFeatureList.getTourFeatureListFromGeoJSONFile(context, chosenLang + path);
            tourFeatureList.setTourFeaturesToGlobal(context, featureType, tourFeatureArrayList);
        }
    }

    class LoadGeoJsonFilesInBackground extends AsyncTask<Void, Pair<Integer, String>, Boolean> {
        private final int INIT_START = 1;
        private final int LOAD_START = 2;
        private final int LOAD_DONE = 3;
        private final int CHECK_START = 4;
        private final int UPDATE_START = 5;

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                // Since language selection has finished, Set features lists
                String chosenLang = pref.getString("app_lang", null);
                String path = null;

                // Do below, when first launch and since new update has downloaded
                if (isFirstLaunch || (countOfDownloaded = pref.getInt("download_request_count", 0)) > 0) {
                    if (countOfDownloaded > 0) {
                        publishProgress(new Pair<Integer, String>(UPDATE_START, "new update"));
                        for (int i = 0; i < countOfDownloaded; i++) {
                            String uriString = pref.getString("downloaded_uri" + i, "");
                            String filePath = Uri.parse(uriString).getPath();

                            // If the downloaded file is zipped, Unzip it
                            if (uriString.contains("zip")) {
                                // Before unzip, delete all GeoJSON files in the directory
                                FileUtil.deleteAllExternalFilesWithExtension(LogoActivity.this, "", "geojson");
                                ZipFileUtil.unZipToExteranlDir(LogoActivity.this, filePath, ZipFile.OPEN_DELETE);
                            } else {
                                FileUtil.fileMoveToExternalDir(LogoActivity.this, filePath);
                            }
                        }
                        // Don't forget belows
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putInt("download_request_count", 0);
                        editor.putLong("last_updated", new Date().getTime()); // Set updated date
                        editor.commit();
                    }

                    publishProgress(new Pair<Integer, String>(INIT_START, ""));
                    if (isFirstLaunch) {
                        // All downloaded GeoJSON files from server will be located in ExternalDir
                        // So working directory is ExternalDir, all files in the asset should be copied to ExternalDir at first launch
                        if (!TourFeatureList.CopyLocalGeoJSONFilesToExternalDir(LogoActivity.this)) {
                            Toast.makeText(LogoActivity.this, R.string.Err_file_not_found, Toast.LENGTH_SHORT).show();
                        }
                    }

                    TourFeatureList.writeAllPhotosToFiles(LogoActivity.this); // Make all photo data to files
                }

                publishProgress(new Pair<Integer, String>(LOAD_START, getString(R.string.hotels)));
                path = GlobalsClass.featuresGeoJSONFileName[GlobalsClass.FeatureType.HOTEL.ordinal()];
                loadFeaturesToMemory(LogoActivity.this, chosenLang, path, GlobalsClass.FeatureType.HOTEL);
                publishProgress(new Pair<Integer, String>(LOAD_DONE, ""));


                publishProgress(new Pair<Integer, String>(LOAD_START, getString(R.string.foodanddrinks)));
                path = GlobalsClass.featuresGeoJSONFileName[GlobalsClass.FeatureType.FOODNDRINK.ordinal()];
                loadFeaturesToMemory(LogoActivity.this, chosenLang, path, GlobalsClass.FeatureType.FOODNDRINK);
                publishProgress(new Pair<Integer, String>(LOAD_DONE, ""));


                publishProgress(new Pair<Integer, String>(LOAD_START, getString(R.string.attractions)));
                path = GlobalsClass.featuresGeoJSONFileName[GlobalsClass.FeatureType.ATTRACTION.ordinal()];
                loadFeaturesToMemory(LogoActivity.this, chosenLang, path, GlobalsClass.FeatureType.ATTRACTION);
                publishProgress(new Pair<Integer, String>(LOAD_DONE, ""));


                publishProgress(new Pair<Integer, String>(LOAD_START, getString(R.string.shopping)));
                path = GlobalsClass.featuresGeoJSONFileName[GlobalsClass.FeatureType.SHOPPING.ordinal()];
                loadFeaturesToMemory(LogoActivity.this, chosenLang, path, GlobalsClass.FeatureType.SHOPPING);
                publishProgress(new Pair<Integer, String>(LOAD_DONE, ""));


                publishProgress(new Pair<Integer, String>(LOAD_START, getString(R.string.title_suggested_itinerary)));
                path = ItineraryList.myItineraryDirectory + chosenLang + ItineraryList.myItineraryGeoJSONFileName;
                loadFeaturesToMemory(LogoActivity.this, chosenLang, path, GlobalsClass.FeatureType.ITINERARY);
                publishProgress(new Pair<Integer, String>(LOAD_DONE, ""));

                publishProgress(new Pair<Integer, String>(CHECK_START, getString(R.string.new_update)));
                if (NetworkUtils.isNetworkAvailable(LogoActivity.this)) {
                    isNeedFeaturesDownload = isNewUpdate(globals.featuresDownloadURL);
                    isNeedMapDownload = isNewUpdate(globals.mapDownloadURL);
                    if (isNeedFeaturesDownload || isNeedMapDownload) {
                        return true;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onProgressUpdate(Pair<Integer, String>... values) {
            super.onProgressUpdate(values);
            switch (values[0].first) {
                case INIT_START:
                    tvInfo.setText("Initializing " + values[0].second + "...");
                    break;
                case LOAD_START:
                    tvInfo.setText("Loading " + values[0].second + "...");
                    break;
                case LOAD_DONE:
                    tvInfo.setText(tvInfo.getText() + "DONE");
                    break;
                case CHECK_START:
                    tvInfo.setText("Checking " + values[0].second + "...");
                    break;
                case UPDATE_START:
                    tvInfo.setText("Applying " + values[0].second + "...");
                    break;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            globals = (GlobalsClass)getApplicationContext();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (result) {
                mUpdateAvalDialog = new CustomDialog(LogoActivity.this,
                        R.string.title_dialog_update_available,
                        R.string.dialog_update_available,
                        R.string.btn_no,
                        R.string.btn_yes,
                        noClickListener,
                        yesClickListener);
                mUpdateAvalDialog.show();
            } else {
                decideNextActivity();
            }
        }
    }

    private boolean isNewUpdate(String url) {
        URLConnection connection = null;

        try {
            URL serverURL = new URL(url);
            connection = serverURL.openConnection();
            connection.setReadTimeout(10000); // We will wait util max 10s for connection waiting
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        long lastUpdated = pref.getLong("last_updated", 0);
        long lastModifiedServer =  connection.getLastModified();

        if (lastUpdated < lastModifiedServer) {
            return true;
        }
        return false;
    }

    private void decideNextActivity() {
        if (isFirstLaunch) {
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
    }

    private View.OnClickListener yesClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ArrayList<String> URLArrayList = new ArrayList<String>();

            mUpdateAvalDialog.dismiss();

            // Start to download at Background
            if (isNeedFeaturesDownload) {
                URLArrayList.add(globals.featuresDownloadURL);
            }

            if (isNeedMapDownload) {
                URLArrayList.add(globals.mapDownloadURL);
            }
            Downloader downloader = new Downloader(URLArrayList);
            downloader.startDownload(LogoActivity.this, "Sam Tour", "Tour Database");

            decideNextActivity();
        }
    };

    private View.OnClickListener noClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mUpdateAvalDialog.dismiss();
            decideNextActivity();
        }
    };

}