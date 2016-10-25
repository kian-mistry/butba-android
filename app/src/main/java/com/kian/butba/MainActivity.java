package com.kian.butba;

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

import com.kian.butba.committee.CommitteeFragment;
import com.kian.butba.profile.ProfileFragment;

/**
 * Created by Kian Mistry on 25/10/16.
 */

public class MainActivity extends AppCompatActivity implements OnNavigationItemSelectedListener {

    private Toolbar toolbar;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

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
        FragmentManager manager = null;

        //Handle navigation view item clicks here.
        int id = item.getItemId();

        switch(id) {
            case R.id.nav_profile:
                ProfileFragment profileFragment = new ProfileFragment();
                manager = getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.content_main, profileFragment, profileFragment.getTag()).commit();
                Snackbar.make(this.getCurrentFocus(), "Profile", Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.nav_committee:
                CommitteeFragment committeeFragment = new CommitteeFragment();
                manager = getSupportFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.content_main, committeeFragment, committeeFragment.getTag())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
                Snackbar.make(this.getCurrentFocus(), "Committee", Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.nav_settings:
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
}
