package bozic.bajo.weathercast.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.Vector;

import bozic.bajo.weathercast.ForecastFragment;
import bozic.bajo.weathercast.R;
import bozic.bajo.weathercast.data.Contract;

/**
 * Created by savo on 10.5.2015.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";//q=94043&APPID=c095da3c9bf0c1dd6ab0cb3edb7ff76d";

    public  final String TAG = getClass().getSimpleName();
    Context mContext;
    ContentResolver mResolver;
    private String mLocation;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;
        mResolver = context.getContentResolver();
        mLocation = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.pref_location_key), context.getString(R.string.pref_location_default));
    }


    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

            Log.i(TAG, " doInBackground()");



            BufferedReader bufferedReader = null;
            HttpURLConnection httpURLConnection = null;

            final String fullQuery = BuildUrl(mLocation);


            String forecaastJson = null;


            try {
                URL url = new URL(fullQuery);
                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                InputStream stream = httpURLConnection.getInputStream();
                if (stream == null)
                    return;

                bufferedReader = new BufferedReader(new InputStreamReader(stream));

                StringBuilder stringBuilder = new StringBuilder();

                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                if (stringBuilder.length() != 0) {
                    forecaastJson = stringBuilder.toString();
                } else {
                    return;
                }


            } catch (MalformedURLException e) {
                Log.i(getClass().getSimpleName(), "MAILFORMED URL");
                e.printStackTrace();
                return;
            } catch (IOException e) {
                Log.i(getClass().getSimpleName(), "FAILLED TO OPEN FILE");
                e.printStackTrace();
                return;
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }

                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                GetWeatherDataFromJson(forecaastJson, mLocation);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.i(getClass().getSimpleName(), "EXCEPTION READING JSON FILE");
            }





    }

    public void GetWeatherDataFromJson(String json, String query) throws JSONException {
        Log.i(TAG, " GetWeatherFromJson()");


        ContentValues locationValues = new ContentValues();

        locationValues.put(Contract.LocationContract.COLOMUN_QUERY, query);

        JSONObject rootObject = new JSONObject(json);

        JSONObject city = rootObject.getJSONObject("city");
        final String cityName = city.getString("name");
        locationValues.put(Contract.LocationContract.COLOMUN_CITY_NAME, cityName);

        JSONObject coord = city.getJSONObject("coord");
        final double lon = coord.getDouble("lon");
        final double lat = coord.getDouble("lat");
        locationValues.put(Contract.LocationContract.COLOMUN_LON, lon);
        locationValues.put(Contract.LocationContract.COLOMUN_LAT, lat);


        long locationId = InsertIntoLocationTable(query, locationValues);

        JSONArray list = rootObject.getJSONArray("list");

        long dayInMilisec = 24 * 60 * 60 * 1000;

        long currentDayInMilisec = getCurrentDayInMilisec(dayInMilisec);






        int listSize = list.length();
        Vector<ContentValues> contentValuesVector = new Vector<ContentValues>(listSize);
        for (int i = 0; i < listSize; i++) {
            ContentValues weatherValues = new ContentValues();

            JSONObject listRoot = list.getJSONObject(i);

            long nextDay = currentDayInMilisec + dayInMilisec * i;
            weatherValues.put(Contract.WeatherContract.COLOMUN_DATE, nextDay);
            weatherValues.put(Contract.WeatherContract.COLOMUN_LOC_ID, locationId);

            JSONObject temp = listRoot.getJSONObject("temp");
            Double min = temp.getDouble("min");
            Double max = temp.getDouble("max");
            weatherValues.put(Contract.WeatherContract.COLOMUN_MIN, min);
            weatherValues.put(Contract.WeatherContract.COLOMUN_MAX, max);

            long pressure = listRoot.getLong("pressure");
            long humidity = listRoot.getLong("humidity");
            weatherValues.put(Contract.WeatherContract.COLOMUN_PRESSURE, pressure);
            weatherValues.put(Contract.WeatherContract.COLOMUN_HUMIDITY, humidity);


            JSONArray weather = listRoot.getJSONArray("weather");
            for (int a = 0; a < weather.length(); a++) {
                JSONObject top = weather.getJSONObject(a);
                long id = top.getLong("id");
                weatherValues.put(Contract.WeatherContract.COLOMUN_WEATHER_ID, id);

                String weath = top.getString("main");
                String description = top.getString("description");
                weatherValues.put(Contract.WeatherContract.COLOMUN_SHORT_DESC, weath);


            }

            double speed = listRoot.getDouble("speed");
            weatherValues.put(Contract.WeatherContract.COLOMUN_WIND_SPEED, speed);

            long deg = listRoot.getLong("deg");
            weatherValues.put(Contract.WeatherContract.COLOMUN_WIND_DIRECTION, deg);

            contentValuesVector.add(i, weatherValues);

        }
        if (contentValuesVector.size() > 0) {
            ContentValues[] arr = new ContentValues[contentValuesVector.size()];
            contentValuesVector.toArray(arr);

            mResolver.bulkInsert(Contract.WeatherContract.URI, arr);
            mResolver.delete(Contract.WeatherContract.URI, new String(Contract.WeatherContract.COLOMUN_DATE + " < ? "),
                    new String[]{Long.toString(currentDayInMilisec)});
        }

    }

    private long getCurrentDayInMilisec(long dayInMilisec) {
        Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        Date date = new Date();
        calendar.setTime(date);
        long currentDayInMilisec = calendar.getTimeInMillis();
        int dayofjear = calendar.get(GregorianCalendar.DAY_OF_YEAR);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        long theDay = preferences.getLong(ForecastFragment.CURRENT_DAY_MILISEC, -1L);
        if (theDay <= 0) {
            long temp = System.currentTimeMillis();
            long diff = temp % dayInMilisec;
            theDay = temp - diff;
//            theDay = System.currentTimeMillis();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong(ForecastFragment.CURRENT_DAY_MILISEC, theDay).commit();

        }
        calendar.setTime(new Date(theDay));
        int yearDay = calendar.get(GregorianCalendar.DAY_OF_YEAR);
        if (yearDay == dayofjear) {
            currentDayInMilisec = theDay;
        }
        else
        {
            currentDayInMilisec = theDay+dayInMilisec;
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong(ForecastFragment.CURRENT_DAY_MILISEC,currentDayInMilisec ).apply();


        }
        return currentDayInMilisec;
    }

    private long InsertIntoLocationTable(String query, ContentValues contentValues) {
        Log.i(TAG, " IsertIntoLocation()");
        long uriId;
        Cursor returnedCursor = mResolver.query(Contract.LocationContract.URI,
                new String[]{Contract.LocationContract._ID},
                Contract.LocationContract.COLOMUN_QUERY + " = ?",
                new String[]{query},
                null);
        if (returnedCursor.moveToFirst()) {
            uriId = returnedCursor.getLong(returnedCursor.getColumnIndex(Contract.LocationContract._ID));
        }
        else {
            Uri insertedUri = mResolver.insert(Contract.LocationContract.URI, contentValues);
            uriId = ContentUris.parseId(insertedUri);

        }
        returnedCursor.close();
        return uriId;
    }

    public String BuildUrl(String postCode) {
        final String POSTALCODE = "q";
        final String APPID = "APPID";
        final String MODE = "mode";
        final String UNITS = "units";
        final String CNT = "cnt";


        String appId = "c095da3c9bf0c1dd6ab0cb3edb7ff76d";
        String mode = "json";
        String units = "metric";
        String cnt = "14";

        Uri baseUri = Uri.parse(BASE_URL);
        Uri.Builder builder = baseUri.buildUpon();
        Uri bulderUri = builder.appendQueryParameter(POSTALCODE, postCode).appendQueryParameter(APPID, appId).appendQueryParameter(MODE, mode).appendQueryParameter(UNITS, units).appendQueryParameter(CNT, cnt).build();
//        fullUrl = bulderUri.toString();
        return bulderUri.toString();

    }

}
