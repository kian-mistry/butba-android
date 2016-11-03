package com.kian.butba.settings;

import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.kian.butba.R;
import com.kian.butba.database.sqlite.TableBowler;
import com.kian.butba.database.sqlite.entities.Bowler;

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

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_settings);

            //Retrieve shared preferences.
            prefBowlerDetails = getActivity().getSharedPreferences("bowler_details", Context.MODE_PRIVATE);
            bowlerId = prefBowlerDetails.getInt("bowler_id", 0);
            bowlerName = prefBowlerDetails.getString("bowler_name", null);

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

            //Retrieve a list of all of BUTBA members from the local SQLite database.
            List<Bowler> bowlers = new TableBowler(this.getActivity().getBaseContext()).getAllBowlers();
            int bowlersSize = bowlers.size();

            entries = new CharSequence[bowlersSize];
            entryValues = new CharSequence[bowlersSize];

            for(int i = 0; i < bowlersSize; i++) {
                entryValues[i] = String.valueOf(bowlers.get(i).getId());
                entries[i] = bowlers.get(i).getFullName();
            }
            populateListPreference(lpButbaMembers, entries, entryValues);
        }
    }
}
