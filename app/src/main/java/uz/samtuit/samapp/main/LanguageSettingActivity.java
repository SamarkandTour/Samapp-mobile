package uz.samtuit.samapp.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import uz.samtuit.samapp.util.SystemSetting;

public class LanguageSettingActivity extends AppCompatActivity {

    private ImageButton UZ_BTN,RU_BTN,EN_BTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_setting);

        UZ_BTN = (ImageButton) findViewById(R.id.lang_uz);
        RU_BTN = (ImageButton) findViewById(R.id.lang_ru);
        EN_BTN = (ImageButton) findViewById(R.id.lang_en);

        ImageButton.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectLang = view.getTag().toString();
                SystemSetting.setUserLanguage(LanguageSettingActivity.this, selectLang);
                overridePendingTransition(R.anim.slide_content, R.anim.slide_in);

                Intent resultIntent = new Intent();
                resultIntent.putExtra("sel_lang", selectLang);
                setResult(0, resultIntent);
                finish();
            }
        };

        UZ_BTN.setOnClickListener(onClickListener);
        RU_BTN.setOnClickListener(onClickListener);
        EN_BTN.setOnClickListener(onClickListener);
    }

    @Override
    public void onBackPressed() {
        if (this.getSharedPreferences("SamTour_Pref", 0).getBoolean("app_first_launch", true)) {
            Toast.makeText(this, R.string.select_your_language, Toast.LENGTH_SHORT).show();
        } else {
            finish();
        }
    }
}
