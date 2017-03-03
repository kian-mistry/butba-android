package com.kian.butba.rankings;

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
import com.kian.butba.database.QueriesUrl;
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
 * Created by Kian Mistry on 14/12/16.
 */

public class RankingTypesFragment extends Fragment implements OnQueryTextListener, OnRefreshListener {

	public static final String RANKINGS_TYPE = "rankingsType";

	//Will hold the value of parameter passed through when the new fragment instance is created.
	private int rankingsType = 0;

	private ActionBar toolbar;

	private String result;
	private JSONObject jsonObject;
	private JSONArray jsonArray;

	private ArrayList<HashMap<String, String>> rankings;

	private SwipeRefreshLayout swipeRefreshLayout;
	private RecyclerView recyclerView;
	private RankingCardsAdapter cardsAdapter;

	public RankingTypesFragment() {
		//Required: Empty public constructor.
	}
	
	public static RankingTypesFragment newInstance(int rankingsType) {
		RankingTypesFragment fragment = new RankingTypesFragment();

		Bundle args = new Bundle();
		args.putInt(RANKINGS_TYPE, rankingsType);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_ranking_cards, container, false);

		//Obtain toolbar.
		toolbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
		toolbar.setTitle("Rankings");
		toolbar.invalidateOptionsMenu();
		setHasOptionsMenu(true);

		//Initialise the swipe refresh layout and the recycler view.
		swipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.ranking_swipe_refresh);
		swipeRefreshLayout.setOnRefreshListener(this);
		recyclerView = (RecyclerView) layout.findViewById(R.id.ranking_cards_container);

		return layout;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		menu.clear();
		inflater.inflate(R.menu.toolbar_items_rankings, menu);

		//Add a query listener to the search view.
		MenuItem itemActionSearch = menu.findItem(R.id.toolbar_rankings_action_search);
		SearchView searchView = (SearchView) MenuItemCompat.getActionView(itemActionSearch);
		searchView.setOnQueryTextListener(this);
	}

	@Override
	public void onStart() {
		super.onStart();

		/*
		 * If file does not exist, download file from server (providing the device is connected to
		 * the Internet), else read saved file.
		 */
		if(!FileOperations.fileExists(getContext().getFilesDir() + FileOperations.INTERNAL_SERVER_DIR, FileOperations.LATEST_RANKINGS_FILE, ".json")) {
			if(FileOperations.hasInternetConnection(getContext())) {
				getFileDownloader().execute(
						QueriesUrl.URL_GET_LATEST_EVENT_RANKINGS,
						FileOperations.LATEST_RANKINGS_FILE
				);
			}
			else {
				Snackbar.make(getView(), "No internet connection", Snackbar.LENGTH_SHORT).show();
			}
		}
		else {
			getRankingsList();

			cardsAdapter = new RankingCardsAdapter(getActivity(), getRankings());
			recyclerView.setAdapter(cardsAdapter);
			recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		}
	}

	public ArrayList<HashMap<String, String>> getRankings() {
		return rankings;
	}

	/**
	 * Obtains the rankings and other related stats of each BUTBA member.
	 */
	private void getRankingsList() {
		Bundle args = getArguments();

		try {
			if(args != null) {
				rankingsType = args.getInt(RANKINGS_TYPE, 0);
				result = FileOperations.readFile(
					getContext().getFilesDir() + FileOperations.INTERNAL_SERVER_DIR,
					FileOperations.LATEST_RANKINGS_FILE,
					".json"
				);

				jsonObject = new JSONObject(result);
			}

			switch(rankingsType) {
				case 0: //Student Scratch Male
					jsonArray = jsonObject.getJSONArray("SSM");
					break;
				case 1: //Student Scratch Female
					jsonArray = jsonObject.getJSONArray("SSF");
					break;
				case 2: //Student Handicap
					jsonArray = jsonObject.getJSONArray("SH");
					break;
				case 3: //Ex-Student Scratch Male
					jsonArray = jsonObject.getJSONArray("XSM");
					break;
				case 4: //Ex-Student Scratch Female
					jsonArray = jsonObject.getJSONArray("XSF");
					break;
				case 5: //Ex-Student Handicap
					jsonArray = jsonObject.getJSONArray("XH");
					break;
				case 6: //University
					jsonArray = jsonObject.getJSONArray("UNI");
					break;
				default:
					jsonArray = null;
					break;
			}

			String name = null;
			String bestN = null;
			String totalEvents = null;

			if(jsonArray != null) {
				rankings = new ArrayList<>();
				HashMap<String, String> rankingDetails;

				for(int i = 0; i < jsonArray.length(); i++) {
					JSONObject detail = jsonArray.getJSONObject(i);

					name = (detail.has("Name")) ? detail.getString("Name") : null;
					String university = detail.getString("University");
					bestN = (detail.has("Best N")) ? detail.getString("Best N") : null;
					String totalPoints = detail.getString("Total Points");
					totalEvents = (detail.has("Total Events")) ? detail.getString("Total Events") : null;

					rankingDetails = new HashMap<>();
					rankingDetails.put("name", name);
					rankingDetails.put("university", university);
					rankingDetails.put("best_n", bestN);
					rankingDetails.put("total_points", totalPoints);
					rankingDetails.put("total_events", totalEvents);

					rankings.add(rankingDetails);
				}

				if(name != null && bestN != null && totalEvents != null) {
					Collections.sort(rankings, new MapComparator("best_n", MapComparator.Sort.DESCENDING));
				}
				else {
					Collections.sort(rankings, new MapComparator("total_points", MapComparator.Sort.DESCENDING));
				}
			}
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
					getRankingsList();

					cardsAdapter = new RankingCardsAdapter(getActivity(), getRankings());
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

		for(HashMap<String, String> member : rankings) {
			if(member.containsKey("name") && member.get("name") != null) {
				//Convert obtained name to lowercase.
				String name = member.get("name").toLowerCase();

				//Check if the queried text is contained within the member's name.
				if(name.contains(queriedText)) {
					queriedList.add(member);
				}
			}
			else if(member.containsKey("university") && member.get("university") != null) {
				//Convert obtained university name to lowercase.
				String university = member.get("university").toLowerCase();

				//Check if the queried text is contained with the university name.
				if(university.contains(queriedText)) {
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
					QueriesUrl.URL_GET_LATEST_EVENT_RANKINGS,
					FileOperations.LATEST_RANKINGS_FILE
			);
		}
		else {
			swipeRefreshLayout.setRefreshing(false);
			Snackbar.make(getView(), "No internet connection", Snackbar.LENGTH_SHORT).show();
		}
	}
}
