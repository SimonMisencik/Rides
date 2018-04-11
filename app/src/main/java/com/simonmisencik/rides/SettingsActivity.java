package com.simonmisencik.rides;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(R.string.nav_settings);

        TextView textView = (TextView) findViewById(R.id.text_view_settings);
        textView.setText(R.string.in_develop);
    }
}
