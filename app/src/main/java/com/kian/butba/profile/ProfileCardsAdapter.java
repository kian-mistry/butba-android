package com.kian.butba.profile;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kian.butba.R;
import com.kian.butba.profile.ProfileCardsAdapter.ProfileHolder;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Kian Mistry on 29/10/16.
 */

public class ProfileCardsAdapter extends Adapter<ProfileHolder>{

    private LayoutInflater inflater;
    private List<HashMap<String, String>> profiles = Collections.emptyList();

    public ProfileCardsAdapter(Context context, List<HashMap<String, String>> profiles) {
        inflater = LayoutInflater.from(context);
        this.profiles = profiles;
    }

    @Override
    public ProfileHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card_profile, parent, false);
        ProfileHolder holder = new ProfileHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ProfileHolder holder, int position) {
        final HashMap<String, String> current = profiles.get(position);
        holder.getProfileSeason().setText(current.get("academic_year"));

        String studentStatus = current.get("student_status");
        if(!studentStatus.equals("Ex-Student")) {
            holder.getProfileStatus().setText(studentStatus + " // " + current.get("ranking_status") + " // " + current.get("university"));
        }
        else {
            holder.getProfileStatus().setText(studentStatus + " // " + current.get("ranking_status"));
        }

        holder.getProfileAverage().setText("Overall Average: " + current.get("average"));
        holder.getProfileGames().setText("Games: " + current.get("games"));
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    class ProfileHolder extends ViewHolder {

        private TextView profileSeason;
        private TextView profileStatus;
        private TextView profileAverage;
        private TextView profileGames;
        private TextView profileRankingPoints;

        public ProfileHolder(View itemView) {
            super(itemView);

            profileSeason = (TextView) itemView.findViewById(R.id.profile_season);
            profileStatus = (TextView) itemView.findViewById(R.id.profile_status);
            profileAverage = (TextView) itemView.findViewById(R.id.profile_average);
            profileGames = (TextView) itemView.findViewById(R.id.profile_games);
            profileRankingPoints = (TextView) itemView.findViewById(R.id.profile_ranking_points);
        }

        public TextView getProfileSeason() {
            return profileSeason;
        }

        public TextView getProfileStatus() {
            return profileStatus;
        }

        public TextView getProfileAverage() {
            return profileAverage;
        }

        public TextView getProfileGames() {
            return profileGames;
        }

        public TextView getProfileRankingPoints() {
            return profileRankingPoints;
        }
    }
}
