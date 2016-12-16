package com.kian.butba.averages;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.kian.butba.R;
import com.kian.butba.file.FileOperations;

import java.util.HashMap;
import java.util.Map;

import io.karim.MaterialTabs;

/**
 * Created by Kian Mistry on 12/12/16.
 */

public class AveragesFragment extends Fragment implements OnPageChangeListener, OnMenuItemClickListener {
	public static final int STUDENT_MALE = 0;
	public static final int STUDENT_FEMALE = 1;
	public static final int EX_STUDENT_MALE = 2;
	public static final int EX_STUDENT_FEMALE = 3;

	private View layout;
	private ViewPagerAdapter viewPagerAdapter;
	private ActionBar toolbar;
	private PopupMenu popupMenu;
	private ViewPager viewPager;
	private MaterialTabs tabs;

	private SharedPreferences prefShownBowlers;
	private boolean qualifiedBowlers;
	private boolean unQualifiedBowlers;

	AverageTypesFragment fragment;

	public AveragesFragment() {
		//Required: Empty public constructor.
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		layout = inflater.inflate(R.layout.fragment_averages, container, false);

		//Obtain toolbar.
		toolbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
		toolbar.setTitle("Averages");
		toolbar.invalidateOptionsMenu();
		setHasOptionsMenu(true);

		//Initialise view pager and set up adapter.
		viewPager = (ViewPager) layout.findViewById(R.id.averages_view_pager);
		viewPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
		viewPager.setAdapter(viewPagerAdapter);
		viewPager.addOnPageChangeListener(this);

		//Bind tabs to the view pager.
		tabs = (MaterialTabs) layout.findViewById(R.id.averages_tabs);
		tabs.setViewPager(viewPager);

		return layout;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		//Add custom buttons to the toolbar.
		menu.clear();
		inflater.inflate(R.menu.toolbar_items_averages, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		//Obtain shared preferences.
		qualifiedBowlers = prefShownBowlers.getBoolean("qual_bowlers", true);
		unQualifiedBowlers = prefShownBowlers.getBoolean("unqual_bowlers", false);

		switch(id) {
			case R.id.toolbar_averages_action_filter:
				//Add popup menu.
				View menuActionFilter = getActivity().findViewById(R.id.toolbar_averages_action_filter);
				popupMenu = new PopupMenu(getActivity(), menuActionFilter);
				popupMenu.inflate(R.menu.menu_averages);

				//Attach item click listener.
				popupMenu.setOnMenuItemClickListener(this);

				//Initialise the checked values of the menu items.
				popupMenu.getMenu().getItem(0).setChecked(qualifiedBowlers);
				popupMenu.getMenu().getItem(1).setChecked(unQualifiedBowlers);
				popupMenu.show();

				break;
			default:
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		onPageSelected(position);
	}

	@Override
	public void onPageSelected(int position) {
		fragment = (AverageTypesFragment) ((ViewPagerAdapter) viewPager.getAdapter()).getFragment(position);
		boolean fileExists = FileOperations.fileExists(getContext().getFilesDir() + FileOperations.INTERNAL_SERVER_DIR, FileOperations.LATEST_AVERAGES, ".json");

		if(fragment != null && fileExists) {
			//Obtain the preferences which indicate the types of bowlers shown on the averages list.
			prefShownBowlers = getActivity().getSharedPreferences("bowlers_shown", Context.MODE_PRIVATE);
			qualifiedBowlers = prefShownBowlers.getBoolean("qual_bowlers", true);
			unQualifiedBowlers = prefShownBowlers.getBoolean("unqual_bowlers", false);

			fragment.getAveragesList(qualifiedBowlers, unQualifiedBowlers);

			//Update recycler view with list of bowlers if a checkbox has been altered.
			fragment.getCardsAdapter().setAveragesList(fragment.getAverages());
			fragment.getCardsAdapter().notifyDataSetChanged();
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		int id = item.getItemId();
		boolean status = toggleCheckBox(item);

		Editor editor = prefShownBowlers.edit();

		switch(id) {
			case R.id.cb_qualified:
				editor.putBoolean("qual_bowlers", status);
				fragment.getAveragesList(status, unQualifiedBowlers);
				break;
			case R.id.cb_unqualified:
				editor.putBoolean("unqual_bowlers", status);
				fragment.getAveragesList(qualifiedBowlers, status);
				break;
			default:
				break;
		}

		editor.commit();

		//Update recycler view with list of bowlers if a checkbox has been altered.
		fragment.getCardsAdapter().setAveragesList(fragment.getAverages());
		fragment.getCardsAdapter().notifyDataSetChanged();

		return true;
	}

	private boolean toggleCheckBox(MenuItem item) {
		if(item.isChecked()) {
			item.setChecked(false);
			return false;
		}
		else {
			item.setChecked(true);
			return true;
		}
	}

	private class ViewPagerAdapter extends FragmentPagerAdapter {

		private final String[] tabTitles = getResources().getStringArray(R.array.tab_averages);

		private FragmentManager fragmentManager;
		private Map<Integer, String> fragmentTags;

		public ViewPagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);

			this.fragmentManager = fragmentManager;
			fragmentTags = new HashMap<>();
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			Object object = super.instantiateItem(container, position);

			if(object instanceof Fragment) {
				//Add the fragment tag.
				Fragment fragment = (Fragment) object;
				fragmentTags.put(position, fragment.getTag());
			}

			return object;
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;

			switch(position) {
				case STUDENT_MALE:
					fragment = AverageTypesFragment.newInstance(STUDENT_MALE, qualifiedBowlers, unQualifiedBowlers);
					break;
				case STUDENT_FEMALE:
					fragment = AverageTypesFragment.newInstance(STUDENT_FEMALE, qualifiedBowlers, unQualifiedBowlers);
					break;
				case EX_STUDENT_MALE:
					fragment = AverageTypesFragment.newInstance(EX_STUDENT_MALE, qualifiedBowlers, unQualifiedBowlers);
					break;
				case EX_STUDENT_FEMALE:
					fragment = AverageTypesFragment.newInstance(EX_STUDENT_FEMALE, qualifiedBowlers, unQualifiedBowlers);
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

		public Fragment getFragment(int position) {
			String tag = fragmentTags.get(position);

			if(tag != null) {
				return fragmentManager.findFragmentByTag(tag);
			}

			return null;
		}
	}
}
