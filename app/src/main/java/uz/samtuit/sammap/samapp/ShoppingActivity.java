package uz.samtuit.sammap.samapp;

import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class ShoppingActivity extends ActionBarActivity {
    final Hotels[] item = new Hotels[6];
    ListView list;
    TextView tv;
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);
        item[0] = new Hotels("emirxan",123,3);
        item[1] = new Hotels("grand Samarkand",5,4);
        item[2] = new Hotels("emirxan",123,3);
        item[3] = new Hotels("grand Samarkand",5,4);
        item[4] = new Hotels("emirxan",123,3);
        item[5] = new Hotels("grand Samarkand",5,4);
        list = (ListView)findViewById(R.id.shoppingListView);
        tv=(TextView)findViewById(R.id.shoppingTitle);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Baskerville.ttf");
        tv.setTypeface(tf);
        MyListAdapter adapter = new MyListAdapter(this,R.layout.list_item, item);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shopping, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
