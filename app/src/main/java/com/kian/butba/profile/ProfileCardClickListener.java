package com.kian.butba.profile;

import android.support.v7.widget.RecyclerView.ViewHolder;

import java.io.Serializable;

/**
 * Created by Kian Mistry on 03/03/17.
 */

public interface ProfileCardClickListener<VH extends ViewHolder, E extends Serializable> {

	/**
	 * Called when a profile card is clicked.
	 *
	 * @param holder The view holder for the clicked card.
	 * @param entity The entity to be displayed on the card.
	 */
	void onProfileCardClicked(VH holder, E entity);
}