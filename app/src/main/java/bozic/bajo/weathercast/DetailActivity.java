package bozic.bajo.weathercast;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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


public class DetailActivity extends ActionBarActivity {
    public final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        toolbar.setTitle("WeatherCast");
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /*Intent i = getSupportParentActivityIntent();
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);*/
                finish();
            }
        });
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu()");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "onOptionsItemSelected()");
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
        public final String TAG = getClass().getSimpleName();
        private String mMessage;
        public static final int DETAIL_LOADER_ID = 1;
        private ViewGroup rootGroup;

        private ImageView mImageView;
        private TextView mTextShortDesc;
        private TextView mTextDate;
        private TextView mTextMin;
        private TextView mTextMax;
        private TextView mTextHumidity;
        private TextView mTextPressure;
        private TextView mTextWindSpeed;
        private TextView mTextWindDirection;


        private String GetReadableDateString(long time) {
           /* Date date = new Date(time*1000);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E,MMM d");
            return simpleDateFormat.format(date);*/
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
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            if (preferences.getString(getActivity().getString(R.string.pref_degrees_key), "metric").equals("metric"))
                res = t.toString();
            else {
                t = 9 * t / 5 + 32;

                res = t.toString();
            }

            return res;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.detailfragment, menu);
            MenuItem item = (MenuItem) menu.findItem(R.id.action_share);
            ShareActionProvider actionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
            if (actionProvider != null) {
                actionProvider.setShareIntent(GetSharedIntent());
            }
            super.onCreateOptionsMenu(menu, inflater);
        }

        private Intent GetSharedIntent() {
            Intent intent = new Intent(Intent.ACTION_SEND);
//            intent.setType("text/plain");
            intent.setType("image/*");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            intent.putExtra(Intent.EXTRA_TEXT, mMessage);
            return intent;
        }

        public DetailFragment() {
            Log.i(TAG, "DetailFragmenConstructor()");
            setHasOptionsMenu(true);

        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Log.i(TAG, "onCreateView()");
            View rootView = inflater.inflate(R.layout.fragment_detail1, container, false);
            mImageView = (ImageView) rootView.findViewById(R.id.imageView2);
            mTextShortDesc = (TextView) rootView.findViewById(R.id.textViewShortDesc);
            mTextDate = (TextView) rootView.findViewById(R.id.textViewDate);
            mTextMin = (TextView) rootView.findViewById(R.id.textViewMin);
            mTextMax = (TextView) rootView.findViewById(R.id.textViewMax);
            mTextHumidity = (TextView) rootView.findViewById(R.id.textViewHumidity);
            mTextPressure = (TextView) rootView.findViewById(R.id.textViewPressure);
            mTextWindSpeed = (TextView) rootView.findViewById(R.id.textViewWindSpeed);
            mTextWindDirection = (TextView) rootView.findViewById(R.id.textViewWindDirection);

            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            Log.i(TAG, "onActivityCreated()");
            getLoaderManager().initLoader(DETAIL_LOADER_ID, null, this);
            super.onActivityCreated(savedInstanceState);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Log.i(TAG, "onCreateLoader()");
            Intent intent = getActivity().getIntent();
            if (intent == null || intent.getData() == null) {
                return null;

            }
            return new CursorLoader(getActivity(), intent.getData(), null, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            Log.i(TAG, "onLoadFinished()");
            if (data == null) {
                return;
            }
            if (!data.moveToFirst())
                return;

            String shortDesc = data.getString(data.getColumnIndex(Contract.WeatherContract.COLOMUN_SHORT_DESC));
            if (shortDesc != null) {
                Log.i(TAG, "shortDesc is not null");
            }
            long Date = data.getLong(data.getColumnIndex(Contract.WeatherContract.COLOMUN_DATE));
            double Min = data.getDouble(data.getColumnIndex(Contract.WeatherContract.COLOMUN_MIN));
            double Max = data.getDouble(data.getColumnIndex(Contract.WeatherContract.COLOMUN_MAX));
            long Humidity = data.getLong(data.getColumnIndex(Contract.WeatherContract.COLOMUN_HUMIDITY));
            double Pressure = data.getDouble(data.getColumnIndex(Contract.WeatherContract.COLOMUN_PRESSURE));
            double WindSpeed = data.getDouble(data.getColumnIndex(Contract.WeatherContract.COLOMUN_WIND_SPEED));
            long WindDirection = data.getLong(data.getColumnIndex(Contract.WeatherContract.COLOMUN_WIND_DIRECTION));
            long iconId = data.getLong(data.getColumnIndex(Contract.WeatherContract.COLOMUN_WEATHER_ID));

            if ((int) iconId == 800) {
                mImageView.setImageResource(R.drawable.clear_sky);

            } else if ((int) iconId == 801) {
                mImageView.setImageResource(R.drawable.few_clouds);

            } else if ((int) iconId == 802) {
                mImageView.setImageResource(R.drawable.scattered_clouds);

            } else if (((int) iconId == 803) || ((int) iconId == 804)) {
                mImageView.setImageResource(R.drawable.clouds);

            } else if (((int) iconId >= 600) && ((int) iconId <= 622)) {
                mImageView.setImageResource(R.drawable.snow);

            } else if (((int) iconId >= 500) && ((int) iconId <= 504)) {
                mImageView.setImageResource(R.drawable.rain);

            } else if (((int) iconId >= 511) && ((int) iconId <= 531)) {
                mImageView.setImageResource(R.drawable.shower_rain);

            } else if ((int) iconId >= 200 && (int) iconId <= 232) {
                mImageView.setImageResource(R.drawable.thunderstorm);

            }
            mTextShortDesc.setText(shortDesc);
            mTextDate.setText(GetReadableDateString(Date));
            mTextMin.setText(FormatTemperature(Min) + "\u00B0");
            mTextMax.setText(FormatTemperature(Max) + "\u00B0");
            mTextHumidity.setText(Long.toString(Humidity) + "%");
            mTextPressure.setText(Double.toString(Pressure) + "hPa");
            mTextWindSpeed.setText(Double.toString(WindSpeed) + "m/s");
            if (WindDirection >= 0 && WindDirection <= 10) {
                mTextWindDirection.setText("E");


            } else if ((WindDirection >= 0 && WindDirection <= 10) || (WindDirection > 350 && WindDirection <= 360)) {
                mTextWindDirection.setText("E");


            } else if ((WindDirection > 10 && WindDirection <= 80)) {
                mTextWindDirection.setText("NE");


            } else if ((WindDirection > 80 && WindDirection <= 100)) {
                mTextWindDirection.setText("N");


            } else if (WindDirection > 100 && WindDirection <= 170) {
                mTextWindDirection.setText("NW");


            } else if (WindDirection > 170 && WindDirection <= 190) {
                mTextWindDirection.setText("W");


            } else if (WindDirection > 190 && WindDirection <= 260) {
                mTextWindDirection.setText("SW");


            } else if (WindDirection > 260 && WindDirection <= 280) {
                mTextWindDirection.setText("S");


            } else if (WindDirection > 280 && WindDirection <= 350) {
                mTextWindDirection.setText("SE");


            }

//            mTextWindDirection.setText(Long.toString(WindDirection));


        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            Log.i(TAG, "onLoaderReset()");

        }
    }
}
