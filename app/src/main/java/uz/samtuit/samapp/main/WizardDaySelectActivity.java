package uz.samtuit.samapp.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import uz.samtuit.samapp.util.ItineraryList;

public class WizardDaySelectActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    ArrayList<String> items= new ArrayList<String>();
    int selected;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wizard_day_select);

        Spinner spin = (Spinner) findViewById(R.id.daySpinner1);
        spin.setOnItemSelectedListener(this);

        items.add(0, getString(R.string.itinerary_select_day));
        for (int i=1; i <= ItineraryList.MAX_ITINERARY_DAYS; i++) {
            items.add(i, i + " Day");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);

        sharedPreferences = this.getSharedPreferences("SamTour_Pref", 0);
    }

    public void onSkipBtnClick(View view) {
        if (sharedPreferences.getBoolean("app_first_launch", true)) {
            Intent intent = new Intent(this, MainMap.class);
            startActivity(intent);
        } else {
            finish();
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selected = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (sharedPreferences.getBoolean("app_first_launch", true)) {
            Intent intent = new Intent(this, MainMap.class);
            startActivity(intent);
        } else {
            finish();
        }
    }

    @Override
    protected void onStop() { // Don't forget, Set first_launch to false
        super.onStop();

        if (sharedPreferences.getBoolean("app_first_launch", true)) {
            sharedPreferences.edit().putBoolean("app_first_launch", false).commit();
        }
    }
}