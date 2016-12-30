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
            lpButbaMembers = (ListPreference) findPreference("pref_butba_member");

	        spEventNotification = (SwitchPreference) findPreference("pref_event_notification");
	        spAvgRnkNotification = (SwitchPreference) findPreference("pref_avg_rnk_notification");

            //Set list preference options for bowler's gender.
            setListPreferenceEntries(lpBowlerGender, genderEntryValues, genderEntries);

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

                        editor.commit();

                        lpBowlerGender.setValueIndex(0);
                        setGenderListPreferenceSummary(0);
                    }
                    else if(isChecked && lpBowlerGender.findIndexOfValue(lpBowlerGender.getEntry().toString()) == -1) {
                        disableBowlersNameListPreference(0);
                    }

                    return true;
                }
            });

            //Set change listener for the bowler's gender list preference.
            lpBowlerGender.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    int index = lpBowlerGender.findIndexOfValue(newValue.toString());

                    //Obtain bowlers list depending on which gender is chosen.
                    if(index == 1 || index == 2) {
                        obtainGenderSpecificBowlers(index);
                    }

                    //User cannot select a bowler until the gender has been selected as Male or Female.
                    disableBowlersNameListPreference(index);

                    //Reset previously selected bowler if the user selects a different gender.
                    Editor editor = prefBowlerDetails.edit();
                    editor.putInt("bowler_id", 0);
                    editor.putString("bowler_name", null);
                    editor.putInt("bowler_gender", index);
                    editor.commit();

                    //Remove previously selected bowler's name from the summary.
                    lpButbaMembers.setSummary("Select your name.");

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

	        eventNotification = prefNotifications.getBoolean("event_notification", true);
	        avgRnkNotification = prefNotifications.getBoolean("avg_rnk_notification", true);

            //Set gender list option.
            lpBowlerGender.setValueIndex(bowlerGender);
            if(bowlerGender == 1 || bowlerGender == 2) {
                obtainGenderSpecificBowlers(bowlerGender);
            }

            setGenderListPreferenceSummary(bowlerGender);
            setBowlerListPreferenceSummary(bowlerId);
        }
    }
}
