package com.kian.butba.settings;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.SwitchPreference;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.firebase.messaging.FirebaseMessaging;
import com.kian.butba.R;
import com.kian.butba.notifications.NotificationConstants;

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

    public static class SettingsFragment extends SettingsMethods {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_settings);

            //Initialise shared preferences.
            prefBowlerDetails = getActivity().getSharedPreferences("bowler_details", Context.MODE_PRIVATE);
            prefNotifications = getActivity().getSharedPreferences("notifications", Context.MODE_PRIVATE);

            //Initialise preferences.
            spButbaMember = (SwitchPreference) findPreference("pref_is_butba_member");
            lpBowlerGender = (ListPreference) findPreference("pref_gender");
			lpStudentStatus = (ListPreference) findPreference("pref_student_status");
	        lpAcademicYear = (ListPreference) findPreference("pref_academic_year");
            lpButbaMembers = (ListPreference) findPreference("pref_butba_member");

	        spEventNotification = (SwitchPreference) findPreference("pref_event_notification");
	        spAvgRnkNotification = (SwitchPreference) findPreference("pref_avg_rnk_notification");

            //Set list preference options.
            setListPreferenceEntries(lpBowlerGender, genderEntryValues, genderEntries);
	        setListPreferenceEntries(lpStudentStatus, studentStatusEntryValues, studentStatusEntries);
	        setListPreferenceEntries(lpAcademicYear, academicYearEntryValues, academicYearEntries);

            /* Set up listeners */
            //Set change listener for the BUTBA member switch preference.
            spButbaMember.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean isChecked = Boolean.valueOf(newValue.toString());

                    //Store whether user is a BUTBA member in a shared preference.
                    if(!isChecked) {
                        Editor editor = prefBowlerDetails.edit();
                        editor.putInt("bowler_id", 0);
                        editor.putString("bowler_name", null);
                        editor.putInt("bowler_gender", 0);
	                    editor.putInt("bowler_student_status", 0);
	                    editor.putInt("bowler_academic_year", 2);

                        editor.commit();

                        lpBowlerGender.setValueIndex(0);
	                    lpStudentStatus.setValueIndex(0);
	                    lpAcademicYear.setValueIndex(1);

	                    setListPreferenceSummary(lpBowlerGender, genderEntries, 0);
	                    setListPreferenceSummary(lpStudentStatus, studentStatusEntries, 0);
	                    setListPreferenceSummary(lpAcademicYear, academicYearEntries, 1);
                    }
                    else if(isChecked && lpBowlerGender.findIndexOfValue(lpBowlerGender.getEntry().toString()) == -1) {
	                    disableListPreference(lpButbaMembers, 0);
                    }

                    return true;
                }
            });

            //Set change listener for the bowler's gender list preference.
            lpBowlerGender.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    bowlerGender = lpBowlerGender.findIndexOfValue(newValue.toString());

	                Editor editor = prefBowlerDetails.edit();

	                /*
	                 * Obtain bowlers based on which gender is chosen,
	                 * which student status chosen and
	                 * the academic year chosen.
	                 */
	                if(
			                (bowlerGender == 1 || bowlerGender == 2) &&
					        (bowlerStudentStatus == 1 || bowlerStudentStatus == 2) &&
					        (bowlerAcademicYear == 1 || bowlerAcademicYear == 2)

			        ) {
		                enableBowlersNameListPreference();
		                obtainSpecificSetOfBowlers(bowlerGender, bowlerStudentStatus, bowlerAcademicYear);
	                }
	                else {
		                //User cannot select a bowler until the gender, student status and academic year has been selected.
		                disableBowlersNameListPreference();

		                //Reset previously selected bowler if the user selects a different student status.
		                editor.putInt("bowler_id", 0);
		                editor.putString("bowler_name", null);

		                //Remove previously selected bowler's name from the summary.
		                lpButbaMembers.setSummary("Select your name.");
	                }

                    editor.putInt("bowler_gender", bowlerGender);
                    editor.commit();

	                //Set summary once option is chosen.
	                setListPreferenceSummary(lpBowlerGender, genderEntries, bowlerGender);

                    return true;
                }
            });

	        //Set change listener for the student status list preference.
	        lpStudentStatus.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
		        @Override
		        public boolean onPreferenceChange(Preference preference, Object newValue) {
			        bowlerStudentStatus = lpStudentStatus.findIndexOfValue(newValue.toString());

			        Editor editor = prefBowlerDetails.edit();

			        /*
	                 * Obtain bowlers based on which gender is chosen,
	                 * which student status chosen and
	                 * the academic year chosen.
	                 */
			        if(
					        (bowlerGender == 1 || bowlerGender == 2) &&
							(bowlerStudentStatus == 1 || bowlerStudentStatus == 2) &&
							(bowlerAcademicYear == 1 || bowlerAcademicYear == 2)

					) {
				        enableBowlersNameListPreference();
				        obtainSpecificSetOfBowlers(bowlerGender, bowlerStudentStatus, bowlerAcademicYear);
			        }
			        else {
				        //User cannot select a bowler until the gender, student status and academic year has been selected.
				        disableBowlersNameListPreference();

				        //Reset previously selected bowler if the user selects a different student status.
				        editor.putInt("bowler_id", 0);
				        editor.putString("bowler_name", null);

				        //Remove previously selected bowler's name from the summary.
				        lpButbaMembers.setSummary("Select your name.");
			        }

			        editor.putInt("bowler_student_status", bowlerStudentStatus);
			        editor.commit();

			        //Set summary once option is chosen.
			        setListPreferenceSummary(lpStudentStatus, studentStatusEntries, bowlerStudentStatus);

			        return true;
		        }
	        });

	        lpAcademicYear.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
		        @Override
		        public boolean onPreferenceChange(Preference preference, Object newValue) {
			        bowlerAcademicYear = lpAcademicYear.findIndexOfValue(newValue.toString()) + 1;

			        Editor editor = prefBowlerDetails.edit();

			        /*
	                 * Obtain bowlers based on which gender is chosen,
	                 * which student status chosen and
	                 * the academic year chosen.
	                 */
			        if(
					        (bowlerGender == 1 || bowlerGender == 2) &&
							(bowlerStudentStatus == 1 || bowlerStudentStatus == 2) &&
							(bowlerAcademicYear == 1 || bowlerAcademicYear == 2)
					) {
				        enableBowlersNameListPreference();
				        obtainSpecificSetOfBowlers(bowlerGender, bowlerStudentStatus, bowlerAcademicYear);
			        }
			        else {
				        //User cannot select a bowler until the gender, student status and academic year has been selected.
				        disableBowlersNameListPreference();

				        //Reset previously selected bowler if the user selects a different student status.
				        editor.putInt("bowler_id", 0);
				        editor.putString("bowler_name", null);

				        //Remove previously selected bowler's name from the summary.
				        lpButbaMembers.setSummary("Select your name.");
			        }

			        editor.putInt("bowler_academic_year", bowlerAcademicYear);
			        editor.commit();

			        //Set summary once option is chosen.
			        setListPreferenceSummary(lpAcademicYear, academicYearEntries, bowlerAcademicYear - 1);

			        return true;
		        }
	        });

            //Set change listener for the BUTBA members list preference.
            lpButbaMembers.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    int bowlerId = Integer.parseInt(newValue.toString());

                    String bowlerName = setBowlerListPreferenceSummary(bowlerId);

                    Editor editor = prefBowlerDetails.edit();
                    editor.putInt("bowler_id", bowlerId);
                    editor.putString("bowler_name", bowlerName);
                    editor.commit();

                    return true;
                }
            });

	        //Set change listener for the event notification switch preference.
	        spEventNotification.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
		        @Override
		        public boolean onPreferenceChange(Preference preference, Object newValue) {
			        boolean eventNotification = Boolean.parseBoolean(newValue.toString());

			        //Allows the user to subscribe/unsubscribe to/from a particular notification.
			        if(eventNotification) {
				        FirebaseMessaging.getInstance().subscribeToTopic(NotificationConstants.NOTIFICATION_EVENTS);
			        }
			        else {
				        FirebaseMessaging.getInstance().unsubscribeFromTopic(NotificationConstants.NOTIFICATION_EVENTS);
			        }

			        Editor editor = prefNotifications.edit();
			        editor.putBoolean("event_notification", eventNotification);
			        editor.commit();

			        return true;
		        }
	        });

	        //Set change listener for the average/ranking notification switch preference.
	        spAvgRnkNotification.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
		        @Override
		        public boolean onPreferenceChange(Preference preference, Object newValue) {
					boolean avgRnkNotification = Boolean.parseBoolean(newValue.toString());

			        //Allows the user to subscribe/unsubscribe to/from a particular notification.
			        if(avgRnkNotification) {
				        FirebaseMessaging.getInstance().subscribeToTopic(NotificationConstants.NOTIFICATION_AVERAGES);
				        FirebaseMessaging.getInstance().subscribeToTopic(NotificationConstants.NOTIFICATION_RANKINGS);
			        }
			        else {
				        FirebaseMessaging.getInstance().unsubscribeFromTopic(NotificationConstants.NOTIFICATION_AVERAGES);
				        FirebaseMessaging.getInstance().unsubscribeFromTopic(NotificationConstants.NOTIFICATION_RANKINGS);
			        }

			        Editor editor = prefNotifications.edit();
			        editor.putBoolean("avg_rnk_notification", avgRnkNotification);
			        editor.commit();

			        return true;
		        }
	        });
        }

        @Override
        public void onStart() {
            super.onStart();

            //Retrieve shared preferences.
            bowlerId = prefBowlerDetails.getInt("bowler_id", 0);
            bowlerName = prefBowlerDetails.getString("bowler_name", null);
            bowlerGender = prefBowlerDetails.getInt("bowler_gender", 0);
	        bowlerStudentStatus = prefBowlerDetails.getInt("bowler_student_status", 0);
	        bowlerAcademicYear = prefBowlerDetails.getInt("bowler_academic_year", 2);

	        eventNotification = prefNotifications.getBoolean("event_notification", true);
	        avgRnkNotification = prefNotifications.getBoolean("avg_rnk_notification", true);

            //Set gender list options.
            lpBowlerGender.setValueIndex(bowlerGender);
	        setListPreferenceSummary(lpBowlerGender, genderEntries, bowlerGender);

	        //Set student status list options.
	        lpStudentStatus.setValueIndex(bowlerStudentStatus);
	        setListPreferenceSummary(lpStudentStatus, studentStatusEntries, bowlerStudentStatus);

	        //Set academic year list options.
	        lpAcademicYear.setValueIndex(bowlerAcademicYear - 1);
	        setListPreferenceSummary(lpAcademicYear, academicYearEntries, bowlerAcademicYear - 1);

	        //Obtain bowlers based on above values.
	        if(
			        (bowlerGender == 1 || bowlerGender == 2) &&
					(bowlerStudentStatus == 1 || bowlerStudentStatus == 2) &&
					(bowlerAcademicYear == 1 || bowlerAcademicYear == 2)

			) {
		        obtainSpecificSetOfBowlers(bowlerGender, bowlerStudentStatus, bowlerAcademicYear);
	        }

            setBowlerListPreferenceSummary(bowlerId);
        }
    }
}
