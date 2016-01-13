package uz.samtuit.samapp.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;


public class AboutAppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);

        String version = this.getSharedPreferences("SamTour_Pref", 0).getString("app_version", "0");
        TextView textView = (TextView)findViewById(R.id.version);
        textView.setText("Ver. " + version);
    }
}
