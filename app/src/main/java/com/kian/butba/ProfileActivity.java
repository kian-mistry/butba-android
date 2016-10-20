package com.kian.butba;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.kian.butba.navigation.NavigationDrawerFragment;

/**
 * Created by Kian Mistry on 17/10/16.
 */

public class ProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationDrawerFragment drawerFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Set up toolbar.
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Initialise drawer layout.
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setup(drawerLayout, toolbar, R.id.fragment_navigation_drawer);
    }

    @Override
    public void onBackPressed() {
        //Will close the navigation drawer if the back button is pressed.
        if(drawerFragment.isDrawerOpen()) {
            drawerFragment.closeDrawer();
        }
        else {
            super.onBackPressed();
        }
    }
}
