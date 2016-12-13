package com.kian.butba.averages;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kian.butba.R;

import io.karim.MaterialTabs;

/**
 * Created by Kian Mistry on 12/12/16.
 */

public class AveragesFragment extends Fragment {
	public static final int STUDENT_MALE = 0;
	public static final int STUDENT_FEMALE = 1;
	public static final int EX_STUDENT_MALE = 2;
	public static final int EX_STUDENT_FEMALE = 3;

	private View layout;
	private ActionBar toolbar;
	private ViewPager viewPager;
	private MaterialTabs tabs;

	public AveragesFragment() {
		//Required: Empty public constructor.
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		layout = inflater.inflate(R.layout.fragment_averages, container, false);

		//Obtain toolbar.
		toolbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
		toolbar.setTitle("Averages");

		//Initialise view pager and set up adapter.
		viewPager = (ViewPager) layout.findViewById(R.id.averages_view_pager);
		viewPager.setAdapter(new ViewPagerAdapter(getActivity().getSupportFragmentManager()));

		//Bind tabs to the view pager.
		tabs = (MaterialTabs) layout.findViewById(R.id.averages_tabs);
		tabs.setViewPager(viewPager);

		return layout;
	}

	private class ViewPagerAdapter extends FragmentStatePagerAdapter {

		private final String[] tabTitles = getResources().getStringArray(R.array.tab_averages);

		public ViewPagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;

			switch(position) {
				case STUDENT_MALE:
					fragment = AverageTypesFragment.newInstance(STUDENT_MALE);
					break;
				case STUDENT_FEMALE:
					fragment = AverageTypesFragment.newInstance(STUDENT_FEMALE);
					break;
				case EX_STUDENT_MALE:
					fragment = AverageTypesFragment.newInstance(EX_STUDENT_MALE);
					break;
				case EX_STUDENT_FEMALE:
					fragment = AverageTypesFragment.newInstance(EX_STUDENT_FEMALE);
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
