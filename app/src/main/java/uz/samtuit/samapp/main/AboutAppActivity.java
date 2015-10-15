package uz.samtuit.samapp.main;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import uz.samtuit.sammap.main.R;


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
