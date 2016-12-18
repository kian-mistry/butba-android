package com.kian.butba.social;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
		final HashMap<String, String> current = statusList.get(position);

		//Use Picasso library to load image from URL.
		Picasso.with(inflater.getContext())
				.load(current.get("facebook_profile_picture"))
				.error(R.mipmap.ic_logo_circle)
				.into(holder.getFacebookProfilePicture());

		holder.getFacebookStory().setText(current.get("facebook_story"));
		holder.getFacebookMessage().setText(current.get("facebook_message"));

		//Display the interactions with each post (reactions, comments, shares).
		Integer reactions = Integer.parseInt(current.get("facebook_reactions"));
		if(reactions != 0) {
			holder.getFacebookReactionIcon().setVisibility(View.VISIBLE);
			holder.getFacebookReactions().setVisibility(View.VISIBLE);
			holder.getFacebookReactions().setText(reactions.toString());
		}
		else {
			holder.getFacebookReactionIcon().setVisibility(View.GONE);
			holder.getFacebookReactions().setVisibility(View.GONE);
		}

		Integer comments = Integer.parseInt(current.get("facebook_comments"));
		if(comments != 0) {
			holder.getFacebookCommentIcon().setVisibility(View.VISIBLE);
			holder.getFacebookComments().setVisibility(View.VISIBLE);
			holder.getFacebookComments().setText(comments.toString());
		}
		else {
			holder.getFacebookCommentIcon().setVisibility(View.GONE);
			holder.getFacebookComments().setVisibility(View.GONE);
		}

		Integer shares = Integer.parseInt(current.get("facebook_shares"));
		if(shares != 0) {
			holder.getFacebookShareIcon().setVisibility(View.VISIBLE);
			holder.getFacebookShares().setVisibility(View.VISIBLE);
			holder.getFacebookShares().setText(shares.toString());
		}
		else {
			holder.getFacebookShareIcon().setVisibility(View.GONE);
			holder.getFacebookShares().setVisibility(View.GONE);
		}

		/*
		 * Opens post in a web browser.
		 * May give the option to open in Facebook if the user has the app installed.
		 */
		holder.getFacebookViewPost().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String id = current.get("facebook_id");
				String[] ids = id.split("[_]");

				String url = "https://www.facebook.com/" + ids[1];
				Uri uri = Uri.parse(url);

				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				inflater.getContext().startActivity(intent);
			}
		});
	}

	@Override
	public int getItemCount() {
		return statusList.size();
	}

	class FacebookHolder extends ViewHolder {

		private CircularImageView facebookProfilePicture;
		private TextView facebookStory;
		private TextView facebookMessage;

		private ImageView facebookReactionIcon;
		private ImageView facebookCommentIcon;
		private ImageView facebookShareIcon;

		private TextView facebookReactions;
		private TextView facebookComments;
		private TextView facebookShares;

		private Button facebookViewPost;

		public FacebookHolder(View itemView) {
			super(itemView);

			facebookProfilePicture = (CircularImageView) itemView.findViewById(R.id.facebook_profile_picture);
			facebookStory = (TextView) itemView.findViewById(R.id.facebook_story);
			facebookMessage = (TextView) itemView.findViewById(R.id.facebook_message);

			facebookReactionIcon = (ImageView) itemView.findViewById(R.id.facebook_reaction_icon);
			facebookCommentIcon = (ImageView) itemView.findViewById(R.id.facebook_comment_icon);
			facebookShareIcon = (ImageView) itemView.findViewById(R.id.facebook_share_icon);

			facebookReactions = (TextView) itemView.findViewById(R.id.facebook_reactions);
			facebookComments = (TextView) itemView.findViewById(R.id.facebook_comments);
			facebookShares = (TextView) itemView.findViewById(R.id.facebook_shares);

			facebookViewPost = (Button) itemView.findViewById(R.id.facebook_view_post);
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

		public ImageView getFacebookReactionIcon() {
			return facebookReactionIcon;
		}

		public ImageView getFacebookCommentIcon() {
			return facebookCommentIcon;
		}

		public ImageView getFacebookShareIcon() {
			return facebookShareIcon;
		}

		public TextView getFacebookReactions() {
			return facebookReactions;
		}

		public TextView getFacebookComments() {
			return facebookComments;
		}

		public TextView getFacebookShares() {
			return facebookShares;
		}

		public Button getFacebookViewPost() {
			return facebookViewPost;
		}
	}
}
