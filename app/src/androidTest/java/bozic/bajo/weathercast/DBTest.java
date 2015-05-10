package bozic.bajo.weathercast;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import bozic.bajo.weathercast.data.Contract;
import bozic.bajo.weathercast.data.DBHelper;

/**
 * Created by savo on 8.4.2015.
 */
public class DBTest extends AndroidTestCase {

    private SQLiteDatabase sqLiteDatabase;

    public DBTest() {
        super();
        this.sqLiteDatabase = null;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mContext.deleteDatabase(DBHelper.DATABAS_NAME);
        sqLiteDatabase = new DBHelper(this.mContext).getWritableDatabase();
    }
    public void testCreateDB() {

        assertTrue(sqLiteDatabase.isOpen());
    }

    public void testInsertReadLocationDB() throws Exception{


        final String TEST_QUERY = "94043";
        final String TEST_CITY_NAME = "Mountain View";
        final double TEST_LON = -122.077;
        final double TEST_LAT = 37.4121;

        ContentValues values = getLocationContentValues(TEST_QUERY, TEST_CITY_NAME, TEST_LON, TEST_LAT);


        long id = sqLiteDatabase.insert(Contract.LocationContract.TABLE, null, values);
        assertTrue(id != -1);

        Cursor cursor = sqLiteDatabase.query(Contract.LocationContract.TABLE, null, null, null, null, null, null);
        assertTrue(cursor.moveToFirst());

        int index = cursor.getColumnIndex(Contract.LocationContract.COLOMUN_QUERY);
        assertEquals(cursor.getString(index), TEST_QUERY);

        int index1 = cursor.getColumnIndex(Contract.LocationContract.COLOMUN_CITY_NAME);
        assertEquals(cursor.getString(index1),TEST_CITY_NAME);

        int index2 = cursor.getColumnIndex(Contract.LocationContract.COLOMUN_LON);
        assertEquals(cursor.getDouble(index2),TEST_LON);

        int index3 = cursor.getColumnIndex(Contract.LocationContract.COLOMUN_LAT);
        assertEquals(cursor.getDouble(index3), TEST_LAT);

        assertFalse(cursor.moveToNext());
        cursor.close();



    }

    public void testInsertReadWeatherDB() throws Exception{

        final String TEST_QUERY = "94043";
        final String TEST_CITY_NAME = "Mountain View";
        final double TEST_LON = -122.077;
        final double TEST_LAT = 37.4121;

        ContentValues values = getLocationContentValues(TEST_QUERY, TEST_CITY_NAME, TEST_LON, TEST_LAT);


        long id = sqLiteDatabase.insert(Contract.LocationContract.TABLE, null, values);
        assertTrue(id != -1);

        ContentValues values1 = getWeatherContentValues(id);

        long id1 = sqLiteDatabase.insert(Contract.WeatherContract.TABLE,null,values1);
        assertTrue(id1 !=-1);

        Cursor cursor = sqLiteDatabase.query(Contract.WeatherContract.TABLE,null,null,null,null,null,null);
        assertTrue(cursor.moveToFirst());
        cursor.close();


    }

    private ContentValues getLocationContentValues(String TEST_QUERY, String TEST_CITY_NAME, double TEST_LON, double TEST_LAT) {



        ContentValues values = new ContentValues();
        values.put(Contract.LocationContract.COLOMUN_QUERY,TEST_QUERY);
        values.put(Contract.LocationContract.COLOMUN_CITY_NAME,TEST_CITY_NAME);
        values.put(Contract.LocationContract.COLOMUN_LON,TEST_LON);
        values.put(Contract.LocationContract.COLOMUN_LAT, TEST_LAT);
        return values;
    }

    private ContentValues getWeatherContentValues(long id) {

        ContentValues values = new ContentValues();

        Long TEST_DATE = 1256987456L;
        values.put(Contract.WeatherContract.COLOMUN_DATE,TEST_DATE);
        values.put(Contract.WeatherContract.COLOMUN_LOC_ID,id);
        Double TEST_MIN = 25.2;
        values.put(Contract.WeatherContract.COLOMUN_MIN,TEST_MIN);
        Double TEST_MAX = 30.0;
        values.put(Contract.WeatherContract.COLOMUN_MAX,TEST_MAX);
        Double TEST_PRESSURE = 1012.2;
        values.put(Contract.WeatherContract.COLOMUN_PRESSURE,TEST_PRESSURE);
        Long TEST_HUMIDITY = 61L;
        values.put(Contract.WeatherContract.COLOMUN_HUMIDITY,TEST_HUMIDITY);
        Long TEST_WEATHER_ID = 500L;
        values.put(Contract.WeatherContract.COLOMUN_WEATHER_ID,TEST_WEATHER_ID);
        String TEST_SHORT_DESC = "light rain";
        values.put(Contract.WeatherContract.COLOMUN_SHORT_DESC,TEST_SHORT_DESC);
        Double TEST_WIND_SPEED = 25.5;
        values.put(Contract.WeatherContract.COLOMUN_WIND_SPEED,TEST_WIND_SPEED);
        Long TEST_WIND_DIRECTION = 48L;
        values.put(Contract.WeatherContract.COLOMUN_WIND_DIRECTION,TEST_WIND_DIRECTION);

        return values;

    }

    public void testReadFromDB() throws Exception{

    }
    @Override
    protected void tearDown() throws Exception {
        sqLiteDatabase.close();
        super.tearDown();
    }
}
