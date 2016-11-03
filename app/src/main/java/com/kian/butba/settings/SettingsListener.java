package com.kian.butba.settings;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;

import java.util.Arrays;

/**
 * Created by Kian Mistry on 01/11/16.
 */

public class SettingsListener extends PreferenceFragment implements OnPreferenceChangeListener, OnPreferenceClickListener {

    protected SharedPreferences sharedPreferences;
    protected int bowlerId;
    protected String bowlerName;

    protected CheckBoxPreference cbpButbaMember;
    protected ListPreference lpButbaMembers;

    protected CharSequence[] entries = null;
    protected CharSequence[] entryValues = null;

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        if(preference instanceof CheckBoxPreference) {
            //Store whether user is a BUTBA member in a shared preference.
            boolean isChecked = Boolean.valueOf(newValue.toString());

            Editor editor = sharedPreferences.edit();
            editor.putBoolean("is_butba_member", isChecked);
            editor.commit();

            setListPreferenceSummary(isChecked);

            return true;
        }
        else if(preference instanceof ListPreference) {
            /* Obtains the index of the new value which corresponds to the elements in the
             * entryValues variable.
             *
             * The position of each element in the entryValues variable, corresponds to each
             * position of each element in the entries variable.
             */
            int position = Arrays.asList(entryValues).indexOf(newValue);
            preference.setSummary(entries[position]);

            //Store the selected bowler in a shared preference.
            Editor editor = sharedPreferences.edit();
            editor.putInt("bowler_id", Integer.parseInt((String) newValue));
            editor.putString("bowler_name", entries[position].toString());
            editor.commit();

            return true;
        }

        return false;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        populateListPreference(lpButbaMembers, entries, entryValues);

        if(preference instanceof CheckBoxPreference) {

        }
        else if(preference instanceof ListPreference) {

        }

        return false;
    }

    protected void populateListPreference(ListPreference listPreference, CharSequence[] entries, CharSequence[] entryValues) {
        listPreference.setEntries(entries);
        listPreference.setEntryValues(entryValues);
    }

    protected void setListPreferenceSummary() {
        if(cbpButbaMember.isChecked() && (bowlerId != 0 || bowlerName != null)) {
            lpButbaMembers.setSummary(bowlerName);
        }
        else {
            lpButbaMembers.setSummary("Guest");
        }
    }

    protected void setListPreferenceSummary(boolean newValue) {
        if(newValue && (bowlerId != 0 || bowlerName != null)) {
            lpButbaMembers.setSummary(bowlerName);
        }
        else {
            lpButbaMembers.setSummary("Guest");
        }
    }
}
