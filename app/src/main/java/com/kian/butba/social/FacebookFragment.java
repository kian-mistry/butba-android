package com.kian.butba.social;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kian.butba.R;

/**
 * Created by Kian Mistry on 16/12/16.
 */

public class FacebookFragment extends Fragment {

	public FacebookFragment() {
		//Required: Empty public constructor.
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_social_facebook, container, false);

		return layout;
	}
}