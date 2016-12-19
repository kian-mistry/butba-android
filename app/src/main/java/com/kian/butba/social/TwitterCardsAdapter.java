package com.kian.butba.social;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kian.butba.R;
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Kian Mistry on 18/12/16.
 */

public class TwitterCardsAdapter extends RecyclerView.Adapter<TwitterCardsAdapter.TwitterHolder> {

	private LayoutInflater inflater;
	private List<HashMap<String, String>> statusList = Collections.emptyList();

	public TwitterCardsAdapter(Context context, List<HashMap<String, String>> statusList) {
		inflater = LayoutInflater.from(context);
		this.statusList = statusList;
	}

	@Override
	public TwitterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.card_twitter, parent, false);
		TwitterHolder holder= new TwitterHolder(view);

		return holder;
	}

	@Override
	public void onBindViewHolder(TwitterHolder holder, int position) {
		final HashMap<String, String> current = statusList.get(position);

		//Display whether tweet was retweeted.
		if(Boolean.parseBoolean(current.get("twitter_retweeted_status"))) {
			holder.getTwitterRetweetedIcon().setVisibility(View.VISIBLE);
			holder.getTwitterRetweeted().setVisibility(View.VISIBLE);
		}
		else {
			holder.getTwitterRetweetedIcon().setVisibility(View.GONE);
			holder.getTwitterRetweeted().setVisibility(View.GONE);
		}

		//Use Picasso library to load image from URL.
		Picasso.with(holder.itemView.getContext())
				.load(current.get("twitter_profile_picture"))
				.error(R.mipmap.ic_logo_circle)
				.placeholder(R.mipmap.ic_logo_circle)
				.into(holder.getTwitterProfilePicture());

		holder.getTwitterName().setText(current.get("twitter_name"));
		holder.getTwitterHandle().setText("@" + current.get("twitter_handle"));
		holder.getTwitterMessage().setText(current.get("twitter_message"));

		//Display the interactions with each post (retweets, likes).
		int retweets = Integer.parseInt(current.get("twitter_retweets"));
		if(retweets != 0) {
			holder.getTwitterRetweetIcon().setVisibility(View.VISIBLE);
			holder.getTwitterRetweets().setVisibility(View.VISIBLE);
			holder.getTwitterRetweets().setText(String.valueOf(retweets));
		}
		else {
			holder.getTwitterRetweetIcon().setVisibility(View.GONE);
			holder.getTwitterRetweets().setVisibility(View.GONE);
		}

		int likes = Integer.parseInt(current.get("twitter_likes"));
		if(likes != 0) {
			holder.getTwitterLikeIcon().setVisibility(View.VISIBLE);
			holder.getTwitterLikes().setVisibility(View.VISIBLE);
			holder.getTwitterLikes().setText(String.valueOf(likes));
		}
		else {
			holder.getTwitterLikeIcon().setVisibility(View.GONE);
			holder.getTwitterLikes().setVisibility(View.GONE);
		}

		/*
		 * Opens post in a web browser.
		 * May give the option to open in Twitter if the user has the app installed.
		 */
		holder.getTwitterViewTweet().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String id = current.get("twitter_id");

				String url = "http://twitter.com/ukunibowling/status/" + id;
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

	class TwitterHolder extends RecyclerView.ViewHolder {

		private CircularImageView twitterProfilePicture;

		private ImageView twitterRetweetedIcon;
		private TextView twitterRetweeted;

		private TextView twitterName;
		private TextView twitterHandle;
		private TextView twitterMessage;

		private ImageView twitterRetweetIcon;
		private ImageView twitterLikeIcon;

		private TextView twitterRetweets;
		private TextView twitterLikes;

		private Button twitterViewTweet;


		public TwitterHolder(View itemView) {
			super(itemView);

			twitterProfilePicture = (CircularImageView) itemView.findViewById(R.id.twitter_profile_picture);

			twitterRetweetedIcon = (ImageView) itemView.findViewById(R.id.twitter_retweeted_icon);
			twitterRetweeted = (TextView) itemView.findViewById(R.id.twitter_retweeted);

			twitterName = (TextView) itemView.findViewById(R.id.twitter_name);
			twitterHandle = (TextView) itemView.findViewById(R.id.twitter_handle);
			twitterMessage = (TextView) itemView.findViewById(R.id.twitter_message);

			twitterRetweetIcon = (ImageView) itemView.findViewById(R.id.twitter_retweet_icon);
			twitterLikeIcon = (ImageView) itemView.findViewById(R.id.twitter_like_icon);

			twitterRetweets = (TextView) itemView.findViewById(R.id.twitter_retweets);
			twitterLikes = (TextView) itemView.findViewById(R.id.twitter_likes);

			twitterViewTweet = (Button) itemView.findViewById(R.id.twitter_view_tweet);
		}

		public CircularImageView getTwitterProfilePicture() {
			return twitterProfilePicture;
		}

		public ImageView getTwitterRetweetedIcon() {
			return twitterRetweetedIcon;
		}

		public TextView getTwitterRetweeted() {
			return twitterRetweeted;
		}

		public TextView getTwitterName() {
			return twitterName;
		}

		public TextView getTwitterHandle() {
			return twitterHandle;
		}

		public TextView getTwitterMessage() {
			return twitterMessage;
		}

		public ImageView getTwitterRetweetIcon() {
			return twitterRetweetIcon;
		}

		public ImageView getTwitterLikeIcon() {
			return twitterLikeIcon;
		}

		public TextView getTwitterRetweets() {
			return twitterRetweets;
		}

		public TextView getTwitterLikes() {
			return twitterLikes;
		}

		public Button getTwitterViewTweet() {
			return twitterViewTweet;
		}
	}
}
