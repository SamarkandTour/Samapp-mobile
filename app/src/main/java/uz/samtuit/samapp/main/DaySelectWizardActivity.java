package uz.samtuit.samapp.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import uz.samtuit.sammap.main.R;

public class DaySelectWizardActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String items[] = {"1 Day", "2 Day", "3 Day", "4 Day", "5 Day"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_select_wizard);

        Spinner spin = (Spinner) findViewById(R.id.daySelectSpinner);
        spin.setOnItemSelectedListener(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spin.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
