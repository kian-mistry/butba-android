package com.kian.butba.social;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kian.butba.R;
import com.kian.butba.file.FileOperations;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;

/**
 * Created by Kian Mistry on 16/12/16.
 */

public class TwitterFragment extends Fragment implements OnRefreshListener {

	private static final String TWITTER_HANDLE = "ukunibowling";

	private String result;
	private JSONObject jsonObject;
	private JSONArray jsonArray;

	private ArrayList<HashMap<String, String>> tweets;

	private SwipeRefreshLayout swipeRefreshLayout;
	private RecyclerView recyclerView;
	private TwitterCardsAdapter cardsAdapter;

	//Twitter
	private TwitterApiClient twitterApiClient;
	private StatusesService statusesService;

	public TwitterFragment() {
		//Required: Empty public constructor.
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Configure Twitter.
		TwitterAuthConfig authConfig = new TwitterAuthConfig(SocialConstants.TWITTER_KEY, SocialConstants.TWITTER_SECRET);
		Fabric.with(getActivity(), new Twitter(authConfig));

		//Get API Client.
		twitterApiClient = TwitterCore.getInstance().getApiClient();
		statusesService = twitterApiClient.getStatusesService();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_social_twitter, container, false);

		//Initialise the swipe refresh layout and the recycler view.
		swipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.twitter_swipe_refresh);
		swipeRefreshLayout.setOnRefreshListener(this);

		recyclerView = (RecyclerView) layout.findViewById(R.id.twitter_cards_container);
		return layout;
	}

	@Override
	public void onStart() {
		super.onStart();

		if(!FileOperations.fileExists(getContext().getFilesDir() + FileOperations.INTERNAL_SERVER_DIR, FileOperations.TWITTER_RESPONSE, ".json")) {
			if(FileOperations.hasInternetConnection(getContext())) {
				getTweetsOnline(TWITTER_HANDLE, 10, true);
			}
			else {
				Snackbar.make(getView(), "No internet connection", Snackbar.LENGTH_SHORT).show();
			}
		}
		else {
			getTweetsList();

			cardsAdapter = new TwitterCardsAdapter(getActivity(), getTweets());
			recyclerView.setAdapter(cardsAdapter);
			recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		}

	}

	/**
	 * Retrieve tweets from the Fabric API.
	 *
	 * @param twitterHandle The twitter handle of the account
	 * @param tweetsToRetrieve How many tweets to retrieve. Maximum 20.
	 * @param includeRetweets Whether the number of tweets retrieved includes retweets.
	 */
	private void getTweetsOnline(String twitterHandle, int tweetsToRetrieve, boolean includeRetweets) {
		if(tweetsToRetrieve < 0 || tweetsToRetrieve > 20) {
			tweetsToRetrieve = 20;
		}

		Call<List<Tweet>> listCall = statusesService.userTimeline(null, twitterHandle, tweetsToRetrieve, null, null, null, null, null, includeRetweets);
		listCall.enqueue(new Callback<List<Tweet>>() {
			@Override
			public void success(Result<List<Tweet>> result) {
				try {
					//Convert into JSON format.
					JSONArray data = new JSONArray();
					for(int i = 0; i < result.data.size(); i++) {
						JSONObject tweetData = new JSONObject();
						Tweet tweet = result.data.get(i);

						boolean isRetweeted = (tweet.retweetedStatus != null);
						tweetData.put("retweeted_status", String.valueOf(isRetweeted));

						if(!isRetweeted) {
							tweetData.put("id", tweet.idStr);
							tweetData.put("profile_picture", tweet.user.profileImageUrlHttps);
							tweetData.put("name", tweet.user.name);
							tweetData.put("handle", tweet.user.screenName);
							tweetData.put("message", tweet.text);
						}
						else {
							tweetData.put("id", tweet.retweetedStatus.idStr);
							tweetData.put("profile_picture", tweet.retweetedStatus.user.profileImageUrlHttps);
							tweetData.put("name", tweet.retweetedStatus.user.name);
							tweetData.put("handle", tweet.retweetedStatus.user.screenName);
							tweetData.put("message", tweet.retweetedStatus.text);
						}

						data.put(tweetData);
					}

					JSONObject jsonObject = new JSONObject();
					jsonObject.put("data", data);

					FileOperations.writeFile(
							getContext().getFilesDir() + FileOperations.INTERNAL_SERVER_DIR,
							FileOperations.TWITTER_RESPONSE,
							".json",
							jsonObject.toString()
					);

					//Display cards.
					getTweetsList();

					cardsAdapter = new TwitterCardsAdapter(getActivity(), getTweets());
					recyclerView.setAdapter(cardsAdapter);
					recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

					if(swipeRefreshLayout.isRefreshing()) {
						swipeRefreshLayout.setRefreshing(false);
					}
				}
				catch(IOException | JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void failure(TwitterException e) {
				e.printStackTrace();
			}
		});
	}

	public ArrayList<HashMap<String, String>> getTweets() {
		return tweets;
	}

	private void getTweetsList() {
		try {
			result = FileOperations.readFile(
				getContext().getFilesDir() + FileOperations.INTERNAL_SERVER_DIR,
				FileOperations.TWITTER_RESPONSE,
				".json"
			);

			jsonObject = new JSONObject(result);
			jsonArray = jsonObject.getJSONArray("data");

			tweets = new ArrayList<>();

			if(jsonArray != null) {
				HashMap<String, String> tweetDetails;

				for(int i = 0; i < jsonArray.length(); i++) {
					JSONObject tweet = jsonArray.getJSONObject(i);

					String profilePictureUrl = tweet.getString("profile_picture");
					profilePictureUrl = profilePictureUrl.replace("_normal", "");

					String retweetedStatus = tweet.getString("retweeted_status");
					String id = tweet.getString("id");
					String name = tweet.getString("name");
					String handle = tweet.getString("handle");
					String message = tweet.getString("message");

					tweetDetails = new HashMap<>();
					tweetDetails.put("twitter_profile_picture", profilePictureUrl);
					tweetDetails.put("twitter_retweeted_status", retweetedStatus);
					tweetDetails.put("twitter_id", id);
					tweetDetails.put("twitter_name", name);
					tweetDetails.put("twitter_handle", handle);
					tweetDetails.put("twitter_message", message);

					tweets.add(tweetDetails);
				}
			}
		}
		catch(IOException | JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onRefresh() {
		if(FileOperations.hasInternetConnection(getContext())) {
			getTweetsOnline(TWITTER_HANDLE, 10, true);
		}
		else {
			swipeRefreshLayout.setRefreshing(false);
		}
	}
}
