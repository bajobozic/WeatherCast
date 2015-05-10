package bozic.bajo.weathercast;

import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;

import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {
    public final String TAG = getClass().getSimpleName();

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Toolbar toolbar;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            LinearLayout linearLayout = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
            toolbar = (Toolbar)  LayoutInflater.from(this).inflate(R.layout.toolbar, linearLayout, false);
            linearLayout.addView(toolbar, 0);


        } else {
            ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
            ListView listView = (ListView) root.getChildAt(0);
            root.removeAllViews();
            toolbar = (Toolbar)  LayoutInflater.from(this).inflate(R.layout.toolbar, root, false);
            int height;
            TypedValue tv = new TypedValue();
            if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
                height = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            }else{

                height = toolbar.getHeight();
            }

            listView.setPadding(0,height,0,0);

            root.addView(listView);
            root.addView(toolbar);


        }

        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
//        toolbar.setTitleTextColor(getResources().getColor(R.color.color_white));
        toolbar.setTitle("Settings");
        toolbar.setLogo(R.mipmap.ic_launcher);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = this.findViewById(android.R.id.content);

        addPreferencesFromResource(R.xml.pref_general);
        Log.i(TAG, "onCreate()");


        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_location_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_degrees_key)));
       /* Toolbar toolbar = new Toolbar(this);

        Toolbar.LayoutParams params = new Toolbar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        setSupportActionBar(toolbar);
        this.addContentView(toolbar,params);*/
    }
    public void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(this);

       onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), ""));

    }
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String value = newValue.toString();
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(value);
            if (index >= 0)
                preference.setSummary(listPreference.getEntries()[index]);
        } else
            preference.setSummary(value);


        Log.i(TAG, "onPreferenceChange()");
        return true;
    }
}
