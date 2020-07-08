package com.example.stromful.Data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.example.stromful.R;

public class StromfulPrefrences {
    private static final String PREF_CITY_NAME = "Kathmandu";
    private static final String DEFAULT_WEATHERLOCATION = "Kathmandu,Nepal";
    private static final double[] DEFAULT_WEATHER_CORDINATE = {27.7172, 85.3240};
    private static final String PREF_COORD_LAT = "coord_lat";
    private static final String PREF_COORD_LON = "coord_long";

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

    /**
     * setting to location from json result in preference
     * @param context:
     * @param lat:
     * @param lon:
     */
    public static void setLocationDetails(Context context,double lat,double lon){
       SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
       // editor
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(PREF_COORD_LAT,Double.doubleToRawLongBits(lat));
        editor.putLong(PREF_COORD_LON,Double.doubleToRawLongBits(lon));
        editor.apply();
    }

    /**
     * Getting the location coordinates that is set from json result
     * @param context
     * @return
     */
    public static double[] getLocationCoordinate(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        double[] preferedCoordinates = new double[2];
        preferedCoordinates[0] = Double.longBitsToDouble(sp.getLong(PREF_COORD_LAT,Double.doubleToRawLongBits(0.0)));
        preferedCoordinates[1] = Double.longBitsToDouble(sp.getLong(PREF_COORD_LON,Double.doubleToRawLongBits(0.0)));
        return preferedCoordinates;
    }
}
