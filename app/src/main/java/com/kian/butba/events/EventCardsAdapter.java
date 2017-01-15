package com.kian.butba.events;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.google.firebase.database.Query;
import com.kian.butba.R;
import com.kian.butba.permissions.PermissionConstants;
import com.kian.butba.permissions.RequestPermissionsAdapterFragment;
import com.kian.butba.views.FirebaseRecyclerAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Kian Mistry on 14/01/17.
 */

public class EventCardsAdapter extends FirebaseRecyclerAdapter<Event, EventHolder> {

	private RequestPermissionsAdapterFragment fragment;
	private Context context;
	private List<Event> eventsList = Collections.emptyList();

	public EventCardsAdapter(RequestPermissionsAdapterFragment fragment, Query query, Class<Event> itemClass) {
		super(query, itemClass);

		this.fragment = fragment;
		this.context = fragment.getContext();
		this.eventsList = new ArrayList<>();
	}

	@Override
	public EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(context).inflate(R.layout.card_event, parent, false);
		EventHolder holder = new EventHolder(view);

		return holder;
	}

	@Override
	public void onBindViewHolder(final EventHolder holder, int position) {
		final Event current = getItem(position);

		//Set name of event.
		String eventName = current.getName();
		holder.getEventName().setText(eventName);

		//Convert date into parts.
		String date = current.getDate();
		String[] dateParts = convertDateAndSplit(date, "dd-MM-yyyy", "EEE dd MMM yyyy");

		if(dateParts != null) {
			holder.getEventDay().setText(dateParts[0]);
			holder.getEventDayOfMonth().setText(dateParts[1]);
			holder.getEventMonth().setText(dateParts[2]);
			holder.getEventYear().setText(dateParts[3]);
		}

		holder.getEventVenue().setText(current.getVenue());

		//Handles the downloading and displaying of the entry form, when available.
		String entryForm = current.getEntryForm();
		if(entryForm != null) {
			holder.getEventEntryForm().setVisibility(View.VISIBLE);
			holder.getEventEntryForm().setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					fragment.requestPermission(v, current, holder, Manifest.permission.WRITE_EXTERNAL_STORAGE, PermissionConstants.REQUEST_CODE_RESULT_EXTERNAL_STORAGE);
				}
			});
		}
		else {
			holder.getEventEntryForm().setVisibility(View.GONE);
		}

		//Handles opening the item in Facebook (or other applications if the user does not have Facebook).
		final String facebookEvent = current.getFacebookEvent();
		if(facebookEvent != null) {
			holder.getEventFacebook().setVisibility(View.VISIBLE);
			holder.getEventFacebook().setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//Open in Facebook app or other recommended apps.
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(facebookEvent));
					fragment.startActivity(intent);
				}
			});
		}
		else {
			holder.getEventFacebook().setVisibility(View.GONE);
		}

		//Handles the downloading and displaying of the entry form, when available.
		String results = current.getResults();
		if(results != null) {
			holder.getEventResults().setVisibility(View.VISIBLE);
			holder.getEventResults().setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					fragment.requestPermission(v, current, holder, Manifest.permission.WRITE_EXTERNAL_STORAGE, PermissionConstants.REQUEST_CODE_RESULT_EXTERNAL_STORAGE);
				}
			});
		}
		else {
			holder.getEventResults().setVisibility(View.GONE);
		}

		//Allows a user to request a tournament entry.
		Boolean canEnterTournament = (current.getCanEnter() != null) ? current.getCanEnter() : false;
		int eightDpInPx = dpToPx(8);
		int sixteenDpInPx = dpToPx(16);

		if(canEnterTournament) {
			holder.getEventCardLayout().setPadding(eightDpInPx, sixteenDpInPx, eightDpInPx, 0);
			holder.getEventDivider().setVisibility(View.VISIBLE);
			holder.getEventRequestTournamentEntry().setVisibility(View.VISIBLE);
		}
		else {
			holder.getEventCardLayout().setPadding(eightDpInPx, sixteenDpInPx, eightDpInPx, sixteenDpInPx);
			holder.getEventDivider().setVisibility(View.GONE);
			holder.getEventRequestTournamentEntry().setVisibility(View.GONE);
		}
	}

	@Override
	protected void itemAdded(Event item, String key, int position) {
		Log.d("EventsAdapter", "Item Added: " + item.getName());

		eventsList.add(position, item);
	}

	@Override
	protected void itemChanged(Event oldItem, Event newItem, String key, int position) {
		Log.d("EventsAdapter", "Item Changed: " + oldItem.getName() + " -> " + newItem.getName());

		eventsList.set(position, newItem);
	}

	@Override
	protected void itemRemoved(Event item, String key, int position) {
		Log.d("EventsAdapter", "Item Removed: " + item.getName());

		eventsList.remove(item);
	}

	@Override
	protected void itemMoved(Event item, String key, int oldPosition, int newPosition) {
		Log.d("EventsAdapter", "Item Moved: " + oldPosition + " -> " + newPosition + " ( " + item.getName() + ")");
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

	/**
	 * Converts density-independent pixel value to screen pixel value.
	 *
	 * @param dp The value to convert into screen pixels.
	 * @return The screen pixel value.
	 */
	private int dpToPx(int dp) {
		float scale = fragment.getResources().getDisplayMetrics().density;

		return (int) (dp * scale + 0.5f);
	}
}
