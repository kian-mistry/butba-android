package com.kian.butba.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kian.butba.R;
import com.kian.butba.database.QueryMap;
import com.kian.butba.database.QueryMap.QueryTag;
import com.kian.butba.database.SeasonDetailsFetcher;

import java.util.ArrayList;

/**
 * Created by Kian Mistry on 17/10/16.
 */

public class ProfileFragment extends Fragment {

    private SharedPreferences sharedPreferences;

    private SeasonDetailsFetcher fetcher;

    public ProfileFragment() {
        //Required: Empty public constructor.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        //Obtain Bowler ID from shared preference.
        sharedPreferences = getActivity().getSharedPreferences("bowler_details", Context.MODE_PRIVATE);
        int bowlerId = sharedPreferences.getInt("bowler_id", 0);
        Log.d("ID", String.valueOf(bowlerId));

        if(bowlerId != 0) {
            //A BUTBA member exists.
            QueryMap queryMap = new QueryMap(QueryTag.GET_BOWLER_STATUSES, "bowler_id", String.valueOf(bowlerId));

            fetcher = new SeasonDetailsFetcher(new SeasonDetailsFetcher.AsyncDelegate() {
                @Override
                public void onProcessResults(ArrayList<String[]> output) {
                    TextView tvBowlerStatuses = (TextView) getActivity().findViewById(R.id.profile_bowler_status);
                    String result = "";

                    for(int i = 0; i < output.size(); i++) {
                        result += output.get(i)[0] + ", " + output.get(i)[1] + ", " + output.get(i)[2] + "\r\n";
                    }
                    Log.d("RESULT", result);
                    tvBowlerStatuses.setText(result);
                }
            });
            fetcher.execute(queryMap);
        }
    }
}
