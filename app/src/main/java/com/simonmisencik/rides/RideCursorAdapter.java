package com.simonmisencik.rides;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.simonmisencik.rides.data.RidesContract;
import com.simonmisencik.rides.data.RidesContract.RidesEntry;

import org.w3c.dom.Text;

/**
 * Created by Simon Misencik on 15. 3. 2018.
 */

public class RideCursorAdapter extends CursorAdapter
{

    public RideCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        TextView cityTextView = (TextView) view.findViewById(R.id.city);
        TextView dateTextView = (TextView) view.findViewById(R.id.date);
        TextView distanceTextView = (TextView) view.findViewById(R.id.distance);

        int cityStartColumnIndex = cursor.getColumnIndex(RidesEntry.COLUMN_City_Start);
        int cityEndColumnIndex = cursor.getColumnIndex(RidesEntry.COLUMN_City_End);
        int cityWorkAreaColumnIndex = cursor.getColumnIndex(RidesEntry.COLUMN_City_Work_Area);
        int dateStartColumnIndex = cursor.getColumnIndex(RidesEntry.COLUMN_Date_start);
        int dateEndColumnIndex = cursor.getColumnIndex(RidesEntry.COLUMN_Date_end);
        int distanceCombinedColumnIndex = cursor.getColumnIndex(RidesEntry.COLUMN_Distance_Combined);
        int distanceCityColumnIndex = cursor.getColumnIndex(RidesEntry.COLUMN_Distance_City);

        String cityStart = cursor.getString(cityStartColumnIndex);
        String cityEnd = cursor.getString(cityEndColumnIndex);
        String cityWorkArea = cursor.getString(cityWorkAreaColumnIndex);
        String dateStart = cursor.getString(dateStartColumnIndex);
        String dateEnd = cursor.getString(dateEndColumnIndex);
        int distanceCombined = Integer.parseInt(cursor.getString(distanceCombinedColumnIndex));
        int distanceCity = Integer.parseInt(cursor.getString(distanceCityColumnIndex));

        if(cityWorkArea == null || cityWorkArea.equals(""))
        {
            cityTextView.setText(cityStart + " >> " + cityEnd);
            dateTextView.setText(dateStart);
            distanceTextView.setText((distanceCity + distanceCombined) + "km");
        }

        else
        {
            cityTextView.setText(cityStart + " >> " + cityWorkArea + " >> " + cityEnd);
            dateTextView.setText(dateStart + " -> " + dateEnd);
            distanceTextView.setText((distanceCity + distanceCombined) + "km");
        }
    }
}
