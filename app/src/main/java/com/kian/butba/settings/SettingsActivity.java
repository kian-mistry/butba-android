package com.kian.butba.settings;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.kian.butba.R;
import com.kian.butba.database.QueryFetcher;
import com.kian.butba.database.QueryMap;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Kian Mistry on 26/10/16.
 */

public class SettingsActivity extends AppCompatActivity {

    private Toolbar toolbar;
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


        //Set up Settings fragment.
        fragment = new SettingsFragment();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content_main, fragment, fragment.getTag())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragment {

        private QueryMap bowlersQueryMap;
        private QueryFetcher queryFetcher;

        private ListPreference listButbaMembers;

        private CharSequence[] entries;
        private CharSequence[] entryValues;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_settings);

            //Set up list preference.
            listButbaMembers = (ListPreference) findPreference("pref_butba_member");
            setListPreference(listButbaMembers, entries, entryValues);
            listButbaMembers.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    setListPreference(listButbaMembers, entries, entryValues);
                    return false;
                }
            });

            listButbaMembers.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    /* Obtains the index of the new value which corresponds to the elements in the
                     * entryValues variable.
                     *
                     * The position of each element in the entryValues variable, corresponds to each
                     * position of each element in the entries variable.
                     */
                    int position = Arrays.asList(entryValues).indexOf(newValue);
                    preference.setSummary(entries[position]);

                    return true;
                }
            });

            //Set up query fetcher to retrieve a list of all of BUTBA members.
            bowlersQueryMap = new QueryMap(QueryMap.QueryTag.SELECT_ALL_BOWLERS, "", "");
            queryFetcher = new QueryFetcher(new QueryFetcher.AsyncResponse() {
                @Override
                public void onProcessResults(ArrayList<String[]> output) {
                    int outputSize = output.size();

                    entries = new CharSequence[outputSize];
                    entryValues = new CharSequence[outputSize];

                    for(int i = 0; i < output.size(); i++) {
                        entryValues[i] = output.get(i)[0];
                        entries[i] = output.get(i)[1];
                    }

                    Log.d("DB", entries.toString());

                    setListPreference(listButbaMembers, entries, entryValues);
                }
            });
            queryFetcher.execute(bowlersQueryMap);

        }

        private void setListPreference(ListPreference listPreference, CharSequence[] entries, CharSequence[] entryValues) {
            listPreference.setEntries(entries);
            listPreference.setEntryValues(entryValues);
        }
    }
}
