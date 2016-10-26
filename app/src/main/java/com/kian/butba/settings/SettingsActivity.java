package com.kian.butba.settings;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.kian.butba.R;

import java.util.Arrays;

/**
 * Created by Kian Mistry on 26/10/16.
 */

public class SettingsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private SettingsFragment fragment = new SettingsFragment();

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
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content_main, fragment, fragment.getTag())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragment {

        private ListPreference listButbaMembers;

        private CharSequence[] entries = {
                "Kian Mistry",
                "Sarah Hood",
                "Paul Marks"
        };

        CharSequence[] entryValues = {
                "41", "2", "5"
        };

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

        }

        private void setListPreference(ListPreference listPreference, CharSequence[] entries, CharSequence[] entryValues) {
            listPreference.setEntries(entries);
            listPreference.setEntryValues(entryValues);
        }
    }
}
