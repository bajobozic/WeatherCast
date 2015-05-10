package bozic.bajo.weathercast.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by savo on 9.4.2015.
 */
public class Contract {

    public static final String CONTENT_AUTHORITY = "bozic.bajo.weathercast";
    //psf
    public static final Uri BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final class LocationContract implements BaseColumns {


        //table name
        public static final String TABLE ="location";

        public static final Uri URI = BASE_URI.buildUpon().appendPath(TABLE).build();

        public static final String CONTENT_DIR_TYPE = "vnd.android.cursor.dir" +"/"+CONTENT_AUTHORITY +"/"+ TABLE;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item" + "/"+CONTENT_AUTHORITY +"/"+ TABLE;
        //colomuns names
        public static final  String COLOMUN_QUERY = "query";

        public static final  String COLOMUN_CITY_NAME = "city_name";

        public static final  String COLOMUN_LAT = "lat";

        public static final  String COLOMUN_LON = "lon";


    }

    public static final class WeatherContract implements BaseColumns {
        //table name
        public static final String TABLE ="weather";

        public static final Uri URI = BASE_URI.buildUpon().appendPath(TABLE).build();


        public static final String CONTENT_DIR_TYPE = "vnd.android.cursor.dir" +"/"+CONTENT_AUTHORITY +"/"+ TABLE;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item" + "/"+CONTENT_AUTHORITY +"/"+ TABLE;


        //colomuns names
        public static final  String COLOMUN_DATE = "date";

        public static final  String COLOMUN_LOC_ID = "loc_id";

        public static final  String COLOMUN_MIN = "min";

        public static final  String COLOMUN_MAX = "max";

        public static final  String COLOMUN_PRESSURE = "pressure";

        public static final  String COLOMUN_HUMIDITY = "humidity";

        public static final  String COLOMUN_WEATHER_ID = "weather_id";

        public static final String COLOMUN_SHORT_DESC = "short_desc";

        public static final  String COLOMUN_WIND_SPEED = "wind_speed";

        public static final String COLOMUN_WIND_DIRECTION = "wind_direction";





    }

}
