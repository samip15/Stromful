package com.example.stromful.Utilities;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public class OpenWeatherJsonWeather {
    public static String[] getWeatherDataFromJson(Context context, String forcastJsonStr) throws JSONException {
        final String OWM_LIST = "list";
        final String OWM_MAX = "temp_max";
        final String OWM_MIN = "temp_min";
        final String OWM_WEATHER = "weather";
        final String OWM_TEMPERETURE = "main";
        final String OWM_MESSAGE_CODE = "cod";
        final String OWM_DESCRYPTION = "main";
        String[] parsedWeatherData = null;
        JSONObject forcastJson = new JSONObject(forcastJsonStr);
        if (forcastJson.has(OWM_MESSAGE_CODE)) {
            int errorcode = forcastJson.getInt(OWM_MESSAGE_CODE);
            switch (errorcode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    return null;
                default:
                    return null;
            }

        }
        JSONArray weatherArray = forcastJson.getJSONArray(OWM_LIST);
        parsedWeatherData = new String[weatherArray.length()];
        long localdte = System.currentTimeMillis();
        long utcDate = StromfulDateUtils.getUTCDateFromLocal(localdte);
        long startDate = StromfulDateUtils.normalizeDate(localdte);
        for (int i = 0; i < weatherArray.length(); i++) {
            String date;
            String highlow;
            long datetimemillis;
            double high;
            double low;
            String descryption;
            JSONObject dayforcast = weatherArray.getJSONObject(i);
            datetimemillis = startDate + StromfulDateUtils.DAY_IN_MILLIS * i;
            date = StromfulDateUtils.getFriendlyDateString(context, datetimemillis, false);
            JSONObject weatherObject = dayforcast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            descryption = weatherObject.getString(OWM_DESCRYPTION);
            JSONObject temperstureObject = dayforcast.getJSONObject(OWM_TEMPERETURE);
            high = temperstureObject.getDouble(OWM_MAX);
            low = temperstureObject.getDouble(OWM_MIN);
            highlow = StromfulWeatherUtils.formatHighLow(context, high, low);
            parsedWeatherData[i] = date + "-" + highlow;


        }
        return parsedWeatherData;
    }
}
