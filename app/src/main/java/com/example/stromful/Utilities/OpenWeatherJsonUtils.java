package com.example.stromful.Utilities;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.stromful.Data.StromfulPrefrences;
import com.example.stromful.Data.WeatherContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public class OpenWeatherJsonUtils {
    // location and lat long from json
    private static final String OWM_CITY = "city";
    private static final String OWM_COORD = "coord";
    private static final String OWM_LATITUDE = "lat";
    private static final String OWM_LONGITUDE = "lon";
    private static final String OWM_LIST = "list";
    private static final String OWM_WEATHER_ID = "id";
    private static final String OWM_MAX = "temp_max";
    private static final String OWM_MIN = "temp_min";
    private static final String OWM_WEATHER = "weather";
    private static final String OWM_TEMPERETURE = "main";
    private static final String OWM_MESSAGE_CODE = "cod";
    private static final String OWM_DESCRYPTION = "main";
    // humidity pressure wind speed and wind direction
    private static final String OWM_PRESSURE = "pressure";
    private static final String OWM_HUMIDITY = "humidity";
    private static final String OWM_WIND = "wind";
    private static final String OWM_WIND_SPEED = "speed";
    private static final String OWM_WIND_DIRECTION = "deg";

    public static String[] getWeatherDataFromJson(Context context, String forcastJsonStr) throws JSONException {

        String[] parsedWeatherData = null;
        JSONObject forcastJson = new JSONObject(forcastJsonStr);
        if (forcastJson.has(OWM_MESSAGE_CODE)) {
            int errorcode = forcastJson.getInt(OWM_MESSAGE_CODE);
            switch (errorcode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
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

    /**
     * This Method Will Help On:content provider bulk insert and json parse
     */

    public static ContentValues[] getWeatherContentValuesFromJson(Context context, String forcastJsonStr) throws JSONException {
        String[] parsedWeatherData = null;
        JSONObject forcastJson = new JSONObject(forcastJsonStr);
        if (forcastJson.has(OWM_MESSAGE_CODE)) {
            int errorcode = forcastJson.getInt(OWM_MESSAGE_CODE);
            switch (errorcode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }

        }
        JSONArray weatherArray = forcastJson.getJSONArray(OWM_LIST);
        JSONObject cityJson = forcastJson.getJSONObject(OWM_CITY);
        JSONObject cityCoord = cityJson.getJSONObject(OWM_COORD);
        double cityLatitude = cityCoord.getDouble(OWM_LATITUDE);
        double cityLongitude = cityCoord.getDouble(OWM_LONGITUDE);
        StromfulPrefrences.setLocationDetails(context,cityLatitude,cityLongitude);
        ContentValues[] weatherContentValues = new ContentValues[weatherArray.length()];
      //  long localdte = System.currentTimeMillis();
        long normalizedUTCStartDate = StromfulDateUtils.getNormalizedUTCDateForToday();
      //  long startDate = StromfulDateUtils.normalizeDate(localdte);
        for (int i = 0; i < weatherArray.length(); i++) {
            String description;
            long datetimemillis;
            double high;
            double low;
            double humudity;
            double pressure;
            double windspeed;
            double windDirection;
            int weatherId;
            JSONObject dayforcast = weatherArray.getJSONObject(i);
            datetimemillis = normalizedUTCStartDate + StromfulDateUtils.DAY_IN_MILLIS * i;
            // getting weather info
            JSONObject weatherObject = dayforcast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            weatherId = weatherObject.getInt(OWM_WEATHER_ID);
            description = weatherObject.getString(OWM_DESCRYPTION);
            JSONObject temperstureObject = dayforcast.getJSONObject(OWM_TEMPERETURE);
            high = temperstureObject.getDouble(OWM_MAX);
            low = temperstureObject.getDouble(OWM_MIN);
            // getting humidity and pressure
            humudity = temperstureObject.getDouble(OWM_HUMIDITY);
            pressure = temperstureObject.getDouble(OWM_PRESSURE);
            // wind speed and direction
            JSONObject windObject = dayforcast.getJSONObject(OWM_WIND);
            windspeed = windObject.getDouble(OWM_WIND_SPEED);
            windDirection = windObject.getDouble(OWM_WIND_DIRECTION);
            // auta day ko weather
            ContentValues weatherValues = new ContentValues();
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATE, datetimemillis);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, humudity);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, pressure);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, windspeed);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREE, windDirection);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, high);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, low);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, weatherId);
            weatherContentValues[i] = weatherValues;

        }
        return weatherContentValues;
    }

}
