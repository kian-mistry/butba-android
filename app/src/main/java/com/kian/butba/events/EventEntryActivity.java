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
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.kian.butba.R;
import com.kian.butba.entities.Entrant;
import com.kian.butba.views.DelayAutoCompleteTextView;

import java.util.HashMap;

/**
 * Created by Kian Mistry on 17/01/17.
 */

public class EventEntryActivity extends AppCompatActivity implements OnCheckedChangeListener, OnItemSelectedListener {

	private static String ENTRIES_EMAIL = "entries@butba.co.uk";
	private static int AUTOCOMPLETE_THRESHOLD = 1;

	private ActionBar toolbar;

	private String eventName;
	private int eventTeamSize;
	private int numberOfTeams = 1;
	private boolean showEntrants = true;

	private TextView tvEventName;
	private TextInputEditText etTeamName;
	private AppCompatSpinner spNumberOfTeams;
	private CheckBox cbEnterBowlers;
	private GridLayout glEntrants;
	private RadioGroup rgPaymentMethods;
	private TextInputEditText etMoreInfo;
	
	private EntrantAutoCompleteAdapter autoCompleteAdapter = new EntrantAutoCompleteAdapter(this);

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
		
		cbEnterBowlers = (CheckBox) findViewById(R.id.event_entry_enter_bowlers);
		cbEnterBowlers.setOnCheckedChangeListener(this);

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
				obtainTextFieldEntries(eventTeamSize, numberOfTeams, showEntrants);
				submitEntry(eventTeamSize, numberOfTeams, showEntrants);
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
	 * @param showEntrants A flag to show whether to display of entrants on the entry form.
	 */
	private void setupEntrantsView(int teamSize, int numberOfTeams, boolean showEntrants) {
		//Remove all child views from the grid layout, before redrawing the views.
		glEntrants.removeAllViews();
		
		if(teamSize > 0 && showEntrants) {
			
			for(int i = 0; i < numberOfTeams; i++) {
				for(int j = 1; j <= teamSize; j++) {
					//Create a linear layout for each row of the grid layout.
					LinearLayout linearLayout = new LinearLayout(this);
					linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
					linearLayout.setOrientation(LinearLayout.HORIZONTAL);
					
					//Create text input layout.
					TextInputLayout textInputLayoutBowler = new TextInputLayout(this);
					textInputLayoutBowler.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 2));
					
					//Create autocomplete text view for bowler's name.
					final DelayAutoCompleteTextView textViewBowler = new DelayAutoCompleteTextView(this);
					textViewBowler.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
					textViewBowler.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
					textViewBowler.setHint("Bowler " + ((i * teamSize) + j));
					textViewBowler.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_ACTION_SEARCH);
					textViewBowler.setThreshold(AUTOCOMPLETE_THRESHOLD);
					textViewBowler.setAdapter(autoCompleteAdapter);
					
					//Add autocomplete text view to text input layout.
					textInputLayoutBowler.addView(textViewBowler);
					
					//Create progress bar for the autocomplete text view.
					ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleSmall);
					LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
					progressBar.setLayoutParams(layoutParams);
					progressBar.setVisibility(View.GONE);
					
					textViewBowler.setProgressBar(progressBar);
					
					//Add text input layout and progress bar to text view container.
					linearLayout.addView(textInputLayoutBowler);
					linearLayout.addView(progressBar);
					
					//Create text input layout.
					TextInputLayout textInputLayoutBowlerAverage = new TextInputLayout(this);
					textInputLayoutBowlerAverage.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
					
					//Create edit text for bowler's average.
					final TextInputEditText editTextBowlerAverage = new TextInputEditText(this);
					editTextBowlerAverage.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
					editTextBowlerAverage.setInputType(InputType.TYPE_CLASS_NUMBER);
					editTextBowlerAverage.setHint("Average");
					
					textViewBowler.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							Entrant entrant = (Entrant) parent.getItemAtPosition(position);
							textViewBowler.setText(entrant.getName());
							editTextBowlerAverage.setText(String.valueOf(entrant.getLatestQualifiedAverage()));
						}
					});
					
					textViewBowler.addTextChangedListener(new TextWatcher() {
						@Override
						public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
						
						@Override
						public void onTextChanged(CharSequence s, int start, int before, int count) {
							editTextBowlerAverage.setText("");
						}
						
						@Override
						public void afterTextChanged(Editable s) {}
					});
					
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
	 * @param showEntrants A flag to show whether to display of entrants on the entry form.
	 */
	private void obtainTextFieldEntries(int teamSize, int numberOfTeams, boolean showEntrants) {
		if(showEntrants) {
			bowlersNames = new HashMap<>();
			bowlersAverages = new HashMap<>();
			
			for(int i = 0; i < numberOfTeams; i++) {
				for(int j = 1; j <= teamSize; j++) {
					LinearLayout linearLayout = (LinearLayout) glEntrants.getChildAt((i * teamSize) + (j - 1));
					
					//Bowler's Name
					TextInputLayout textInputLayoutBowler = (TextInputLayout) linearLayout.getChildAt(0);
					FrameLayout frameLayout = (FrameLayout) textInputLayoutBowler.getChildAt(0);
					DelayAutoCompleteTextView textViewBowler = (DelayAutoCompleteTextView) frameLayout.getChildAt(0);
					String name = (!textViewBowler.getText().toString().equals("")) ? textViewBowler.getText().toString() : null;
					bowlersNames.put("bowler_" + ((i * teamSize) + j), name);
					
					//Bowler's Average
					TextInputLayout textInputLayoutBowlerAverage = (TextInputLayout) linearLayout.getChildAt(2);
					frameLayout = (FrameLayout) textInputLayoutBowlerAverage.getChildAt(0);
					TextInputEditText editTextBowlerAverage = (TextInputEditText) frameLayout.getChildAt(0);
					Integer average = (!editTextBowlerAverage.getText().toString().equals("")) ? Integer.parseInt(editTextBowlerAverage.getText().toString()) : null;
					
					bowlersAverages.put("bowler_" + ((i * teamSize) + j), average);
				}
			}
		}
	}

	/**
	 * Packages the data obtained from the text fields to send to an email intent.
	 *
	 * @param teamSize The size of each team for the event.
	 * @param numberOfTeams The number of teams the user wants to enter.
	 * @param showEntrants A flag to show whether to display of entrants on the entry form.
	 */
	private void submitEntry(int teamSize, int numberOfTeams, boolean showEntrants) {
		//Construct body of message.
		StringBuilder stringBuilder = new StringBuilder();

		//Team Name.
		String teamName = etTeamName.getText().toString();
		if(!teamName.equals("")) {
			stringBuilder.append("Club/Team Name: " + teamName + "\n\n");
		}

		if(showEntrants) {
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
		}
		else {
			//Number of teams.
			stringBuilder.append("Number of Teams: " + numberOfTeams + " \n");
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
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		showEntrants = isChecked;
		setupEntrantsView(eventTeamSize, numberOfTeams, showEntrants);
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		numberOfTeams = Integer.parseInt(parent.getItemAtPosition(position).toString());
		setupEntrantsView(eventTeamSize, numberOfTeams, showEntrants);
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> parent) {}
}