package com.kian.butba;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.TextView;

import com.kian.butba.database.sqlite.DatabaseOperations;
import com.kian.butba.file.FileOperations;

public class SplashActivity extends Activity {

	private SharedPreferences prefInitialisation;
	private boolean isInitialised = false;

	private boolean isConnected = false;

	private SharedPreferences prefDatabase;
	private boolean tableAcademicYearExists;
	private boolean tableBowlerExists;
	private boolean tableBowlerSeasonsExists;
	private boolean tableEventCodesExists;
	private boolean tableRankingStatusExists;
	private boolean tableStudentStatusExists;
	private boolean tableUniversityExists;

	private TextView tvStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

	    //Initialise views.
	    tvStatus = (TextView) findViewById(R.id.splash_status);

	    //Check whether app has been ran for the first time.
	    prefInitialisation = getSharedPreferences("butba_initialisation", Context.MODE_PRIVATE);
	    isInitialised = prefInitialisation.getBoolean("pref_initialised", false);

	    //Check whether tables have been downloaded.
	    prefDatabase = getSharedPreferences("butba_database", Context.MODE_PRIVATE);
	    tableAcademicYearExists = prefDatabase.getBoolean("pref_table_academic_year", false);
	    tableBowlerExists = prefDatabase.getBoolean("pref_table_bowlers", false);
	    tableBowlerSeasonsExists = prefDatabase.getBoolean("pref_table_bowlers_seasons", false);
	    tableEventCodesExists = prefDatabase.getBoolean("pref_table_event_code", false);
	    tableRankingStatusExists = prefDatabase.getBoolean("pref_table_ranking_status", false);
	    tableStudentStatusExists = prefDatabase.getBoolean("pref_table_student_status", false);
	    tableUniversityExists = prefDatabase.getBoolean("pref_table_university", false);

	    //Wait for x seconds, until the home screen is shown.
	    Thread timer = new Thread() {

		    @Override
		    public void run() {
			    try {
				    //If all data has been downloaded, show the splash screen for 1.5 seconds.
				    if(
						    !tableAcademicYearExists ||
							!tableBowlerExists ||
							!tableBowlerSeasonsExists ||
							!tableEventCodesExists ||
							!tableRankingStatusExists ||
							!tableStudentStatusExists ||
							!tableUniversityExists
					) {
						//Stay on splash screen until all the data has been downloaded.
					    tvStatus.setText("Fetching database...");
					    prefCheckTablesExists();

					    do {
						    sleep(100);
					    } while(!DatabaseOperations.isCompleted[0] &&
							    !DatabaseOperations.isCompleted[1] &&
							    !DatabaseOperations.isCompleted[2] &&
							    !DatabaseOperations.isCompleted[3] &&
							    !DatabaseOperations.isCompleted[4] &&
							    !DatabaseOperations.isCompleted[5] &&
							    !DatabaseOperations.isCompleted[6]);

					    Editor editor = prefInitialisation.edit();
					    editor.putBoolean("pref_initialised", true);
					    editor.commit();

					    Intent iProfile = new Intent(SplashActivity.this, MainActivity.class);
					    startActivity(iProfile);
				    }
			    }
			    catch(InterruptedException e) {
				    Editor editor = prefInitialisation.edit();
				    editor.putBoolean("pref_initialised", false);
				    editor.commit();

				    e.printStackTrace();
			    }
		    }
	    };

	    try {
		    if(!isInitialised) {
			    tvStatus.setText("Checking Internet connection...");
			    Thread.sleep(500);
			    isConnected = FileOperations.hasInternetConnection(this);

			    if(isConnected) {
				    timer.start();
			    }
			    else {
				    //TODO: App isn't connected to the Internet, prompt for retry or close.
				    finish();
			    }
		    }
		    else {
			    tvStatus.setText("Completed!");
			    Thread.sleep(1500);

			    Intent iProfile = new Intent(SplashActivity.this, MainActivity.class);
			    startActivity(iProfile);
		    }
	    }
	    catch(InterruptedException e) {
		    e.printStackTrace();
	    }
    }

	@Override
    protected void onPause() {
        super.onPause();

        //Kills the activity.
        finish();
    }

    /**
     * Use a preference to check whether a table exists.
     */
    private void prefCheckTablesExists() {
        //TODO: Create threads for each operation.
        //TODO: Sort of solved using parallel pools of AsyncTasks.
        if(!tableAcademicYearExists) {
            DatabaseOperations.getAllAcademicYears(this);
        }

        if(!tableEventCodesExists) {
            DatabaseOperations.getAllEvents(this);
        }

        if(!tableRankingStatusExists) {
            DatabaseOperations.getAllRankingStatuses(this);
        }

        if(!tableStudentStatusExists) {
            DatabaseOperations.getAllStudentStatuses(this);
        }

        if(!tableUniversityExists) {
            DatabaseOperations.getAllUniversities(this);
        }

        if(!tableBowlerExists) {
            DatabaseOperations.getAllBowlers(this);
        }

        if(!tableBowlerSeasonsExists) {
            DatabaseOperations.getAllBowlersSeasons(this);
        }
    }
}
