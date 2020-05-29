package com.example.stromful.Utilities;

import android.net.Uri;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {
    private static final String TAG = "NetworkUtils";
    private static final String BASE_WEATHER_URL = "http://api.openweathermap.org/data/2.5/forecast";
    private static final String Units = "metric";
    private static final int days = 14;
    private static final String Format = "json";
    private static final String appid = "04402e294a942dab68a991dc735cf2e4";
    static String QUERY_PARAM = "q";
    static String LAT_PARAM = "lat";
    static String LONG_PARAM = "lon";
    static String FORMAT_PARAM = "mode";
    static String UNITS_PARAM = "units";
    static String DAYS_PARAM = "cnt";
    static String APPID_PARAM = "appid";

    public static URL buildUrl(String locationqurry) {
        Uri builduri = Uri.parse(BASE_WEATHER_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, locationqurry)
                .appendQueryParameter(FORMAT_PARAM, Format)
                .appendQueryParameter(UNITS_PARAM, Units)
                .appendQueryParameter(DAYS_PARAM, Integer.toString(days))
                .appendQueryParameter(APPID_PARAM, appid)
                .build();
        URL url = null;
        try {
            url = new URL(builduri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;

            }
        } finally {
            urlConnection.disconnect();
        }
    }

}
