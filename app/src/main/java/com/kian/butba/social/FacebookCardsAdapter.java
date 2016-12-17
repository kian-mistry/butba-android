package com.kian.butba.social;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kian.butba.R;
import com.kian.butba.social.FacebookCardsAdapter.FacebookHolder;
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Kian Mistry on 17/12/16.
 */

public class FacebookCardsAdapter extends Adapter<FacebookHolder> {

	private LayoutInflater inflater;
	private List<HashMap<String, String>> statusList = Collections.emptyList();

	public FacebookCardsAdapter(Context context, List<HashMap<String, String>> statusList) {
		inflater = LayoutInflater.from(context);
		this.statusList = statusList;
	}

	@Override
	public FacebookHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.card_facebook, parent, false);
		FacebookHolder holder = new FacebookHolder(view);

		return holder;
	}

	@Override
	public void onBindViewHolder(FacebookHolder holder, int position) {
		HashMap<String, String> current = statusList.get(position);

		//Use Picasso library to load image from URL.
		Picasso.with(inflater.getContext())
				.load(current.get("facebook_profile_picture"))
				.error(R.mipmap.ic_logo_circle)
				.into(holder.getFacebookProfilePicture());

		holder.getFacebookStory().setText(current.get("facebook_story"));
		holder.getFacebookMessage().setText(current.get("facebook_message"));
	}

	@Override
	public int getItemCount() {
		return statusList.size();
	}

	class FacebookHolder extends ViewHolder {

		private CircularImageView facebookProfilePicture;
		private TextView facebookStory;
		private TextView facebookMessage;

		public FacebookHolder(View itemView) {
			super(itemView);

			facebookProfilePicture = (CircularImageView) itemView.findViewById(R.id.facebook_profile_picture);
			facebookStory = (TextView) itemView.findViewById(R.id.facebook_story);
			facebookMessage = (TextView) itemView.findViewById(R.id.facebook_message);
		}

		public CircularImageView getFacebookProfilePicture() {
			return facebookProfilePicture;
		}

		public TextView getFacebookStory() {
			return facebookStory;
		}

		public TextView getFacebookMessage() {
			return facebookMessage;
		}
	}
}
