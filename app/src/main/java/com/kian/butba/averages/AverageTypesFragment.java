package com.kian.butba.averages;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.kian.butba.R;
import com.kian.butba.database.server.QueriesUrl;
import com.kian.butba.file.AsyncDelegate;
import com.kian.butba.file.FileOperations;
import com.kian.butba.file.MapComparator;
import com.kian.butba.file.ServerFileDownloader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Kian Mistry on 12/12/16.
 */

public class AverageTypesFragment extends Fragment implements OnMenuItemClickListener {

	public static final String AVERAGES_TYPE = "averagesType";
	private static final String QUAL_BOWLERS = "qual_bowlers";
	private static final String UNQUAL_BOWLERS = "unqual_bowlers";

	//Will hold the value of parameter passed through when the new fragment instance is created.
	private int averagesType = 0;

	private ActionBar toolbar;
	private PopupMenu popupMenu;

	private String result;
	private JSONObject jsonObject;
	private JSONArray jsonArray;

	private ArrayList<HashMap<String, String>> averages;

	private RecyclerView recyclerView;
	private AverageCardsAdapter cardsAdapter;

	private SharedPreferences prefShownBowlers;
	private boolean qualifiedBowlers = true;
	private boolean unQualifiedBowlers = false;

	public AverageTypesFragment() {
		//Required: Empty public constructor.
	}

	public static AverageTypesFragment newInstance(int averagesType, Boolean... qualifiedTypes) {
		AverageTypesFragment fragment = new AverageTypesFragment();

		Bundle args = new Bundle();
		args.putInt(AVERAGES_TYPE, averagesType);
		args.putBoolean(QUAL_BOWLERS, qualifiedTypes[0]);
		args.putBoolean(UNQUAL_BOWLERS, qualifiedTypes[1]);
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Obtain the preferences which indicate the types of bowlers shown on the averages list.
		prefShownBowlers = getActivity().getSharedPreferences("bowlers_shown", Context.MODE_PRIVATE);
		qualifiedBowlers = prefShownBowlers.getBoolean("qual_bowlers", true);
		unQualifiedBowlers = prefShownBowlers.getBoolean("unqual_bowlers", false);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_average_cards, container, false);

		//Obtain toolbar.
		toolbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
		toolbar.setTitle("Averages");
		toolbar.invalidateOptionsMenu();
		setHasOptionsMenu(true);

		recyclerView = (RecyclerView) layout.findViewById(R.id.average_cards_container);

		return layout;
	}

	@Override
	public void onStart() {
		super.onStart();

		ServerFileDownloader fileDownloader = new ServerFileDownloader(getContext(), new AsyncDelegate() {
			@Override
			public void onProcessResults(Boolean success) {
				getAveragesList(null);

				cardsAdapter = new AverageCardsAdapter(getActivity(), getAverages());
				recyclerView.setAdapter(cardsAdapter);
				recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
			}
		});

		//If file does not exist, download file from server, else read saved file.
		if(!FileOperations.fileExists(getContext().getFilesDir() + FileOperations.INTERNAL_SERVER_DIR, FileOperations.LATEST_AVERAGES, ".json")) {
			fileDownloader.execute(
					QueriesUrl.URL_GET_LATEST_EVENT_AVERAGES,
					FileOperations.LATEST_AVERAGES
			);
		}
		else {
			getAveragesList(null);

			cardsAdapter = new AverageCardsAdapter(getActivity(), getAverages());
			recyclerView.setAdapter(cardsAdapter);
			recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		}
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

		//Obtian shared preferences.
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
	public boolean onMenuItemClick(MenuItem item) {
		int id = item.getItemId();
		boolean status = toggleCheckBox(item);

		//Prevent popup menu from closing when an option is selected.
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		item.setActionView(new View(getContext()));

		Editor editor = prefShownBowlers.edit();

		switch(id) {
			case R.id.cb_qualified:
				editor.putBoolean("qual_bowlers", status);
				getAveragesList(status, unQualifiedBowlers);
				break;
			case R.id.cb_unqualified:
				editor.putBoolean("unqual_bowlers", status);
				getAveragesList(qualifiedBowlers, status);
				break;
			default:
				break;
		}

		editor.commit();

		//Update recycler view with list of bowlers if a checkbox has been altered.
		cardsAdapter.setAveragesList(getAverages());
		cardsAdapter.notifyDataSetChanged();

		return false;
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

	public ArrayList<HashMap<String, String>> getAverages() {
		return averages;
	}

	/**
	 * Obtains the average and other related stats of each BUTBA member.
	 *
	 * @param qualifiedTypes The shared preference values of qualified and unqualified bowlers.
	 */
	private void getAveragesList(Boolean... qualifiedTypes) {
		Bundle args = getArguments();
		boolean qualBowlers;
		boolean unqualBowlers;

		if(qualifiedTypes != null) {
			qualBowlers = qualifiedTypes[0];
			unqualBowlers = qualifiedTypes[1];
		}
		else {
			qualBowlers = args.getBoolean(QUAL_BOWLERS);
			unqualBowlers = args.getBoolean(UNQUAL_BOWLERS);
		}

		try {
			if(args != null) {
				averagesType = args.getInt(AVERAGES_TYPE, 0);
				result = FileOperations.readFile(
						getContext().getFilesDir() + FileOperations.INTERNAL_SERVER_DIR,
						FileOperations.LATEST_AVERAGES,
						".json");

				jsonObject = new JSONObject(result);
			}

			switch(averagesType) {
				case 0: //Student Male
					jsonArray = jsonObject.getJSONArray("SM");
					break;
				case 1: //Student Female
					jsonArray = jsonObject.getJSONArray("SF");
					break;
				case 2: //Ex-Student Male
					jsonArray = jsonObject.getJSONArray("XM");
					break;
				case 3: //Ex-Student Female
					jsonArray = jsonObject.getJSONArray("XF");
					break;
				default:
					jsonArray = null;
					break;
			}

			averages = new ArrayList<>();
			HashMap<String, String> averageDetails;

			for(int i = 0; i < jsonArray.length(); i++) {
				JSONObject detail = jsonArray.getJSONObject(i);

				String totalGames = detail.getString("Games");
				if(
						(Integer.parseInt(totalGames) >= 12 && qualBowlers) ||
						(Integer.parseInt(totalGames) < 12 && unqualBowlers)
				) {
					String name = detail.getString("Name");
					String university = detail.getString("University");
					String average = detail.getString("Average1617");
					String totalPinfall = detail.getString("Total Pinfall");
					String improvement = detail.getString("Improvement");

					averageDetails = new HashMap<>();
					averageDetails.put("name", name);
					averageDetails.put("university", university);
					averageDetails.put("average", average);
					averageDetails.put("total_pinfall", totalPinfall);
					averageDetails.put("total_games", totalGames);
					averageDetails.put("improvement", improvement);

					averages.add(averageDetails);
				}
			}

			Collections.sort(averages, new MapComparator("average", MapComparator.Sort.DESCENDING));
		}
		catch(IOException | JSONException e) {
			e.printStackTrace();
		}
	}
}
