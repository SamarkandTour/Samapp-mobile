package uz.samtuit.samapp.main;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;


public class AboutAppActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
    }
    public void CheckUsOnSocial(View view) {
        Log.d("Social Clicked", view.getTag().toString());
    }
}
