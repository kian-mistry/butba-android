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
import com.kian.butba.database.QueryMap;
import com.kian.butba.database.QueryMap.QueryTag;
import com.kian.butba.database.SeasonDetailsFetcher;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Kian Mistry on 17/10/16.
 */

public class ProfileFragment extends Fragment {

    private SharedPreferences sharedPreferences;

    private SeasonDetailsFetcher fetcher;

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
        sharedPreferences = getActivity().getSharedPreferences("bowler_details", Context.MODE_PRIVATE);
        int bowlerId = sharedPreferences.getInt("bowler_id", 0);

        if(bowlerId != 0) {
            //A BUTBA member exists.
            QueryMap queryMap = new QueryMap(QueryTag.GET_BOWLER_STATUSES, "bowler_id", String.valueOf(bowlerId));

            fetcher = new SeasonDetailsFetcher(new SeasonDetailsFetcher.AsyncDelegate() {
                @Override
                public void onProcessResults(ArrayList<String[]> output) {
                    profiles = new ArrayList<>();
                    HashMap<String, String> seasonDetails;

                    for(int i = 0; i < output.size(); i++) {
                        String studentStatus = output.get(i)[0];
                        String rankingStatus = output.get(i)[1];
                        String university = output.get(i)[2];

                        seasonDetails = new HashMap<>();
                        seasonDetails.put("student_status", studentStatus);
                        seasonDetails.put("ranking_status", rankingStatus);
                        seasonDetails.put("university", university);

                        profiles.add(seasonDetails);
                    }

                    //Set up recycler view to display the bowler's profile for each season.
                    profileCardsAdapter = new ProfileCardsAdapter(getActivity(), getProfiles());
                    recyclerView.setAdapter(profileCardsAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                }
            });
            fetcher.execute(queryMap);
        }
    }

    public ArrayList<HashMap<String, String>> getProfiles() {
        return profiles;
    }
}
