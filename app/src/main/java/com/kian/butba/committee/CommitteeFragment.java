package com.kian.butba.committee;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kian.butba.R;
import com.kian.butba.views.SimpleViewPagerAdapter;

/**
 * Created by Kian Mistry on 20/10/16.
 */

public class CommitteeFragment extends Fragment {

    private static final int EXEC_COMMITTEE = 0;
    private static final int NON_EXEC_COMMITTEE = 1;

    private ActionBar toolbar;
    private TabLayout tabLayout;

	private View layout;
	private SimpleViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;

    public CommitteeFragment() {
        //Required: Empty public constructor.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_committee, container, false);

	    //Obtain toolbar.
	    toolbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
	    toolbar.setTitle("Committee");
	    toolbar.invalidateOptionsMenu();
	    setHasOptionsMenu(true);

	    //Initialise the tab layout.
	    tabLayout = (TabLayout) layout.findViewById(R.id.committee_tab_layout);

        //Initialise view pager and set up adapter.
        viewPager = (ViewPager) layout.findViewById(R.id.committee_view_pager);
        viewPagerAdapter = new SimpleViewPagerAdapter(this);

	    //Add tabs using the view pager adapter.
	    String[] committeeTabs = getResources().getStringArray(R.array.tab_committees);
	    viewPagerAdapter.addFragments(CommitteeTypesFragment.newInstance(EXEC_COMMITTEE), committeeTabs[EXEC_COMMITTEE]);
	    viewPagerAdapter.addFragments(CommitteeTypesFragment.newInstance(NON_EXEC_COMMITTEE), committeeTabs[NON_EXEC_COMMITTEE]);

        //Add the adapter to the view pager.
	    viewPager.setAdapter(viewPagerAdapter);
	    tabLayout.setupWithViewPager(viewPager);

        return layout;
    }

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		//Remove icons from the toolbar.
		menu.clear();
	}
}
