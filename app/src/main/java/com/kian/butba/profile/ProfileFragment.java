package com.kian.butba.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kian.butba.R;
import com.kian.butba.database.server.SeasonDetailsFetcher;
import com.kian.butba.database.sqlite.entities.BowlerSeason;
import com.kian.butba.database.sqlite.tables.TableAcademicYear;
import com.kian.butba.database.sqlite.tables.TableBowlerSeason;
import com.kian.butba.database.sqlite.tables.TableRankingStatus;
import com.kian.butba.database.sqlite.tables.TableStudentStatus;
import com.kian.butba.database.sqlite.tables.TableUniversity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Kian Mistry on 17/10/16.
 */

public class ProfileFragment extends Fragment {

    private SharedPreferences prefBowlerDetails;

    private RecyclerView recyclerView;
    private ProfileCardsAdapter profileCardsAdapter;
    private ArrayList<HashMap<String, String>> profiles;

    public ProfileFragment() {
        //Required: Empty public constructor.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.profile_cards_container);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        //Obtain Bowler ID from shared preference.
        prefBowlerDetails = getActivity().getSharedPreferences("bowler_details", Context.MODE_PRIVATE);
        int bowlerId = prefBowlerDetails.getInt("bowler_id", 0);

        if(bowlerId != 0) {
            List<BowlerSeason> bowlersSeason = new TableBowlerSeason(getActivity().getBaseContext()).getBowlersSeason(bowlerId);
            int bowlersSeasonSize = bowlersSeason.size();

            //TODO: Bit hacky, need to neaten up.
            profiles = new ArrayList<>();
            HashMap<String, String> seasonDetails;

            for(int i = 0; i < bowlersSeasonSize; i++) {
                seasonDetails = new HashMap<>();

                String academicYear = new TableAcademicYear(getActivity().getBaseContext()).getAcademicYear(bowlersSeason.get(i).getAcademicYear());
                String rankingStatus = new TableRankingStatus(getActivity().getBaseContext()).getRankingStatus(bowlersSeason.get(i).getRankingStatus());
                String studentStatus = new TableStudentStatus(getActivity().getBaseContext()).getStudentStatus(bowlersSeason.get(i).getStudentStatus());
                String university = new TableUniversity(getActivity().getBaseContext()).getUniversity(bowlersSeason.get(i).getUniversityId());



                seasonDetails.put("academic_year", academicYear);
                seasonDetails.put("ranking_status", rankingStatus);
                seasonDetails.put("student_status", studentStatus);
                seasonDetails.put("university", university);

                profiles.add(seasonDetails);
            }

            //Set up recycler view to display the bowler's profile for each season.
            profileCardsAdapter = new ProfileCardsAdapter(getActivity(), getProfiles());
            recyclerView.setAdapter(profileCardsAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
//
//        if(bowlerId != 0) {
//            //A BUTBA member exists.
//            QueryMap queryMap = new QueryMap(QueryTag.GET_BOWLER_STATUSES, "bowler_id", String.valueOf(bowlerId));
//
//            fetcher = new SeasonDetailsFetcher(new SeasonDetailsFetcher.AsyncDelegate() {
//                @Override
//                public void onProcessResults(ArrayList<String[]> output) {
//                    profiles = new ArrayList<>();
//                    HashMap<String, String> seasonDetails;
//
//                    for(int i = 0; i < output.size(); i++) {
//                        String studentStatus = output.get(i)[1];
//                        String rankingStatus = output.get(i)[2];
//                        String university = output.get(i)[3];
//                        String average = output.get(i)[4];
//                        String games = output.get(i)[5];
//                        String totalRankings = output.get(i)[6];
//                        String bestX = output.get(i)[7];
//
//                        seasonDetails = new HashMap<>();
//                        seasonDetails.put("student_status", studentStatus);
//                        seasonDetails.put("ranking_status", rankingStatus);
//                        seasonDetails.put("university", university);
//                        seasonDetails.put("average", average);
//                        seasonDetails.put("games", games);
//                        seasonDetails.put("total_rankings", totalRankings);
//                        seasonDetails.put("best_x", bestX);
//
//                        Log.d("RANKINGS ", academicYear + " // " + totalRankings + " // " + bestX);
//
//                        profiles.add(seasonDetails);
//                    }
//
//                    //Set up recycler view to display the bowler's profile for each season.
//                    profileCardsAdapter = new ProfileCardsAdapter(getActivity(), getProfiles());
//                    recyclerView.setAdapter(profileCardsAdapter);
//                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//                }
//            });
//            fetcher.execute(queryMap);
//        }
    }


    public ArrayList<HashMap<String, String>> getProfiles() {
        return profiles;
    }
}
