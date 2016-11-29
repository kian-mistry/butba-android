package com.kian.butba.profile;

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
import com.kian.butba.database.sqlite.entities.BowlerSeason;
import com.kian.butba.database.sqlite.entities.OverallAverage;
import com.kian.butba.database.sqlite.tables.TableAcademicYear;
import com.kian.butba.database.sqlite.tables.TableBowlerSeason;
import com.kian.butba.database.sqlite.tables.TableEventAverage;
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
            List<OverallAverage> bowlersAverage = new TableEventAverage(getActivity().getBaseContext()).getOverallAverageOverAllSeasons(bowlerId);
            int bowlersSeasonSize = bowlersSeason.size();
            int bowlersAverageSize = bowlersAverage.size();

            //TODO: Bit hacky, need to neaten up.
            profiles = new ArrayList<>();
            HashMap<String, String> seasonDetails;

            for(int i = 0; i < bowlersSeasonSize && i < bowlersAverageSize; i++) {
                seasonDetails = new HashMap<>();

                String academicYear = new TableAcademicYear(getActivity().getBaseContext()).getAcademicYear(bowlersSeason.get(i).getAcademicYear());
                String rankingStatus = new TableRankingStatus(getActivity().getBaseContext()).getRankingStatus(bowlersSeason.get(i).getRankingStatus());
                String studentStatus = new TableStudentStatus(getActivity().getBaseContext()).getStudentStatus(bowlersSeason.get(i).getStudentStatus());
                String university = new TableUniversity(getActivity().getBaseContext()).getUniversity(bowlersSeason.get(i).getUniversityId());
                String average = String.valueOf(bowlersAverage.get(i).getAverage());
                String games = String.valueOf(bowlersAverage.get(i).getTotalGames());

                seasonDetails.put("academic_year", academicYear);
                seasonDetails.put("ranking_status", rankingStatus);
                seasonDetails.put("student_status", studentStatus);
                seasonDetails.put("university", university);
                seasonDetails.put("average", average);
                seasonDetails.put("games", games);

                profiles.add(seasonDetails);
            }

            //Set up recycler view to display the bowler's profile for each season.
            profileCardsAdapter = new ProfileCardsAdapter(getActivity(), getProfiles());
            recyclerView.setAdapter(profileCardsAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
    }


    public ArrayList<HashMap<String, String>> getProfiles() {
        return profiles;
    }
}
