package com.kian.butba.settings;

import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import com.kian.butba.entities.Bowler;
import com.kian.butba.file.FileOperations;
import com.kian.butba.file.FileParsers;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Kian Mistry on 01/11/16.
 */

public class SettingsMethods extends PreferenceFragment  {

    /* PROFILE ************************************************************************************/
    protected SharedPreferences prefBowlerDetails;
    protected int bowlerId;
    protected String bowlerName;
    protected int bowlerGender;
	protected int bowlerStudentStatus;
	protected int bowlerAcademicYear;

    protected SwitchPreference spButbaMember;
    protected ListPreference lpBowlerGender;
    protected ListPreference lpStudentStatus;
	protected ListPreference lpAcademicYear;
    protected ListPreference lpButbaMembers;

    protected final CharSequence[] genderEntryValues = new CharSequence[] {"0", "1", "2"};
    protected final CharSequence[] genderEntries = new CharSequence[] {"None", "Male", "Female"};

	protected final CharSequence[] studentStatusEntryValues = new CharSequence[] {"0", "1", "2"};
	protected final CharSequence[] studentStatusEntries = new CharSequence[] {"None", "Student", "Ex-Student"};

	protected final CharSequence[] academicYearEntryValues = new CharSequence[] {"1", "2"};
	protected final CharSequence[] academicYearEntries = new CharSequence[] {"2015/16", "2016/17"};

    protected CharSequence[] bowlerEntries = null;
    protected CharSequence[] bowlerEntryValues = null;

	/* NOTIFICATIONS ******************************************************************************/
	protected SharedPreferences prefNotifications;
	protected boolean eventNotification;
	protected boolean avgRnkNotification;

	protected SwitchPreference spEventNotification;
	protected SwitchPreference spAvgRnkNotification;

	/* ********************************************************************************************/

    /** Set the values and list options for the given list preference.
     *
     * @param listPreference The list preference that needs to be set.
     * @param entryValues The identifiers for each entry value.
     * @param entries The entries that need to populate the list preference.
     */
    protected void setListPreferenceEntries(ListPreference listPreference, CharSequence[] entryValues, CharSequence[] entries) {
        listPreference.setEntryValues(entryValues);
        listPreference.setEntries(entries);
    }

	/**
	 * Enable the list preference which shows the bowler's names according to the criteria given.
	 */
	protected void enableBowlersNameListPreference() {
		if(bowlerGender > 0 || bowlerStudentStatus > 0) {
			lpButbaMembers.setEnabled(true);
		}

		setListPreferenceSummary(lpBowlerGender, genderEntries, bowlerGender);
		setListPreferenceSummary(lpStudentStatus, studentStatusEntries, bowlerStudentStatus);
		setListPreferenceSummary(lpAcademicYear, academicYearEntries, bowlerAcademicYear - 1);
	}

	/**
	 * Disables the list preference which shows the bowler's names according to the criteria given.
	 */
    protected void disableBowlersNameListPreference() {
	    if(bowlerGender == 0 || bowlerStudentStatus == 0) {
		    lpButbaMembers.setEnabled(false);
	    }

        setListPreferenceSummary(lpBowlerGender, genderEntries, bowlerGender);
	    setListPreferenceSummary(lpStudentStatus, studentStatusEntries, bowlerStudentStatus);
	    setListPreferenceSummary(lpAcademicYear, academicYearEntries, bowlerAcademicYear - 1);
    }

	/**
	 * Disables a list preference.
	 *
	 * @param listPreference The list preference to be disabled.
	 * @param entryValue 0 => Disables list preference; 1 => Enables list preference.
	 */
	protected void disableListPreference(ListPreference listPreference, int entryValue) {
		if(entryValue == 0) {
			listPreference.setEnabled(false);
		}
		else {
			listPreference.setEnabled(true);
		}
	}

	/**
	 * Sets the summary of a list preference.
	 *
	 * @param listPreference The list preference.
	 * @param entries The entries which the list preference hold.
	 * @param entryValue The entry values which the list preference hold.
	 */
	protected void setListPreferenceSummary(ListPreference listPreference, CharSequence[] entries, int entryValue) {
		if(entryValue >= 0) {
			listPreference.setSummary(entries[entryValue]);
		}
	}

	/**
	 * Sets the summary for the bowler's list preference.
	 *
	 * @param index Position of which the bowler is stored in the list preference.
	 * @return 0 = Guest; Otherwise the name of the bowler.
	 */
    protected String setBowlerListPreferenceSummary(int index) {
        if(index > 0) {
            //Obtains the index of the bowler selected which corresponds its position in the array.
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

	/**
	 * Populates the list preference of BUTBA members, according to their gender, student status and academic year.
	 *
	 * @param genderIndex The index of the gender.
	 * @param studentStatusIndex The index of the student status.
	 * @param academicYearIndex The index of the academic year.
	 */
	protected void obtainSpecificSetOfBowlers(int genderIndex, int studentStatusIndex, int academicYearIndex) {

		if(FileOperations.fileExists(getActivity().getFilesDir() + FileOperations.INTERNAL_SERVER_DIR, FileOperations.ALL_BOWLERS_FILE, ".json")) {
			List<Bowler> bowlersList = FileParsers.parseBowlersList(getActivity(), genderIndex, studentStatusIndex, academicYearIndex);

			if(bowlersList != null) {
				int size = bowlersList.size();

				bowlerEntryValues = new CharSequence[size];
				bowlerEntries = new CharSequence[size];

				for(int i = 0; i < size; i++) {
					bowlerEntryValues[i] = String.valueOf(bowlersList.get(i).getId());
					bowlerEntries[i] = bowlersList.get(i).getFullName();
				}

				setListPreferenceEntries(lpButbaMembers, bowlerEntryValues, bowlerEntries);
			}
		}
	}

	/**
	 * Retrieves the bowler's latest status.
	 *
	 * @param bowlerId The ID of the bowler.
	 */
	protected void obtainBowlersStatus(int bowlerId) {

		if(FileOperations.fileExists(getActivity().getFilesDir() + FileOperations.INTERNAL_SERVER_DIR, FileOperations.LATEST_STATUS_FILE + "_" + bowlerId, ".json")) {
			FileParsers.parseBowlersStatus(getActivity(), bowlerId);
		}
	}
}