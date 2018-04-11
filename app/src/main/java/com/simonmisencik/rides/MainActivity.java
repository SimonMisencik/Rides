package com.simonmisencik.rides;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.simonmisencik.rides.data.*;
import com.simonmisencik.rides.data.RidesContract.RidesEntry;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor>
{

    private static final int RIDE_LOADER = 0;
    RideCursorAdapter cursorAdapter;

    protected void setCompany(int view)
    {

    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        View headerView = navigationView.getHeaderView(0);

        TextView company = (TextView) headerView.findViewById(R.id.textView_company_name);
        TextView companyAddress = (TextView) headerView.findViewById(R.id.textView_company_address);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String companyString = sharedPrefs.getString(getString(R.string.company_settings_company_key), getString(R.string.company_settings_company_default));
        String cityString = sharedPrefs.getString(getString(R.string.company_settings_city_key), getString(R.string.company_settings_city_default));
        String postalCodeString = sharedPrefs.getString(getString(R.string.company_settings_postal_code_key), getString(R.string.company_settings_postal_code_default));
        String streetString = sharedPrefs.getString(getString(R.string.company_settings_street_key), getString(R.string.company_settings_street_default));
        String hauseNumberString = sharedPrefs.getString(getString(R.string.company_settings_hause_number_key), getString(R.string.company_settings_hause_number_default));
        String icoString = sharedPrefs.getString(getString(R.string.company_settings_ico_key), getString(R.string.company_settings_ico_default));
        String dicString = sharedPrefs.getString(getString(R.string.company_settings_dic_key), getString(R.string.company_settings_dic_default));

        company.setText(companyString);

        if(cityString.equals(streetString))
            companyAddress.setText(streetString + " " + hauseNumberString + ", " + postalCodeString);
        else
            companyAddress.setText(streetString + " " + hauseNumberString + ", " + postalCodeString + " " + cityString);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        ListView rideListView = (ListView) findViewById(R.id.list);

        View emptyView = (View) findViewById(R.id.empty_view);
        rideListView.setEmptyView(emptyView);

        rideListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);

                Uri currentRideUri = ContentUris.withAppendedId(RidesEntry.CONTENT_URI, id);
                intent.setData(currentRideUri);
                startActivity(intent);
            }
        });

        cursorAdapter = new RideCursorAdapter(this, null);
        rideListView.setAdapter(cursorAdapter);
        getLoaderManager().initLoader(RIDE_LOADER, null, this);
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void insertData()
    {
        ContentValues values = new ContentValues();
        values.put(RidesEntry.COLUMN_Date_start, "14.07.1998");
        values.put(RidesEntry.COLUMN_Date_end, "14.07.1998");
        values.put(RidesEntry.COLUMN_City_Start, "Šarišské Dravce");
        values.put(RidesEntry.COLUMN_City_End, "Bratislava");
        values.put(RidesEntry.COLUMN_Purpose, "Pracovná cesta");
        values.put(RidesEntry.COLUMN_Distance_Combined, 410);

        Uri uri = getContentResolver().insert(RidesEntry.CONTENT_URI, values);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId())
        {
            /*
            case R.id.insert_dummy_data:
                insertData();
                return true;
            */
            case R.id.action_delete_all_entries:
                showDeleteConfirmationDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_rides)
        {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_car)
        {
            Intent intent = new Intent(getApplicationContext(), CarActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_company)
        {
            Intent intent = new Intent(getApplicationContext(), CompanyActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_import_export)
        {
            Intent intent = new Intent(getApplicationContext(), ImportActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_settings)
        {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_feedback)
        {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:simon.misencik@gmail.com")); // only email apps should handle this
            intent.putExtra(Intent.EXTRA_SUBJECT, "Spätná väzba: Aplikácia Jazdy");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        String[] projection =
                {
                        RidesEntry._ID,
                        RidesEntry.COLUMN_City_Start,
                        RidesEntry.COLUMN_City_End,
                        RidesEntry.COLUMN_City_Work_Area,
                        RidesEntry.COLUMN_Date_start,
                        RidesEntry.COLUMN_Date_end,
                        RidesEntry.COLUMN_Distance_Combined,
                        RidesEntry.COLUMN_Distance_City
                };

        return new CursorLoader(this, RidesEntry.CONTENT_URI, projection, null, null, RidesEntry._ID + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        cursorAdapter.swapCursor(null);
    }

    /**
     * Helper method to delete all pets in the database.
     */
    private void deleteAllRides()
    {
        int rowsDeleted = getContentResolver().delete(RidesEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");
        Toast.makeText(this, rowsDeleted + getString(R.string.all_rows_deleted), Toast.LENGTH_SHORT);
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
                deleteAllRides();
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
}
