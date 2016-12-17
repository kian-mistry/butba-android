package com.kian.butba.social;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequest.Callback;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.kian.butba.R;
import com.kian.butba.file.AsyncDelegate;
import com.kian.butba.file.FacebookResponseDownloader;
import com.kian.butba.file.FileOperations;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Kian Mistry on 16/12/16.
 */

public class FacebookFragment extends Fragment {

	private String result;
	private JSONObject jsonObject;
	private JSONObject postsObject;
	private JSONObject taggedPostsObject;
	private JSONObject pictureObject;
	private JSONArray jsonArray;

	private ArrayList<HashMap<String, String>> statuses;

	//Facebook
	private AccessToken accessToken;
	private FacebookResponseDownloader responseDownloader;
	private GraphRequest graphRequest;

	private RecyclerView recyclerView;
	private FacebookCardsAdapter cardsAdapter;


	public FacebookFragment() {
		//Required: Empty public constructor.
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Initialise Facebook SDK.
		FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
		AppEventsLogger.activateApp(getActivity().getApplication());

		//Access Token
		accessToken = new AccessToken(
				SocialConstants.FACEBOOK_ACCESS_TOKEN,
				SocialConstants.FACEBOOK_APP_ID,
				SocialConstants.FACEBOOK_USER_ID,
				null, null, null, null, null
		);

		responseDownloader = new FacebookResponseDownloader(getContext(), new AsyncDelegate() {
			@Override
			public void onProcessResults(Boolean success) {
				if(success) {
					getStatusList();

					cardsAdapter = new FacebookCardsAdapter(getActivity(), getStatuses());
					recyclerView.setAdapter(cardsAdapter);
					recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
				}
			}
		});

		graphRequest = GraphRequest.newGraphPathRequest(accessToken, SocialConstants.FACEBOOK_GRAPH_PATH, new Callback() {
			@Override
			public void onCompleted(GraphResponse response) {
				/*
				 * After the JSON Object from the graph response has generated,
				 * save the response to a JSON file so that it can be used when the
				 * device is offline.
				 */
				responseDownloader.execute(response);
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_social_facebook, container, false);

		//Initialise recycler view.
		recyclerView = (RecyclerView) layout.findViewById(R.id.facebook_cards_container);

		return layout;
	}

	@Override
	public void onStart() {
		super.onStart();

		if(!FileOperations.fileExists(getContext().getFilesDir() + FileOperations.INTERNAL_SERVER_DIR, FileOperations.FACEBOOK_RESPONSE, ".json")) {
			GraphRequest.executeBatchAsync(graphRequest);
		}
		else {
			getStatusList();

			cardsAdapter = new FacebookCardsAdapter(getActivity(), getStatuses());
			recyclerView.setAdapter(cardsAdapter);
			recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		AppEventsLogger.activateApp(getActivity().getApplication());
	}

	public ArrayList<HashMap<String, String>> getStatuses() {
		return statuses;
	}

	private void getStatusList() {
		try {
			result = FileOperations.readFile(
					getContext().getFilesDir() + FileOperations.INTERNAL_SERVER_DIR,
					FileOperations.FACEBOOK_RESPONSE,
					".json"
			);

			jsonObject = new JSONObject(result);
			postsObject = jsonObject.getJSONObject("posts");
			taggedPostsObject = jsonObject.getJSONObject("tagged");

			statuses = new ArrayList<>();

			//Get status updates posted by BUTBA.
			jsonArray = postsObject.getJSONArray("data");
			if(jsonArray != null) {
				//Obtain BUTBA's profile picture.
				String profilePictureUrl = jsonObject.getJSONObject("picture").getJSONObject("data").getString("url");

				HashMap<String, String> statusDetails;

				for(int i = 0; i < jsonArray.length(); i++) {
					JSONObject status = jsonArray.getJSONObject(i);

					String id = status.getString("id");
					String story = (status.has("story")) ? status.getString("story") : "British University Tenpin Bowling Association";
					String message = status.getString("message");

					statusDetails = new HashMap<>();
					statusDetails.put("facebook_id", id);
					statusDetails.put("facebook_profile_picture", profilePictureUrl);
					statusDetails.put("facebook_story", story);
					statusDetails.put("facebook_message", message);

					statuses.add(statusDetails);
				}
			}
		}
		catch(IOException | JSONException e) {
			e.printStackTrace();
		}
	}
}
