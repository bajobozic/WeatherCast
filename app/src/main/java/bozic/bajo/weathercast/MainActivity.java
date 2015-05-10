package bozic.bajo.weathercast;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;


public class MainActivity extends ActionBarActivity {
    public static final String FORECAST_FRAGMENT_TAG = "MAT";
    private final String TAG = getClass().getSimpleName();

    public static final String AUTHORITY = "bozic.bajo.weathercast";
    public static final String ACCOUNT_TYPE = "bozic.bajo.weathercast.authenticator";
    public static final String ACCOUNT = "dummy acount";
    Account mAccount;
    public MainActivity() {
        super();
        Log.i(TAG, "MainActivity()");
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop()");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy()");
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause()");
        super.onPause();
    }



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate()");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);

        mAccount = new Account(ACCOUNT, ACCOUNT_TYPE);
        AccountManager accountManager = (AccountManager) this.getSystemService(ACCOUNT_SERVICE);
        if (accountManager.addAccountExplicitly(mAccount,null,null)== false) {
            Log.i(TAG, "account not created bail out");


        }

        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment(), FORECAST_FRAGMENT_TAG)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu()");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");

        ForecastFragment ff = (ForecastFragment) getSupportFragmentManager().findFragmentByTag(FORECAST_FRAGMENT_TAG);
        ff.RestartLoader();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Log.i(TAG, "onOptionItemSelected()");
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));

            return true;
        }

        if (id == R.id.action_map) {
            Intent mapIntent = new Intent(Intent.ACTION_VIEW);
            String loc = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
            Uri mapUri = Uri.parse("geo:0,0?").buildUpon().appendQueryParameter("q", loc).build();
            mapIntent.setData(mapUri);
            if (mapIntent.resolveActivity(this.getPackageManager()) != null)
                startActivity(mapIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
