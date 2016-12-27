package com.kian.butba.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kian.butba.R;
import com.kian.butba.database.server.QueriesUrl;
import com.kian.butba.file.AsyncDelegate;
import com.kian.butba.file.FileOperations;
import com.kian.butba.file.MapComparator;
import com.kian.butba.file.ServerFileDownloader;
import com.kian.butba.profile.ProfileCardsAdapter.ProfileHolder;
import com.kian.butba.views.CardClickListener.ProfileCardClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Kian Mistry on 17/10/16.
 */

public class ProfileFragment extends Fragment implements ProfileCardClickListener {

	private ActionBar toolbar;

	private SharedPreferences prefBowlerDetails;
	private int bowlerId;

	private RecyclerView recyclerView;
	private ProfileCardsAdapter cardsAdapter;
	private ArrayList<HashMap<String, String>> profiles;

	private JSONObject jsonObject;
	private JSONArray jsonArray;
	private String result;

	public ProfileFragment() {
		//Required: Empty public constructor.
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_profile, container, false);

		//Obtain toolbar.
		toolbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
		toolbar.setTitle("Profile");
		toolbar.invalidateOptionsMenu();
		setHasOptionsMenu(true);

		//Initialise recycler view.
		recyclerView = (RecyclerView) layout.findViewById(R.id.profile_cards_container);

		return layout;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		//Remove icons from the toolbar.
		menu.clear();
	}

	@Override
	public void onStart() {
		super.onStart();

		//Obtain Bowler ID from shared preference.
		prefBowlerDetails = getActivity().getSharedPreferences("bowler_details", Context.MODE_PRIVATE);
		bowlerId = prefBowlerDetails.getInt("bowler_id", 0);

		ServerFileDownloader fileDownloader = new ServerFileDownloader(getContext(), new AsyncDelegate() {
			@Override
			public void onProcessResults(Boolean success) {
				getProfileSeasonsList(bowlerId);

				//Set up recycler view to display the bowler's profile for each season.
				cardsAdapter = new ProfileCardsAdapter(getActivity(), ProfileFragment.this, getProfiles());
				recyclerView.setAdapter(cardsAdapter);
				recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
			}
		});

		//If file does not exist, download file from server, else read saved file.
		if(!FileOperations.fileExists(getContext().getFilesDir() + FileOperations.INTERNAL_SERVER_DIR, FileOperations.BOWLERS_SEASON_STATS_FILE + "_" + bowlerId, ".json")) {
			fileDownloader.execute(
					QueriesUrl.get_particular_bowlers_ranking(bowlerId),
					FileOperations.BOWLERS_SEASON_STATS_FILE + "_" + bowlerId
			);
		}
		else {
			getProfileSeasonsList(bowlerId);

			//Set up recycler view to display the bowler's profile for each season.
			cardsAdapter = new ProfileCardsAdapter(getActivity(), ProfileFragment.this, getProfiles());
			recyclerView.setAdapter(cardsAdapter);
			recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		}
	}

	public ArrayList<HashMap<String, String>> getProfiles() {
		return profiles;
	}

	public void getProfileSeasonsList(int bowlerId) {
		if(bowlerId != 0) {
			try {
				result = FileOperations.readFile(
						getContext().getFilesDir() + FileOperations.INTERNAL_SERVER_DIR,
						FileOperations.BOWLERS_SEASON_STATS_FILE + "_" + bowlerId,
						".json"
				);

				jsonObject = new JSONObject(result);
				jsonArray = jsonObject.getJSONArray("Seasons");

				profiles = new ArrayList<>();
				HashMap<String, String> seasonDetails;

				for(int i = 0; i < jsonArray.length(); i++) {
					JSONObject detail = jsonArray.getJSONObject(i);

					if(!detail.isNull("AcademicYear")) {
						String academicYear = String.valueOf(detail.getInt("AcademicYear"));
						String rankingStatus = detail.getString("Ranking");
						String studentStatus = detail.getString("Student");
						String university = detail.getString("University");
						String average = String.valueOf(detail.getString("OverallAverage"));
						String games = detail.getString("TotalGames");
						String points = String.valueOf(detail.getString("TotalPoints"));
						String bestN = String.valueOf(detail.getString("BestN"));

						seasonDetails = new HashMap<>();
						seasonDetails.put("academic_year", academicYear.toString());
						seasonDetails.put("ranking_status", rankingStatus);
						seasonDetails.put("student_status", studentStatus);
						seasonDetails.put("university", university);
						seasonDetails.put("average", average);
						seasonDetails.put("games", games);
						seasonDetails.put("points", points);
						seasonDetails.put("best_n", bestN);

						profiles.add(seasonDetails);
					}
				}

				Collections.sort(profiles, new MapComparator("academic_year", MapComparator.Sort.DESCENDING));
			}
			catch(IOException | JSONException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onProfileCardClicked(ViewHolder holder, int academicYearId) {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

			//Define intent.
			Intent intent = new Intent(getActivity(), ProfileCardDetails.class);

			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

				//Define new shared element.
				View cardView = ((ProfileHolder) holder).getProfileCard();
				String expandCardName = getResources().getString(R.string.transition_expand_card);
				cardView.setTransitionName(expandCardName);
				Pair<View, String> cardSharedElement = new Pair<>(cardView, expandCardName);

				//Put extras into intent.
				intent.putExtra("ACADEMIC_YEAR_ID", academicYearId);
				intent.putExtra("BOWLER_ID", bowlerId);

				ActivityOptionsCompat sceneTransition = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), cardSharedElement);
//				ActivityCompat.startActivity(getContext(), intent, sceneTransition.toBundle());
				getActivity().startActivityFromFragment(this, intent, getTargetRequestCode(), sceneTransition.toBundle());
			}
			else {
				getActivity().startActivity(intent);
			}
		}
	}
}
