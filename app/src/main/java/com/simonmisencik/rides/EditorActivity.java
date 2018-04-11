package com.simonmisencik.rides;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.simonmisencik.rides.data.RidesDbHelper;
import com.simonmisencik.rides.data.RidesContract.RidesEntry;

import java.util.Calendar;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>
{
    private static final int EXISTING_RIDE_LOADER = 0;
    Uri curretRideUri;
    private boolean RideHasChanged = false;

    // Variables for Date Picker
    private DatePicker datePicker;
    private Calendar calendar;
    private int year, month, day;
    private TextView endDate;
    private TextView startDate;
    private static int whichTextViewDate = -1;

    // Variables for Time Picker
    private static TextView startTime;
    private static TextView endTime;
    private static int whichTextViewTime = -1;
    private int hour, minute;

    // Variables for insert Data
    private EditText startCity;
    private EditText endCity;
    private EditText placeOfWork;
    private EditText purpuseOfTheRide;
    private EditText distanceCombined;
    private EditText distanceCity;
    private EditText notes;

    private Switch workStay;
    private LinearLayout linearLayoutPlaceOfWork;
    private LinearLayout linearLayoutDistanceCity;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        curretRideUri = intent.getData();

        Log.e("    ...         ", curretRideUri + "");
        if(curretRideUri == null)
        {
            setTitle(getString(R.string.title_activity_add_ride));
            invalidateOptionsMenu();
        }
        else
        {
            setTitle(getString(R.string.title_activity_edit_ride));
            getLoaderManager().initLoader(EXISTING_RIDE_LOADER, null, this);

        }
        // Date Picker
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        startDate = (TextView) findViewById(R.id.start_date);
        endDate = (TextView) findViewById(R.id.end_date);
        showDate(year, month + 1, day, startDate);
        showDate(year, month + 1, day, endDate);

        // Time Picker
        startTime = (TextView) findViewById(R.id.start_time);
        endTime = (TextView) findViewById(R.id.end_time);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        showTime(hour, minute, startTime);
        showTime(hour + 1, minute, endTime);

        // Insert Data
        startCity = (EditText) findViewById(R.id.start_city);
        endCity = (EditText) findViewById(R.id.end_city);
        placeOfWork = (EditText) findViewById(R.id.place_of_work);
        purpuseOfTheRide = (EditText) findViewById(R.id.purpuse_of_the_ride);
        distanceCombined = (EditText) findViewById(R.id.distance_combined);
        distanceCity = (EditText) findViewById(R.id.distance_city);
        notes = (EditText) findViewById(R.id.notes);

        workStay = (Switch) findViewById(R.id.work_stay);
        linearLayoutPlaceOfWork = (LinearLayout) findViewById(R.id.linear_layout_place_of_work);
        linearLayoutDistanceCity = (LinearLayout) findViewById(R.id.linear_layout_distance_city);

        purpuseOfTheRide.setText("Pracovná cesta");

        // On Click Listener for WorkStay switch
        workStay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                if(b)
                {
                    linearLayoutPlaceOfWork.setVisibility(View.VISIBLE);
                    linearLayoutDistanceCity.setVisibility(View.VISIBLE);
                    endDate.setVisibility(View.VISIBLE);
                }
                else
                {
                    linearLayoutPlaceOfWork.setVisibility(View.GONE);
                    linearLayoutDistanceCity.setVisibility(View.GONE);
                    endDate.setVisibility(View.INVISIBLE);
                }
            }
        });

        // On Click Listener for Date
        // - Date start
        // - Date end

        startDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                whichTextViewDate = 0;
                setDate(v);
            }
        });

        endDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                whichTextViewDate = 1;
                setDate(v);
            }
        });

        // On Click Listener for Time
        // - Time start
        // - Time end

        startTime.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                whichTextViewTime = 0;
                showTimePickerDialog(v);
            }
        });

        endTime.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                whichTextViewTime = 1;
                showTimePickerDialog(v);
            }
        });

        startDate.setOnTouchListener(TouchListener);
        startTime.setOnTouchListener(TouchListener);
        startCity.setOnTouchListener(TouchListener);
        endDate.setOnTouchListener(TouchListener);
        endTime.setOnTouchListener(TouchListener);
        endCity.setOnTouchListener(TouchListener);
        placeOfWork.setOnTouchListener(TouchListener);
        purpuseOfTheRide.setOnTouchListener(TouchListener);
        distanceCombined.setOnTouchListener(TouchListener);
        distanceCity.setOnTouchListener(TouchListener);
        notes.setOnTouchListener(TouchListener);

    }

    private void saveRide()
    {
        //Read from input fields
        String DateStart = startDate.getText().toString().trim();
        String TimeStart = startTime.getText().toString().trim();
        String CityStart = startCity.getText().toString().trim();
        String DateEnd = endDate.getText().toString().trim();
        String TimeEnd = endTime.getText().toString().trim();
        String CityEnd = endCity.getText().toString().trim();
        String PlaceOfWork = placeOfWork.getText().toString().trim();
        String PurpuseOfTheRide = purpuseOfTheRide.getText().toString().trim();
        int Distance_Combined = 0;
        int Distance_City = 0;
        if(!(distanceCombined.getText().toString().trim().equals("")))
            Distance_Combined = Integer.parseInt(distanceCombined.getText().toString().trim());
        if(!(distanceCity.getText().toString().trim().equals("")))
            Distance_City = Integer.parseInt(distanceCity.getText().toString().trim());
        String Notes = notes.getText().toString().trim();


        if ((curretRideUri == null && TextUtils.isEmpty(CityStart)) || (curretRideUri == null &&TextUtils.isEmpty(CityEnd)))
        {
            Toast.makeText(this, getString(R.string.error_with_add_ride), Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(RidesEntry.COLUMN_Date_start, DateStart);
        values.put(RidesEntry.COLUMN_Time_start, TimeStart);
        values.put(RidesEntry.COLUMN_City_Start, CityStart);
        values.put(RidesEntry.COLUMN_Date_end, DateEnd);
        values.put(RidesEntry.COLUMN_Time_end, TimeEnd);
        values.put(RidesEntry.COLUMN_City_End, CityEnd);
        values.put(RidesEntry.COLUMN_City_Work_Area, PlaceOfWork);
        values.put(RidesEntry.COLUMN_Purpose, PurpuseOfTheRide);
        values.put(RidesEntry.COLUMN_Distance_Combined, Distance_Combined);
        values.put(RidesEntry.COLUMN_Distance_City, Distance_City);
        values.put(RidesEntry.COLUMN_Note, Notes);


        if (curretRideUri == null)
        {
            Uri newUri = getContentResolver().insert(RidesEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful
            if (newUri == null)
            {
                // If the row ID is -1, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_ride_failed), Toast.LENGTH_SHORT).show();
            }
            else
            {
                // Otherwise, the insertion was successful and we can display a toast with the row ID.
                Toast.makeText(this, getString(R.string.editor_insert_ride_successful), Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            int rowsAffected = getContentResolver().update(curretRideUri, values, null, null);

            if (rowsAffected == 0)
            {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_ride_failed), Toast.LENGTH_SHORT).show();
            }
            else
            {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_ride_successful), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_save:
                saveRide();
                finish();
                return true;
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if(!RideHasChanged)
                {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        // User clicked "Discard" button, navigate to parent activity.
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };
                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
        * This method is called when the back button is pressed.
    */
    @Override
    public void onBackPressed()
    {
        // If the pet hasn't changed, continue with handling back button press
        if (!RideHasChanged)
        {
            super.onBackPressed();
            return;
        }
        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                // User clicked "Discard" button, close the current activity.
                finish();
            }
        };
        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showDate(int year, int month, int day, TextView textView)
    {
        textView.setText(new StringBuilder().append(day).append(".").append(month).append(".").append(year));
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view)
    {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id)
    {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener()
    {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day
            if(whichTextViewDate == 0)
            {
                showDate(arg1, arg2+1, arg3, startDate);
                showDate(arg1, arg2+1, arg3, endDate);
            }
            if(whichTextViewDate == 1)
                showDate(arg1, arg2+1, arg3, endDate);
        }
    };

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
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


        return new CursorLoader(this, curretRideUri, projection, null, null, RidesEntry._ID + " DESC");

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        if(data == null || data.getCount() < 1)
            return;

        if (data.moveToFirst())
        {

            int dateStartColumnIndex = data.getColumnIndex(RidesEntry.COLUMN_Date_start);
            int timeStartColumnIndex = data.getColumnIndex(RidesEntry.COLUMN_Time_start);
            int cityStartColumnIndex = data.getColumnIndex(RidesEntry.COLUMN_City_Start);
            int dateEndColumnIndex = data.getColumnIndex(RidesEntry.COLUMN_Date_end);
            int timeEndColumnIndex = data.getColumnIndex(RidesEntry.COLUMN_Time_end);
            int cityEndColumnIndex = data.getColumnIndex(RidesEntry.COLUMN_City_End);
            int cityWorkAreaColumnIndex = data.getColumnIndex(RidesEntry.COLUMN_City_Work_Area);
            int purpuseColumnIndex = data.getColumnIndex(RidesEntry.COLUMN_Purpose);
            int distanceCombinedColumnIndex = data.getColumnIndex(RidesEntry.COLUMN_Distance_Combined);
            int distanceCityColumnIndex = data.getColumnIndex(RidesEntry.COLUMN_Distance_City);
            int noteColumnIndex = data.getColumnIndex(RidesEntry.COLUMN_Note);

            String dateStart = data.getString(dateStartColumnIndex);
            String timeStart = data.getString(timeStartColumnIndex);
            String cityStart = data.getString(cityStartColumnIndex);
            String dateEnd = data.getString(dateEndColumnIndex);
            String timeEnd = data.getString(timeEndColumnIndex);
            String cityEnd = data.getString(cityEndColumnIndex);
            String cityWorkArea = data.getString(cityWorkAreaColumnIndex);
            String purpuse = data.getString(purpuseColumnIndex);
            int distanceCombinedN = Integer.parseInt(data.getString(distanceCombinedColumnIndex));
            int distanceCityN = Integer.parseInt(data.getString(distanceCityColumnIndex));
            String note = data.getString(noteColumnIndex);

            if(!cityWorkArea.equals(""))
            {
                workStay.setChecked(true);
            }

            startDate.setText(dateStart);
            startTime.setText(timeStart);
            startCity.setText(cityStart);
            endDate.setText(dateEnd);
            endTime.setText(timeEnd);
            endCity.setText(cityEnd);
            placeOfWork.setText(cityWorkArea);
            purpuseOfTheRide.setText(purpuse);
            distanceCombined.setText(distanceCombinedN + "");
            distanceCity.setText(distanceCityN + "");
            notes.setText(note);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        startDate.setText("");
        startTime.setText("");
        startCity.setText("");
        endDate.setText("");
        endTime.setText("");
        endCity.setText("");
        placeOfWork.setText("");
        purpuseOfTheRide.setText("");
        distanceCombined.setText("");
        distanceCity.setText("");
        notes.setText("");
    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener
    {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute)
        {
            if(whichTextViewTime == 0)
            {
                showTime(hourOfDay, minute, startTime);
                showTime(hourOfDay, minute, endTime);
            }
            if (whichTextViewTime == 1)
                showTime(hourOfDay, minute, endTime);
        }
    }

    public void showTimePickerDialog(View v)
    {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    private static void showTime(int hour, int minute, TextView textView)
    {
        if(minute < 10)
            textView.setText(new StringBuilder().append(hour).append(":").append(minute).append("0"));
        else
            textView.setText(new StringBuilder().append(hour).append(":").append(minute));
    }

    // OnTouchListener that listens for any user touches on a View, implying that they are modifying
    // the view, and we change the mPetHasChanged boolean to true.

    private View.OnTouchListener TouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            RideHasChanged = true;
            return false;
        }
    };

    /**
        * Show a dialog that warns the user there are unsaved changes that will be lost
        * if they continue leaving the editor.
        *
        * @param discardButtonClickListener is the click listener for what to do when
        *                                   the user confirms they want to discard their changes
    **/
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener)
    {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null)
                {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (curretRideUri == null)
        {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    private void showDeleteConfirmationDialog()
    {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                // User clicked the "Delete" button, so delete the pet.
                deleteRide();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null)
                {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deleteRide()
    {
        if (curretRideUri != null)
        {
            // Call the ContentResolver to delete the pet at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the pet that we want.
            int rowsDeleted = getContentResolver().delete(curretRideUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_pet_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_pet_successful),
                        Toast.LENGTH_SHORT).show();
            }

            finish();
        }
    }

}