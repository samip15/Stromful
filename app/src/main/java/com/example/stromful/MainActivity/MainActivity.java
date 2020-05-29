package com.example.stromful.MainActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.stromful.Data.StromfulPrefrences;
import com.example.stromful.R;
import com.example.stromful.Utilities.NetworkUtils;
import com.example.stromful.Utilities.OpenWeatherJsonWeather;
import com.github.ybq.android.spinkit.SpinKitView;

import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "com/example/stromful/MainActivity";
    private TextView mweatherdata,mErrorMessageDisplay;
    private SpinKitView mLoadingindicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forcast);
        mweatherdata = findViewById(R.id.tv_weather_data);
        mErrorMessageDisplay = findViewById(R.id.tv_error_message);
        mLoadingindicator = findViewById(R.id.progress_bar_laoding_indicator);
        loadWeateerData();
    }

    private void loadWeateerData() {
        String location = StromfulPrefrences.getPreferedWeatherLocation(this);
        new FetchWeather().execute(location);
    }

    private void showWeatherDataView(){
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mweatherdata.setVisibility(View.VISIBLE);
    }
    private void showErrorMessage(){
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mweatherdata.setVisibility(View.INVISIBLE);
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
                for (String weatherstring : weatherdata) {
                    mweatherdata.append(weatherstring + "\n\n\n");
                }
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
            mweatherdata.setText("");
            loadWeateerData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
