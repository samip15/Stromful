package com.example.stromful.Data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.example.stromful.R;

public class StromfulPrefrences {
    private static final String PREF_CITY_NAME = "Kathmandu";
    private static final String DEFAULT_WEATHERLOCATION = "Kathmandu,Nepal";
    private static final double[] DEFAULT_WEATHER_CORDINATE = {27.7172, 85.3240};

    public static boolean isMetric(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String keyForUnits = context.getString(R.string.pref_units_key);
        String defaultUnits = context.getString(R.string.pref_units_metric);
        String preferedUnits = pref.getString(keyForUnits,defaultUnits);
        return preferedUnits.equals(defaultUnits);
    }

    public static String getPreferedWeatherLocation(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String keyLocation = context.getString(R.string.pref_location_key);
        String defaultLocation = context.getString(R.string.pref_location_default);
        return pref.getString(keyLocation,defaultLocation);

    }

    public static String getDefaultWeatherlocation() {
        return DEFAULT_WEATHERLOCATION;
    }

    public static double[] getDefaultWeatherCordinate() {
        return DEFAULT_WEATHER_CORDINATE;
    }
}
