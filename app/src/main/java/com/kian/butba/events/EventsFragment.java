package com.kian.butba.events;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.kian.butba.R;
import com.kian.butba.file.FileDownloader;
import com.kian.butba.file.FileOperations;
import com.kian.butba.permissions.PermissionConstants;
import com.kian.butba.permissions.RequestPermissionsAdapterFragment;

import java.io.File;

/**
 * Created by Kian Mistry on 04/12/16.
 */

public class EventsFragment extends RequestPermissionsAdapterFragment<Event, EventHolder> implements OnMenuItemClickListener {

    private ActionBar toolbar;
    private PopupMenu popupMenu;

	private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
	private EventCardsAdapter cardsAdapter;

	private SharedPreferences prefShownEvents;
	private boolean btbaEvents;
	private boolean exStudentEvents;
	private boolean studentEvents;

	public EventsFragment() {
        //Required: Empty public constructor.
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Obtain shared preferences for the filter.
		prefShownEvents = getActivity().getSharedPreferences("events_shown", Context.MODE_PRIVATE);
		btbaEvents = prefShownEvents.getBoolean("btba_events", true);
		exStudentEvents = prefShownEvents.getBoolean("ex_student_events", true);
		studentEvents = prefShownEvents.getBoolean("student_events", true);

		//Establish reference to database.
		databaseReference = FirebaseDatabase.getInstance().getReference().child("events").child("16-17");
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_events, container, false);

        //Obtain toolbar.
        toolbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        toolbar.setTitle("Events");
        toolbar.invalidateOptionsMenu();
        setHasOptionsMenu(true);

        //Initialise recycler view.
        recyclerView = (RecyclerView) layout.findViewById(R.id.event_cards_container);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return layout;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        //Add custom buttons to the toolbar.
        menu.clear();
        inflater.inflate(R.menu.toolbar_items_events, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //Obtain which events are shown from the shared preferences.
	    btbaEvents = prefShownEvents.getBoolean("btba_events", true);
	    exStudentEvents = prefShownEvents.getBoolean("ex_student_events", true);
        studentEvents = prefShownEvents.getBoolean("student_events", true);

        switch(id) {
            case R.id.toolbar_events_action_filter:
                //Add popup menu.
                View menuActionFilter = getActivity().findViewById(R.id.toolbar_events_action_filter);
                popupMenu = new PopupMenu(getActivity(), menuActionFilter);
                popupMenu.inflate(R.menu.menu_events);

                //Attach item click listener.
                popupMenu.setOnMenuItemClickListener(this);

                //Initialise the checked values of the menu items.
	            if(btbaEvents && exStudentEvents && studentEvents) {
		            popupMenu.getMenu().getItem(3).setChecked(true);
	            }
	            else if(btbaEvents) {
		            popupMenu.getMenu().getItem(2).setChecked(true);
	            }
	            else if(exStudentEvents) {
		            popupMenu.getMenu().getItem(1).setChecked(true);
	            }
	            else if(studentEvents) {
		            popupMenu.getMenu().getItem(0).setChecked(true);
	            }
                popupMenu.show();

                break;
            default:
                break;
        }

        return true;
    }

	@Override
	public void onStart() {
		super.onStart();
		setupAdapter(btbaEvents, exStudentEvents, studentEvents);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		cardsAdapter.destroy();
	}

