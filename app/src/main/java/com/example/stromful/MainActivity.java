package com.example.stromful;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.stromful.Data.StromfulPrefrences;
import com.example.stromful.Data.WeatherContract;
import com.example.stromful.Utilities.FakeDataUtils;
import com.example.stromful.Utilities.NetworkUtils;
import com.example.stromful.Utilities.OpenWeatherJsonUtils;
import com.github.ybq.android.spinkit.SpinKitView;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements ForcastAdapter.ForcastAdapterOnclickListner, LoaderManager.LoaderCallbacks<Cursor> {
    private static final int FORCAST_LOADER_ID = 0;
    // recycler view
    private int mPosition = RecyclerView.NO_POSITION;
    private static final String TAG = "com/example/stromful/MainActivity";
    private static final String LOCATION_QUERY = "location";
    private TextView mErrorMessageDisplay;
    private SpinKitView mLoadingindicator;
    RecyclerView mrecycler;
    private ForcastAdapter mForcastAdapter;
    private Context mContext = MainActivity.this;
    // if shared preference has been changed
    // weather columns that are displayed and queried
    public static final String[] MAIN_FORCAST_PROJECTION = {
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
    };
    // weather table ko column ko indexes
    public static final int INDEX_WEATHER_DATE = 0;
    public static final int INDEX_WEATHER_MAX_TEMP = 1;
    public static final int INDEX_WEATHER_MIN_TEMP = 2;
    public static final int INDEX_WEATHER_CONDITION_ID = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forcast);
        mrecycler = findViewById(R.id.recycler_view_forcast);
        mErrorMessageDisplay = findViewById(R.id.tv_error_message);
        mLoadingindicator = findViewById(R.id.progress_bar_laoding_indicator);
        FakeDataUtils.insertFakeData(this);
        //set rv
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mrecycler.setLayoutManager(linearLayoutManager);
        mrecycler.setHasFixedSize(true);
        //adapter
        mForcastAdapter = new ForcastAdapter(this, this);
        mrecycler.setAdapter(mForcastAdapter);
        // loading indicator
        showLoading();
        //initilizing the loader
        getSupportLoaderManager().initLoader(FORCAST_LOADER_ID, null, this);
    }

    /**
     * -----------------------LOADING UI-----------------------
     */

    private void showLoading() {
        mrecycler.setVisibility(View.INVISIBLE);
        mLoadingindicator.setVisibility(View.VISIBLE);
    }

    /*
     *  ----------------------  Loading Functions --------------------------
     * */
    private void showWeatherDataView() {
        mLoadingindicator.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mrecycler.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mrecycler.setVisibility(View.INVISIBLE);
    }


    //-------------------------Loader Manager Function---------------------------

    /**
     * Creating a async task to fetch the data
     *
     * @param args
     * @return
     */

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, @Nullable final Bundle args) {
        switch (loaderId) {
            case FORCAST_LOADER_ID:
                // uri
                Uri forcastQueryUri = WeatherContract.WeatherEntry.CONTENT_URI;
                String sortOder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
                // selection: from today onwards
                String selection = WeatherContract.WeatherEntry.getSqlSelectForTodayOnwards();

                return new CursorLoader(this,
                        forcastQueryUri,
                        MAIN_FORCAST_PROJECTION,
                        selection,
                        null,
                        sortOder);
            default:
                throw new RuntimeException("Loader Not Implemented" + loaderId);
        }
    }

    /**
     * This function is executed after data is fetched
     *
     * @param loader
     * @param weatherdata
     */

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor weatherdata) {
        mForcastAdapter.swapCursor(weatherdata);
        if (mPosition==RecyclerView.NO_POSITION){
            mPosition = 0;
        }
        if (weatherdata.getCount() != 0) {
            showWeatherDataView();
        }

    }

    /**
     * This function Helps to reset the loader
     *
     * @param loader
     */
    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mForcastAdapter.swapCursor(null);
    }


    /* --------------------------- menue---------------------
     * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.forcast, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_map) {
            openLocationInMap();
            return true;
        }

        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
     *  ----------------------  Recyclerview OnClick --------------------------
     * */
    @Override
    public void onClick(long dateInMillis) {
        Intent intent = new Intent(mContext, DetailActivity.class);
        // uri
        Uri uriforDetail = WeatherContract.WeatherEntry.buildWeatherUriWithDate(dateInMillis);
        intent.setData(uriforDetail);
        startActivity(intent);
    }


    /* ----------------------------intents------------------------------------

     */
    private void openLocationInMap() {
        double[] coords = StromfulPrefrences.getLocationCoordinate(mContext);
        String posLat = Double.toHexString(coords[0]);
        String posLon = Double.toHexString(coords[1]);
        Uri geoloaction = Uri.parse("geo:" + posLat + "," + posLon);
        Intent intent = new Intent(Intent.ACTION_VIEW, geoloaction);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
