package com.example.stromful.Utilities;

import android.content.ContentValues;
import android.content.Context;

import com.example.stromful.Data.WeatherContract;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FakeDataUtils {
    private static int[] weathereIds = {200,300,500,711,900,962};

    /**
     * creating random data to add to content provider
     * @param date
     * @return
     */
    private static ContentValues createTestWeatherContentValues(long date){
        ContentValues testWeatherValue = new ContentValues();
        testWeatherValue.put(WeatherContract.WeatherEntry.COLUMN_DATE,date);
        testWeatherValue.put(WeatherContract.WeatherEntry.COLUMN_DEGREE,Math.random()*2);
        testWeatherValue.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY,Math.random()*100);
        testWeatherValue.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE,900+Math.random()*100);
        int maxTemp = (int)(Math.random()*100);
        testWeatherValue.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,maxTemp);
        testWeatherValue.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,maxTemp-(int)(Math.random()*10));
        testWeatherValue.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,Math.random()*10);
        testWeatherValue.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,weathereIds[(int)(Math.random()*10)%5]);
        return testWeatherValue;
    }
    /**
     * Inserting  data  to content provider
     */
    public static void insertFakeData(Context context){
        // today's normalized date
        long today = StromfulDateUtils.normalizeDate(System.currentTimeMillis());
        List<ContentValues> fakeValues = new ArrayList<>();
        for (int i=0;i<7;i++){
            fakeValues.add(FakeDataUtils.createTestWeatherContentValues(today+ TimeUnit.DAYS.toMillis(i)));

        }
        // bulk insert
        context.getContentResolver().bulkInsert(
                WeatherContract.WeatherEntry.CONTENT_URI,
                fakeValues.toArray(new ContentValues[7])
        );
    }
}
