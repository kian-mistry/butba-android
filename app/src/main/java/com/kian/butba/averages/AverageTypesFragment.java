package com.kian.butba.averages;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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

public class AverageTypesFragment extends Fragment {

	public static final String AVERAGES_TYPE = "averagesType";
	private static final String QUAL_BOWLERS = "qual_bowlers";
	private static final String UNQUAL_BOWLERS = "unqual_bowlers";

	//Will hold the value of parameter passed through when the new fragment instance is created.
	private int averagesType = 0;

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

	public static AverageTypesFragment newInstance(int averagesType) {
		AverageTypesFragment fragment = new AverageTypesFragment();

		Bundle args = new Bundle();
		args.putInt(AVERAGES_TYPE, averagesType);
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
		recyclerView = (RecyclerView) layout.findViewById(R.id.average_cards_container);

		return layout;
	}

	@Override
	public void onResume() {
		super.onResume();

		ServerFileDownloader fileDownloader = new ServerFileDownloader(getContext(), new AsyncDelegate() {
			@Override
			public void onProcessResults(Boolean success) {
				getAveragesList(qualifiedBowlers, unQualifiedBowlers);

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
			getAveragesList(qualifiedBowlers, unQualifiedBowlers);

			cardsAdapter = new AverageCardsAdapter(getActivity(), getAverages());
			recyclerView.setAdapter(cardsAdapter);
			recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		}
	}

	public AverageCardsAdapter getCardsAdapter() {
		return cardsAdapter;
	}

	public ArrayList<HashMap<String, String>> getAverages() {
		return averages;
	}

	/**
	 * Obtains the average and other related stats of each BUTBA member.
	 *
	 * @param qualifiedTypes The shared preference values of qualified and unqualified bowlers.
	 */
	public void getAveragesList(Boolean... qualifiedTypes) {
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
