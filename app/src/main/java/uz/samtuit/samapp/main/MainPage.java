package uz.samtuit.samapp.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import uz.samtuit.sammap.main.R;


public class MainPage extends Activity {

    Button skipButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        skipButton = (Button)findViewById(R.id.skipBtn);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
