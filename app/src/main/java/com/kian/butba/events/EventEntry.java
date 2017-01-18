package com.kian.butba.events;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.kian.butba.R;

/**
 * Created by Kian Mistry on 17/01/17.
 */

public class EventEntry extends AppCompatActivity {

	private ActionBar toolbar;

	private String eventName;
	private int eventTeamSize;

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
		Toast.makeText(this, eventName + " " + eventTeamSize, Toast.LENGTH_SHORT).show();
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
