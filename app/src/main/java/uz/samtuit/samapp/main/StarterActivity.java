package uz.samtuit.samapp.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class StarterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent splash = new Intent(StarterActivity.this, Splash.class);
        startActivity(splash);
        Intent mainMap = new Intent(StarterActivity.this, MainMap.class);
        startActivity(mainMap);
    }
}
