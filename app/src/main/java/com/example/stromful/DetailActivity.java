package com.example.stromful;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = "DetailActivity";
    private static final String HAS_TAG = "#stromful";
    String mForcast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        TextView mWeatherDisplay = findViewById(R.id.tv_display_weather);

        //getting intent
        Intent fromForcast = getIntent();
        if (fromForcast != null) {
            if (fromForcast.hasExtra(Intent.EXTRA_TEXT)) {
                mForcast = fromForcast.getStringExtra(Intent.EXTRA_TEXT);
                mWeatherDisplay.setText(mForcast);
            }
        }

    }

    /* --------------------------Menu-----------------------------
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        menuItem.setIntent(createForcastIntent());
        return true;
    }

    private Intent createForcastIntent() {
        Intent shareintent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(mForcast + HAS_TAG)
                .getIntent();
        return shareintent;
    }
}