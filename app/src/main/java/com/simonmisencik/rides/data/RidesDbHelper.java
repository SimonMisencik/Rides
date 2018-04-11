package com.simonmisencik.rides.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.simonmisencik.rides.data.RidesContract.RidesEntry;

/**
 * Created by Simon Misencik on 12. 3. 2018.
 */

public class RidesDbHelper extends SQLiteOpenHelper
{
    public static final String LOG_TAG = RidesDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "rides.db";

    private static final int DATABASE_VERSION = 1;

    public RidesDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String SQL_CREATE_RIDES_TABLE = "CREATE TABLE " + RidesEntry.TABLE_NAME + "("
                + RidesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + RidesEntry.COLUMN_Date_start          +   " TEXT NOT NULL, "
                + RidesEntry.COLUMN_Time_start          +   " TEXT, "
                + RidesEntry.COLUMN_Date_end            +   " TEXT NOT NULL, "
                + RidesEntry.COLUMN_Time_end            +   " TEXT, "
                + RidesEntry.COLUMN_City_Start          +   " TEXT NOT NULL, "
                + RidesEntry.COLUMN_City_Work_Area      +   " TEXT, "
                + RidesEntry.COLUMN_City_End            +   " TEXT NOT NULL, "
                + RidesEntry.COLUMN_Purpose             +   " TEXT, "
                + RidesEntry.COLUMN_Tachometer          +   " INTEGER, "
                + RidesEntry.COLUMN_Distance_Combined   +   " INTEGER NOT NULL DEFAULT 0, "
                + RidesEntry.COLUMN_Distance_City       +   " INTEGER NOT NULL DEFAULT 0, "
                + RidesEntry.COLUMN_Note                +   " TEXT);";

        db.execSQL(SQL_CREATE_RIDES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }

}