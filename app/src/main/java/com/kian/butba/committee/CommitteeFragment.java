package com.kian.butba.committee;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kian.butba.R;

import io.karim.MaterialTabs;

/**
 * Created by Kian Mistry on 20/10/16.
 */

public class CommitteeFragment extends Fragment {
    public static final int EXEC_COMMITTEE = 0;
    public static final int NON_EXEC_COMMITTEE = 1;

    private ViewPager viewPager;
    private MaterialTabs tabs;

    public CommitteeFragment() {
        //Required: Empty public constructor.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_committee, container, false);

        //Initialise view pager and set up adapter.
        viewPager = (ViewPager) view.findViewById(R.id.committee_view_pager);
        viewPager.setAdapter(new ViewPagerAdapter(getActivity().getSupportFragmentManager()));

        //Bind tabs to the view pager.
        tabs = (MaterialTabs) view.findViewById(R.id.committee_tabs);
        tabs.setViewPager(viewPager);

        return view;
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private final String[] tabTitles = getResources().getStringArray(R.array.tab_committees);

        public ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;

            switch (position) {
                case EXEC_COMMITTEE:
                    fragment = CommitteeTypesFragment.newInstance(0);
                    break;
                case NON_EXEC_COMMITTEE:
                    fragment = CommitteeTypesFragment.newInstance(1);
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
