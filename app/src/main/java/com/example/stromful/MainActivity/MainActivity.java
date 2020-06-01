package com.example.stromful.MainActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stromful.Data.StromfulPrefrences;
import com.example.stromful.ForcastAdapter;
import com.example.stromful.R;
import com.example.stromful.Utilities.NetworkUtils;
import com.example.stromful.Utilities.OpenWeatherJsonWeather;
import com.github.ybq.android.spinkit.SpinKitView;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements ForcastAdapter.ForcastAdapterOnclickListner {
    private static final String TAG = "com/example/stromful/MainActivity";
    private TextView mErrorMessageDisplay;
    private SpinKitView mLoadingindicator;
    RecyclerView mrecycler;
    private ForcastAdapter mForcastAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forcast);
        mrecycler = findViewById(R.id.recycler_view_forcast);
        mErrorMessageDisplay = findViewById(R.id.tv_error_message);
        mLoadingindicator = findViewById(R.id.progress_bar_laoding_indicator);

        //set iv
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mrecycler.setLayoutManager(linearLayoutManager);
        mrecycler.setHasFixedSize(true);
        //adapter
        mForcastAdapter = new ForcastAdapter(this);
        mrecycler.setAdapter(mForcastAdapter);
        loadWeateerData();
    }

    private void loadWeateerData() {
        String location = StromfulPrefrences.getPreferedWeatherLocation(this);
        new FetchWeather().execute(location);
    }

    private void showWeatherDataView(){
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mrecycler.setVisibility(View.VISIBLE);
    }
    private void showErrorMessage(){
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mrecycler.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(String weatherfordy) {
        Toast.makeText(this, "weather day is:"+weatherfordy, Toast.LENGTH_SHORT).show();
    }

    /* --------------------------- Async Task---------------------
     * */

    public class FetchWeather extends AsyncTask<String, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingindicator.setVisibility(View.VISIBLE);
        }

        @SuppressLint("LongLogTag")
        @Override
        protected String[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            String location = params[0];
            URL weatherrequestUrl = NetworkUtils.buildUrl(location);
            try {
                String jsonWeatherResponce = NetworkUtils.getResponseFromHttpUrl(weatherrequestUrl);
                String[] weatherDataFromJson = OpenWeatherJsonWeather.getWeatherDataFromJson(MainActivity.this, jsonWeatherResponce);
                Log.e(TAG, "The Weather Data Is" + weatherDataFromJson[0]);
                return weatherDataFromJson;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] weatherdata) {
            mLoadingindicator.setVisibility(View.INVISIBLE);
            if (weatherdata != null) {
                showWeatherDataView();
                mForcastAdapter.setWeatherData(weatherdata);

            }else{
                showErrorMessage();
            }
        }
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
        return super.onOptionsItemSelected(item);
    }
}
