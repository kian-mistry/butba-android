package com.kian.butba.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kian.butba.R;
import com.kian.butba.database.server.BowlersSeasonStatsFetcher;
import com.kian.butba.database.server.QueriesUrl;
import com.kian.butba.database.sqlite.entities.BowlersSeasonStats;
import com.kian.butba.database.sqlite.tables.TableAcademicYear;
import com.kian.butba.database.sqlite.tables.TableRankingStatus;
import com.kian.butba.database.sqlite.tables.TableStudentStatus;
import com.kian.butba.file.FileOperations;
import com.kian.butba.file.ServerFileDownloader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Kian Mistry on 17/10/16.
 */

public class ProfileFragment extends Fragment {

	private ActionBar toolbar;

    private SharedPreferences prefBowlerDetails;

    private RecyclerView recyclerView;
    private ProfileCardsAdapter profileCardsAdapter;
    private ArrayList<HashMap<String, String>> profiles;

    public ProfileFragment() {
        //Required: Empty public constructor.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_profile, container, false);

	    //Obtain toolbar.
	    toolbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
	    toolbar.setTitle("Profile");

	    //Initialise recycler view.
        recyclerView = (RecyclerView) layout.findViewById(R.id.profile_cards_container);

        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();

        //Obtain Bowler ID from shared preference.
        prefBowlerDetails = getActivity().getSharedPreferences("bowler_details", Context.MODE_PRIVATE);
        final int bowlerId = prefBowlerDetails.getInt("bowler_id", 0);

        ServerFileDownloader fileDownloader = new ServerFileDownloader(getContext(), new com.kian.butba.file.AsyncDelegate() {
	        @Override
	        public void onProcessResults(Boolean success) {
				if(success) {
					BowlersSeasonStatsFetcher bowlersSeasonStatsFetcher = new BowlersSeasonStatsFetcher(getActivity(), new com.kian.butba.database.server.AsyncDelegate() {
						@Override
						public void onProcessResults(List<?> results) {
							if(bowlerId != 0) {
								List<BowlersSeasonStats> bowlersSeasonStats = (List<BowlersSeasonStats>) results;
								int bowlersSeasonStatsSize = bowlersSeasonStats.size();

								//TODO: Bit hacky, need to neaten up.
								profiles = new ArrayList<>();
								HashMap<String, String> seasonDetails;

								for(int i = 0; i < bowlersSeasonStatsSize; i++) {
									seasonDetails = new HashMap<>();

									String academicYear = new TableAcademicYear(getActivity().getBaseContext()).getAcademicYear(bowlersSeasonStats.get(i).getAcademicYear());
									String rankingStatus = new TableRankingStatus(getActivity().getBaseContext()).getRankingStatus(bowlersSeasonStats.get(i).getRankingStatus());
									String studentStatus = new TableStudentStatus(getActivity().getBaseContext()).getStudentStatus(bowlersSeasonStats.get(i).getStudentStatus());
									String university = bowlersSeasonStats.get(i).getUniversity();
									String average = String.valueOf(bowlersSeasonStats.get(i).getOverallAverage());
									String games = String.valueOf(bowlersSeasonStats.get(i).getTotalGames());
									String points = String.valueOf(bowlersSeasonStats.get(i).getTotalPoints());
									String bestN = String.valueOf(bowlersSeasonStats.get(i).getBestN());

									seasonDetails.put("academic_year", academicYear);
									seasonDetails.put("ranking_status", rankingStatus);
									seasonDetails.put("student_status", studentStatus);
									seasonDetails.put("university", university);
									seasonDetails.put("average", average);
									seasonDetails.put("games", games);
									seasonDetails.put("points", points);
									seasonDetails.put("best_n", bestN);

									profiles.add(seasonDetails);
								}

								//Set up recycler view to display the bowler's profile for each season.
								profileCardsAdapter = new ProfileCardsAdapter(getActivity(), getProfiles());
								recyclerView.setAdapter(profileCardsAdapter);
								recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
							}
						}
					});
					bowlersSeasonStatsFetcher.execute();
				}
	        }
        });

	    fileDownloader.execute(
			    QueriesUrl.get_particular_bowlers_ranking(bowlerId),
			    FileOperations.BOWLERS_SEASON_STATS_FILE
	    );
    }

    public ArrayList<HashMap<String, String>> getProfiles() {
        return profiles;
    }
}
