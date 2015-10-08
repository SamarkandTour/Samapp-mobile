package uz.samtuit.samapp.main;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import uz.samtuit.sammap.main.R;

/**
 * Created by sammap on 7/8/15.
 */
public class OnItemClickListenerItem implements ListView.OnItemClickListener {

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Context context = view.getContext();
        TextView textViewItem = ((TextView) view.findViewById(R.id.title));
    }
}
