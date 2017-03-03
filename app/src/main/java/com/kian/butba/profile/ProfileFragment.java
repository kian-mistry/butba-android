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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kian.butba.R;
import com.kian.butba.database.QueriesUrl;
import com.kian.butba.entities.BowlerSeasonStats;
import com.kian.butba.file.AsyncDelegate;
import com.kian.butba.file.FileOperations;
import com.kian.butba.file.ServerFileDownloader;
import com.kian.butba.profile.ProfileCardsAdapter.ProfileHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Kian Mistry on 17/10/16.
 */

public class ProfileFragment extends Fragment implements ProfileCardClickListener<ProfileHolder, BowlerSeasonStats> {

	private ActionBar toolbar;

	private SharedPreferences prefBowlerDetails;
	private int bowlerId;

	private RecyclerView recyclerView;
	private ProfileCardsAdapter cardsAdapter;
	private List<BowlerSeasonStats> profiles;

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
					QueriesUrl.url_get_bowlers_stats(bowlerId),
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

	public List<BowlerSeasonStats> getProfiles() {
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
				jsonArray = jsonObject.getJSONArray("seasons");

				profiles = new ArrayList<>();

				for(int i = 0; i < jsonArray.length(); i++) {
					JSONObject detail = jsonArray.getJSONObject(i);

					if(!detail.isNull("AcademicYear")) {
						int academicYear = detail.getInt("AcademicYear");
						int rankingStatus = Integer.parseInt(detail.getString("RankingStatus"));
						int studentStatus = Integer.parseInt(detail.getString("StudentStatus"));
						String university = detail.getString("University");
						int overallAverage = Integer.parseInt(detail.getString("OverallAverage"));
						int totalGames = Integer.parseInt(detail.getString("TotalGames"));
						int totalPoints = detail.getInt("TotalPoints");
						int bestN = detail.getInt("BestN");

						JSONArray stops = detail.getJSONArray("Stops");
						JSONObject averages = detail.getJSONObject("Averages");
						JSONArray points = detail.getJSONArray("RankingPoints");

						List<String> stopsList = new ArrayList<>();
						HashMap<String, String> tournamentAverages = new HashMap<>();
						HashMap<String, String> tournamentPoints = new HashMap<>();

						for(int j = 0; j < stops.length(); j++) {
							String stopName = stops.getString(j);
							stopsList.add(stopName);

							tournamentAverages.put(stopName, averages.getString(stopName));
							tournamentPoints.put(stopName, points.getString(j));
						}

						BowlerSeasonStats seasonStats = new BowlerSeasonStats(academicYear, rankingStatus, studentStatus, university, overallAverage, totalGames, totalPoints, bestN);
						seasonStats.setStopsList(stopsList);
						seasonStats.setTournamentAverages(tournamentAverages);
						seasonStats.setTournamentPoints(tournamentPoints);

						profiles.add(seasonStats);
					}
				}
				Collections.sort(profiles, new Comparator<BowlerSeasonStats>() {
					@Override
					public int compare(BowlerSeasonStats firstObject, BowlerSeasonStats secondObject) {
						int firstAcadYear = firstObject.getAcademicYear();
						int secondAcadYear = secondObject.getAcademicYear();

						return sortDescending(firstAcadYear, secondAcadYear);
					}

					private int sortAscending(int x, int y) {
						return (x < y) ? -1 : ((x == y) ? 0 : 1);
					}

					private int sortDescending(int x, int y) {
						return (x > y) ? -1 : ((x == y) ? 0 : 1);
					}
				});
			}
			catch(IOException | JSONException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onProfileCardClicked(ProfileHolder holder, BowlerSeasonStats seasonStats) {
		//Define intent.
		Intent intent = new Intent(getActivity(), ProfileCardDetails.class);

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

			//Define new shared element.
			View cardView = holder.getProfileCard();
			String expandCardName = getResources().getString(R.string.transition_expand_card);
			cardView.setTransitionName(expandCardName);
			Pair<View, String> cardSharedElement = new Pair<>(cardView, expandCardName);

			//Put extras into intent, to send to the Profile Cards Details activity.
			intent.putExtra("SEASON_STATS", seasonStats);

			ActivityOptionsCompat sceneTransition = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), cardSharedElement);
			getActivity().startActivityFromFragment(this, intent, getTargetRequestCode(), sceneTransition.toBundle());
		}
		else {
			getActivity().startActivity(intent);
		}
	}
}