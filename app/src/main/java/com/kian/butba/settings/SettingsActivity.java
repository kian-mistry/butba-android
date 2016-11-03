package com.kian.butba.settings;

import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.kian.butba.R;
import com.kian.butba.database.server.AsyncDelegate;
import com.kian.butba.database.server.QueryTag;
import com.kian.butba.database.server.TablesFetcher;

import java.util.List;

/**
 * Created by Kian Mistry on 26/10/16.
 */

public class SettingsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private SettingsFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set up toolbar.
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Disallow navigation drawer to open in the Settings activity.
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        //Set up Settings fragment.
        fragment = new SettingsFragment();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content_main, fragment, fragment.getTag())
                .commit();
    }

    public static class SettingsFragment extends SettingsListener {

        private TablesFetcher bowlersFetcher;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_settings);

            //Retrieve shared preferences.
            sharedPreferences = getActivity().getSharedPreferences("bowler_details", Context.MODE_PRIVATE);
            bowlerId = sharedPreferences.getInt("bowler_id", 0);
            bowlerName = sharedPreferences.getString("bowler_name", null);

            //Initialise preferences.
            cbpButbaMember = (CheckBoxPreference) findPreference("pref_is_butba_member");
            lpButbaMembers = (ListPreference) findPreference("pref_butba_member");

            //Set up check box preference.
            setListPreferenceSummary();
            cbpButbaMember.setOnPreferenceChangeListener(this);

            //Set up list preference.
            populateListPreference(lpButbaMembers, entries, entryValues);
            lpButbaMembers.setOnPreferenceClickListener(this);
            lpButbaMembers.setOnPreferenceChangeListener(this);

            //Set up query fetcher to retrieve a list of all of BUTBA members.
            bowlersFetcher = new TablesFetcher(new AsyncDelegate() {
                @Override
                public void onProcessResults(List<String[]> results) {
                    int outputSize = results.size();

                    entries = new CharSequence[outputSize];
                    entryValues = new CharSequence[outputSize];

                    for(int i = 0; i < results.size(); i++) {
                        entryValues[i] = results.get(i)[0];
                        entries[i] = results.get(i)[1];
                    }
                    populateListPreference(lpButbaMembers, entries, entryValues);
                }
            });
            bowlersFetcher.execute(QueryTag.GET_ALL_BOWLERS);
        }
    }
}
