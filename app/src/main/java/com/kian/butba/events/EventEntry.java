package com.kian.butba.events;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.kian.butba.R;

import java.util.HashMap;

/**
 * Created by Kian Mistry on 17/01/17.
 */

public class EventEntry extends AppCompatActivity implements OnItemSelectedListener {

	public static String ENTRIES_EMAIL = "entries@butba.co.uk";

	private ActionBar toolbar;

	private String eventName;
	private int eventTeamSize;
	private int numberOfTeams = 1;

	private TextView tvEventName;
	private TextInputEditText etTeamName;
	private AppCompatSpinner spNumberOfTeams;
	private GridLayout glEntrants;
	private RadioGroup rgPaymentMethods;
	private TextInputEditText etMoreInfo;

	private HashMap<String, String> bowlersNames;
	private HashMap<String, Integer> bowlersAverages;

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
		tvEventName = (TextView) findViewById(R.id.event_entry_name);
		tvEventName.setText(eventName);

		etTeamName = (TextInputEditText) findViewById(R.id.event_entry_team_name);
		
		spNumberOfTeams = (AppCompatSpinner) findViewById(R.id.event_entry_number_of_teams);
		ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.number_of_teams, android.R.layout.simple_spinner_item);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spNumberOfTeams.setAdapter(spinnerAdapter);
		spNumberOfTeams.setOnItemSelectedListener(this);

		glEntrants = (GridLayout) findViewById(R.id.event_entry_averages_grid);

		rgPaymentMethods = (RadioGroup) findViewById(R.id.event_entry_payment_methods);

		etMoreInfo = (TextInputEditText) findViewById(R.id.event_entry_more_info);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		//Add custom buttons to the toolbar.
		menu.clear();
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.menu_event_entry, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		switch(id) {
			case android.R.id.home:
				onBackPressed();
				break;
			case R.id.btn_event_entry_submit:
				obtainTextFieldEntries(eventTeamSize, numberOfTeams);
				submitEntry(eventTeamSize, numberOfTeams);
				break;
			default:
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Populate the entry form with fields asking for the bowler's name and average depending on the team size for the event.
	 *
	 * @param teamSize The size of each team for the event.
	 * @param numberOfTeams The number of teams the user wants to enter.
	 */
	private void setupEntrantsView(int teamSize, int numberOfTeams) {
		if(teamSize > 0) {
			
			//Remove all child views from the grid layout, before redrawing the views.
			glEntrants.removeAllViews();
			
			for(int i = 0; i < numberOfTeams; i++) {
				for(int j = 1; j <= teamSize; j++) {
					//Create a linear layout for each row of the grid layout.
					LinearLayout linearLayout = new LinearLayout(this);
					linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
					linearLayout.setOrientation(LinearLayout.HORIZONTAL);
					
					//Create text input layout.
					TextInputLayout textInputLayoutBowler = new TextInputLayout(this);
					textInputLayoutBowler.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 3));
					
					//Create edit text for bowler's name.
					TextInputEditText editTextBowler = new TextInputEditText(this);
					editTextBowler.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
					editTextBowler.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
					editTextBowler.setHint("Bowler " + ((i * teamSize) + j));
					
					//Add to text input layout.
					textInputLayoutBowler.addView(editTextBowler);
					
					//Add to linear layout.
					linearLayout.addView(textInputLayoutBowler);
					
					//Create text input layout.
					TextInputLayout textInputLayoutBowlerAverage = new TextInputLayout(this);
					textInputLayoutBowlerAverage.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
					
					//Create edit text for bowler's average.
					TextInputEditText editTextBowlerAverage = new TextInputEditText(this);
					editTextBowlerAverage.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
					editTextBowlerAverage.setInputType(InputType.TYPE_CLASS_NUMBER);
					editTextBowlerAverage.setHint("Average");
					
					//Add to text input layout.
					textInputLayoutBowlerAverage.addView(editTextBowlerAverage);
					
					//Add to linear layout.
					linearLayout.addView(textInputLayoutBowlerAverage);
					
					//Add linear layout to grid layout.
					glEntrants.addView(linearLayout);
				}
			}
		}
	}

	/**
	 * Obtain entries from each text field on the form.
	 *
	 * @param teamSize The size of each team for the event.
	 * @param numberOfTeams The number of teams the user wants to enter.
	 */
	private void obtainTextFieldEntries(int teamSize, int numberOfTeams) {
		bowlersNames = new HashMap<>();
		bowlersAverages = new HashMap<>();

		for(int i = 0; i < numberOfTeams; i++) {
			for(int j = 1; j <= teamSize; j++) {
				LinearLayout linearLayout = (LinearLayout) glEntrants.getChildAt((i * teamSize) + (j - 1));
				
				//Bowler's Name
				TextInputLayout textInputLayoutBowler = (TextInputLayout) linearLayout.getChildAt(0);
				FrameLayout frameLayout = (FrameLayout) textInputLayoutBowler.getChildAt(0);
				TextInputEditText editTextBowler = (TextInputEditText) frameLayout.getChildAt(0);
				String name = (!editTextBowler.getText().toString().equals("")) ? editTextBowler.getText().toString() : null;
				bowlersNames.put("bowler_" + ((i * teamSize) + j), name);
				
				//Bowler's Average
				TextInputLayout textInputLayoutBowlerAverage = (TextInputLayout) linearLayout.getChildAt(1);
				frameLayout = (FrameLayout) textInputLayoutBowlerAverage.getChildAt(0);
				TextInputEditText editTextBowlerAverage = (TextInputEditText) frameLayout.getChildAt(0);
				Integer average = (!editTextBowlerAverage.getText().toString().equals("")) ? Integer.parseInt(editTextBowlerAverage.getText().toString()) : null;
				
				bowlersAverages.put("bowler_" + ((i * teamSize) + j), average);
			}
		}
	}

	/**
	 * Packages the data obtained from the text fields to send to an email intent.
	 *
	 * @param teamSize The size of each team for the event.
	 * @param numberOfTeams The number of teams the user wants to enter.
	 */
	private void submitEntry(int teamSize, int numberOfTeams) {
		//Construct body of message.

		//Team Name.
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Club/Team Name: ");
		stringBuilder.append(etTeamName.getText().toString() + "\n\n");

		//Bowlers within team.
		stringBuilder.append("Bowlers: \n");
		for(int i = 0; i < numberOfTeams; i++) {
			stringBuilder.append("Team " + (i + 1) + ": \n");
			for(int j = 1; j <= teamSize; j++) {
				String name = bowlersNames.get("bowler_" + ((i * teamSize) + j));
				Integer average = bowlersAverages.get("bowler_" + ((i * teamSize) + j));
				
				if(name != null) {
					if(average != null) {
						stringBuilder.append(name + " (" + average + ") \n");
					} else {
						stringBuilder.append(name + " \n");
					}
				}
			}
		}

		//Payment Method.
		int checkedRadioButtonId = rgPaymentMethods.getCheckedRadioButtonId();
		stringBuilder.append("\nPayment Method: ");

		if(checkedRadioButtonId != -1) {
			//One of the radio buttons are checked.
			RadioButton radioButton = (RadioButton) findViewById(checkedRadioButtonId);
			stringBuilder.append(radioButton.getText().toString() + ". \n\n");
		}
		else {
			//None of the radio buttons are checked.
			stringBuilder.append("Not specified. \n\n");
		}
		
		String moreInfo = etMoreInfo.getText().toString();
		if(!moreInfo.equals("")) {
			stringBuilder.append("More Information: \n");
			stringBuilder.append(etMoreInfo.getText().toString() + "\n\n");
		}

		//Create a new email intent.
		Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
		StringBuilder uriBuilder = new StringBuilder();
		uriBuilder.append("mailto:" + Uri.encode(ENTRIES_EMAIL));
		uriBuilder.append("?subject=" + Uri.encode("BUTBA: " + eventName + " Entry"));
		uriBuilder.append("&body=" + Uri.encode(stringBuilder.toString()));
		emailIntent.setData(Uri.parse(uriBuilder.toString()));
		startActivity(emailIntent);
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		numberOfTeams = Integer.parseInt(parent.getItemAtPosition(position).toString());
		setupEntrantsView(eventTeamSize, numberOfTeams);
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> parent) {}
}