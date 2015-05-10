package bozic.bajo.weathercast.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by savo on 9.4.2015.
 */
public class DBHelper extends SQLiteOpenHelper {
   private static final int DATABASE_VERSION = 1;
    public static final String DATABAS_NAME ="database.db";
    private final String TAG = getClass().getSimpleName();

    public DBHelper(Context context) {
        super(context, DATABAS_NAME, null, DATABASE_VERSION);
        Log.i(TAG, " DBHelper()");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, " onCreate()");
        final String LOCATION_CREATE_EXE = "CREATE TABLE " + Contract.LocationContract.TABLE + " (" + Contract.LocationContract._ID + " INTEGER PRIMARY KEY, "
                + Contract.LocationContract.COLOMUN_QUERY + " TEXT UNIQUE NOT NULL, "
                + Contract.LocationContract.COLOMUN_CITY_NAME + " TEXT NOT NULL, "
                + Contract.LocationContract.COLOMUN_LAT + " REAL NOT NULL, "
                + Contract.LocationContract.COLOMUN_LON + " REAL NOT NULL);";

//

        final String WEATHER_CREATE_EXE = "CREATE TABLE " + Contract.WeatherContract.TABLE + " (" + Contract.WeatherContract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Contract.WeatherContract.COLOMUN_DATE + " INTEGER NOT NULL, "
                + Contract.WeatherContract.COLOMUN_LOC_ID + " INTEGER NOT NULL, "
                + Contract.WeatherContract.COLOMUN_MIN + " REAL NOT NULL, "
                + Contract.WeatherContract.COLOMUN_MAX + " REAL NOT NULL, "
                + Contract.WeatherContract.COLOMUN_PRESSURE + " REAL NOT NULL, "
                + Contract.WeatherContract.COLOMUN_HUMIDITY + " INTEGER NOT NULL, "
                + Contract.WeatherContract.COLOMUN_WEATHER_ID + " INTEGER NOT NULL, "
                + Contract.WeatherContract.COLOMUN_SHORT_DESC + " TEXT NOT NULL, "
                + Contract.WeatherContract.COLOMUN_WIND_SPEED + " REAL NOT NULL, "
                + Contract.WeatherContract.COLOMUN_WIND_DIRECTION + " INTEGER NOT NULL, "
                + " FOREIGN KEY ( " + Contract.WeatherContract.COLOMUN_LOC_ID + " ) REFERENCES " + Contract.LocationContract.TABLE + "( " + Contract.LocationContract._ID + " ),"
                + " UNIQUE ( " + Contract.WeatherContract.COLOMUN_DATE + ", " + Contract.WeatherContract.COLOMUN_LOC_ID + " ) ON CONFLICT REPLACE);";


        db.execSQL(LOCATION_CREATE_EXE);
        db.execSQL(WEATHER_CREATE_EXE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, " onUpgrade()");
        db.execSQL("DROP TABLE IF EXIST "+Contract.WeatherContract.TABLE);
        db.execSQL("DROP TABLE IF EXIST "+Contract.LocationContract.TABLE);
        onCreate(db);


    }
}
