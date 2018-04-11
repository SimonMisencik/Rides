package com.simonmisencik.rides.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Simon Misencik on 7. 3. 2018.
 */

public final class RidesContract
{
    public static final String CONTENT_AUTHORITY = "com.simonmisencik.rides";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_RIDES = "rides";

    private RidesContract() {}

    public static final class RidesEntry implements BaseColumns
    {
        public final static String TABLE_NAME = "rides";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_Date_start = "date_start";
        public final static String COLUMN_Time_start = "time_start";
        public final static String COLUMN_Date_end = "date_end";
        public final static String COLUMN_Time_end = "time_end";
        public final static String COLUMN_City_Start = "city_start";
        public final static String COLUMN_City_Work_Area = "city_work_area";
        public final static String COLUMN_City_End = "city_end";
        public final static String COLUMN_Purpose = "purpose";
        public final static String COLUMN_Tachometer = "tachometer";
        public final static String COLUMN_Distance_Combined = "distance_combined";
        public final static String COLUMN_Distance_City = "distance_city";
        public final static String COLUMN_Note = "notes";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_RIDES);

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RIDES;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RIDES;
    }

}
