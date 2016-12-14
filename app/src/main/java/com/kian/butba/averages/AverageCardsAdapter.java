package com.kian.butba.averages;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.kian.butba.R;
import com.kian.butba.averages.AverageCardsAdapter.AverageHolder;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Kian Mistry on 12/12/16.
 */

public class AverageCardsAdapter extends Adapter<AverageHolder> {

	private LayoutInflater inflater;
	private List<HashMap<String, String>> averagesList = Collections.emptyList();

	public AverageCardsAdapter(Context context, List<HashMap<String, String>> averagesList) {
		inflater = LayoutInflater.from(context);
		this.averagesList = averagesList;
	}

	@Override
	public AverageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.card_average, parent, false);
		AverageHolder holder = new AverageHolder(view);

		return holder;
	}

	@Override
	public void onBindViewHolder(AverageHolder holder, int position) {
		HashMap<String, String> current = averagesList.get(position);

		/* Set card colours.
		 * Light background for qualified averages (12 or more games).
		 * Dark background for unqualified averages (less than 12 games).
		 */
		int colourBackground;
		int colourText;

		int totalGames = Integer.parseInt(current.get("total_games"));
		if(totalGames < 12) {
			colourBackground = ContextCompat.getColor(inflater.getContext(), R.color.cardview_dark_background);
			colourText = ContextCompat.getColor(inflater.getContext(), android.R.color.primary_text_dark);
		}
		else {
			colourBackground = ContextCompat.getColor(inflater.getContext(), R.color.cardview_light_background);
			colourText = ContextCompat.getColor(inflater.getContext(), R.color.cardview_dark_background);
		}

		//Set card colours.
		holder.getCard().setBackgroundColor(colourBackground);
		holder.getBowlerName().setTextColor(colourText);
		holder.getUniversity().setTextColor(colourText);
		holder.getAverage().setTextColor(colourText);
		holder.getTotalPinfall().setTextColor(colourText);
		holder.getTotalGames().setTextColor(colourText);
		holder.getImprovement().setTextColor(colourText);

		//Set text of each card.
		holder.getBowlerName().setText(current.get("name"));

		String university = current.get("university");
		if(university.equals("Ex-Student")) {
			holder.getUniversity().setVisibility(View.GONE);
		}
		else {
			holder.getUniversity().setVisibility(View.VISIBLE);
			holder.getUniversity().setText(university);
		}

		holder.getAverage().setText("Average: " + current.get("average"));
		holder.getTotalPinfall().setText("Total Pinfall: " + current.get("total_pinfall"));
		holder.getTotalGames().setText("Total Games: " + current.get("total_games"));

		String improvement = current.get("improvement");
		if(improvement.equals("") || improvement.equals("null")) {
			LayoutParams layoutParams = holder.getTotalGames().getLayoutParams();

			holder.getTotalGames().setLayoutParams(layoutParams);
			holder.getTotalGames().setPadding(
					holder.getImprovement().getCompoundPaddingLeft(),
					holder.getImprovement().getCompoundPaddingTop(),
					holder.getImprovement().getCompoundPaddingRight(),
					holder.getImprovement().getCompoundPaddingBottom()
			);
			holder.getImprovement().setVisibility(View.GONE);
		}
		else {
			holder.getImprovement().setVisibility(View.VISIBLE);
			holder.getTotalGames().setPadding(
					holder.getImprovement().getCompoundPaddingLeft(),
					holder.getImprovement().getCompoundPaddingTop(),
					holder.getImprovement().getCompoundPaddingRight(),
					0
			);

			holder.getImprovement().setText("Improvement: " + improvement);
		}
	}

	@Override
	public int getItemCount() {
		return averagesList.size();
	}

	public void setAveragesList(List<HashMap<String, String>> averagesList) {
		this.averagesList = averagesList;
	}

	class AverageHolder extends ViewHolder {

		private CardView card;

		private TextView bowlerName;
		private TextView university;
		private TextView average;
		private TextView totalPinfall;
		private TextView totalGames;
		private TextView improvement;

		public AverageHolder(View itemView) {
			super(itemView);

			card = (CardView) itemView.findViewById(R.id.average_card);

			bowlerName = (TextView) itemView.findViewById(R.id.average_bowler_name);
			university = (TextView) itemView.findViewById(R.id.average_university);
			average = (TextView) itemView.findViewById(R.id.average);
			totalPinfall = (TextView) itemView.findViewById(R.id.average_total_pinfall);
			totalGames = (TextView) itemView.findViewById(R.id.average_total_games);
			improvement = (TextView) itemView.findViewById(R.id.average_improvement);
		}

		public CardView getCard() {
			return card;
		}

		public TextView getBowlerName() {
			return bowlerName;
		}

		public TextView getUniversity() {
			return university;
		}

		public TextView getAverage() {
			return average;
		}

		public TextView getTotalPinfall() {
			return totalPinfall;
		}

		public TextView getTotalGames() {
			return totalGames;
		}

		public TextView getImprovement() {
			return improvement;
		}
	}
}
