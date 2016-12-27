package com.kian.butba.profile;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kian.butba.R;
import com.kian.butba.database.sqlite.tables.TableAcademicYear;
import com.kian.butba.profile.ProfileCardsAdapter.ProfileHolder;
import com.kian.butba.views.CardClickListener.ProfileCardClickListener;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Kian Mistry on 29/10/16.
 */

public class ProfileCardsAdapter extends Adapter<ProfileHolder> {

	private Context context;
	private ProfileCardClickListener cardClickListener;
    private List<HashMap<String, String>> profiles = Collections.emptyList();

    public ProfileCardsAdapter(Context context, ProfileCardClickListener cardClickListener, List<HashMap<String, String>> profiles) {
	    this.context = context;
	    this.cardClickListener = cardClickListener;
        this.profiles = profiles;
    }

    @Override
    public ProfileHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_profile, parent, false);
        ProfileHolder holder = new ProfileHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ProfileHolder holder, final int position) {
        final HashMap<String, String> current = profiles.get(position);

		//Set the contents of the card.
	    final int academicYearId = Integer.valueOf(current.get("academic_year"));
        String academicYear = new TableAcademicYear(context).getAcademicYear(academicYearId);
        holder.getProfileSeason().setText(academicYear);

        String studentStatus = current.get("student_status");
        if(studentStatus.equals("Student")) {
            holder.getProfileStatus().setText(studentStatus + " // " + current.get("ranking_status") + " // " + current.get("university"));
        }
        else {
            holder.getProfileStatus().setText(studentStatus + " // " + current.get("ranking_status"));
        }

        holder.getProfileAverage().setText("Overall Average: " + current.get("average"));
        holder.getProfileGames().setText("Games: " + current.get("games"));

        if(academicYear.equals("2016/17") && studentStatus.equals("Student")) {
            holder.getProfileRankingPoints().setText("Total Points: " + current.get("points") + " // Best 4: " + current.get("best_n"));
        }
        else {
            holder.getProfileRankingPoints().setText("Total Points: " + current.get("points") + " // Best 5: " + current.get("best_n"));
        }

	    /*
	     * Add a click listener to the profile card so it is able to expand
	     * when clicked to reveal more details.
	     */
	    holder.getProfileCard().setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View v) {
			    cardClickListener.onProfileCardClicked(holder, academicYearId);
		    }
	    });
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

	class ProfileHolder extends ViewHolder {

        private CardView profileCard;

        private TextView profileSeason;
        private TextView profileStatus;
        private TextView profileAverage;
        private TextView profileGames;
        private TextView profileRankingPoints;

        public ProfileHolder(View itemView) {
            super(itemView);

	        profileCard = (CardView) itemView.findViewById(R.id.profile_card);

            profileSeason = (TextView) itemView.findViewById(R.id.profile_season);
            profileStatus = (TextView) itemView.findViewById(R.id.profile_status);
            profileAverage = (TextView) itemView.findViewById(R.id.profile_average);
            profileGames = (TextView) itemView.findViewById(R.id.profile_games);
            profileRankingPoints = (TextView) itemView.findViewById(R.id.profile_ranking_points);
        }

	    public CardView getProfileCard() {
		    return profileCard;
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
