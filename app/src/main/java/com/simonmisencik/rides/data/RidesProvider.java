package com.simonmisencik.rides.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.simonmisencik.rides.data.RidesContract.RidesEntry;

/**
 * Created by Simon Misencik on 14. 3. 2018.
 */

public class RidesProvider extends ContentProvider
{
    public static final String LOG_TAG = RidesProvider.class.getSimpleName();
    private RidesDbHelper ridesDbHelper;

    private static final int RIDES = 100;
    private static final int RIDE_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static
    {
        sUriMatcher.addURI(RidesContract.CONTENT_AUTHORITY, RidesContract.PATH_RIDES, RIDES);
        sUriMatcher.addURI(RidesContract.CONTENT_AUTHORITY, RidesContract.PATH_RIDES + "/#", RIDE_ID);
    }

    @Override
    public boolean onCreate()
    {
        ridesDbHelper = new RidesDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder)
    {
        // Get readable database
        SQLiteDatabase database = ridesDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match)
        {
            case RIDES:
                cursor = database.query(RidesEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case RIDE_ID:
                selection = RidesEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(RidesEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri)
    {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case RIDES:
                return RidesEntry.CONTENT_LIST_TYPE;
            case RIDE_ID:
                return RidesEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values)
    {
        // Check that the name is not null
        String cityStart = values.getAsString(RidesEntry.COLUMN_City_Start);
        if (cityStart == null || cityStart.equals(""))
        {
            throw new IllegalArgumentException("Ride requires a Start City");
        }

        String cityEnd = values.getAsString(RidesEntry.COLUMN_City_End);
        if(cityEnd == null || cityEnd.equals(""))
        {
            throw new IllegalArgumentException("Ride requires a End City");
        }

        Integer distanceCombined = values.getAsInteger(RidesEntry.COLUMN_Distance_Combined);
        if (distanceCombined == null || distanceCombined < 0)
        {
            throw new IllegalArgumentException("Ride requires valid distance");
        }

        final int match = sUriMatcher.match(uri);
        switch (match)
        {
            case RIDES:
                return insertRide(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertRide(Uri uri, ContentValues values)
    {
        SQLiteDatabase db = ridesDbHelper.getWritableDatabase();
        long id = db.insert(RidesEntry.TABLE_NAME, null, values);
        if (id == -1)
        {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs)
    {
        int rowsDeleted;

        // Get writeable database
        SQLiteDatabase database = ridesDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case RIDES:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(RidesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case RIDE_ID:
                // Delete a single row given by the ID in the URI
                selection = RidesEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(RidesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs)
    {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case RIDES:
                return updateRide(uri, values, selection, selectionArgs);
            case RIDE_ID:
                // For the RIDE_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = RidesEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateRide(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }



    }

    private int updateRide(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        String cityStart = values.getAsString(RidesEntry.COLUMN_City_Start);
        if (cityStart == null || cityStart.equals(""))
        {
            throw new IllegalArgumentException("Ride requires a Start City");
        }

        String cityEnd = values.getAsString(RidesEntry.COLUMN_City_End);
        if(cityEnd == null || cityEnd.equals(""))
        {
            throw new IllegalArgumentException("Ride requires a End City");
        }

        Integer distanceCombined = values.getAsInteger(RidesEntry.COLUMN_Distance_Combined);
        if (distanceCombined == null || distanceCombined < 0)
        {
            throw new IllegalArgumentException("Ride requires valid distance");
        }

        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = ridesDbHelper.getWritableDatabase();

        // Returns the number of database rows affected by the update statement
        int rowsUpdated = database.update(RidesEntry.TABLE_NAME, values, selection, selectionArgs);

        if(rowsUpdated != 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}
