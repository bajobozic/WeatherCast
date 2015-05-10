package bozic.bajo.weathercast;


import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toolbar;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import bozic.bajo.weathercast.data.Contract;

/**
 * Created by savo on 8.2.2015.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String CURRENT_DAY_MILISEC = "CDM";
    private String mLocation;
    public MyAdapter myAdapter;
    public static final int LOADER_TAG = 0;
    private final String TAG = getClass().getSimpleName();


    public ForecastFragment() {
        Log.i(TAG, "ForecastFragment()");
    }


    private void updateWeather() {


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = preferences.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));

        if (location != mLocation) {
            FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity().getApplicationContext());
            weatherTask.execute(location);
            mLocation = location;
        }



    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onViewCreate()");
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);


        myAdapter = new MyAdapter(getActivity(), null, 0);

        listView.setAdapter(myAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {
                    String loc = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
                    Uri temp = Contract.WeatherContract.URI.buildUpon().appendPath(loc).appendPath(Long.toString(cursor.getLong(cursor.getColumnIndex(Contract.WeatherContract.COLOMUN_DATE)))).build();
                    Intent detalIntent = new Intent(getActivity(), DetailActivity.class);
                    detalIntent.setData(temp);
                    startActivity(detalIntent);
                }

            }
        });


        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated()");
//        updateWeather();
       /* SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(CURRENT_DAY_MILISEC, System.currentTimeMillis()).apply();*/

        getLoaderManager().initLoader(LOADER_TAG, null, this);

    }

    public void RestartLoader() {
        updateWeather();
        getLoaderManager().restartLoader(LOADER_TAG, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.i(TAG, "onCreateOptionsMenu()");
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "onOptionItemSelected()");
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.i(TAG, "onCreateLoader()");

       // updateWeather();

        Uri uri = Contract.WeatherContract.URI.buildUpon().appendPath(mLocation).build();
        /*String selection = new String(Contract.WeatherContract.COLOMUN_DATE + " < ? ");
        String[] selectArgs = new String[]{Long.toString(System.currentTimeMillis() / 1000)};
*/
        return new CursorLoader(getActivity(), uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.i(TAG, "onLoadFinished()");
        myAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.i(TAG, "onLoaderReset()");
        myAdapter.swapCursor(null);

    }


    @Override
    public void onPause() {
        Log.i(TAG, "onPause()");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i(TAG, "onStop()");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.i(TAG, "onDestroyView()");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy()");
        super.onDestroy();
    }
}
