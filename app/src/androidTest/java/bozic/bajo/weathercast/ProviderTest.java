package bozic.bajo.weathercast;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import bozic.bajo.weathercast.data.Contract;

/**
 * Created by savo on 15.4.2015.
 */
public class ProviderTest extends AndroidTestCase {
    public ProviderTest() {
        super();
    }

    private ContentValues getLocationContentValues(String q) {

        final String TEST_QUERY = q;
        final String TEST_CITY_NAME = "Mountain View";
        final double TEST_LON = -122.077;
        final double TEST_LAT = 37.4121;

        ContentValues values = new ContentValues();
        values.put(Contract.LocationContract.COLOMUN_QUERY, TEST_QUERY);
        values.put(Contract.LocationContract.COLOMUN_CITY_NAME, TEST_CITY_NAME);
        values.put(Contract.LocationContract.COLOMUN_LON, TEST_LON);
        values.put(Contract.LocationContract.COLOMUN_LAT, TEST_LAT);
        return values;
    }

    private ContentValues getWeatherContentValues(long id) {

        ContentValues values = new ContentValues();

        Long TEST_DATE = System.currentTimeMillis();
        values.put(Contract.WeatherContract.COLOMUN_DATE, TEST_DATE);
        values.put(Contract.WeatherContract.COLOMUN_LOC_ID, id);
        Double TEST_MIN = 25.2;
        values.put(Contract.WeatherContract.COLOMUN_MIN, TEST_MIN);
        Double TEST_MAX = 30.0;
        values.put(Contract.WeatherContract.COLOMUN_MAX, TEST_MAX);
        Double TEST_PRESSURE = 1012.2;
        values.put(Contract.WeatherContract.COLOMUN_PRESSURE, TEST_PRESSURE);
        Long TEST_HUMIDITY = 61L;
        values.put(Contract.WeatherContract.COLOMUN_HUMIDITY, TEST_HUMIDITY);
        Long TEST_WEATHER_ID = 500L;
        values.put(Contract.WeatherContract.COLOMUN_WEATHER_ID, TEST_WEATHER_ID+id);
        String TEST_SHORT_DESC = "light rain"+id;
        values.put(Contract.WeatherContract.COLOMUN_SHORT_DESC, TEST_SHORT_DESC);
        Double TEST_WIND_SPEED = 25.5;
        values.put(Contract.WeatherContract.COLOMUN_WIND_SPEED, TEST_WIND_SPEED);
        Long TEST_WIND_DIRECTION = 48L;
        values.put(Contract.WeatherContract.COLOMUN_WIND_DIRECTION, TEST_WIND_DIRECTION);

        return values;

    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteDB();
    }

    public void testGetType() {
        assertEquals(getContext().getContentResolver().getType(Contract.LocationContract.URI), Contract.LocationContract.CONTENT_DIR_TYPE);
        assertEquals(getContext().getContentResolver().getType(Contract.WeatherContract.URI), Contract.WeatherContract.CONTENT_DIR_TYPE);

        final String TEST_QUERY = "London, UK";
        final long TEST_DATE = 12345678L;
        final Uri uriLoc = Contract.WeatherContract.URI.buildUpon().appendPath(TEST_QUERY).build();
        final Uri uriLocDate = Contract.WeatherContract.URI.buildUpon().appendPath(TEST_QUERY).appendPath(Long.toString(TEST_DATE)).build();

        assertEquals(getContext().getContentResolver().getType(uriLoc), Contract.WeatherContract.CONTENT_DIR_TYPE);
        assertEquals(getContext().getContentResolver().getType(uriLocDate), Contract.WeatherContract.CONTENT_ITEM_TYPE);
    }

    public void testBulkInsert() {

    }

