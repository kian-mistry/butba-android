package com.kian.butba.views;

import android.support.v7.widget.RecyclerView.ViewHolder;

/**
 * Created by Kian Mistry on 26/12/16.
 */

public interface CardClickListener {

	interface ProfileCardClickListener {

		/**
		 * Called when a card is clicked.
		 *
		 * @param holder The view holder for the clicked card.
		 * @param yearId The unique ID of the academic year.
		 */
		void onProfileCardClicked(ViewHolder holder, int yearId);
	}

	/**
	 * Called when a card is clicked.
	 *
	 * @param holder The view holder for the clicked card.
	 * @param position The position of the card within the recycler view.
	 */
	abstract void onCardClicked(ViewHolder holder, int position);

}
