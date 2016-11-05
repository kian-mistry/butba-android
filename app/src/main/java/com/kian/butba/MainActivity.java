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
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.kian.butba.committee.CommitteeFragment;
import com.kian.butba.database.sqlite.DatabaseOperations;
import com.kian.butba.profile.ProfileFragment;
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

    private TextView tvBowlerName;
    private TextView tvBowlerEmail;

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

        //Display ProfileFragment on startup.
        if (savedInstanceState == null) {
            manager.beginTransaction().replace(R.id.content_main, new ProfileFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_profile);
        }

        //Get bowler's details from a shared preference.
        SharedPreferences sharedPreferences = getSharedPreferences("bowler_details", Context.MODE_PRIVATE);
        boolean isButbaMember = sharedPreferences.getBoolean("is_butba_member", false);
        String bowlerName = sharedPreferences.getString("bowler_name", null);

        navigationHeader = navigationView.getHeaderView(0);
        tvBowlerName = (TextView) navigationHeader.findViewById(R.id.nav_header_name);
        tvBowlerEmail = (TextView) navigationHeader.findViewById(R.id.nav_header_email);

        if(!isButbaMember || bowlerName == null) {
            tvBowlerName.setText("Guest");
            tvBowlerEmail.setText("");
        }
        else {
            tvBowlerName.setText(bowlerName);
            tvBowlerEmail.setText("");
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
                manager.beginTransaction().replace(R.id.content_main, profileFragment, profileFragment.getTag()).commit();
                Snackbar.make(this.getCurrentFocus(), "Profile", Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.nav_committee:
                CommitteeFragment committeeFragment = new CommitteeFragment();
                manager.beginTransaction()
                        .replace(R.id.content_main, committeeFragment, committeeFragment.getTag())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
                break;
            case R.id.nav_settings:
                Intent iSettings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(iSettings);
                Snackbar.make(this.getCurrentFocus(), "Settings", Snackbar.LENGTH_SHORT).show();
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
        boolean tableRankingStatusExists = prefDatabase.getBoolean("pref_table_ranking_status", false);
        boolean tableStudentStatusExists = prefDatabase.getBoolean("pref_table_student_status", false);
        boolean tableUniversityExists = prefDatabase.getBoolean("pref_table_university", false);


        if(!tableAcademicYearExists) {
            DatabaseOperations.getAllAcademicYears(this);
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
