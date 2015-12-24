package uz.samtuit.samapp.util;

/**
 * Created by Bakhrom Rakhmonov on 11.12.2015.
 */

import android.content.Context;
import android.database.*;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public final class MyItineraryDatabaseHelper extends SQLiteOpenHelper {

    public static final String TABLE_MY_ITINERARY = "myitinerary";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "_name";
    public static final String COLUMN_LOCATION = "_location";
    public static final String COLUMN_DAY_NUM = "_day_num";

    private static final String DATABASE_NAME = "myitinerary.db";
    private static final int DATABASE_VERSION = 1;

    //Database create String
    private static final String DATABASE_CREATE = "create table " + TABLE_MY_ITINERARY + "("
            + COLUMN_ID + " integer primary key, "
            + COLUMN_NAME + " text, "
            + COLUMN_LOCATION + " text, "
            + COLUMN_DAY_NUM + " integer);";

    public MyItineraryDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MyItineraryDatabaseHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MY_ITINERARY);
        onCreate(db);
    }
}