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
import android.widget.TextView;

import com.kian.butba.R;

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

		holder.getTwitterName().setText(current.get("twitter_name"));
		holder.getTwitterHandle().setText("@" + current.get("twitter_handle"));
		holder.getTwitterMessage().setText(current.get("twitter_message"));

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

//		private CircularImageView twitterProfilePicture;
		private TextView twitterName;
		private TextView twitterHandle;
		private TextView twitterMessage;

		private Button twitterViewTweet;


		public TwitterHolder(View itemView) {
			super(itemView);

			twitterName = (TextView) itemView.findViewById(R.id.twitter_name);
			twitterHandle = (TextView) itemView.findViewById(R.id.twitter_handle);
			twitterMessage = (TextView) itemView.findViewById(R.id.twitter_message);

			twitterViewTweet = (Button) itemView.findViewById(R.id.twitter_view_tweet);
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

		public Button getTwitterViewTweet() {
			return twitterViewTweet;
		}
	}
}
