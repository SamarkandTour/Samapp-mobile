package uz.samtuit.samapp.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uz.samtuit.samapp.util.GlobalsClass;
import uz.samtuit.samapp.util.ItineraryList;
import uz.samtuit.sammap.main.R;

public class WizardCourseSelectActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private ArrayList<String> course1= new ArrayList<String>();
    private ArrayList<String> course2= new ArrayList<String>();
    private ArrayList<String> course3= new ArrayList<String>();
    private ArrayList<String> course4= new ArrayList<String>();
    private ArrayList<String> course5= new ArrayList<String>();
    private ArrayList<String> selectedCourseList = new ArrayList<String>();
    private float selectedTotalDay;
    private Spinner spin1, spin2, spin3, spin4, spin5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wizard_course_select);

        spin1 = (Spinner) findViewById(R.id.courseSpinner1);
        spin1.setOnItemSelectedListener(this);
        spin2 = (Spinner) findViewById(R.id.courseSpinner2);
        spin2.setOnItemSelectedListener(this);
        spin3 = (Spinner) findViewById(R.id.courseSpinner3);
        spin3.setOnItemSelectedListener(this);
        spin4 = (Spinner) findViewById(R.id.courseSpinner4);
        spin4.setOnItemSelectedListener(this);
        spin5 = (Spinner) findViewById(R.id.courseSpinner5);
        spin5.setOnItemSelectedListener(this);

        Intent intent = getIntent();
        selectedTotalDay = intent.getIntExtra("day", 0);
        setSpinnerValueAndInflate(null, spin1, course1, getString(R.string.itinerary_1st_course));
    }

    private void setSpinnerValueAndInflate(String currentSelected, Spinner spinner, ArrayList<String> course, String initText) {
        course.add(0, initText);

        int i = 1;
        for (String key :  ItineraryList.getCourseHashMap().keySet()) {
            if (ItineraryList.getCourseHashMap().get(key) <= selectedTotalDay && !key.equals(currentSelected)) { // Except current selected
                course.add(i++, key + "(" + ItineraryList.getCourseHashMap().get(key).toString() + "day" + ")");
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, course);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.invalidate();
    }

    public void onSkipBtnClick(View view) {
        if (this.getSharedPreferences("SamTour_Pref", 0).getBoolean("app_first_launch", true)) {
            Intent intent = new Intent(this, MainMap.class);
            startActivity(intent);
        } else {
            finish();
        }
    }

    public void onPreviousBtnClick(View view) {
        Intent intent = new Intent(this, WizardDaySelectActivity.class);
        startActivity(intent);
        finish();
    }

    public void onDoneBtnClick(View view) {
        String lang = this.getSharedPreferences("SamTour_Pref", 0).getString("app_lang", null);
        String path = null;
        ItineraryList itineraryListInstance = ItineraryList.getInstance();

        if (spin1.getSelectedItemPosition() == 0) {
            Toast.makeText(this, R.string.itinerary_1st_course, Toast.LENGTH_LONG).show();
            return;
        }

        itineraryListInstance.clearItineraryFeatureList(); // For new, clear the old itinerary
        for (String selectedCourse : selectedCourseList) { // Courses will be merged here
            float day = ItineraryList.getCourseHashMap().get(selectedCourse);
            path = lang + "_itinerary_" + String.valueOf(day) + "_" + selectedCourse + ".geojson";

            itineraryListInstance.getItineraryFeatureListFromGeoJSONFile(this, path);
        }

        itineraryListInstance.setItinearyFeaturesToGlobal(this);
        ItineraryList.itineraryWriteToGeoJSONFile(this, lang);

        Intent intent = new Intent(this, MainMap.class);
        intent.putExtra("type", "features");
        intent.putExtra("featureType", GlobalsClass.FeatureType.ITINERARY.toString());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    void setCurrentCourseAndInflateNextCourse(ArrayList<String> currentArray, int position, Spinner nextSpinner, ArrayList<String> nextArray, String initText ) {
        String selectedCourse = currentArray.get(position);
        Matcher m = Pattern.compile("[a-z]+").matcher(selectedCourse); // Detach braces
        m.find();
        String selectedPureCourse = m.group(0);
        selectedCourseList.add(selectedPureCourse);
        selectedTotalDay -= ItineraryList.getCourseHashMap().get(selectedPureCourse);
        if (selectedTotalDay != 0) {
            setSpinnerValueAndInflate(selectedPureCourse, nextSpinner, nextArray, initText);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.courseSpinner1:
                if (parent.getSelectedItemPosition() != 0) {
                    setCurrentCourseAndInflateNextCourse(course1, position, spin2, course2, getString(R.string.itinerary_2nd_course));
                }
                break;

            case R.id.courseSpinner2:
                if (parent.getSelectedItemPosition() != 0) {
                    setCurrentCourseAndInflateNextCourse(course2, position, spin3, course3, getString(R.string.itinerary_3rd_course));
                }
                break;
            case R.id.courseSpinner3:
                if (parent.getSelectedItemPosition() != 0) {
                    setCurrentCourseAndInflateNextCourse(course3, position, spin4, course4, getString(R.string.itinerary_4th_course));
                }
                break;
            case R.id.courseSpinner4:
                if (parent.getSelectedItemPosition() != 0) {
                    setCurrentCourseAndInflateNextCourse(course4, position, spin5, course5, getString(R.string.itinerary_5th_course));
                }
                break;
            case R.id.courseSpinner5:
                if (parent.getSelectedItemPosition() != 0) {
                    setCurrentCourseAndInflateNextCourse(course5, position, null, null, null);
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
