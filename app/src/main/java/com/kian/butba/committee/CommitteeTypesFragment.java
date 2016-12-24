package com.kian.butba.committee;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kian.butba.R;
import com.kian.butba.file.JSONHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Kian Mistry on 20/10/16.
 */

public class CommitteeTypesFragment extends Fragment {

    public static final String COMMITTEE_TYPE = "committeeType";

    //Will hold the value of parameter passed through when the new fragment instance is created.
    private int committeeType = 0;

    private JSONObject jsonObject;
    private JSONArray jsonArray;

    private ArrayList<HashMap<String, String>> members;

    private RecyclerView recyclerView;
    private CommitteeCardsAdapter committeeCardsAdapter;


    public CommitteeTypesFragment() {
        //Required: Empty public constructor.
    }

    public static CommitteeTypesFragment newInstance(int committeeType) {
        CommitteeTypesFragment fragment = new CommitteeTypesFragment();

        Bundle args = new Bundle();
        args.putInt(COMMITTEE_TYPE, committeeType);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Obtains the profiles of each committee member.
        getCommitteeProfiles();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the layout for this fragment.
        View layout = inflater.inflate(R.layout.fragment_committee_members, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.committee_cards_container);

        committeeCardsAdapter = new CommitteeCardsAdapter(getActivity(), getMembers());
        recyclerView.setAdapter(committeeCardsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return layout;
    }

    public ArrayList<HashMap<String, String>> getMembers() {
        return members;
    }

    private void getCommitteeProfiles() {
        Bundle args = getArguments();
        if(args != null) {
            committeeType = args.getInt(COMMITTEE_TYPE, 0);
            jsonObject = JSONHandler.loadJson(getContext(), "committee_members.json");
        }

        try {
            if (committeeType == 0) {
                jsonArray = jsonObject.getJSONArray("exec");
            }
            else if (committeeType == 1) {
                jsonArray = jsonObject.getJSONArray("non-exec");
            }

            members = new ArrayList<>();
            HashMap<String, String> memberDetails;

            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject member = jsonArray.getJSONObject(i);

                String imageUrl = member.getString("image");
                String position = member.getString("position");
                String name = member.getString("forename") + " " + member.getString("surname");
                String university = member.getString("university");
                String email = member.getString("email");
                String highGame = String.valueOf(member.getInt("high-game"));
                String highSeries = String.valueOf(member.getInt("high-series"));

                memberDetails = new HashMap<>();
                memberDetails.put("image", imageUrl);
                memberDetails.put("position", position);
                memberDetails.put("name", name);
                memberDetails.put("university", university);
                memberDetails.put("email", email);
                memberDetails.put("high-game", highGame);
                memberDetails.put("high-series", highSeries);

                members.add(memberDetails);
            }
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
    }
}