    public void testReadWriteProvider() {
        mContext.getContentResolver().delete(Contract.WeatherContract.URI, null, null);
        mContext.getContentResolver().delete(Contract.LocationContract.URI, null, null);


        String q1 = "94043";
        String q2 = "75001";
        String q3 = "Beograd";
        ContentValues values1 = getLocationContentValues(q1);
        ContentValues values2 = getLocationContentValues(q2);
        ContentValues values3 = getLocationContentValues(q3);
        Uri uri1 = mContext.getContentResolver().insert(Contract.LocationContract.URI, values1);
        Uri uri2=mContext.getContentResolver().insert(Contract.LocationContract.URI, values2);
        Uri uri3=mContext.getContentResolver().insert(Contract.LocationContract.URI, values3);
        assertTrue(uri1 != null);
//        assertTrue(uri2 != null);

        long id1 = ContentUris.parseId(uri1);
        assertTrue(id1 != -1);
        long id2 = ContentUris.parseId(uri2);
        assertTrue(id2 != -1);
        long id3 = ContentUris.parseId(uri3);
        assertTrue(id3 != -1);



       /* Cursor cursor = mContext.getContentResolver().query(Contract.LocationContract.URI,
                null,
                null,
                null,
                null);
        assertTrue(cursor.moveToFirst());


        final String q = cursor.getString(cursor.getColumnIndex(Contract.LocationContract.COLOMUN_QUERY));
        final String name = cursor.getString(cursor.getColumnIndex(Contract.LocationContract.COLOMUN_CITY_NAME));
        final double lon = cursor.getDouble(cursor.getColumnIndex(Contract.LocationContract.COLOMUN_LON));
        final double lat = cursor.getDouble(cursor.getColumnIndex(Contract.LocationContract.COLOMUN_LAT));

        final String TEST_QUERY = "94043";
        final String TEST_CITY_NAME = "Mountain View";
        final double TEST_LON = -122.077;
        final double TEST_LAT = 37.4121;


        assertTrue(q.equals(TEST_QUERY));
        assertTrue(name.equals(TEST_CITY_NAME));
        assertTrue(lon == TEST_LON);
        assertTrue(lat == TEST_LAT);

        assertTrue(cursor.moveToNext());*/
        //cursor.close();
        values1.clear();
        uri1 = null;
        values2.clear();
        uri2 = null;
        values3.clear();
        uri3 = null;


        values1 = getWeatherContentValues(id1);
        values2 = getWeatherContentValues(id2);
        values3 = getWeatherContentValues(id3);
        ContentValues[] arr = new ContentValues[]{values1, values2, values3};

        /*uri1 = mContext.getContentResolver().insert(Contract.WeatherContract.URI, values1);

        uri2 = mContext.getContentResolver().insert(Contract.WeatherContract.URI, values2);

        uri3 = mContext.getContentResolver().insert(Contract.WeatherContract.URI, values3);*/

        mContext.getContentResolver().bulkInsert(Contract.WeatherContract.URI, arr);

       /* assertTrue(ContentUris.parseId(uri1) != -1);
        assertTrue(ContentUris.parseId(uri2) != -1);
        assertTrue(ContentUris.parseId(uri3) != -1);*/

//        assertTrue(ContentUris.parseId(uri2) != ContentUris.parseId(uri1));
        Cursor cursor = mContext.getContentResolver().query(Contract.WeatherContract.URI, new String[]{Contract.WeatherContract.TABLE + "." + Contract.WeatherContract._ID}, null, null, null);

        int rows = cursor.getCount();
        long rowId = -1;
        while (cursor.moveToNext()) {
            rowId = cursor.getLong(cursor.getColumnIndex(Contract.WeatherContract._ID));

        }
        assertTrue(rows > 2);
        cursor.close();
        values2 = getWeatherContentValues(id2);
        Uri uri4 = mContext.getContentResolver().insert(Contract.WeatherContract.URI, values2);
        assertTrue(ContentUris.parseId(uri4) != -1);
        cursor = mContext.getContentResolver().query(Contract.WeatherContract.URI, new String[]{Contract.WeatherContract.TABLE + "." + Contract.WeatherContract._ID}, null, null, null);
         rows = cursor.getCount();
        rowId = -1;
        while (cursor.moveToNext()) {
            rowId = cursor.getLong(cursor.getColumnIndex(Contract.WeatherContract._ID));

        }
        /*assertTrue(cursor.moveToFirst());

        Long testid = cursor.getLong(cursor.getColumnIndex(Contract.WeatherContract.COLOMUN_LOC_ID));
        Long windDir = cursor.getLong(cursor.getColumnIndex(Contract.WeatherContract.COLOMUN_WIND_DIRECTION));
        assertEquals(id, testid.longValue());
        assertEquals(windDir.longValue(), 48L);*/
        cursor.close();


    }

    public void testQueryProvider() {
        mContext.getContentResolver().delete(Contract.LocationContract.URI, null, null);
        mContext.getContentResolver().delete(Contract.WeatherContract.URI, null, null);

    }

    private void deleteDB() {
        mContext.getContentResolver().delete(Contract.LocationContract.URI, null, null);
        mContext.getContentResolver().delete(Contract.WeatherContract.URI, null, null);
    }


    @Override
    protected void tearDown() throws Exception {
        deleteDB();
        super.tearDown();
    }
}
