package uz.samtuit.samapp.main;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class StarterActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                Intent splash = new Intent(StarterActivity.this, Logo.class);
                startActivityForResult(splash, 0);
                Intent mainMap = new Intent(StarterActivity.this, MainMap.class);
                startActivity(mainMap);
                Intent about = new Intent(StarterActivity.this, AboutAppActivity.class);
                startActivity(about);
            }
        });
    }
}
