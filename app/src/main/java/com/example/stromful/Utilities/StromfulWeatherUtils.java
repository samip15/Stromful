package com.example.stromful.Utilities;

import android.content.Context;

import com.example.stromful.Data.StromfulPrefrences;
import com.example.stromful.R;

public class StromfulWeatherUtils {
    private static final String TAG = "StromfulWeatherUtils";

    private static double celsiusToFaranahite(double temperatureincelsius) {
        double tempinFerinite = (temperatureincelsius * 1.8) + 32;
        return tempinFerinite;

    }

    public static String formatTemperature(Context context, double temperature) {
        int temperatureFromResId = R.string.format_temp_celsius;
        if (!StromfulPrefrences.isMetric(context)) {
            temperature = celsiusToFaranahite(temperature);
            temperatureFromResId = R.string.format_temp_faranite;

        }
        return String.format(context.getString(temperatureFromResId), temperature);

    }

    public static String formatHighLow(Context context, double high, double low) {
        long rowndedhigh = Math.round(high);
        long rowndedlow = Math.round(low);
        String formatedhigh = formatTemperature(context, rowndedhigh);
        String formatedLow = formatTemperature(context, rowndedlow);
        String highlowstr = formatedhigh + "/" + formatedLow;
        return highlowstr;

    }
}
