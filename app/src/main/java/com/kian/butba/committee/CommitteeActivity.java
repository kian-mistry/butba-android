package com.kian.butba.committee;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.kian.butba.R;

import io.karim.MaterialTabs;

/**
 * Created by Kian Mistry on 20/10/16.
 */

public class CommitteeActivity extends AppCompatActivity {
    public static final int EXEC_COMMITTEE = 0;
    public static final int NON_EXEC_COMMITTEE = 1;

    private Toolbar toolbar;
    private ViewPager viewPager;
    private MaterialTabs tabs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_committee);

        //Set up toolbar.
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Initialise view page and set up adapter.
        viewPager = (ViewPager) findViewById(R.id.committee_view_pager);
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));

        //Bind tabs to view pager.
        tabs = (MaterialTabs) findViewById(R.id.committee_tabs);
        tabs.setViewPager(viewPager);

    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private final String[] tabTitles = getResources().getStringArray(R.array.tab_committees);

        public ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;

            switch (position) {
                case EXEC_COMMITTEE:
                    fragment = ExecutiveCommittee.newInstance("", "");
                    break;
                case NON_EXEC_COMMITTEE:
                    fragment = ExecutiveCommittee.newInstance("", "");
                    break;
                default:
                    break;
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }
}
