package com.example.stromful.Data;

import android.content.Context;

public class StromfulPrefrences {
    private static final String PREF_CITY_NAME = "Kathmandu";
    private static final String DEFAULT_WEATHERLOCATION = "Kathmandu,Nepal";
    private static final double[] DEFAULT_WEATHER_CORDINATE = {27.7172, 85.3240};

    public static boolean isMetric(Context context) {
        return true;
    }

    public static String getPreferedWeatherLocation(Context context) {
        return getDefaultWeatherlocation();
    }

    public static String getDefaultWeatherlocation() {
        return DEFAULT_WEATHERLOCATION;
    }

    public static double[] getDefaultWeatherCordinate() {
        return DEFAULT_WEATHER_CORDINATE;
    }
}
