package uz.samtuit.sammap.samapp;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mapbox.mapboxsdk.geometry.LatLng;


public class AttractionsActivity extends ActionBarActivity {
    final Attractions[] item = new Attractions[6];
    ListView list;
    TextView tv;
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attractions);
        item[0] = new Attractions("emirxan",123,3,"+998981234567","St.sadasd",new LatLng(36,66));
        item[1] = new Attractions("emirxan",123,3,"+998981234567","St.sadasd",new LatLng(36,66));
        item[2] = new Attractions("emirxan",123,3,"+998981234567","St.sadasd",new LatLng(36,66));
        item[3] = new Attractions("emirxan",123,3,"+998981234567","St.sadasd",new LatLng(36,66));
        item[4] = new Attractions("emirxan",123,3,"+998981234567","St.sadasd",new LatLng(36,66));
        item[5] = new Attractions("emirxan",123,3,"+998981234567","St.sadasd",new LatLng(36,66));
        list = (ListView)findViewById(R.id.attractionsListView);
        tv=(TextView)findViewById(R.id.attractionsTitle);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Baskerville.ttf");
        tv.setTypeface(tf);
        AttractionsListAdapter adapter = new AttractionsListAdapter(this,R.layout.list_item, item);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }
}
