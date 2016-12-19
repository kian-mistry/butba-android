package com.kian.butba.rankings;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kian.butba.R;
import com.kian.butba.views.SimpleViewPagerAdapter;

/**
 * Created by Kian Mistry on 14/12/16.
 */

public class RankingsFragment extends Fragment {

	private ActionBar toolbar;
	private TabLayout tabLayout;

	private View layout;
	private SimpleViewPagerAdapter viewPagerAdapter;
	private ViewPager viewPager;

	public RankingsFragment() {
		//Required: Empty public constructor.
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		layout = inflater.inflate(R.layout.fragment_rankings, container, false);

		//Obtain toolbar.
		toolbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
		toolbar.setTitle("Rankings");

		//Initialise tab layout.
		tabLayout = (TabLayout) layout.findViewById(R.id.rankings_tab_layout);

		//Initialise view pager and view pager adapter.
		viewPager = (ViewPager) layout.findViewById(R.id.rankings_view_pager);
		viewPagerAdapter = new SimpleViewPagerAdapter(this);

		//Add tabs using the view pager adapter.
		for(RankingCategories category : RankingCategories.values()) {
			viewPagerAdapter.addFragments(
					RankingTypesFragment.newInstance(category.getId()),
					category.getTitle()
			);
		}

		//Add the adapter to the view pager.
		viewPager.setAdapter(viewPagerAdapter);
		tabLayout.setupWithViewPager(viewPager);

		return layout;
	}
}
