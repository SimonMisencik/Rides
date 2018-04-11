package com.simonmisencik.rides;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.simonmisencik.rides.data.RidesContract;
import com.simonmisencik.rides.data.RidesDbHelper;
import com.simonmisencik.rides.data.RidesContract.RidesEntry;

public class ImportActivity extends AppCompatActivity
{
    private TextView importTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        importTextView = (TextView) findViewById(R.id.import_text_view);
        importTextView.setTextIsSelectable(true);
        importTextView.setText(getString(R.string.in_develop)+ "\n\n");

        RidesDbHelper dbHelper = new RidesDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection =
                {
                        RidesEntry._ID,
                        RidesEntry.COLUMN_Date_start,
                        RidesEntry.COLUMN_Time_start,
                        RidesEntry.COLUMN_City_Start,
                        RidesEntry.COLUMN_Date_end,
                        RidesEntry.COLUMN_Time_end,
                        RidesEntry.COLUMN_City_End,
                        RidesEntry.COLUMN_City_Work_Area,
                        RidesEntry.COLUMN_Purpose,
                        RidesEntry.COLUMN_Distance_Combined,
                        RidesEntry.COLUMN_Distance_City,
                        RidesEntry.COLUMN_Note
                };

        Cursor cursor = db.query(RidesEntry.TABLE_NAME, projection, null, null, null, null, null);

        try
        {
            // Create a header in the Text View that looks like this:
            //
            // The pets table contains <number of rows in Cursor> pets.
            // _id - name - breed - gender - weight
            //
            // In the while loop below, iterate through the rows of the cursor and display
            // the information from each column in this order.
            importTextView.append("The rides table contains " + cursor.getCount() + " rides.\n\n");
            importTextView.append(
                    RidesEntry._ID + " - " +
                    RidesEntry.COLUMN_Date_start + " - " +
                    RidesEntry.COLUMN_Time_start + " - " +
                    RidesEntry.COLUMN_City_Start + " - " +
                    RidesEntry.COLUMN_Date_end + " - " +
                    RidesEntry.COLUMN_Time_end + " - " +
                    RidesEntry.COLUMN_City_End + " - " +
                    RidesEntry.COLUMN_City_Work_Area + " - " +
                    RidesEntry.COLUMN_Purpose + " - " +
                    RidesEntry.COLUMN_Distance_Combined + " - " +
                    RidesEntry.COLUMN_Distance_City + " - " +
                    RidesEntry.COLUMN_Note + "\n");

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(RidesEntry._ID);
            int dateStartColumnIndex = cursor.getColumnIndex(RidesEntry.COLUMN_Date_start);
            int timeStartColumnIndex = cursor.getColumnIndex(RidesEntry.COLUMN_Time_start);
            int cityStartColumnIndex = cursor.getColumnIndex(RidesEntry.COLUMN_City_Start);
            int dateEndColumnIndex = cursor.getColumnIndex(RidesEntry.COLUMN_Date_end);
            int timeEndColumnIndex = cursor.getColumnIndex(RidesEntry.COLUMN_Time_end);
            int cityEndColumnIndex = cursor.getColumnIndex(RidesEntry.COLUMN_City_End);
            int cityWorkAreaColumnIndex = cursor.getColumnIndex(RidesEntry.COLUMN_City_Work_Area);
            int purpuseColumnIndex = cursor.getColumnIndex(RidesEntry.COLUMN_Purpose);
            int distanceCombinedColumnIndex = cursor.getColumnIndex(RidesEntry.COLUMN_Distance_Combined);
            int distanceCityColumnIndex = cursor.getColumnIndex(RidesEntry.COLUMN_Distance_City);
            int noteColumnIndex = cursor.getColumnIndex(RidesEntry.COLUMN_Note);

            while (cursor.moveToNext())
            {
                int currentID = cursor.getInt(idColumnIndex);
                String dateStart = cursor.getString(dateStartColumnIndex);
                String timeStart = cursor.getString(timeStartColumnIndex);
                String cityStart = cursor.getString(cityStartColumnIndex);
                String dateEnd = cursor.getString(dateEndColumnIndex);
                String timeEnd = cursor.getString(timeEndColumnIndex);
                String cityEnd = cursor.getString(cityEndColumnIndex);
                String cityWorkArea = cursor.getString(cityWorkAreaColumnIndex);
                String purpuse = cursor.getString(purpuseColumnIndex);
                int distanceCombinedN = Integer.parseInt(cursor.getString(distanceCombinedColumnIndex));
                int distanceCityN = Integer.parseInt(cursor.getString(distanceCityColumnIndex));
                String note = cursor.getString(noteColumnIndex);


                // Display the values from each column of the current row in the cursor in the TextView
                importTextView.append(("\n" + "->"+currentID + " - " +
                        dateStart + " - " +
                        timeStart + " - " +
                        cityStart + " - " +
                        dateEnd + " - " +
                        timeEnd + " - " +
                        cityEnd + " - " +
                        cityWorkArea + " - " +
                        purpuse + " - " +
                        distanceCombinedN + " - " +
                        distanceCityN + " - " +
                        note));
            }
        }
        finally
        {
            cursor.close();
        }
    }
}
