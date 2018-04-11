package com.simonmisencik.rides;

import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SettingsCompanyActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_company);
        setTitle(R.string.edit_company_menu);
    }

    public static class CompanyPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener
    {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.company_settings_main);

            Preference company = findPreference(getString(R.string.company_settings_company_key));
            bindPreferenceSummaryToValue(company);

            Preference city = findPreference(getString(R.string.company_settings_city_key));
            bindPreferenceSummaryToValue(city);

            Preference postalCode = findPreference(getString(R.string.company_settings_postal_code_key));
            bindPreferenceSummaryToValue(postalCode);

            Preference street = findPreference(getString(R.string.company_settings_street_key));
            bindPreferenceSummaryToValue(street);

            Preference hauseNumber = findPreference(getString(R.string.company_settings_hause_number_key));
            bindPreferenceSummaryToValue(hauseNumber);

            Preference ico = findPreference(getString(R.string.company_settings_ico_key));
            bindPreferenceSummaryToValue(ico);

            Preference dic = findPreference(getString(R.string.company_settings_dic_key));
            bindPreferenceSummaryToValue(dic);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            preference.setSummary(stringValue);
            return true;
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }

    }
}
