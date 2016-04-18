package uz.samtuit.samapp.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mapbox.mapboxsdk.util.NetworkUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.zip.ZipFile;

import uz.samtuit.samapp.util.CheckUpdateManager;
import uz.samtuit.samapp.util.CustomDialog;
import uz.samtuit.samapp.util.Downloader;
import uz.samtuit.samapp.util.FileUtil;
import uz.samtuit.samapp.util.GlobalsClass;
import uz.samtuit.samapp.util.ItineraryList;
import uz.samtuit.samapp.util.SystemSetting;
import uz.samtuit.samapp.util.TourFeature;
import uz.samtuit.samapp.util.TourFeatureList;
import uz.samtuit.samapp.util.TypefaceHelper;
import uz.samtuit.samapp.util.ZipFileUtil;


public class LogoActivity extends Activity {
    private CustomDialog mUpdateAvalDialog;
    private TextView tvInfo;
    private GlobalsClass globals;
    private SharedPreferences pref;
    private boolean isNeedFeaturesDownload;
    private boolean isNeedMapDownload;
    private boolean isFirstLaunch, isCheckUpdateOnboot;
    private int downloadRequestCnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_logo);
        overridePendingTransition(R.anim.slide_content, R.anim.slide_in);
        tvInfo = (TextView) findViewById(R.id.tv_info);
        tvInfo.setTypeface(TypefaceHelper.getTypeface(this, "segoeui"));
        Glide.with(this).load(R.drawable.logo).into((ImageView)findViewById(R.id.back_image));

        pref = this.getSharedPreferences("SamTour_Pref", 0);
        isFirstLaunch = pref.getBoolean("app_first_launch", true);

        SharedPreferences defaultPref = PreferenceManager.getDefaultSharedPreferences(this);
        isCheckUpdateOnboot = defaultPref.getBoolean("update_check_on_boot", true);

        if (isFirstLaunch) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("app_version", "0.5.2"); // Set App version
            editor.putLong("last_updated", new Date().getTime()); // Set installed date
            editor.commit();

            // IF the system locale is same as one of supported languages, set as it
            String systemLocale = SystemSetting.checkSystemLocale();
            if (systemLocale.equals(GlobalsClass.supportedLanguages[0])
                    || systemLocale.equals(GlobalsClass.supportedLanguages[1])
                    || systemLocale.equals(GlobalsClass.supportedLanguages[2])) {

                SystemSetting.setUserLanguage(LogoActivity.this, systemLocale);
                editor.putString("app_lang", systemLocale).commit(); // Set App language

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
        editor.putString("app_lang", data.getExtras().getString("sel_lang")).commit(); // Set App language

        continueInBackgroundTask();
    }

    private void continueInBackgroundTask(){
        LoadGeoJsonFilesInBackground loadGeoJsonFilesInBackground = new LoadGeoJsonFilesInBackground();
        loadGeoJsonFilesInBackground.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("LogoActivity", "onResume()");
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
                SystemSetting.setUserLanguage(LogoActivity.this, chosenLang);
                String path = null;

                // Do below, when first launch or since new update has been downloaded
                if (isFirstLaunch) {
                    publishProgress(new Pair<Integer, String>(INIT_START, ""));

                    // All downloaded GeoJSON files from server will be located in ExternalDir
                    // So working directory is ExternalDir, all files in the asset should be copied to ExternalDir at first launch
                    TourFeatureList.CopyLocalGeoJSONFilesToExternalDir(LogoActivity.this);

                    FileUtil.deleteAllExternalFilesWithExtension(LogoActivity.this, TourFeatureList.photoDirectory, "");
                    TourFeatureList.writeAllPhotosToFiles(LogoActivity.this); // Make all photo data to files

                } else if (Downloader.isDownloadFinished(LogoActivity.this)) {
                    publishProgress(new Pair<Integer, String>(UPDATE_START, "new update"));

                    downloadRequestCnt = Downloader.countOfDownloadRequest(LogoActivity.this);
                    for (int i = 0; i < downloadRequestCnt; i++) {
                        String uriString = pref.getString("downloaded_uri" + i, "");
                        String filePath = Uri.parse(uriString).getPath();

                        // If the downloaded file is zipped, Unzip it
                        if (uriString.contains("zip")) {
                            // Before unzip, delete all GeoJSON files in the directory
                            FileUtil.deleteAllExternalFilesWithExtension(LogoActivity.this, "", "geojson");
                            ZipFileUtil.unZipToExteranlDir(LogoActivity.this, filePath, ZipFile.OPEN_DELETE);

                            FileUtil.deleteAllExternalFilesWithExtension(LogoActivity.this, TourFeatureList.photoDirectory, "");
                            TourFeatureList.writeAllPhotosToFiles(LogoActivity.this); // Make all photo data to files
                        } else { // Map file
                            FileUtil.fileMoveToExternalDir(LogoActivity.this, filePath);
                        }
                    }

                    // Don't forget below when the update process finished
                    Downloader.initDownloaderState(LogoActivity.this);
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

                if (!Downloader.isAlreadyDownloadRequest(LogoActivity.this) && NetworkUtils.isNetworkAvailable(LogoActivity.this) && isCheckUpdateOnboot) {
                    publishProgress(new Pair<Integer, String>(CHECK_START, getString(R.string.new_update)));
                    CheckUpdateManager checkUpdateManager = new CheckUpdateManager();

                    isNeedFeaturesDownload = checkUpdateManager.isNewUpdate(LogoActivity.this, handler, globals.featuresDownloadURL);
                    isNeedMapDownload = checkUpdateManager.isNewUpdate(LogoActivity.this, handler, globals.mapDownloadURL);
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
                    tvInfo.setText(getApplicationContext().getResources().getString(R.string.logo_init, values[0].second)+ "...");
                    break;
                case LOAD_START:
                    tvInfo.setText(getApplicationContext().getResources().getString(R.string.logo_load,values[0].second) + "...");
//                    tvInfo.setText("Loading " + values[0].second + "...");
                    break;
                case LOAD_DONE:
                    tvInfo.setText(tvInfo.getText() + getResources().getString(R.string.logo_done));
                    break;
                case CHECK_START:
                    tvInfo.setText(getApplicationContext().getResources().getString(R.string.logo_check, values[0].second) + "...");
//                    tvInfo.setText("Checking " + values[0].second + "...");
                    break;
                case UPDATE_START:
                    tvInfo.setText(getApplicationContext().getResources().getString(R.string.logo_apply, values[0].second) + "...");
//                    tvInfo.setText("Applying " + values[0].second + "...");
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

    // Added for when check update is delayed
    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.arg1 == 1) {
                Toast.makeText(LogoActivity.this, R.string.Err_network_state, Toast.LENGTH_LONG).show();
                Log.e("LogoActivity", "network state warning");
            }
        }
    };

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