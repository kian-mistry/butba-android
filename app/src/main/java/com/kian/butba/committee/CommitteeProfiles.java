package com.kian.butba.committee;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kian.butba.R;
import com.kian.butba.json.JSONHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Kian Mistry on 20/10/16.
 */

public class CommitteeProfiles extends Fragment {

    public static final String ARG_1 = "arg1";

    //Will hold the value of parameter passed through when the new fragment instance is created.
    private int arg1 = 0;

    private JSONObject jsonObject;
    private JSONArray jsonArray;

    private ArrayList<HashMap<String, String>> members;

    private RecyclerView recyclerView;
    private CommitteeCardsAdapter committeeCardsAdapter;


    public CommitteeProfiles() {
        //Required: Empty public constructor.
    }

    public static CommitteeProfiles newInstance(int committeeType) {
        CommitteeProfiles fragment = new CommitteeProfiles();

        Bundle args = new Bundle();
        args.putInt(ARG_1, committeeType);
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
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_committee, container, false);
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
            arg1 = args.getInt(ARG_1, 0);

            jsonObject = JSONHandler.loadJson(getContext(), "committee_members.json");
        }

        if(arg1 == 0) {
            try {
                jsonArray = jsonObject.getJSONArray("exec");
            }
            catch(JSONException e) {
                e.printStackTrace();
            }
        }
        else if(arg1 == 1) {
            try {
                jsonArray = jsonObject.getJSONArray("non-exec");
            }
            catch(JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            members = new ArrayList<>();
            HashMap<String, String> memberDetails;

            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject member = jsonArray.getJSONObject(i);

                String position = member.getString("position");
                String name = member.getString("forename") + " " + member.getString("surname");
                String university = member.getString("university");
                String email = member.getString("email");

                memberDetails = new HashMap<>();
                memberDetails.put("position", position);
                memberDetails.put("name", name);
                memberDetails.put("university", university);
                memberDetails.put("email", email);

                members.add(memberDetails);
            }
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
    }
}