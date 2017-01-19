package com.kian.butba.events;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
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

public class EventEntry extends AppCompatActivity {

	public static String ENTRIES_EMAIL = "entries@butba.co.uk";

	private ActionBar toolbar;

	private String eventName;
	private int eventTeamSize;

	private TextView eventEntryName;
	private TextInputEditText eventEntryTeamName;
	private GridLayout eventEntryAveragesGrid;
	private RadioGroup eventEntryPaymentMethods;
	private TextInputEditText eventEntryMoreInfo;

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
		eventEntryName = (TextView) findViewById(R.id.event_entry_name);
		eventEntryName.setText(eventName);

		eventEntryTeamName = (TextInputEditText) findViewById(R.id.event_entry_team_name);

		eventEntryAveragesGrid = (GridLayout) findViewById(R.id.event_entry_averages_grid);
		setupAveragesView(eventTeamSize);

		eventEntryPaymentMethods = (RadioGroup) findViewById(R.id.event_entry_payment_methods);

		eventEntryMoreInfo = (TextInputEditText) findViewById(R.id.event_entry_more_info);

		//Push activity up when keyboard is present.
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
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
				obtainTextFieldEntries();
				submitEntry();
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
				TextInputEditText editTextBowler = new TextInputEditText(this);
				editTextBowler.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				editTextBowler.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
				editTextBowler.setHint("Bowler " + (i + 1));

				//Add to text input layout.
				textInputLayoutBowler.addView(editTextBowler);

				//Add to linear layout.
				linearLayout.addView(textInputLayoutBowler);

				//Create text input layout.
				TextInputLayout textInputLayoutBowlerAverage = new TextInputLayout(this);
				textInputLayoutBowlerAverage.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));

				//Create edit text for bowler's name.
				TextInputEditText editTextBowlerAverage = new TextInputEditText(this);
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

	/**
	 * Obtain entries from each text field on the form.
	 */
	private void obtainTextFieldEntries() {
		bowlersNames = new HashMap<>();
		bowlersAverages = new HashMap<>();

		for(int i = 0; i < eventTeamSize; i++) {
			LinearLayout linearLayout = (LinearLayout) eventEntryAveragesGrid.getChildAt(i);

			//Bowler's Name
			TextInputLayout textInputLayoutBowler = (TextInputLayout) linearLayout.getChildAt(0);
			FrameLayout frameLayout = (FrameLayout) textInputLayoutBowler.getChildAt(0);
			TextInputEditText editTextBowler = (TextInputEditText) frameLayout.getChildAt(0);
			String name = (!editTextBowler.getText().toString().equals("")) ? editTextBowler.getText().toString() : null;
			bowlersNames.put("bowler_" + (i + 1), name);

			//Bowler's Average
			TextInputLayout textInputLayoutBowlerAverage = (TextInputLayout) linearLayout.getChildAt(1);
			frameLayout = (FrameLayout) textInputLayoutBowlerAverage.getChildAt(0);
			TextInputEditText editTextBowlerAverage = (TextInputEditText) frameLayout.getChildAt(0);
			Integer average = (!editTextBowlerAverage.getText().toString().equals("")) ? Integer.parseInt(editTextBowlerAverage.getText().toString()) : null;

			bowlersAverages.put("bowler_" + (i + 1), average);
		}
	}

	/**
	 * Packages the data obtained from the text fields to send to an email intent.
	 */
	private void submitEntry() {
		//Construct body of message.

		//Team Name.
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Club/Team Name: ");
		stringBuilder.append(eventEntryTeamName.getText().toString() + "\n\n");

		//Bowlers within team.
		stringBuilder.append("Bowlers: \n");
		for(int i = 0; i < eventTeamSize; i++) {
			String name = bowlersNames.get("bowler_" + (i + 1));
			Integer average = bowlersAverages.get("bowler_" + (i + 1));

			if(name != null) {
				stringBuilder.append(name + " (" + average + ") \n");
			}
		}

		//Payment Method.
		int checkedRadioButtonId = eventEntryPaymentMethods.getCheckedRadioButtonId();
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

		stringBuilder.append("More Information: \n");
		stringBuilder.append(eventEntryMoreInfo.getText().toString() + "\n\n");

		//Create a new email intent.
		Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
		StringBuilder uriBuilder = new StringBuilder();
		uriBuilder.append("mailto:" + Uri.encode(ENTRIES_EMAIL));
		uriBuilder.append("?subject=" + Uri.encode("BUTBA: " + eventName + " Entry"));
		uriBuilder.append("&body=" + Uri.encode(stringBuilder.toString()));
		emailIntent.setData(Uri.parse(uriBuilder.toString()));
		startActivity(emailIntent);
	}
}
