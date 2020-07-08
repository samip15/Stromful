package com.example.stromful;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.stromful.Data.WeatherContract;
import com.example.stromful.Utilities.StromfulDateUtils;
import com.example.stromful.Utilities.StromfulWeatherUtils;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "DetailActivity";
    private static final String HAS_TAG = "#stromful";
    String mForcastSummary;

    //views
    private TextView mDateView;
    private TextView mDescriptionView;
    private TextView mHighTempView;
    private TextView mLowTempView;
    private TextView mHumidityView;
    private TextView mWindView;
    private TextView mPressureView;

    // cursor and cursor provider
    private static final int ID_DETAIL_LOADER = 10;
    //uri
    private Uri mUri;

    //columns
    public static final String[] WEATHER_DETAIL_PROJECTION = {
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREE,
    };


    // weather table column ko indexes
    public static final int INDEX_WEATHER_DATE = 0;
    public static final int INDEX_WEATHER_MAX_TEMP = 1;
    public static final int INDEX_WEATHER_MIN_TEMP = 2;
    public static final int INDEX_WEATHER_CONDITION_ID = 3;
    public static final int INDEX_WEATHER_HUMIDITY = 4;
    public static final int INDEX_WEATHER_PRESSURE = 5;
    public static final int INDEX_WEATHER_WIND_SPEED = 6;
    public static final int INDEX_WEATHER_DEGREES = 7;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mDateView = findViewById(R.id.date);
        mDescriptionView = findViewById(R.id.weather_description);
        mHighTempView = findViewById(R.id.high_temperature);
        mLowTempView = findViewById(R.id.low_temperature);
        mHumidityView = findViewById(R.id.humidity);
        mWindView = findViewById(R.id.wind);
        mPressureView = findViewById(R.id.pressure);

        //getting intent
       mUri = getIntent().getData();
       if (mUri==null){
           throw new NullPointerException("Uri for detail activity cannot be null");
       }
       getSupportLoaderManager().initLoader(ID_DETAIL_LOADER,null,this);

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
                .setText(mForcastSummary + HAS_TAG)
                .getIntent();
        return shareintent;
    }

    /* --------------------------Cursor Loader-----------------------------
     */

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case ID_DETAIL_LOADER:
                return new CursorLoader(this,
                        mUri,
                        WEATHER_DETAIL_PROJECTION,
                        null,
                        null,
                        null
                );
            default:
                throw new RuntimeException("Loader Not Implemented" + id);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

        // validating cursor data
        boolean cursorHasValidData = false;
        if (data != null && data.moveToFirst()) {
            cursorHasValidData = true;
        }
        if (!cursorHasValidData) {
            return;
        }
        // get data from cursor using index
        // date
        long localDate = data.getLong(INDEX_WEATHER_DATE);
        String dateText = StromfulDateUtils.getFriendlyDateString(this, localDate, true);
        mDateView.setText(dateText);
        // weather id
        int weatherId = data.getInt(INDEX_WEATHER_CONDITION_ID);
        String description = StromfulWeatherUtils.getStringForWeatherCondition(this, weatherId);
        mDescriptionView.setText(description);
        // max temperature
        double highTemp = data.getDouble(INDEX_WEATHER_MAX_TEMP);
        String highString = StromfulWeatherUtils.formatTemperature(this,highTemp);
        mHighTempView.setText(highString);

        // min temperature
        double minTemp = data.getDouble(INDEX_WEATHER_MIN_TEMP);
        String lowString = StromfulWeatherUtils.formatTemperature(this,minTemp);
        mHighTempView.setText(lowString);
        //  humidity
        float humidity = data.getFloat(INDEX_WEATHER_HUMIDITY);
        String humidityString = getString(R.string.format_humidity,humidity);
        mHumidityView.setText(humidityString);
        // pressure
        float pressure = data.getFloat(INDEX_WEATHER_PRESSURE);
        String pressureString = getString(R.string.format_pressure,pressure);
        mPressureView.setText(pressureString);
        // wind
        float windSpeed = data.getFloat(INDEX_WEATHER_WIND_SPEED);
        float windDirection = data.getFloat(INDEX_WEATHER_DEGREES);
        String windString = StromfulWeatherUtils.getFormattedWind(this,windSpeed,windDirection);
        mWindView.setText(windString);
        // summary for sharing intent
        mForcastSummary = String.format("%s- %s- %s/%s",dateText,description,highString,lowString);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}