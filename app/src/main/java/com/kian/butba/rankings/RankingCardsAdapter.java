package com.kian.butba.rankings;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kian.butba.R;
import com.kian.butba.rankings.RankingCardsAdapter.RankingHolder;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Kian Mistry on 14/12/16.
 */

public class RankingCardsAdapter extends Adapter<RankingHolder> {

	private LayoutInflater inflater;
	private List<HashMap<String, String>> rankingsList = Collections.emptyList();

	public RankingCardsAdapter(Context context, List<HashMap<String, String>> rankingsList) {
		inflater = LayoutInflater.from(context);
		this.rankingsList = rankingsList;
	}

	@Override
	public RankingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.card_ranking, parent, false);
		RankingHolder holder = new RankingHolder(view);

		return holder;
	}

	@Override
	public void onBindViewHolder(RankingHolder holder, int position) {
		HashMap<String, String> current = rankingsList.get(position);

		String name = current.get("name");

		if(name != null) {
			holder.getBowlerName().setText(name);
			String university = current.get("university");
			String bestN = current.get("best_n");
			if(university.equals("Ex-Student")) {
				holder.getUniversity().setVisibility(View.GONE);
				holder.getBestN().setText("Best 5: " + bestN);
			}
			else {
				holder.getUniversity().setVisibility(View.VISIBLE);
				holder.getUniversity().setText(university);
				holder.getBestN().setText("Best 4: " + bestN);
			}

			holder.getTotalPoints().setText("Total Points: " + current.get("total_points"));
			holder.getTotalEvents().setText("Total Events: " + current.get("total_events"));
		}
		else {
			holder.getBowlerName().setText(current.get("university"));
			holder.getBestN().setText("Total Points: " + current.get("total_points"));

			holder.getUniversity().setVisibility(View.GONE);
			holder.getTotalPoints().setVisibility(View.GONE);
			holder.getTotalEvents().setVisibility(View.GONE);
		}
	}

	@Override
	public int getItemCount() {
		return rankingsList.size();
	}

	class RankingHolder extends ViewHolder {

		private TextView bowlerName;
		private TextView university;
		private TextView bestN;
		private TextView totalPoints;
		private TextView totalEvents;

		public RankingHolder(View itemView) {
			super(itemView);

			bowlerName = (TextView) itemView.findViewById(R.id.ranking_bowler_name);
			university = (TextView) itemView.findViewById(R.id.ranking_university);
			bestN = (TextView) itemView.findViewById(R.id.ranking_best_n);
			totalPoints = (TextView) itemView.findViewById(R.id.ranking_total_points);
			totalEvents = (TextView) itemView.findViewById(R.id.ranking_total_events);
		}

		public TextView getBowlerName() {
			return bowlerName;
		}

		public TextView getUniversity() {
			return university;
		}

		public TextView getBestN() {
			return bestN;
		}

		public TextView getTotalPoints() {
			return totalPoints;
		}

		public TextView getTotalEvents() {
			return totalEvents;
		}
	}
}