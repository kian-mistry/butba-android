package com.kian.butba.events;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kian.butba.R;
import com.kian.butba.file.FileDownloader;
import com.kian.butba.file.FileOperations;
import com.kian.butba.permissions.PermissionConstants;
import com.kian.butba.permissions.RequestPermissionsAdapterFragment;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Kian Mistry on 04/12/16.
 */

public class EventsFragment extends RequestPermissionsAdapterFragment<Event, EventHolder> implements OnMenuItemClickListener {

    private ActionBar toolbar;
    private PopupMenu popupMenu;

	private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
	private FirebaseRecyclerAdapter<Event, EventHolder> adapter;

	private SharedPreferences prefShownEvents;
	private boolean studentEvents;
	private boolean exStudentEvents;
	private boolean btbaEvents;

	public EventsFragment() {
        //Required: Empty public constructor.
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Obtain shared preferences for the filter.
		prefShownEvents = getActivity().getSharedPreferences("events_shown", Context.MODE_PRIVATE);
		studentEvents = prefShownEvents.getBoolean("student_events", true);
		exStudentEvents = prefShownEvents.getBoolean("ex_student_events", true);
		btbaEvents = prefShownEvents.getBoolean("btba_events", true);

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
        studentEvents = prefShownEvents.getBoolean("student_events", true);
        exStudentEvents = prefShownEvents.getBoolean("ex_student_events", true);
        btbaEvents = prefShownEvents.getBoolean("btba_events", true);

        switch(id) {
            case R.id.toolbar_events_action_filter:
                //Add popup menu.
                View menuActionFilter = getActivity().findViewById(R.id.toolbar_events_action_filter);
                popupMenu = new PopupMenu(getActivity(), menuActionFilter);
                popupMenu.inflate(R.menu.menu_events);

                //Attach item click listener.
                popupMenu.setOnMenuItemClickListener(this);

                //Initialise the checked values of the menu items.
                popupMenu.getMenu().getItem(0).setChecked(studentEvents);
                popupMenu.getMenu().getItem(1).setChecked(exStudentEvents);
                popupMenu.getMenu().getItem(2).setChecked(btbaEvents);
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
		setupAdapter();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		adapter.cleanup();
	}

	/**
	 * Creates a recycler adapter and will display a list of events according to the content of the
	 * Firebase database.
	 */
	private void setupAdapter() {
		adapter = new FirebaseRecyclerAdapter<Event, EventHolder>(
				Event.class,
				R.layout.card_event,
				EventHolder.class,
				databaseReference
		) {
			@Override
			protected void populateViewHolder(final EventHolder viewHolder, final Event model, final int position) {
				//Set name of event.
				String eventName = model.getName();
				viewHolder.getEventName().setText(eventName);

				//Convert date into parts.
				String date = model.getDate();
				String[] dateParts = convertDateAndSplit(date, "dd-MM-yyyy", "EEE dd MMM yyyy");

				if(dateParts != null) {
					viewHolder.getEventDay().setText(dateParts[0]);
					viewHolder.getEventDayOfMonth().setText(dateParts[1]);
					viewHolder.getEventMonth().setText(dateParts[2]);
					viewHolder.getEventYear().setText(dateParts[3]);
				}

				viewHolder.getEventVenue().setText(model.getVenue());

				//Handles the downloading and displaying of the entry form, when available.
				String entryForm = model.getEntryForm();
				if(entryForm != null) {
					viewHolder.getEventEntryForm().setVisibility(View.VISIBLE);
					viewHolder.getEventEntryForm().setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							requestPermission(v, model, viewHolder, Manifest.permission.WRITE_EXTERNAL_STORAGE, PermissionConstants.REQUEST_CODE_RESULT_EXTERNAL_STORAGE);
						}
					});
				}
				else {
					viewHolder.getEventEntryForm().setVisibility(View.GONE);
				}

				//Handles opening the item in Facebook (or other applications if the user does not have Facebook).
				final String facebookEvent = model.getFacebookEvent();
				if(facebookEvent != null) {
					viewHolder.getEventFacebook().setVisibility(View.VISIBLE);
					viewHolder.getEventFacebook().setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							//Open in Facebook app or other recommended apps.
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setData(Uri.parse(facebookEvent));
							startActivity(intent);
						}
					});
				}
				else {
					viewHolder.getEventFacebook().setVisibility(View.GONE);
				}

				//Handles the downloading and displaying of the entry form, when available.
				String results = model.getResults();
				if(results != null) {
					viewHolder.getEventResults().setVisibility(View.VISIBLE);
					viewHolder.getEventResults().setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							requestPermission(v, model, viewHolder, Manifest.permission.WRITE_EXTERNAL_STORAGE, PermissionConstants.REQUEST_CODE_RESULT_EXTERNAL_STORAGE);
						}
					});
				}
				else {
					viewHolder.getEventResults().setVisibility(View.GONE);
				}

				//Allows a user to request a tournament entry.
				Boolean canEnterTournament = (model.getCanEnter() != null) ? model.getCanEnter() : false;
				int eightDpInPx = dpToPx(8);
				int sixteenDpInPx = dpToPx(16);

				if(canEnterTournament) {
					viewHolder.getEventCardLayout().setPadding(eightDpInPx, sixteenDpInPx, eightDpInPx, 0);

					viewHolder.getEventDivider().setVisibility(View.VISIBLE);
					viewHolder.getEventRequestTournamentEntry().setVisibility(View.VISIBLE);
				}
				else {
					viewHolder.getEventCardLayout().setPadding(eightDpInPx, sixteenDpInPx, eightDpInPx, sixteenDpInPx);
					viewHolder.getEventDivider().setVisibility(View.GONE);
					viewHolder.getEventRequestTournamentEntry().setVisibility(View.GONE);
				}
			}
		};

		recyclerView.setAdapter(adapter);
	}

	/**
	 * Converts date into a different format and splits into date components according to the new format.
	 *
	 * @param date The date to be converted.
	 * @param odf The current format of the date.
	 * @param ndf The new format of the date.
	 *
	 * @return The components of the date.
	 */
	private String[] convertDateAndSplit(String date, String odf, String ndf) {
		String[] dateParts = null;
		SimpleDateFormat oldDateFormat = new SimpleDateFormat(odf, Locale.UK);
		SimpleDateFormat newDateFormat = new SimpleDateFormat(ndf, Locale.UK);

		try {
			if(date != null) {
				Date parsedDate = oldDateFormat.parse(date);
				String formattedDate = newDateFormat.format(parsedDate);

				dateParts = formattedDate.split(" ");
			}
		}
		catch(ParseException e) {
			e.printStackTrace();
			return null;
		}

		return dateParts;
	}

	private boolean toggleCheckBox(MenuItem item) {
		if(item.isChecked()) {
			item.setChecked(false);
			return false;
		}
		else {
			item.setChecked(true);
			return true;
		}
	}

	/**
	 * Converts density-independent pixel value to screen pixel value.
	 *
	 * @param dp The value to convert into screen pixels.
	 * @return The screen pixel value.
	 */
	private int dpToPx(int dp) {
		float scale = getResources().getDisplayMetrics().density;

		return (int) (dp * scale + 0.5f);
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		int id = item.getItemId();
		boolean status = toggleCheckBox(item);

		Editor editor = prefShownEvents.edit();

		switch(id) {
			case R.id.cb_students:
				editor.putBoolean("student_events", status).apply();
				studentEvents = status;
				break;
			case R.id.cb_ex_students:
				editor.putBoolean("ex_student_events", status).apply();
				exStudentEvents = status;
				break;
			case R.id.cb_btba:
				editor.putBoolean("btba_events", status).apply();
				btbaEvents = status;
				break;
			default:
				break;
		}

//		getEventDetails(studentEvents, exStudentEvents, btbaEvents);

		//Display the list of events.
//		cardsAdapter.setList(events);
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
