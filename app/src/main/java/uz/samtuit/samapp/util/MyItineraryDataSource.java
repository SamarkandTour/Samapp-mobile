package uz.samtuit.samapp.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Bakhrom Rakhmonov on 11.12.2015.
 */
public class MyItineraryDataSource {
    private SQLiteDatabase database;
    private MyItineraryDatabaseHelper dbHelper;
    private String[] allColumns = {
            MyItineraryDatabaseHelper.COLUMN_ID, MyItineraryDatabaseHelper.COLUMN_NAME, MyItineraryDatabaseHelper.COLUMN_DAY_NUM, MyItineraryDatabaseHelper.COLUMN_LOCATION
    };

    public MyItineraryDataSource(Context context){
        dbHelper = new MyItineraryDatabaseHelper(context);
    }

    public void open() throws SQLiteException{
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    public TourFeature createItineraryItem(int OrderNumber, String ItemName, LatLng Location, int DayNumber) {
        TourFeature newItineraryItem = null;
        try{
            open();
            ContentValues values = new ContentValues();
            values.put(MyItineraryDatabaseHelper.COLUMN_ID, OrderNumber);
            values.put(MyItineraryDatabaseHelper.COLUMN_NAME, ItemName);
            values.put(MyItineraryDatabaseHelper.COLUMN_DAY_NUM, DayNumber);
            values.put(MyItineraryDatabaseHelper.COLUMN_LOCATION, ""+Location.getLatitude() + "," + Location.getLongitude());
            Log.e(OrderNumber + " " + ItemName, Location.toString()+ " " + DayNumber);
            long insertId = database.insert(MyItineraryDatabaseHelper.TABLE_MY_ITINERARY, null,
                    values);

            Cursor cursor = database.query(MyItineraryDatabaseHelper.TABLE_MY_ITINERARY,
                    allColumns, MyItineraryDatabaseHelper.COLUMN_ID + " = " + insertId, null,
                    null, null, null);

            cursor.moveToFirst();
            cursor.close();
            newItineraryItem = cursorToItineraryItem(cursor);
            close();
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return newItineraryItem;
    }


    public void deleteItem(TourFeature itineraryItem) {
        long id = itineraryItem.getItineraryId();
        System.out.println("ItineraryItem deleted with id: " + id);
        database.delete(MyItineraryDatabaseHelper.TABLE_MY_ITINERARY, MyItineraryDatabaseHelper.COLUMN_ID
                + " = " + id, null);
    }

    public LinkedList getAllItineraryItems() {
        LinkedList<TourFeature> itineraryItems = new LinkedList<>();

        Cursor cursor = database.query(MyItineraryDatabaseHelper.TABLE_MY_ITINERARY,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            TourFeature itineraryItem = cursorToItineraryItem(cursor);
            itineraryItems.add(itineraryItem);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return itineraryItems;
    }

    private TourFeature cursorToItineraryItem(Cursor cursor) {
        TourFeature itineraryItem = new TourFeature();
        Log.e(cursor.getInt(0)+" "+cursor.getString(1),cursor.getString(2)+" "+cursor.getString(3));
        itineraryItem.setItineraryId(cursor.getInt(0));
        itineraryItem.setStringHashMap("name", cursor.getString(1));
        itineraryItem.setLatitude(Double.parseDouble(cursor.getString(3).split(",")[0]));
        itineraryItem.setLongitude(Double.parseDouble(cursor.getString(3).split(",")[1]));
        itineraryItem.setDay(cursor.getInt(2));
        return itineraryItem;
    }
}
