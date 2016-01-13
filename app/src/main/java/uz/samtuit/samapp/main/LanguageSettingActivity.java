package uz.samtuit.samapp.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import uz.samtuit.samapp.util.GlobalsClass;
import uz.samtuit.samapp.util.SystemSetting;
import uz.samtuit.samapp.util.TourFeatureList;

public class LanguageSettingActivity extends AppCompatActivity {

    private ImageButton UZ_BTN,RU_BTN,EN_BTN;
    private SharedPreferences sharedPreferences;
    private boolean isFirstLaunch;
    private ProgressDialog progressDialog;
    private String selectLang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_setting);

        sharedPreferences = this.getSharedPreferences("SamTour_Pref", 0);
        isFirstLaunch = sharedPreferences.getBoolean("app_first_launch", true);
        final String currentLang = sharedPreferences.getString("app_lang", null);

        UZ_BTN = (ImageButton) findViewById(R.id.lang_uz);
        RU_BTN = (ImageButton) findViewById(R.id.lang_ru);
        EN_BTN = (ImageButton) findViewById(R.id.lang_en);

        ImageButton.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectLang = view.getTag().toString();
                SystemSetting.setUserLanguage(LanguageSettingActivity.this, selectLang);

                if (isFirstLaunch) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("sel_lang", selectLang);
                    setResult(0, resultIntent);

                    finish();
                    overridePendingTransition(R.anim.slide_content, R.anim.slide_in);
                } else if (!currentLang.equals(selectLang)) {
                    progressDialog = ProgressDialog.show(LanguageSettingActivity.this, "", getString(R.string.dialog_load_features), true, false);
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void ... params)
                        {
                            TourFeatureList.loadAllFeaturesToMemory(LanguageSettingActivity.this, selectLang);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void result)
                        {
                            progressDialog.dismiss();
                            sharedPreferences.edit().putString("app_lang", selectLang).commit(); // Set App language

                            if (!isFirstLaunch) {
                                Intent intent = new Intent(LanguageSettingActivity.this, MainMap.class);
                                intent.putExtra("type", "features");
                                intent.putExtra("featureType", GlobalsClass.FeatureType.ITINERARY.toString());
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                            } else {
                                finish();
                            }
                            overridePendingTransition(R.anim.slide_content, R.anim.slide_in);
                        }
                    }.execute();
                } else {
                    finish();
                    overridePendingTransition(R.anim.slide_content, R.anim.slide_in);
                }
            }
        };

        UZ_BTN.setOnClickListener(onClickListener);
        RU_BTN.setOnClickListener(onClickListener);
        EN_BTN.setOnClickListener(onClickListener);
    }

    @Override
    public void onBackPressed() {
        if (isFirstLaunch) {
            Toast.makeText(this, R.string.select_your_language, Toast.LENGTH_SHORT).show();
        } else {
            finish();
        }
    }
}
