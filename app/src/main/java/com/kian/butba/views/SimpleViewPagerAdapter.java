package com.kian.butba.views;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;

import com.kian.butba.R;

import java.util.ArrayList;

/**
 * Created by Kian Mistry on 16/12/16.
 */

public class SimpleViewPagerAdapter extends FragmentStatePagerAdapter {

	private Context context;
	private TabLayout tabLayout;

	private ArrayList<Fragment> fragments;
	private ArrayList<String> tabTitles;
	private ArrayList<Integer> tabIcons;

	public SimpleViewPagerAdapter(Fragment fragment) {
		super(fragment.getActivity().getSupportFragmentManager());

		this.context = fragment.getContext();

		fragments = new ArrayList<>();
		tabTitles = new ArrayList<>();
	}

	public SimpleViewPagerAdapter(Fragment fragment, TabLayout tabLayout) {
		this(fragment);

		this.tabLayout = tabLayout;
		this.tabIcons = new ArrayList<>();
	}

	public void addFragments(Fragment fragment, String tabTitle) {
		fragments.add(fragment);
		tabTitles.add(tabTitle);
	}

	public void addFragments(Fragment fragment, int tabIcon) {
		fragments.add(fragment);
		tabIcons.add(tabIcon);
	}

	public TabLayout getTabLayout() {
		if(tabIcons != null || tabIcons.size() != 0) {
			for(int i = 0; i < fragments.size(); i++) {
				tabLayout.getTabAt(i).setIcon(tabIcons.get(i));

				//Set colour of unselected tab icon (70% white).
				tabLayout.getTabAt(i).getIcon().setColorFilter(ContextCompat.getColor(context, R.color.colourInactiveTab), PorterDuff.Mode.SRC_IN);
			}

			//Set colour of first tab icon to white.
			tabLayout.getTabAt(0).getIcon().setColorFilter(ContextCompat.getColor(context, android.R.color.white), PorterDuff.Mode.SRC_IN);
		}

		return tabLayout;
	}

	@Override
	public Fragment getItem(int position) {
		return fragments.get(position);
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		if(tabTitles.size() != 0) {
			return tabTitles.get(position);
		}

		return null;
	}
}
