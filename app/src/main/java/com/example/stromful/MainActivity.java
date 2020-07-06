package com.example.stromful;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.stromful.Data.StromfulPrefrences;
import com.example.stromful.Utilities.NetworkUtils;
import com.example.stromful.Utilities.OpenWeatherJsonWeather;
import com.github.ybq.android.spinkit.SpinKitView;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements ForcastAdapter.ForcastAdapterOnclickListner, LoaderManager.LoaderCallbacks<String[]>, SharedPreferences.OnSharedPreferenceChangeListener {
    private static final int FORCAST_LOADER_ID = 0;
    private static final String TAG = "com/example/stromful/MainActivity";
    private static final String LOCATION_QUERY = "location";
    private TextView mErrorMessageDisplay;
    private SpinKitView mLoadingindicator;
    RecyclerView mrecycler;
    private ForcastAdapter mForcastAdapter;
    private Context mContext = MainActivity.this;
    // if shared preference has been changed
    private static boolean PREFRENCE_UPDATED = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forcast);
        mrecycler = findViewById(R.id.recycler_view_forcast);
        mErrorMessageDisplay = findViewById(R.id.tv_error_message);
        mLoadingindicator = findViewById(R.id.progress_bar_laoding_indicator);
        //set rv
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mrecycler.setLayoutManager(linearLayoutManager);
        mrecycler.setHasFixedSize(true);
        //adapter
        mForcastAdapter = new ForcastAdapter(this);
        mrecycler.setAdapter(mForcastAdapter);

        //initilizing the loader
        getSupportLoaderManager().initLoader(FORCAST_LOADER_ID, null, this);
        // resister preference
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    /*
     *  ----------------------  Loading Functions --------------------------
     * */
    private void showWeatherDataView() {
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
     * @param id
     * @param args
     * @return
     */

    @NonNull
    @Override
    public Loader<String[]> onCreateLoader(int id, @Nullable final Bundle args) {
        //create a async task loader
        return new AsyncTaskLoader<String[]>(this) {
            String[] mWeatherData = null;

            @Override
            protected void onStartLoading() {
                //-----Checking If We Have Cache Data-----------
                if (mWeatherData != null) {
                    deliverResult(mWeatherData);
                }
                mLoadingindicator.setVisibility(View.VISIBLE);
                // triggers the load in background function to load data
                forceLoad();
            }

            @SuppressLint("LongLogTag")
            @Nullable
            @Override
            public String[] loadInBackground() {

                String location = StromfulPrefrences.getPreferedWeatherLocation(mContext);
                URL weatherRequestUrl = NetworkUtils.buildUrl(location);
                try {
                    String jsonWeatherResponse = NetworkUtils.getResponseFromHttpUrl(weatherRequestUrl);
                    String[] weatherDataFromJson = OpenWeatherJsonWeather.getWeatherDataFromJson(MainActivity.this, jsonWeatherResponse);
                    Log.e(TAG, "The weather data is " + weatherDataFromJson.length + weatherDataFromJson);
                    return weatherDataFromJson;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

            }

            @Override
            public void deliverResult(@Nullable String[] mLocationJson) {
                mWeatherData = mLocationJson;
                super.deliverResult(mLocationJson);
            }
        };
    }

    /**
     * This function is executed after data is fetched
     *
     * @param loader
     * @param weatherdata
     */

    @Override
    public void onLoadFinished(@NonNull Loader<String[]> loader, String[] weatherdata) {

        mLoadingindicator.setVisibility(View.INVISIBLE);
        if (weatherdata != null) {
            showWeatherDataView();
            mForcastAdapter.setWeatherData(weatherdata);

        } else {
            showErrorMessage();
        }
    }

    /**
     * This function Helps to reset the loader
     *
     * @param loader
     */
    @Override
    public void onLoaderReset(@NonNull Loader<String[]> loader) {

    }


    /* --------------------------- menue---------------------
     * */

    private void invalidateData() {
        mForcastAdapter.setWeatherData(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.forcast, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            invalidateData();
            getSupportLoaderManager().restartLoader(FORCAST_LOADER_ID, null, this);
            return true;
        }
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
    public void onClick(String weatherForDay) {
        Intent intent = new Intent(mContext, DetailActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, weatherForDay);
        startActivity(intent);
    }


    /* ----------------------------intents------------------------------------

     */
    private void openLocationInMap() {
        String address = StromfulPrefrences.getPreferedWeatherLocation(mContext);
        Uri geoloaction = Uri.parse("geo:0,0?q=" + address);
        Intent intent = new Intent(Intent.ACTION_VIEW, geoloaction);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        PREFRENCE_UPDATED = true;

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (PREFRENCE_UPDATED) {
            getSupportLoaderManager().restartLoader(FORCAST_LOADER_ID, null, this);
            PREFRENCE_UPDATED = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }
}
