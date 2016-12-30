package com.kian.butba.settings;

import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import com.kian.butba.database.sqlite.entities.Bowler;
import com.kian.butba.database.sqlite.tables.TableBowler;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Kian Mistry on 01/11/16.
 */

public class SettingsMethods extends PreferenceFragment  {

    protected SharedPreferences prefBowlerDetails;
    protected int bowlerId;
    protected String bowlerName;
    protected int bowlerGender;

    protected SwitchPreference spButbaMember;
    protected ListPreference lpBowlerGender;
    protected ListPreference lpButbaMembers;

    protected final CharSequence[] genderEntryValues = new CharSequence[] {"0", "1", "2"};
    protected final CharSequence[] genderEntries = new CharSequence[] {"None", "Male", "Female"};

    protected CharSequence[] bowlerEntries = null;
    protected CharSequence[] bowlerEntryValues = null;

    /** Set the values and list options for the given list preference.
     *
     * @param listPreference
     * @param entryValues
     * @param entries
     */
    protected void setListPreferenceEntries(ListPreference listPreference, CharSequence[] entryValues, CharSequence[] entries) {
        listPreference.setEntryValues(entryValues);
        listPreference.setEntries(entries);
    }

    protected void disableBowlersNameListPreference(int index) {
        if(index == 0) {
            lpButbaMembers.setEnabled(false);
        }
        else {
            lpButbaMembers.setEnabled(true);
        }

        setGenderListPreferenceSummary(index);
    }

    protected void setGenderListPreferenceSummary(int index) {
        if(index >= 0) {
            lpBowlerGender.setSummary(genderEntries[index]);
        }
    }

    protected String setBowlerListPreferenceSummary(int index) {
        if(index > 0) {
            //Obtains the index of the bowler selected which corresponds its position in the arrray.
            int position = Arrays.asList(bowlerEntryValues).indexOf(String.valueOf(index));
            CharSequence bowlerName = bowlerEntries[position];
            lpButbaMembers.setSummary(bowlerName);

            return bowlerName.toString();
        }
        else {
            lpButbaMembers.setSummary("Guest");
            return null;
        }
    }

    /** Retrieve a list of gender-specific BUTBA members from the local SQLite database
     * and set their entries and entry values so that it can be displayed in a list preference.
     *
     * @param index The index of the gender: 1 => Male; 2 => Female.
     */
    protected void obtainGenderSpecificBowlers(int index) {
        String gender = (index == 1) ? "M" : (index == 2) ? "F" : "";

        List<Bowler> bowlers = new TableBowler(getActivity().getBaseContext()).getGenderSpecificBowlers(gender);
        int size = bowlers.size();

        bowlerEntryValues = new CharSequence[size];
        bowlerEntries = new CharSequence[size];

        for(int i = 0; i < size; i++) {
            bowlerEntryValues[i] = String.valueOf(bowlers.get(i).getId());
            bowlerEntries[i] = bowlers.get(i).getFullName();
        }

        setListPreferenceEntries(lpButbaMembers, bowlerEntryValues, bowlerEntries);
    }
}
