package com.kian.butba.averages;

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
 * Created by Kian Mistry on 12/12/16.
 */

public class AveragesFragment extends Fragment {

	private ActionBar toolbar;
	private TabLayout tabLayout;

	private View layout;
	private SimpleViewPagerAdapter viewPagerAdapter;
	private ViewPager viewPager;

	public AveragesFragment() {
		//Required: Empty public constructor.
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		layout = inflater.inflate(R.layout.fragment_averages, container, false);

		//Obtain toolbar.
		toolbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
		toolbar.setTitle("Averages");

		//Initialise tab layout.
		tabLayout = (TabLayout) layout.findViewById(R.id.averages_tab_layout);

		//Initialise view pager and set up adapter.
		viewPager = (ViewPager) layout.findViewById(R.id.averages_view_pager);
		viewPagerAdapter = new SimpleViewPagerAdapter(this);

		//Add tabs using the view pager adapter.
		for(AverageCategories category : AverageCategories.values()) {
			viewPagerAdapter.addFragments(
					AverageTypesFragment.newInstance(category.getId()),
					category.getTitle()
			);
		}

		//Add the adapter to the view pager.
		viewPager.setAdapter(viewPagerAdapter);
		tabLayout.setupWithViewPager(viewPager);

		return layout;
	}
}
