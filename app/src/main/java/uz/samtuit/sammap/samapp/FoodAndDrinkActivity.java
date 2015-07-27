package uz.samtuit.sammap.samapp;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mapbox.mapboxsdk.geometry.LatLng;


public class FoodAndDrinkActivity extends Activity {
    final Foods[] item = new Foods[6];
    ListView list;
    TextView tv;
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_and_drink);
        item[0] = new Foods("emirxan",123,3,"+998981234567","St.sadasd",new LatLng(36,66));
        item[1] = new Foods("grand Samarkand",5,4,"+998981234567","St.sadasd",new LatLng(36,66));
        item[2] = new Foods("emirxan",123,3,"+998981234567","St.sadasd",new LatLng(36,66));
        item[3] = new Foods("grand Samarkand",5,4,"+998981234567","St.sadasd",new LatLng(36,66));
        item[4] = new Foods("emirxan",123,3,"+998981234567","St.sadasd",new LatLng(36,66));
        item[5] = new Foods("grand Samarkand",5,4,"+998981234567","St.sadasd",new LatLng(36,66));
        list = (ListView)findViewById(R.id.foodAndDrinkListView);
        tv=(TextView)findViewById(R.id.foodAndDrinkTitle);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Baskerville.ttf");
        tv.setTypeface(tf);
        FoodsListAdapter adapter = new FoodsListAdapter(this,R.layout.list_item, item);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

    }
}
