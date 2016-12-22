package com.kian.butba.averages;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
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
import java.util.List;

/**
 * Created by Kian Mistry on 12/12/16.
 */

public class AverageTypesFragment extends Fragment implements OnQueryTextListener, OnRefreshListener {

	public static final String AVERAGES_TYPE = "averagesType";

	//Will hold the value of parameter passed through when the new fragment instance is created.
	private int averagesType = 0;

	private ActionBar toolbar;

	private String result;
	private JSONObject jsonObject;
	private JSONArray jsonArray;

	private ArrayList<HashMap<String, String>> averages;

	private SwipeRefreshLayout swipeRefreshLayout;
	private RecyclerView recyclerView;
	private AverageCardsAdapter cardsAdapter;

	public AverageTypesFragment() {
		//Required: Empty public constructor.
	}

	public static AverageTypesFragment newInstance(int averagesType) {
		AverageTypesFragment fragment = new AverageTypesFragment();

		Bundle args = new Bundle();
		args.putInt(AVERAGES_TYPE, averagesType);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_average_cards, container, false);

		//Obtain toolbar.
		toolbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
		toolbar.setTitle("Averages");
		toolbar.invalidateOptionsMenu();
		setHasOptionsMenu(true);

		//Initialise the swipe refresh layout and recycler view.
		swipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.averages_swipe_refresh);
		swipeRefreshLayout.setOnRefreshListener(this);
		recyclerView = (RecyclerView) layout.findViewById(R.id.average_cards_container);

		return layout;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		menu.clear();
		inflater.inflate(R.menu.toolbar_items_averages, menu);

		//Add a query listener to the search view.
		MenuItem itemActionSearch = menu.findItem(R.id.toolbar_averages_action_search);
		SearchView searchView = (SearchView) MenuItemCompat.getActionView(itemActionSearch);
		searchView.setOnQueryTextListener(this);

		final MenuItem itemActionFilter = menu.findItem(R.id.toolbar_averages_action_filter);

		//Set click listeners to the search view.
		searchView.setOnSearchClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//Hides filter icon from the toolbar when the search view is in use.
				itemActionFilter.setVisible(false);
			}
		});

		searchView.setOnCloseListener(new SearchView.OnCloseListener() {
			@Override
			public boolean onClose() {
				//Shows filter icon on the toolbar when the search view is not in use.
				itemActionFilter.setVisible(true);
				return false;
			}
		});
	}

	@Override
	public void onStart() {
		super.onStart();

		/*
		 * If file does not exist, download file from server (providing the device is connected to
		 * the Internet), else read saved file.
		 */
		if(!FileOperations.fileExists(getContext().getFilesDir() + FileOperations.INTERNAL_SERVER_DIR, FileOperations.LATEST_AVERAGES, ".json")) {
			if(FileOperations.hasInternetConnection(getContext())) {
				getFileDownloader().execute(
						QueriesUrl.URL_GET_LATEST_EVENT_AVERAGES,
						FileOperations.LATEST_AVERAGES
				);
			}
			else {
				Snackbar.make(getView(), "No internet connection", Snackbar.LENGTH_SHORT).show();
			}
		}
		else {
			getAveragesList();

			cardsAdapter = new AverageCardsAdapter(getActivity(), getAverages());
			recyclerView.setAdapter(cardsAdapter);
			recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		}
	}

	public ArrayList<HashMap<String, String>> getAverages() {
		return averages;
	}

	/**
	 * Obtains the average and other related stats of each BUTBA member.
	 */
	public void getAveragesList() {
		Bundle args = getArguments();
		boolean qualBowlers = true;
		boolean unqualBowlers = true;

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

	/**
	 * Creates a new AsyncTask to download the JSON file from the server.
	 * (AsyncTasks can not be executed more than once).
	 *
	 * @return A new ServerFileDownloader object.
	 */
	private ServerFileDownloader getFileDownloader() {
		return new ServerFileDownloader(getContext(), new AsyncDelegate() {
			@Override
			public void onProcessResults(Boolean success) {
				if(success) {
					getAveragesList();

					cardsAdapter = new AverageCardsAdapter(getActivity(), getAverages());
					recyclerView.setAdapter(cardsAdapter);
					recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

					if(swipeRefreshLayout.isRefreshing()) {
						swipeRefreshLayout.setRefreshing(false);
					}
				}
			}
		});
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		//Convert queried text to lowercase.
		String queriedText = newText.toLowerCase();
		List<HashMap<String, String>> queriedList = new ArrayList<>();

		for(HashMap<String, String> member : averages) {
			if(member.containsKey("name") && member.get("name") != null) {
				//Convert obtained name to lowercase.
				String name = member.get("name").toLowerCase();

				//Check if the queried text is contained within the member's name.
				if(name.contains(queriedText)) {
					queriedList.add(member);
				}
			}
		}

		//Display the list of members which match the query.
		cardsAdapter.setList(queriedList);
		return true;
	}

	@Override
	public void onRefresh() {
		if(FileOperations.hasInternetConnection(getContext())) {
			getFileDownloader().execute(
					QueriesUrl.URL_GET_LATEST_EVENT_AVERAGES,
					FileOperations.LATEST_AVERAGES
			);
		}
		else {
			swipeRefreshLayout.setRefreshing(false);
			Snackbar.make(getView(), "No internet connection", Snackbar.LENGTH_SHORT).show();
		}
	}
}
