package com.kian.butba.profile;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.kian.butba.R;
import com.kian.butba.database.sqlite.tables.TableAcademicYear;

/**
 * Created by Kian Mistry on 24/12/16.
 */

public class ProfileCardDetails extends AppCompatActivity {

	private ActionBar toolbar;

	private int bowlerId;
	private int academicYearId;
	private String academicYear;

	private TextView profileDetailsSeason;

	public ProfileCardDetails() {
		//Required: Empty public constructor.
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_profile_details);

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Transition cardCollapseTransition = TransitionInflater.from(this).inflateTransition(R.transition.transition_card_collapse);

			//Set transitions for entering to this activity and returning to the previous activity.
			//TODO: Doesn't work correctly.
			getWindow().setEnterTransition(cardCollapseTransition);
			getWindow().setReturnTransition(cardCollapseTransition);
		}

		//Obtain toolbar.
		setSupportActionBar((Toolbar) findViewById(R.id.profile_details_app_bar));
		toolbar = getSupportActionBar();
		toolbar.setDisplayHomeAsUpEnabled(true);
		toolbar.invalidateOptionsMenu();
		toolbar.setTitle("Profile Details");

		//Get extras from intent.
		Intent intent = getIntent();

		bowlerId = intent.getIntExtra("BOWLER_ID", 0);
		academicYearId = intent.getIntExtra("ACADEMIC_YEAR_ID", 0);
		academicYear = (academicYearId != 0) ? new TableAcademicYear(this).getAcademicYear(academicYearId) : "";

		profileDetailsSeason = (TextView) findViewById(R.id.profile_details_season);
		profileDetailsSeason.setText(academicYear);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		switch(id) {
			case android.R.id.home:
				onBackPressed();
				break;
			default:
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			supportFinishAfterTransition();
		}
		else {
			super.onBackPressed();
		}
	}
}
