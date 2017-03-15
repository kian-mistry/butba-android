package com.kian.butba.profile;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import com.kian.butba.R;
import com.kian.butba.entities.AcademicYear;
import com.kian.butba.entities.BowlerSeasonStats;
import com.kian.butba.entities.RankingStatus;
import com.kian.butba.entities.StudentStatus;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Kian Mistry on 24/12/16.
 */

public class ProfileDetailsActivity extends AppCompatActivity {

	private ActionBar toolbar;

	private TextView profileDetailsSeason;
	private TextView profileDetailsStatus;
	private TextView profileDetailsAverage;
	private TextView profileDetailsGames;
	private TextView profileDetailsPoints;
	private TableLayout profileDetailsTable;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Transition cardCollapseTransition = TransitionInflater.from(this).inflateTransition(R.transition.transition_card_collapse);

			//Set transitions for entering to this activity and returning to the previous activity.
			//TODO: Doesn't work correctly.
			getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
			getWindow().setEnterTransition(cardCollapseTransition);
			getWindow().setReturnTransition(cardCollapseTransition);
		}

		setContentView(R.layout.activity_profile_details);

		//Obtain toolbar.
		setSupportActionBar((Toolbar) findViewById(R.id.profile_details_app_bar));
		toolbar = getSupportActionBar();
		toolbar.setDisplayHomeAsUpEnabled(true);
		toolbar.invalidateOptionsMenu();
		toolbar.setTitle("Profile Details");

		//Get extras from intent.
		Intent intent = getIntent();
		BowlerSeasonStats seasonStats = (BowlerSeasonStats) intent.getSerializableExtra("SEASON_STATS");

		//Initialise the views on the layout.
		profileDetailsSeason = (TextView) findViewById(R.id.profile_details_season);
		profileDetailsStatus = (TextView) findViewById(R.id.profile_details_status);
		profileDetailsAverage = (TextView) findViewById(R.id.profile_details_average);
		profileDetailsGames = (TextView) findViewById(R.id.profile_details_games);
		profileDetailsPoints = (TextView) findViewById(R.id.profile_details_points);

		//Set up table layout.
		profileDetailsTable = (TableLayout) findViewById(R.id.profile_details_table);

		//Set up layout parameters for the children of each table row.
		LayoutParams layoutParamsStops = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 0.75f);
		LayoutParams layoutParamsAP = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f);

		//Populate views with data.
		if(seasonStats != null) {
			int academicYearId = seasonStats.getAcademicYear();
			String academicYear = (academicYearId != 0) ? new AcademicYear(academicYearId).getAcademicYear() : "";

			profileDetailsSeason.setText(academicYear);

			String rankingStatus = new RankingStatus(seasonStats.getRankingStatus()).getRankingStatus();
			String studentStatus = new StudentStatus(seasonStats.getStudentStatus()).getStudentStatus();
			String university = seasonStats.getUniversity();

			if(studentStatus.equals("Student")) {
				profileDetailsStatus.setText(studentStatus + " // " + rankingStatus + " // " + university);
			}
			else {
				profileDetailsStatus.setText(studentStatus + " // " + rankingStatus);
			}

			int overallAverage = seasonStats.getAverage();
			int totalGames = seasonStats.getGames();
			int totalPoints = seasonStats.getPoints();
			int bestN = seasonStats.getBestN();

			profileDetailsAverage.setText("Overall Average: " + overallAverage);
			profileDetailsGames.setText("Total Games: " + totalGames);

			if(academicYear.equals("2016/17") && studentStatus.equals("Student")) {
				profileDetailsPoints.setText("Total Ranking Points: " + totalPoints + " // " + "Best 4: " + bestN);
			}
			else {
				profileDetailsPoints.setText("Total Ranking Points: " + totalPoints + " // " + "Best 5: " + bestN);
			}

			List<String> stopsList = seasonStats.getStopsList();
			HashMap<String, String> tournamentAverages = seasonStats.getTournamentAverages();
			HashMap<String, String> tournamentPoints = seasonStats.getTournamentPoints();

			//Populate table.
			for(String stop : stopsList) {
				TableRow tableRow = new TableRow(this);
				TextView stopName = new TextView(this);
				TextView average = new TextView(this);
				TextView points = new TextView(this);

				//Set layout parameters for each text view.
				stopName.setLayoutParams(layoutParamsStops);
				average.setLayoutParams(layoutParamsAP);
				points.setLayoutParams(layoutParamsAP);

				stopName.setTypeface(null, Typeface.BOLD);
				stopName.setTextSize(14);
				average.setTextSize(14);
				points.setTextSize(14);

				stopName.setGravity(Gravity.CENTER);
				average.setGravity(Gravity.CENTER);
				points.setGravity(Gravity.CENTER);

				stopName.setPadding(4, 4, 4, 4);
				average.setPadding(4, 4, 4, 4);
				points.setPadding(4, 4, 4, 4);


				//Populate the text views.
				stopName.setText(stop);
				average.setText(tournamentAverages.get(stop));
				points.setText(tournamentPoints.get(stop));

				//Add the text views to the table row.
				tableRow.addView(stopName);
				tableRow.addView(average);
				tableRow.addView(points);

				//Add the table row to the table layout.
				profileDetailsTable.addView(tableRow);
			}
		}
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