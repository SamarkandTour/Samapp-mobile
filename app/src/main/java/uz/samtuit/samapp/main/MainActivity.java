package uz.samtuit.samapp.main;

import android.app.Activity;
import android.os.Bundle;

import uz.samtuit.sammap.main.R;


public class MainActivity extends Activity {

    boolean updateAvailable = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);



    }
}
