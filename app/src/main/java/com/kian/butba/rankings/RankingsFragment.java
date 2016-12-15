package com.kian.butba.rankings;

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
 * Created by Kian Mistry on 14/12/16.
 */

public class RankingsFragment extends Fragment {
	public static final int STUDENT_SCRATCH_MALE = 0;
	public static final int STUDENT_SCRATCH_FEMALE = 1;
	public static final int STUDENT_HANDICAP = 2;
	public static final int EX_STUDENT_SCRATCH_MALE = 3;
	public static final int EX_STUDENT_SCRATCH_FEMALE = 4;
	public static final int EX_STUDENT_HANDICAP = 5;
	public static final int UNIVERSITY = 6;

	private View layout;
	private ViewPagerAdapter viewPagerAdapter;
	private ActionBar toolbar;
	private ViewPager viewPager;
	private MaterialTabs tabs;

	public RankingsFragment() {
		//Required: Empty public constructor.
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		layout = inflater.inflate(R.layout.fragment_rankings, container, false);

		//Obtain toolbar.
		toolbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
		toolbar.setTitle("Rankings");

		//Initialise view pager and set up adapter.
		viewPager = (ViewPager) layout.findViewById(R.id.rankings_view_pager);
		viewPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
		viewPager.setAdapter(viewPagerAdapter);

		//Bind tabs to the view pager.
		tabs = (MaterialTabs) layout.findViewById(R.id.rankings_tabs);
		tabs.setViewPager(viewPager);

		return layout;
	}

	private class ViewPagerAdapter extends FragmentStatePagerAdapter {

		private final String[] tabTitles = getResources().getStringArray(R.array.tab_rankings);

		public ViewPagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;

			switch(position) {
				case STUDENT_SCRATCH_MALE:
					fragment = RankingTypesFragment.newInstance(STUDENT_SCRATCH_MALE);
					break;
				case STUDENT_SCRATCH_FEMALE:
					fragment = RankingTypesFragment.newInstance(STUDENT_SCRATCH_FEMALE);
					break;
				case STUDENT_HANDICAP:
					fragment = RankingTypesFragment.newInstance(STUDENT_HANDICAP);
					break;
				case EX_STUDENT_SCRATCH_MALE:
					fragment = RankingTypesFragment.newInstance(EX_STUDENT_SCRATCH_MALE);
					break;
				case EX_STUDENT_SCRATCH_FEMALE:
					fragment = RankingTypesFragment.newInstance(EX_STUDENT_SCRATCH_FEMALE);
					break;
				case EX_STUDENT_HANDICAP:
					fragment = RankingTypesFragment.newInstance(EX_STUDENT_HANDICAP);
					break;
				case UNIVERSITY:
					fragment = RankingTypesFragment.newInstance(UNIVERSITY);
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
