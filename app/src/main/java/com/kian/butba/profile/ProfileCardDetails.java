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
import com.kian.butba.database.sqlite.tables.TableAcademicYear;
import com.kian.butba.file.FileOperations;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Kian Mistry on 24/12/16.
 */

public class ProfileCardDetails extends AppCompatActivity {

	private ActionBar toolbar;

	private int bowlerId;
	private int academicYearId;
	private String academicYear;

	private JSONObject jsonObject;
	private JSONArray jsonArray;

	private boolean isDataRetrieved;

	private List<String> stopsList = new ArrayList<>();
	private HashMap<String, String> profileSeasonDetails;
	private HashMap<String, String> profileSeasonAverages = new HashMap<>();
	private HashMap<String, String> profileSeasonPoints = new HashMap<>();

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

		bowlerId = intent.getIntExtra("BOWLER_ID", 0);
		academicYearId = intent.getIntExtra("ACADEMIC_YEAR_ID", 0);
		academicYear = (academicYearId != 0) ? new TableAcademicYear(this).getAcademicYear(academicYearId) : "";

		//Get profile details.
		isDataRetrieved = getProfileDetailsSeason(bowlerId, academicYearId);

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
		if(isDataRetrieved) {
			profileDetailsSeason.setText(academicYear);

			String rankingStatus = profileSeasonDetails.get("ranking_status");
			String studentStatus = profileSeasonDetails.get("student_status");
			String university = profileSeasonDetails.get("university");

			if(studentStatus.equals("Student")) {
				profileDetailsStatus.setText(studentStatus + " // " + rankingStatus + " // " + university);
			}
			else {
				profileDetailsStatus.setText(studentStatus + " // " + rankingStatus);
			}

			profileDetailsAverage.setText("Overall Average: " + profileSeasonDetails.get("average"));
			profileDetailsGames.setText("Total Games: " + profileSeasonDetails.get("games"));

			if(academicYear.equals("2016/17") && studentStatus.equals("Student")) {
				profileDetailsPoints.setText("Total Ranking Points: " + profileSeasonDetails.get("points") + " // " + "Best 4: " + profileSeasonDetails.get("best_n"));
			}
			else {
				profileDetailsPoints.setText("Total Ranking Points: " + profileSeasonDetails.get("points") + " // " + "Best 5: " + profileSeasonDetails.get("best_n"));
			}

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
				average.setText(profileSeasonAverages.get(stop));
				points.setText(profileSeasonPoints.get(stop));

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

	private boolean getProfileDetailsSeason(int bowlerId, int academicYearId) {
		String result;

		if(bowlerId != 0 && academicYearId != 0) {
			try {
				result = FileOperations.readFile(
						getFilesDir() + FileOperations.INTERNAL_SERVER_DIR,
						FileOperations.BOWLERS_SEASON_STATS_FILE + "_" + bowlerId,
						".json"
				);

				jsonObject = new JSONObject(result);
				jsonArray = jsonObject.getJSONArray("Seasons");

				for(int i = 0; i < jsonArray.length(); i++) {
					JSONObject season = jsonArray.getJSONObject(i);

					if(!season.isNull("AcademicYear")) {
						if(academicYearId == season.getInt("AcademicYear")) {
							String rankingStatus = season.getString("Ranking");
							String studentStatus = season.getString("Student");
							String university = season.getString("University");

							String average = season.getString("OverallAverage");
							String games = season.getString("TotalGames");
							String totalPoints = season.getString("TotalPoints");
							String bestN = season.getString("BestN");

							//Add to hash map.
							profileSeasonDetails = new HashMap<>();
							profileSeasonDetails.put("ranking_status", rankingStatus);
							profileSeasonDetails.put("student_status", studentStatus);
							profileSeasonDetails.put("university", university);
							profileSeasonDetails.put("average", average);
							profileSeasonDetails.put("games", games);
							profileSeasonDetails.put("points", totalPoints);
							profileSeasonDetails.put("best_n", bestN);

							JSONArray stops = season.getJSONArray("Stops");
							for(int j = 0; j < stops.length(); j++) {
								String stopName = stops.getString(j);
								stopsList.add(stopName);
								JSONObject averages = season.getJSONObject("Averages");
								JSONArray points = season.getJSONArray("RankingPoints");

								profileSeasonAverages.put(stopName, averages.getString(stopName));
								profileSeasonPoints.put(stopName, points.get(j).toString());
							}
						}
					}
				}

				return true;
			}
			catch(IOException | JSONException e) {
				e.printStackTrace();
				return false;
			}
		}

		return false;
	}
}
