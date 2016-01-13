package uz.samtuit.samapp.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import uz.samtuit.samapp.util.GlobalsClass;
import uz.samtuit.samapp.util.ItineraryList;

public class WizardCourseSelectActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private String selectedCourseList[] = new String[ItineraryList.MAX_ITINERARY_COURSES];
    private float selectedTourDay, usedDay, remainDay;
    private ProgressBar progressBar;
    SharedPreferences sharedPreferences;
    boolean isFirstLaunch;

    private ArrayList<String>[] courseArray = new ArrayList[ItineraryList.MAX_ITINERARY_COURSES];
    private int[] courseInitNameId = {
            R.string.itinerary_1st_course,
            R.string.itinerary_2nd_course,
            R.string.itinerary_3rd_course,
            R.string.itinerary_4th_course,
            R.string.itinerary_5th_course,
            R.string.itinerary_6th_course,
    };

    private Spinner[] spinnerArray = new Spinner[ItineraryList.MAX_ITINERARY_COURSES];
    private int[] spinnerId = {
            R.id.courseSpinner1,
            R.id.courseSpinner2,
            R.id.courseSpinner3,
            R.id.courseSpinner4,
            R.id.courseSpinner5,
            R.id.courseSpinner6,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wizard_course_select);

        if (ItineraryList.MAX_ITINERARY_COURSES == 0) {
            Toast.makeText(this, R.string.Err_not_supported, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        initCourses();

        Intent intent = getIntent();
        selectedTourDay = intent.getIntExtra("day", 0);

        progressBar = (ProgressBar) findViewById(R.id.courseWizardProgressBar);
        progressBar.setMax((int)(selectedTourDay*10));

        TextView textView = (TextView) findViewById(R.id.selected_total_day);
        textView.setText(0 + " / " + selectedTourDay + getString(R.string.itinerary_day));

        remainDay = selectedTourDay;
        setSpinnerValueAndInflate(spinnerArray[0], courseArray[0]);

        sharedPreferences = this.getSharedPreferences("SamTour_Pref", 0);
        isFirstLaunch = sharedPreferences.getBoolean("app_first_launch", true);
    }

    private void initCourses() {
        for (int i = 0; i < ItineraryList.MAX_ITINERARY_COURSES; i++) {
            courseArray[i] = new ArrayList<String>();
            courseArray[i].add(getString(courseInitNameId[i]));

            // Set spinner and adapter
            spinnerArray[i] = (Spinner) findViewById(spinnerId[i]);
            spinnerArray[i].setOnItemSelectedListener(this);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    this, android.R.layout.simple_spinner_item, courseArray[i]);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinnerArray[i].setAdapter(adapter);
            spinnerArray[i].setVisibility(View.INVISIBLE);
        }
    }

    private boolean isAlreadySelected(String courseName) {
        for (String selectedCourse : selectedCourseList) {
            if (selectedCourse != null && selectedCourse.equals(courseName)) {
                return true;
            }
        }
        return false;
    }

    private void setSpinnerValueAndInflate(Spinner spinner, ArrayList<String> courseArray) {
        int i = 1;

        for (String courseName :  ItineraryList.getCourseHashMap()) {
            float courseDay = ItineraryList.getCourseDayFromHashMap(courseName);
            if ( courseDay <= remainDay && !isAlreadySelected(courseName)) { // Except already selected
                courseArray.add(i++, ItineraryList.getUiNameFromHashMap(courseName) + " (" + courseDay + getString(R.string.itinerary_day) + ")");
            }
        }

        spinner.setSelection(0);
        spinner.setVisibility(View.VISIBLE);
    }

    void setCurrentCourseAndInflateNextCourse(String selectedName, int curIndex, Spinner nextSpinner, ArrayList<String> nextArray) {
        // Detach braces from selected text
        String split[] = selectedName.split("\\(");
        String selectedCourseUiName = split[0].trim();
        String selectedCourseName = ItineraryList.UiCourseNameToCourseName(selectedCourseUiName);
        selectedCourseList[curIndex] = selectedCourseName;

        float selectedCourseDay = ItineraryList.getCourseDayFromHashMap(selectedCourseName);
        usedDay += selectedCourseDay;

        remainDay -= selectedCourseDay;
        progressBar.setProgress((int) (usedDay * 10)); // For Progress bar support only integer

        TextView textView = (TextView) findViewById(R.id.selected_total_day);
        textView.setText(usedDay + " / " + selectedTourDay + getString(R.string.itinerary_day));

        if (remainDay > 0 && nextSpinner != null) {
            setSpinnerValueAndInflate(nextSpinner, nextArray);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.courseSpinner1:
                if (parent.getSelectedItemPosition() != 0 && !isAlreadySelected(ItineraryList.UiCourseNameToCourseName(courseArray[0].get(position)))) {
                    spinnerArray[0].setEnabled(false);
                    setCurrentCourseAndInflateNextCourse(courseArray[0].get(position), 0, spinnerArray[1], courseArray[1]);
                }
                break;

            case R.id.courseSpinner2:
                if (parent.getSelectedItemPosition() != 0 && !isAlreadySelected(ItineraryList.UiCourseNameToCourseName(courseArray[1].get(position)))) {
                    spinnerArray[1].setEnabled(false);
                    setCurrentCourseAndInflateNextCourse(courseArray[1].get(position), 1, spinnerArray[2], courseArray[2]);
                }
                break;

            case R.id.courseSpinner3:
                if (parent.getSelectedItemPosition() != 0 && !isAlreadySelected(ItineraryList.UiCourseNameToCourseName(courseArray[2].get(position)))) {
                    spinnerArray[2].setEnabled(false);
                    setCurrentCourseAndInflateNextCourse(courseArray[2].get(position), 2, spinnerArray[3], courseArray[3]);
                }
                break;

            case R.id.courseSpinner4:
                if (parent.getSelectedItemPosition() != 0 && !isAlreadySelected(ItineraryList.UiCourseNameToCourseName(courseArray[3].get(position)))) {
                    spinnerArray[3].setEnabled(false);
                    setCurrentCourseAndInflateNextCourse(courseArray[3].get(position), 3, spinnerArray[4], courseArray[4]);
                }
                break;

            case R.id.courseSpinner5:
                if (parent.getSelectedItemPosition() != 0 && !isAlreadySelected(ItineraryList.UiCourseNameToCourseName(courseArray[4].get(position)))) {
                    spinnerArray[4].setEnabled(false);
                    setCurrentCourseAndInflateNextCourse(courseArray[4].get(position), 4, null, courseArray[5]);
                }
                break;

            case R.id.courseSpinner6:
                if (parent.getSelectedItemPosition() != 0 && !isAlreadySelected(ItineraryList.UiCourseNameToCourseName(courseArray[5].get(position)))) {
                    spinnerArray[5].setEnabled(false);
                    setCurrentCourseAndInflateNextCourse(courseArray[5].get(position), 5, spinnerArray[6], courseArray[6]);
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void onLaterBtnClick(View view) {
        if (this.getSharedPreferences("SamTour_Pref", 0).getBoolean("app_first_launch", true)) {
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

    public void onPreviousBtnClick(View view) {
        Intent intent = new Intent(this, WizardDaySelectActivity.class);
        startActivity(intent);
        finish();
    }

    public void onDoneBtnClick(View view) {
        String lang = this.getSharedPreferences("SamTour_Pref", 0).getString("app_lang", null);
        String path = null;
        ItineraryList itineraryListInstance = ItineraryList.getInstance();

        if (spinnerArray[0].getSelectedItemPosition() == 0) {
            Toast.makeText(this, R.string.itinerary_1st_course, Toast.LENGTH_LONG).show();
            return;
        }

        itineraryListInstance.clearItineraryFeatureList(); // For new, clear the old itinerary
        itineraryListInstance.initTourday();

        for (String selectedCourse : selectedCourseList) { // Courses will be merged here
            if (selectedCourse == null) break;

            float day = ItineraryList.getCourseDayFromHashMap(selectedCourse);
            path = lang + "_itinerary_" + String.valueOf(day) + "_" + selectedCourse + ".geojson";

            itineraryListInstance.mergeCoursesFromGeoJSONFileToItineraryList(this, path);
        }

        itineraryListInstance.setItinearyFeaturesToGlobal(this);
        ItineraryList.itineraryWriteToGeoJSONFile(this, lang);

        Intent intent = new Intent(this, MainMap.class);
        intent.putExtra("type", "features");
        intent.putExtra("featureType", GlobalsClass.FeatureType.ITINERARY.toString());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);

        if (isFirstLaunch) { // Don't forget, Set first_launch to false
            sharedPreferences.edit().putBoolean("app_first_launch", false).commit();
        }
    }
}