	/**
	 * Creates a recycler adapter and will display a list of events according to the content of the
	 * Firebase database.
	 *
	 * @param eventTypes The event types which the user wants to display: BTBA Events; Ex-Student Events; Student Events.
	 */
	private void setupAdapter(Boolean... eventTypes) {
		boolean btbaEvents = eventTypes[0];
		boolean exStudentEvents = eventTypes[1];
		boolean studentEvents = eventTypes[2];

		//Called if, for some reason, neither one of the options are checked.
		if(!btbaEvents && ! exStudentEvents && !studentEvents) {
			Editor editor = prefShownEvents.edit();

			editor.putBoolean("btba_events", true).apply();
			editor.putBoolean("ex_student_events", true).apply();
			editor.putBoolean("student_events", true).apply();

			btbaEvents = true;
			exStudentEvents = true;
			studentEvents = true;
		}


		//Queries the Firebase database to only shown specific events.
		Query filterQuery = null;

		if(btbaEvents && exStudentEvents && studentEvents) {
			filterQuery = databaseReference;
		}
		else if(btbaEvents) {
			filterQuery = databaseReference.orderByChild("tags/btba").equalTo(true);
		}
		else if(exStudentEvents) {
			filterQuery = databaseReference.orderByChild("tags/exStudent").equalTo(true);
		}
		else if(studentEvents) {
			filterQuery = databaseReference.orderByChild("tags/student").equalTo(true);
		}

		cardsAdapter = new EventCardsAdapter(this, filterQuery, Event.class);
		recyclerView.setAdapter(cardsAdapter);
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		int id = item.getItemId();

		Editor editor = prefShownEvents.edit();

		switch(id) {
			case R.id.rb_students:
				editor.putBoolean("btba_events", false).apply();
				editor.putBoolean("ex_student_events", false).apply();
				editor.putBoolean("student_events", true).apply();

				btbaEvents = false;
				exStudentEvents = false;
				studentEvents = true;

				item.setChecked(true);
				break;
			case R.id.rb_ex_students:
				editor.putBoolean("btba_events", false).apply();
				editor.putBoolean("ex_student_events", true).apply();
				editor.putBoolean("student_events", false).apply();

				btbaEvents = false;
				exStudentEvents = true;
				studentEvents = false;

				item.setChecked(true);
				break;
			case R.id.rb_btba:
				editor.putBoolean("btba_events", true).apply();
				editor.putBoolean("ex_student_events", false).apply();
				editor.putBoolean("student_events", false).apply();

				btbaEvents = true;
				exStudentEvents = false;
				studentEvents = false;

				item.setChecked(true);
				break;
			case R.id.rb_all:
				editor.putBoolean("btba_events", true).apply();
				editor.putBoolean("ex_student_events", true).apply();
				editor.putBoolean("student_events", true).apply();

				btbaEvents = true;
				exStudentEvents = true;
				studentEvents = true;

				item.setChecked(true);
				break;
			default:
				break;
		}

		setupAdapter(btbaEvents, exStudentEvents, studentEvents);

		return false;
	}

	@Override
	protected void executeActions(View view, Event model, EventHolder viewHolder) {
		int viewId = view.getId();
		final String name = model.getName();

		if(viewId == viewHolder.getEventEntryForm().getId()) {
			final String entryForm = model.getEntryForm();
			/*
             * If file exists, open file.
             * If not: download file; open file.
             */
			final String fileName = entryForm.substring(entryForm.lastIndexOf("/") + 1);
			File file = new File(FileDownloader.ENTRY_FORMS_DIR, fileName);
			String fileType = "application/pdf";

			if(!file.exists()) {
				if(FileOperations.hasInternetConnection(getContext())) {
					FileDownloader fileDownloader = new FileDownloader(
							getContext(),
							view,
							viewHolder.getAdapterPosition(),
							name + " Entry Form",
							R.mipmap.ic_pdf_circle);
					fileDownloader.execute(entryForm, fileName, FileDownloader.ENTRY_FORMS_DIR, fileType);
				}
				else {
					Snackbar.make(getView(), "No internet connection", Snackbar.LENGTH_SHORT).show();
				}
			}
			else {
				getContext().startActivity(
					FileDownloader.openFileActivity(FileDownloader.ENTRY_FORMS_DIR,
						fileName,
						fileType
					)
				);
			}
		}
		else if(viewId == viewHolder.getEventResults().getId()) {
			final String results = model.getResults();
			/*
             * If file exists, open file.
             * If not: download file; open file.
             */
			final String fileName = results.substring(results.lastIndexOf("/") + 1);
			File file = new File(FileDownloader.RESULTS_DIR, fileName);
			String fileType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

			if(!file.exists()) {
				if(FileOperations.hasInternetConnection(getContext())) {
					FileDownloader fileDownloader = new FileDownloader(
							getContext(),
							view,
							viewHolder.getAdapterPosition(),
							name + " Results",
							R.mipmap.ic_excel_circle);
					fileDownloader.execute(results, fileName, FileDownloader.RESULTS_DIR, fileType);
				}
				else {
					Snackbar.make(getView(), "No internet connection", Snackbar.LENGTH_SHORT).show();
				}
			}
			else {
				getContext().startActivity(
					FileDownloader.openFileActivity(FileDownloader.RESULTS_DIR,
						fileName,
						fileType
					)
				);
			}
		}
	}

	@Override
	protected void executeActionsIfNotGranted() {
		Snackbar.make(getView(), "Cannot download/open file.", Snackbar.LENGTH_SHORT)
			.setAction("Request Access", new OnClickListener() {
				@Override
				public void onClick(View v) {
					requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PermissionConstants.REQUEST_CODE_RESULT_EXTERNAL_STORAGE);
				}
			})
			.show();
	}
}
