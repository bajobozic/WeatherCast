package bozic.bajo.weathercast.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import bozic.bajo.weathercast.data.Contract.LocationContract;

public class WCProvider extends ContentProvider {

    private final String TAG = getClass().getSimpleName();
    public static final int LOCATION = 100;
    // public static final int LOCATION_ID = 101;
    public static final int WEATHER = 300;
    public static final int WEATHER_WITH_LOCATION = 301;
    public static final int WEATHER_WITH_LOCATION_AND_DATE = 302;

    private DBHelper helper;

    public static final UriMatcher uriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);


        matcher.addURI(Contract.CONTENT_AUTHORITY, LocationContract.TABLE, LOCATION);  //bozic.bajo.weathercast/location
        // matcher.addURI(Contract.CONTENT_AUTHORITY, LocationContract.TABLE+"/#",101); //bozic.bajo.weathercast/location/1

        matcher.addURI(Contract.CONTENT_AUTHORITY, Contract.WeatherContract.TABLE, WEATHER);         //bozic.bajo.weathercast/weather
        matcher.addURI(Contract.CONTENT_AUTHORITY, Contract.WeatherContract.TABLE + "/*", WEATHER_WITH_LOCATION );    //bozic.bajo.weathercast/weather/query_location
        matcher.addURI(Contract.CONTENT_AUTHORITY, Contract.WeatherContract.TABLE + "/*/#", WEATHER_WITH_LOCATION_AND_DATE );  //bozic.bajo.weathercast/weather/query_location/day


        return matcher;
    }

    public WCProvider() {
        Log.i(TAG, " WCProvider()");
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        Log.i(TAG, " BulkInsert()");
        int rowsInserted = 0;
        SQLiteDatabase db = helper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case WEATHER:{

                try {
                    db.beginTransaction();
                    for (ContentValues value : values) {
                        long id = db.insert(Contract.WeatherContract.TABLE, null, value);
                        if (id > -1) {
                            rowsInserted++;
                        }

                    }


                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();

                }
                getContext().getContentResolver().notifyChange(uri,null);
                return rowsInserted;
            }

        }
        return super.bulkInsert(uri, values);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.i(TAG, " delete()");
        // Implement this to handle requests to delete one or more rows.
        final int match = uriMatcher.match(uri);
        SQLiteDatabase database = helper.getWritableDatabase();
        int rowsDeleted = 0;
        if (selection == null) {
            selection = "1";
        }
        switch (match) {
            case WEATHER: {
                rowsDeleted = database.delete(Contract.WeatherContract.TABLE, selection, selectionArgs);
                break;
            }
            case LOCATION: {
                rowsDeleted = database.delete(Contract.LocationContract.TABLE, selection, selectionArgs);
                break;
            }

            default:
                throw new UnsupportedOperationException("Not yet implemented");

        }
        if ((selection != null) && (rowsDeleted > 0)) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        Log.i(TAG, " getType()");
        final int match = uriMatcher.match(uri);
        switch (match) {
            case LOCATION:
                return LocationContract.CONTENT_DIR_TYPE;
            // case LOCATION_ID:
            //   return LocationContract.CONTENT_ITEM_TYPE;
            case WEATHER:
                return Contract.WeatherContract.CONTENT_DIR_TYPE;
            case WEATHER_WITH_LOCATION:
                return Contract.WeatherContract.CONTENT_DIR_TYPE;
            case WEATHER_WITH_LOCATION_AND_DATE:
                return Contract.WeatherContract.CONTENT_ITEM_TYPE;
        }
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.i(TAG, " insert()");
        final int match = uriMatcher.match(uri);
        SQLiteDatabase database = helper.getWritableDatabase();
        Uri resultUri;
        switch (match) {
            case WEATHER: {
                long id = database.insert(Contract.WeatherContract.TABLE, null, values);
                if (id > 0) {
                    resultUri = ContentUris.withAppendedId(uri, id);
                } else
                    throw new UnsupportedOperationException("FAILED TO INSERT IN WEATHER");
                break;
            }
            case LOCATION: {
                long id = database.insert(Contract.LocationContract.TABLE, null, values);
                if (id > 0) {
                    resultUri = ContentUris.withAppendedId(uri, id);
                } else
                    throw new UnsupportedOperationException("FAILED TO INSERT IN LOCATION");
                break;

            }
            default:
                throw new UnsupportedOperationException("inserton failed bad uri" + uri);

        }
        getContext().getContentResolver().notifyChange(uri, null);

        return resultUri;


    }

    @Override
    public boolean onCreate() {
        Log.i(TAG, " onCreate()");
        helper = new DBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Log.i(TAG, " query()");
        final int match = uriMatcher.match(uri);
        Cursor cursor = null;

        SQLiteDatabase database = helper.getReadableDatabase();

        switch (match) {
            case LOCATION: {
                cursor = database.query(LocationContract.TABLE,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }


            case WEATHER: {
                cursor = database.query(Contract.WeatherContract.TABLE,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            case WEATHER_WITH_LOCATION: {
                //bozic.bajo.weathercast/weather/query_location
                SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
                final String QUERY_BUILDER_STRING = Contract.WeatherContract.TABLE + " INNER JOIN " + LocationContract.TABLE + " ON " + Contract.WeatherContract.TABLE + "."
                        + Contract.WeatherContract.COLOMUN_LOC_ID + " = " + LocationContract.TABLE + "." + LocationContract._ID;
                queryBuilder.setTables(QUERY_BUILDER_STRING);
                String select =  LocationContract.TABLE + "." + LocationContract.COLOMUN_QUERY + " = ? ";
//                selection =  LocationContract.TABLE + "." + LocationContract.COLOMUN_QUERY + " = ? AND "+Contract.WeatherContract.COLOMUN_DATE+" > ? ";

                final String []selectArgs =  new String[]{uri.getPathSegments().get(1)};
//                final String []selectArgs = new String[]{uri.getPathSegments().get(1),Long.toString(System.currentTimeMillis()/1000)};
                cursor = queryBuilder.query(database, projection, select, selectArgs, null, null, sortOrder);
                break;

            }

            case WEATHER_WITH_LOCATION_AND_DATE: {
                //bozic.bajo.weathercast/weather/query_location/query_date
                SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
                final String QUERY_BUILDER_STRING = Contract.WeatherContract.TABLE + " INNER JOIN " + LocationContract.TABLE + " ON " + Contract.WeatherContract.TABLE + "."
                        + Contract.WeatherContract.COLOMUN_LOC_ID + " = " + LocationContract.TABLE + "." + LocationContract._ID;
                queryBuilder.setTables(QUERY_BUILDER_STRING);
                selection = LocationContract.TABLE + "." + LocationContract.COLOMUN_QUERY + " = ? "+" AND "+ Contract.WeatherContract.COLOMUN_DATE + " = ?";

                final String selectArgsLoc = uri.getPathSegments().get(1);
                final String selectArgsDate = uri.getPathSegments().get(2);

                final String[] selectArgs = new String[]{selectArgsLoc, selectArgsDate};
                cursor = queryBuilder.query(database, projection, selection, selectArgs, null, null, sortOrder);
                break;


            }

            default:
                throw new UnsupportedOperationException("Not yet implemented");


        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;


    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        Log.i(TAG, " update()");
        final int match = uriMatcher.match(uri);
        SQLiteDatabase database = helper.getWritableDatabase();
        int updatedRows = 0;
        switch (match) {
            case WEATHER: {
                updatedRows = database.update(Contract.WeatherContract.TABLE, values, selection, selectionArgs);
                break;

            }
            case LOCATION: {
                updatedRows = database.update(Contract.LocationContract.TABLE, values, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Not yet implemented");


        }
        if ( (selection != null) && (updatedRows > 0)) {
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return updatedRows;

    }
}
