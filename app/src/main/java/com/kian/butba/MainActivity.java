package com.kian.butba;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.kian.butba.averages.AveragesFragment;
import com.kian.butba.committee.CommitteeFragment;
import com.kian.butba.database.sqlite.DatabaseOperations;
import com.kian.butba.database.sqlite.entities.BowlerSeason;
import com.kian.butba.database.sqlite.tables.TableBowlerSeason;
import com.kian.butba.events.EventsFragment;
import com.kian.butba.profile.ProfileFragment;
import com.kian.butba.rankings.RankingsFragment;
import com.kian.butba.settings.SettingsActivity;

/**
 * Created by Kian Mistry on 25/10/16.
 */

public class MainActivity extends AppCompatActivity implements OnNavigationItemSelectedListener {

    private Toolbar toolbar;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private View navigationHeader;
    private Menu navigationMenu;

    private TextView tvBowlerName;
    private TextView tvBowlerStatus;

    private FragmentManager manager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set up toolbar.
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Initialise drawer layout.
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //Initialise the navigation view.
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
	    navigationMenu = navigationView.getMenu();

        //Display ProfileFragment on startup.
        if (savedInstanceState == null) {
            manager.beginTransaction().replace(R.id.content_main, new ProfileFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_profile);
        }

        //Get bowler's details from a shared preference.
        SharedPreferences sharedPreferences = getSharedPreferences("bowler_details", Context.MODE_PRIVATE);
        int bowlerId = sharedPreferences.getInt("bowler_id", 0);
        String bowlerName = sharedPreferences.getString("bowler_name", null);

        navigationHeader = navigationView.getHeaderView(0);
        tvBowlerName = (TextView) navigationHeader.findViewById(R.id.nav_header_name);
        tvBowlerStatus = (TextView) navigationHeader.findViewById(R.id.nav_header_status);

        if(bowlerId == 0 || bowlerName == null) {
            tvBowlerName.setText("Guest");
            tvBowlerStatus.setText("");

	        /*
	         * If user is a Guest, remove the Profile item from the navigation menu and display the
	         * EventsFragment on startup.
	         */
	        navigationMenu.findItem(R.id.nav_profile).setVisible(false);
	        manager.beginTransaction().replace(R.id.content_main, new EventsFragment()).commit();
	        navigationView.setCheckedItem(R.id.nav_events);
        }
        else {
            tvBowlerName.setText(bowlerName);

            TableBowlerSeason tableBowlerSeason = new TableBowlerSeason(this);
            BowlerSeason bowlerSeason = tableBowlerSeason.getBowlersSeason(bowlerId).get(0);

            //Only displays the bowler's status for the current academic year.
            if(bowlerSeason != null && bowlerSeason.getAcademicYear() == 2) {
                int studentStatus = bowlerSeason.getStudentStatus();
                int rankingStatus = bowlerSeason.getRankingStatus();

                String student = (studentStatus == 1) ? "Student" : "Ex-Student";
                String ranking = (rankingStatus == 1) ? "Scratch" : "Handicap";

                tvBowlerStatus.setText(student + " // " + ranking);
            }
            else {
                tvBowlerStatus.setText("");
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Check whether table exists, if not create and populate table.
        prefCheckTablesExists();
    }

    @Override
    public void onBackPressed() {
        //Will close the navigation drawer if the back button is pressed.
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //Handle navigation view item clicks here.
        int id = item.getItemId();

        switch(id) {
            case R.id.nav_profile:
                ProfileFragment profileFragment = new ProfileFragment();
                manager.beginTransaction()
		                .replace(R.id.content_main, profileFragment, profileFragment.getTag())
		                .commit();
                break;
            case R.id.nav_committee:
                CommitteeFragment committeeFragment = new CommitteeFragment();
                manager.beginTransaction()
                        .replace(R.id.content_main, committeeFragment, committeeFragment.getTag())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
                break;
            case R.id.nav_events:
                EventsFragment eventsFragment = new EventsFragment();
                manager.beginTransaction()
		                .replace(R.id.content_main, eventsFragment, eventsFragment.getTag())
		                .commit();
                break;
	        case R.id.nav_averages:
		        AveragesFragment averagesFragment = new AveragesFragment();
		        manager.beginTransaction()
				        .replace(R.id.content_main, averagesFragment, averagesFragment.getTag())
				        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				        .commit();
	            Snackbar.make(this.getCurrentFocus(), "Averages", Snackbar.LENGTH_SHORT).show();
		        break;
	        case R.id.nav_rankings:
		        RankingsFragment rankingsFragment = new RankingsFragment();
		        manager.beginTransaction()
				        .replace(R.id.content_main, rankingsFragment, rankingsFragment.getTag())
				        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				        .commit();
		        Snackbar.make(this.getCurrentFocus(), "Rankings", Snackbar.LENGTH_SHORT).show();
		        break;
            case R.id.nav_settings:
                Intent iSettings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(iSettings);
                break;
            case R.id.nav_help:
                Snackbar.make(this.getCurrentFocus(), "Help", Snackbar.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Use a preference to check whether a table exists.
     */
    private void prefCheckTablesExists() {
        SharedPreferences prefDatabase = getSharedPreferences("butba_database", Context.MODE_PRIVATE);
        boolean tableAcademicYearExists = prefDatabase.getBoolean("pref_table_academic_year", false);
        boolean tableBowlerExists = prefDatabase.getBoolean("pref_table_bowlers", false);
        boolean tableBowlerSeasonsExists = prefDatabase.getBoolean("pref_table_bowlers_seasons", false);
//        boolean tableEventAverageExists = prefDatabase.getBoolean("pref_table_event_average", false);
        boolean tableEventCodesExists = prefDatabase.getBoolean("pref_table_event_code", false);
        boolean tableRankingStatusExists = prefDatabase.getBoolean("pref_table_ranking_status", false);
        boolean tableStudentStatusExists = prefDatabase.getBoolean("pref_table_student_status", false);
        boolean tableUniversityExists = prefDatabase.getBoolean("pref_table_university", false);

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

//        if(!tableEventAverageExists) {
//            DatabaseOperations.getAllEventAverages(this);
//        }
    }
}
