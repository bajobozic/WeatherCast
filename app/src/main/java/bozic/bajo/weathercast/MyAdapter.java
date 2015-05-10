package bozic.bajo.weathercast;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import bozic.bajo.weathercast.data.Contract;

/**
 * Created by savo on 22.4.2015.
 */
public class MyAdapter extends CursorAdapter {

    private final String TAG = getClass().getSimpleName();
    private static int oldNumRows = 0;
    private static int newdNumRows = 0;
    public static final int ROW_BIG = 0;
    public static final int ROW_STANDARD = 1;
    private int mRowType;

    private String GetReadableDateString(long time) {
        Date date = new Date(time);
        Date dateCurrent = new Date();

        Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT"));

        calendar.setTime(dateCurrent);
        int currentDayOfYear = calendar.get(GregorianCalendar.DAY_OF_YEAR);

        calendar.setTime(date);
        int dayOfYear = calendar.get(GregorianCalendar.DAY_OF_YEAR);

        if (currentDayOfYear == dayOfYear) {
            return "Today";

        } else if (currentDayOfYear == (dayOfYear - 1)) {
            return "Tomorow";
        } else if (((currentDayOfYear + 2) <= dayOfYear) && ((currentDayOfYear + 6) >= dayOfYear)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE");
            return simpleDateFormat.format(date);
        }


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E,MMM d");
        return simpleDateFormat.format(date);
    }


    private String FormatTemperature(double temp) {
        Long t = Math.round(temp);

        String res;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        if (preferences.getString(mContext.getString(R.string.pref_degrees_key), "metric").equals("metric"))
            res = t.toString();
        else {
            t = 9 * t / 5 + 32;

            res = t.toString();
        }

        return res;
    }

    @Override
    public int getItemViewType(int position) {
        Log.i(TAG, "getItemViewType()");
        return (position == 0)? ROW_BIG :ROW_STANDARD;
    }

    @Override
    public int getViewTypeCount() {
        Log.i(TAG, "getViewTypeCount()");
        return 2;
    }


    public MyAdapter(Context context, Cursor c, int flags) {

        super(context, c, flags);
        Log.i(TAG, "MyAdapter()");

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.i(TAG, "newView()");
        View rootView;
        int layoutId = -1;
        if (getItemViewType(cursor.getPosition())== ROW_BIG) {

            layoutId = R.layout.list_item_first;

        } else {


            layoutId = R.layout.list_item_final;
        }

        rootView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return rootView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.i(TAG, "bindView()");

        String cityName = " ";
        newdNumRows = cursor.getCount();


        Log.i(TAG, "NEW CURSOR ROW " + newdNumRows);

        TextView city ;
        if (getItemViewType(cursor.getPosition()) == ROW_BIG) {
            city = (TextView) view.findViewById(R.id.city);
            cityName = cursor.getString(cursor.getColumnIndex(Contract.LocationContract.COLOMUN_CITY_NAME));
            city.setText(cityName);
        }

        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        TextView date = (TextView) view.findViewById(R.id.date);
        TextView condition = (TextView) view.findViewById(R.id.condition);
        TextView min = (TextView) view.findViewById(R.id.min);
        TextView max = (TextView) view.findViewById(R.id.max);

        //now applay values from cursor to individual views
        long iconId = cursor.getLong(cursor.getColumnIndex(Contract.WeatherContract.COLOMUN_WEATHER_ID));

        if ((int) iconId == 800) {
            imageView.setImageResource(R.drawable.clear_sky);

        } else if ((int) iconId == 801) {
            imageView.setImageResource(R.drawable.few_clouds);

        } else if ((int) iconId == 802) {
            imageView.setImageResource(R.drawable.scattered_clouds);

        } else if (((int) iconId == 803) || ((int) iconId == 804)) {
            imageView.setImageResource(R.drawable.clouds);

        } else if (((int) iconId >= 600) && ((int) iconId <= 622)) {
            imageView.setImageResource(R.drawable.snow);

        } else if (((int) iconId >= 500) && ((int) iconId <= 504)) {
            imageView.setImageResource(R.drawable.rain);

        } else if (((int) iconId >= 511) && ((int) iconId <= 531)) {
            imageView.setImageResource(R.drawable.shower_rain);

        } else if ((int) iconId >= 200 && (int) iconId <= 232) {
            imageView.setImageResource(R.drawable.thunderstorm);

        }

        date.setText(GetReadableDateString(cursor.getLong(cursor.getColumnIndex(Contract.WeatherContract.COLOMUN_DATE))));
        condition.setText(cursor.getString(cursor.getColumnIndex(Contract.WeatherContract.COLOMUN_SHORT_DESC)));
        min.setText(FormatTemperature(cursor.getDouble(cursor.getColumnIndex(Contract.WeatherContract.COLOMUN_MIN))) + "\u00B0");
        max.setText(FormatTemperature(cursor.getDouble(cursor.getColumnIndex(Contract.WeatherContract.COLOMUN_MAX))) + "\u00B0");


    }
}
