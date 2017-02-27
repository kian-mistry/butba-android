package com.kian.butba;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.google.firebase.database.FirebaseDatabase;
import com.kian.butba.social.SocialConstants;

/**
 * Created by Kian Mistry on 16/01/17.
 */

public class BUTBA extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		//Enable Firebase database offline capabilities.
		FirebaseDatabase.getInstance().setPersistenceEnabled(true);

		//Set Facebook application ID.
		FacebookSdk.setApplicationId(SocialConstants.FACEBOOK_APP_ID);
	}
}
