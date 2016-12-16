package com.kian.butba.social;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.OnTabSelectedListener;
import android.support.design.widget.TabLayout.Tab;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kian.butba.R;
import com.kian.butba.views.SimpleViewPagerAdapter;

/**
 * Created by Kian Mistry on 16/12/16.
 */

public class SocialFragment extends Fragment implements OnTabSelectedListener {

	private ActionBar toolbar;
	private TabLayout tabLayout;

	private ViewPager viewPager;
	private SimpleViewPagerAdapter viewPagerAdapter;

	public SocialFragment() {
		//Required: Empty public constructor.
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_social, container, false);

		//Obtain toolbar.
		toolbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
		toolbar.setTitle("Social");

		//Initialise tab layout.
		tabLayout = (TabLayout) layout.findViewById(R.id.social_tab_layout);

		//Initialise view pager and view pager adapter.
		viewPager = (ViewPager) layout.findViewById(R.id.social_view_pager);
		viewPagerAdapter = new SimpleViewPagerAdapter(getContext(), getActivity().getSupportFragmentManager(), tabLayout);

		//Add tabs using the view pager adapter.
		viewPagerAdapter.addFragments(new FacebookFragment(), R.drawable.ic_tab_facebook);
		viewPagerAdapter.addFragments(new TwitterFragment(), R.drawable.ic_tab_twitter);

		//Add the adapter to the view pager.
		viewPager.setAdapter(viewPagerAdapter);
		tabLayout.setupWithViewPager(viewPager);
		tabLayout = viewPagerAdapter.getTabLayout();
		tabLayout.addOnTabSelectedListener(this);

		return layout;
	}

	@Override
	public void onTabSelected(Tab tab) {
		tab.getIcon().setColorFilter(ContextCompat.getColor(getContext(), android.R.color.white), PorterDuff.Mode.SRC_IN);
	}

	@Override
	public void onTabUnselected(Tab tab) {
		tab.getIcon().setColorFilter(ContextCompat.getColor(getContext(), R.color.colourInactiveTab), PorterDuff.Mode.SRC_IN);
	}

	@Override
	public void onTabReselected(Tab tab) {}
}
