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

public class CompanyActivity extends AppCompatActivity
{

    private TextView company;
    private TextView address;
    private TextView ico;
    private TextView dic;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);
        setTitle(R.string.company_settings_title);

        company = (TextView) findViewById(R.id.company_text_view);
        address = (TextView) findViewById(R.id.address_text_view);
        ico = (TextView) findViewById(R.id.ico_text_view);
        dic = (TextView) findViewById(R.id.dic_text_view);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String companyString = sharedPrefs.getString(getString(R.string.company_settings_company_key), getString(R.string.company_settings_company_default));
        String cityString = sharedPrefs.getString(getString(R.string.company_settings_city_key), getString(R.string.company_settings_city_default));
        String postalCodeString = sharedPrefs.getString(getString(R.string.company_settings_postal_code_key), getString(R.string.company_settings_postal_code_default));
        String streetString = sharedPrefs.getString(getString(R.string.company_settings_street_key), getString(R.string.company_settings_street_default));
        String hauseNumberString = sharedPrefs.getString(getString(R.string.company_settings_hause_number_key), getString(R.string.company_settings_hause_number_default));
        String icoString = sharedPrefs.getString(getString(R.string.company_settings_ico_key), getString(R.string.company_settings_ico_default));
        String dicString = sharedPrefs.getString(getString(R.string.company_settings_dic_key), getString(R.string.company_settings_dic_default));

        company.setText(companyString);
        address.setText(streetString + " " + hauseNumberString + ", " + postalCodeString + " " + cityString);
        ico.setText(getString(R.string.company_settings_ico) + ": " + icoString);
        dic.setText(getString(R.string.company_settings_dic) + ": " + dicString);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.company_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.edit_company)
        {
            Intent settingsIntent = new Intent(this, SettingsCompanyActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
