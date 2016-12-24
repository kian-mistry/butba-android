package com.kian.butba;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.kian.butba.database.sqlite.DatabaseOperations;

public class SplashActivity extends Activity {

	private SharedPreferences prefDatabase;
	private boolean tableAcademicYearExists;
	private boolean tableBowlerExists;
	private boolean tableBowlerSeasonsExists;
	private boolean tableEventCodesExists;
	private boolean tableRankingStatusExists;
	private boolean tableStudentStatusExists;
	private boolean tableUniversityExists;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

	    //Obtain shared preferences.
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
			                tableAcademicYearExists &&
			                tableBowlerExists &&
			                tableBowlerSeasonsExists &&
			                tableEventCodesExists &&
			                tableRankingStatusExists &&
			                tableStudentStatusExists &&
			                tableUniversityExists
	                ) {
		                sleep(1500);
	                }
	                else {
		                //Stay on splash screen until all the data has been downloaded.
		                prefCheckTablesExists();

		                do {
							sleep(10);
		                } while(!DatabaseOperations.isCompleted[0] &&
				                !DatabaseOperations.isCompleted[1] &&
				                !DatabaseOperations.isCompleted[2] &&
				                !DatabaseOperations.isCompleted[3] &&
				                !DatabaseOperations.isCompleted[4] &&
				                !DatabaseOperations.isCompleted[5] &&
				                !DatabaseOperations.isCompleted[6]);
	                }
                }
                catch(InterruptedException e) {
	                e.printStackTrace();
                }
                finally {
                    Intent iProfile = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(iProfile);
                }
            }
        };
        timer.start();
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
