package com.kian.butba.committee;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kian.butba.R;
import com.kian.butba.committee.CommitteeCardsAdapter.CommitteeProfileHolder;

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

        holder.getCommitteePosition().setText(current.get("position"));
        holder.getCommitteeName().setText(current.get("name"));
        holder.getCommitteeUniversity().setText(current.get("university"));
        holder.getCommitteeEmail().setText(current.get("email"));
    }

    @Override
    public int getItemCount() {
        return committeeProfiles.size();
    }

    class CommitteeProfileHolder extends ViewHolder {

        private TextView committeePosition;
        private TextView committeeName;
        private TextView committeeEmail;
        private TextView committeeUniversity;

        public CommitteeProfileHolder(View itemView) {
            super(itemView);

            committeePosition = (TextView) itemView.findViewById(R.id.committee_position);
            committeeName = (TextView) itemView.findViewById(R.id.committee_name);
            committeeEmail = (TextView) itemView.findViewById(R.id.committee_email);
            committeeUniversity = (TextView) itemView.findViewById(R.id.committee_uni);
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

    }
}
