package com.kian.butba.events;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kian.butba.R;

/**
 * Created by Kian Mistry on 07/01/17.
 */

public class EventHolder extends ViewHolder {

	private RelativeLayout eventCardLayout;

	private TextView eventName;
	private TextView eventDay;
	private TextView eventDayOfMonth;
	private TextView eventMonth;
	private TextView eventYear;
	private TextView eventVenue;

	private ImageButton eventEntryForm;
	private ImageButton eventFacebook;
	private ImageButton eventResults;

	private View eventDivider;
	private Button eventRequestTournamentEntry;

	public EventHolder(View itemView) {
		super(itemView);

		eventCardLayout = (RelativeLayout) itemView.findViewById(R.id.event_card_layout);

		eventName = (TextView) itemView.findViewById(R.id.event_name);
		eventDay = (TextView) itemView.findViewById(R.id.event_day);
		eventDayOfMonth = (TextView) itemView.findViewById(R.id.event_day_of_month);
		eventMonth = (TextView) itemView.findViewById(R.id.event_month);
		eventYear = (TextView) itemView.findViewById(R.id.event_year);
		eventVenue = (TextView) itemView.findViewById(R.id.event_venue);

		eventEntryForm = (ImageButton) itemView.findViewById(R.id.event_entry_form);
		eventFacebook = (ImageButton) itemView.findViewById(R.id.event_facebook);
		eventResults = (ImageButton) itemView.findViewById(R.id.event_results);

		eventDivider = itemView.findViewById(R.id.event_divider);
		eventRequestTournamentEntry = (Button) itemView.findViewById(R.id.event_request_tournament_entry);
	}

	public RelativeLayout getEventCardLayout() {
		return eventCardLayout;
	}

	public TextView getEventName() {
		return eventName;
	}

	public TextView getEventDay() {
		return eventDay;
	}

	public TextView getEventDayOfMonth() {
		return eventDayOfMonth;
	}

	public TextView getEventMonth() {
		return eventMonth;
	}

	public TextView getEventYear() {
		return eventYear;
	}

	public TextView getEventVenue() {
		return eventVenue;
	}

	public ImageButton getEventEntryForm() {
		return eventEntryForm;
	}

	public ImageButton getEventFacebook() {
		return eventFacebook;
	}

	public ImageButton getEventResults() {
		return eventResults;
	}

	public View getEventDivider() {
		return eventDivider;
	}

	public Button getEventRequestTournamentEntry() {
		return eventRequestTournamentEntry;
	}
}
