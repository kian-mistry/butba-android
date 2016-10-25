package com.kian.butba.committee;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kian.butba.R;
import com.kian.butba.committee.CommitteeCardsAdapter.CommitteeProfileHolder;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CommitteeCardsAdapter extends Adapter<CommitteeProfileHolder> {

    private LayoutInflater inflater;

    private List<HashMap<String, String>> committeeProfiles = Collections.emptyList();

    public CommitteeCardsAdapter(Context context, List<HashMap<String, String>> committeeProfiles) {
        inflater = LayoutInflater.from(context);
        this.committeeProfiles = committeeProfiles;
    }

    @Override
    public CommitteeProfileHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card_committee, parent, false);
        CommitteeProfileHolder holder = new CommitteeProfileHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(CommitteeProfileHolder holder, int position) {
        HashMap<String, String> current = committeeProfiles.get(position);

        //Use of Picasso library to load image from URL.
        Picasso.with(inflater.getContext())
                .load(current.get("image"))
                .error(R.mipmap.ic_launcher)
                .into(holder.getCommitteeImage());


        holder.getCommitteePosition().setText(current.get("position"));
        holder.getCommitteeName().setText(current.get("name"));
        holder.getCommitteeUniversity().setText(current.get("university"));
        holder.getCommitteeEmail().setText(current.get("email"));
        holder.getCommitteeHighGame().setText("High Game: " + current.get("high-game"));
        holder.getCommitteeHighSeries().setText("High Series: " + current.get("high-series"));
    }

    @Override
    public int getItemCount() {
        return committeeProfiles.size();
    }

    class CommitteeProfileHolder extends ViewHolder {

        private ImageView committeeImage;
        private TextView committeePosition;
        private TextView committeeName;
        private TextView committeeEmail;
        private TextView committeeUniversity;
        private TextView committeeHighGame;
        private TextView committeeHighSeries;

        public CommitteeProfileHolder(View itemView) {
            super(itemView);

            committeeImage = (ImageView) itemView.findViewById(R.id.committee_image);
            committeePosition = (TextView) itemView.findViewById(R.id.committee_position);
            committeeName = (TextView) itemView.findViewById(R.id.committee_name);
            committeeEmail = (TextView) itemView.findViewById(R.id.committee_email);
            committeeUniversity = (TextView) itemView.findViewById(R.id.committee_uni);
            committeeHighGame = (TextView) itemView.findViewById(R.id.committee_high_game);
            committeeHighSeries = (TextView) itemView.findViewById(R.id.committee_high_series);
        }

        public ImageView getCommitteeImage() {
            return committeeImage;
        }

        public TextView getCommitteePosition() {
            return committeePosition;
        }

        public TextView getCommitteeName() {
            return committeeName;
        }

        public TextView getCommitteeEmail() {
            return committeeEmail;
        }

        public TextView getCommitteeUniversity() {
            return committeeUniversity;
        }

        public TextView getCommitteeHighGame() {
            return committeeHighGame;
        }

        public TextView getCommitteeHighSeries() {
            return committeeHighSeries;
        }
    }
}
