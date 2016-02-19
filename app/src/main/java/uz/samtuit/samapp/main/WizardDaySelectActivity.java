package uz.samtuit.samapp.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import com.rey.material.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import uz.samtuit.samapp.util.ItineraryList;

public class WizardDaySelectActivity extends AppCompatActivity implements com.rey.material.widget.Spinner.OnItemSelectedListener {
    ArrayList<String> items= new ArrayList<String>();
    int selected;
    SharedPreferences sharedPreferences;
    boolean isFirstLaunch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wizard_day_select);

        Spinner spin = (Spinner) findViewById(R.id.daySpinner1);
        spin.setOnItemSelectedListener(this);

        items.add(0, getString(R.string.itinerary_select_day));
        for (int i=1; i <= ItineraryList.MAX_ITINERARY_DAYS; i++) {
            items.add(i, i + getString(R.string.itinerary_day));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(R.layout.wizard_spinner_item_layout);
        spin.setAdapter(adapter);

        sharedPreferences = this.getSharedPreferences("SamTour_Pref", 0);
        isFirstLaunch = sharedPreferences.getBoolean("app_first_launch", true);
    }

    public void onLaterBtnClick(View view) {
        if (isFirstLaunch) {
            Intent intent = new Intent(this, MainMap.class);
            startActivity(intent);
            finish();
        } else {
            finish();
        }

        if (isFirstLaunch) { // Don't forget, Set first_launch to false
            sharedPreferences.edit().putBoolean("app_first_launch", false).commit();
        }
    }

    public void onNextBtnClick(View view) {
        if (selected == 0) {
            Toast.makeText(this, R.string.itinerary_select_day, Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(this, WizardCourseSelectActivity.class);
        intent.putExtra("day", selected);
        startActivity(intent);
        finish();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (isFirstLaunch) {
            Intent intent = new Intent(this, MainMap.class);
            startActivity(intent);
            finish();
        } else {
            finish();
        }

        if (isFirstLaunch) { // Don't forget, Set first_launch to false
            sharedPreferences.edit().putBoolean("app_first_launch", false).commit();
        }
    }

    @Override
    public void onItemSelected(Spinner parent, View view, int position, long id) {
        selected = position;
    }
}
