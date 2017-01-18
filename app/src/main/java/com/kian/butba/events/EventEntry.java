package com.kian.butba.events;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kian.butba.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Kian Mistry on 17/01/17.
 */

public class EventEntry extends AppCompatActivity {

	private ActionBar toolbar;

	private String eventName;
	private int eventTeamSize;

	private TextView eventEntryName;
	private GridLayout eventEntryAveragesGrid;

	private ArrayList<HashMap<String, String>> bowlersEditTextIds;
	private ArrayList<HashMap<String, Integer>> bowlersAverages;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_entry);

		//Obtain toolbar.
		setSupportActionBar((Toolbar) findViewById(R.id.event_entry_app_bar));
		toolbar = getSupportActionBar();
		toolbar.setDisplayHomeAsUpEnabled(true);
		toolbar.invalidateOptionsMenu();
		toolbar.setTitle("Entry Form");

		//Get extras from intent.
		Intent intent = getIntent();

		eventName = intent.getStringExtra("EVENT_NAME");
		eventTeamSize = intent.getIntExtra("EVENT_TEAM_SIZE", 0);

		//Initialise views.
		eventEntryName = (TextView) findViewById(R.id.event_entry_name);
		eventEntryName.setText(eventName);

		eventEntryAveragesGrid = (GridLayout) findViewById(R.id.event_entry_averages_grid);
		setupAveragesView(eventTeamSize);
	}

	/**
	 * Populate the entry form with fields asking for the bowler's name and average depending on the team size for the event.
	 *
	 * @param teamSize The size of each team for the event.
	 */
	private void setupAveragesView(int teamSize) {
		if(teamSize > 0) {
			for(int i = 0; i < teamSize; i++) {
				//Create a linear layout for each row of the grid layout.
				LinearLayout linearLayout = new LinearLayout(this);
				linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				linearLayout.setOrientation(LinearLayout.HORIZONTAL);

				//Create text input layout.
				TextInputLayout textInputLayoutBowler = new TextInputLayout(this);
				textInputLayoutBowler.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 3));

				//Create edit text for bowler's name.
				//TODO: Find a way to generate unique IDs for views.
				EditText editTextBowler = new EditText(this);
				editTextBowler.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				editTextBowler.setInputType(InputType.TYPE_CLASS_TEXT);
				editTextBowler.setHint("Bowler " + (i + 1));

				//Add to text input layout.
				textInputLayoutBowler.addView(editTextBowler);

				//Add to linear layout.
				linearLayout.addView(textInputLayoutBowler);

				//Create text input layout.
				TextInputLayout textInputLayoutBowlerAverage = new TextInputLayout(this);
				textInputLayoutBowlerAverage.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));

				//Create edit text for bowler's name.
				EditText editTextBowlerAverage = new EditText(this);
				editTextBowlerAverage.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				editTextBowlerAverage.setInputType(InputType.TYPE_CLASS_NUMBER);
				editTextBowlerAverage.setHint("Average");

				//Add to text input layout.
				textInputLayoutBowlerAverage.addView(editTextBowlerAverage);

				//Add to linear layout.
				linearLayout.addView(textInputLayoutBowlerAverage);

				//Add linear layout to grid layout.
				eventEntryAveragesGrid.addView(linearLayout);
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
}
