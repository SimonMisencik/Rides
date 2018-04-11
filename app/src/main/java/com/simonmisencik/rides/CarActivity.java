package com.simonmisencik.rides;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.w3c.dom.Text;

public class CarActivity extends AppCompatActivity
{
    private TextView carName;
    private TextView licensePlateNumberTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);

        setTitle(R.string.car_activity_title);
        carName = (TextView) findViewById(R.id.car_name);
        licensePlateNumberTextView = (TextView) findViewById(R.id.license_plate_number);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String brand = sharedPrefs.getString(getString(R.string.car_settings_brand_key), getString(R.string.car_settings_brand_default));
        String bussinesName = sharedPrefs.getString(getString(R.string.car_settings_business_name_key), getString(R.string.car_settings_business_name_default));
        String licensePlateNumber = sharedPrefs.getString(getString(R.string.car_settings_license_plate_number_key), getString(R.string.car_settings_license_plate_number_default));

        carName.setText(brand + " " + bussinesName);
        licensePlateNumberTextView.setText(licensePlateNumber);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.car_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.edit_car)
        {
            Intent settingsIntent = new Intent(this, SettingsCarActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
