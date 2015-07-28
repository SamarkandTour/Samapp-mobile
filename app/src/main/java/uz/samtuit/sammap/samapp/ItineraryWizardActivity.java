package uz.samtuit.sammap.samapp;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;


public class ItineraryWizardActivity extends Activity {

    Button btn;
    LinearLayout ll;
    FrameLayout fl;



    private final static int daysCount = 3;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary_wizard);
        ll = (LinearLayout)findViewById(R.id.tabButtonsLayout);
        for(int i = 0; i < daysCount; i++)
        {
            btn = new Button(this);
            btn.setBackgroundResource(R.drawable.tab_button_back);
            btn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            ll.addView(btn);
        }
    }

}

