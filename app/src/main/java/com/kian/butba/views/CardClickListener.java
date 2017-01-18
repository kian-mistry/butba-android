package com.kian.butba.views;

import android.support.v7.widget.RecyclerView.ViewHolder;

/**
 * Created by Kian Mistry on 26/12/16.
 */

public interface CardClickListener<VH extends ViewHolder> {

	interface ProfileCardClickListener<VH extends ViewHolder> {

		/**
		 * Called when a card is clicked.
		 *
		 * @param holder The view holder for the clicked card.
		 * @param yearId The unique ID of the academic year.
		 */
		void onProfileCardClicked(VH holder, int yearId);
	}

	/**
	 * Called when a card is clicked.
	 *
	 * @param holder The view holder for the clicked card.
	 * @param position The position of the card within the recycler view.
	 */
	void onCardClicked(VH holder, int position);

}
