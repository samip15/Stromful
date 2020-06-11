package com.example.stromful.MainActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.stromful.Data.StromfulPrefrences;
import com.example.stromful.DetailActivity;
import com.example.stromful.ForcastAdapter;
import com.example.stromful.R;
import com.example.stromful.Utilities.NetworkUtils;
import com.example.stromful.Utilities.OpenWeatherJsonWeather;
import com.example.stromful.Utilities.StromfulWeatherUtils;
import com.github.ybq.android.spinkit.SpinKitView;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements ForcastAdapter.ForcastAdapterOnclickListner, LoaderManager.LoaderCallbacks<String[]> {
    private static final int FORCAST_LOADER_ID = 0;
    private static final String TAG = "com/example/stromful/MainActivity";
    private static final String LOCATION_QUERY = "location";
    private TextView mErrorMessageDisplay;
    private SpinKitView mLoadingindicator;
    RecyclerView mrecycler;
    private ForcastAdapter mForcastAdapter;
    private Context mContext = MainActivity.this;
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
        loadWeateerData();
        //initilizing the loader
        getSupportLoaderManager().initLoader(FORCAST_LOADER_ID,null,this);
    }

    /**
     * Making location to fetch the data from giving to loder menager with bundle
     */

    private void loadWeateerData() {
        String location = StromfulPrefrences.getPreferedWeatherLocation(this);
        Bundle queryBundle = new Bundle();
        queryBundle.putString(LOCATION_QUERY,location);
        //implementing the loader manager
        LoaderManager loaderManager = getSupportLoaderManager();
        //create a loader
        Loader<String> locationSerchLoader = loaderManager.getLoader(FORCAST_LOADER_ID);
        if (locationSerchLoader==null){
            //no data is need to be added to loader
            loaderManager.initLoader(FORCAST_LOADER_ID,queryBundle,this);
        }else{
            //new data need to be loaded to the loader
            loaderManager.restartLoader(FORCAST_LOADER_ID,queryBundle,this);
        }

    }

    private void showWeatherDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mrecycler.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mrecycler.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(String weatherfordy) {
        Intent intent = new Intent(mContext, DetailActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, weatherfordy);
        startActivity(intent);
    }

    //-------------------------Loader Manager Function---------------------------

    /**
     * Creating a async task to fetch the data
     * @param id
     * @param args
     * @return
     */

    @NonNull
    @Override
    public Loader<String[]> onCreateLoader(int id, @Nullable final Bundle args) {
        //create a async task loader
        return  new AsyncTaskLoader<String[]>(this) {
            String[] mWeatherData=null;
            @Override
            protected void onStartLoading() {
                if (args==null){
                    return;
                }
                if (mWeatherData!=null){
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
                try{
                    String jsonWeatherResponse = NetworkUtils.getResponseFromHttpUrl(weatherRequestUrl);
                    String[] weatherDataFromJson = OpenWeatherJsonWeather.getWeatherDataFromJson(MainActivity.this,jsonWeatherResponse);
                    Log.e(TAG,"The weather data is " + weatherDataFromJson.length + weatherDataFromJson);
                    return weatherDataFromJson;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    return null;
                }

            }

            @Override
            public void deliverResult(@Nullable String[] mLocationJson) {
                super.deliverResult(mLocationJson);
            }
        };
    }

    /**
     * This function is executed after data is fetched
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
     * @param loader
     */
    @Override
    public void onLoaderReset(@NonNull Loader<String[]> loader) {

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
        if (id == R.id.action_refresh) {
            mForcastAdapter.setWeatherData(null);
            loadWeateerData();
            return true;
        }
        if (id == R.id.action_map) {
            openLocationInMap();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* ----------------------------intents

     */
    private void openLocationInMap() {
        String address = StromfulPrefrences.getPreferedWeatherLocation(mContext);
        Uri geoloaction = Uri.parse("geo:0,0?q=" + address);
        Intent intent = new Intent(Intent.ACTION_VIEW, geoloaction);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
