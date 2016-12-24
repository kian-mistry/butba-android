package com.kian.butba.events;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.kian.butba.R;
import com.kian.butba.events.EventCardsAdapter.EventDetailsHolder;
import com.kian.butba.file.FileDownloader;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Kian Mistry on 06/12/16.
 */

public class EventCardsAdapter extends Adapter<EventDetailsHolder> {

    private LayoutInflater inflater;

    private List<HashMap<String, String>> eventsList = Collections.emptyList();

    public EventCardsAdapter(Context context, List<HashMap<String, String>> eventsList) {
        inflater = LayoutInflater.from(context);
        this.eventsList = eventsList;
    }

    @Override
    public EventDetailsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card_event, parent, false);
        EventDetailsHolder holder = new EventDetailsHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(EventDetailsHolder holder, final int position) {
        final HashMap<String, String> current = eventsList.get(position);

        final String name = current.get("name");
        holder.getEventName().setText(name);

        //Display each part of the event date.
        String date = current.get("date");
        String[] dateParts = date.split(" ");

        holder.getEventDay().setText(dateParts[0]);
        holder.getEventDayOfMonth().setText(dateParts[1]);
        holder.getEventMonth().setText(dateParts[2]);
        holder.getEventYear().setText(dateParts[3]);

        holder.getEventVenue().setText(current.get("venue"));

        //Handles the downloading and displaying of the entry form, when available.
        final String entryForm = current.get("entry_form");
        if(!entryForm.equals("")) {
            holder.getEventEntryForm().setImageResource(R.mipmap.ic_pdf_square);
        }
        else {
            holder.getEventEntryForm().setImageResource(android.R.color.transparent);
        }

        holder.getEventEntryForm().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!entryForm.equals("")) {
                    /*
                     * If file exists, open file.
                     * If not: download file; open file.
                     */
                    final String fileName = entryForm.substring(entryForm.lastIndexOf("/") + 1);
                    File file = new File(FileDownloader.ENTRY_FORMS_DIR, fileName);
                    String fileType = "application/pdf";

                    if(!file.exists()) {
                        FileDownloader fileDownloader = new FileDownloader(
		                        inflater.getContext(),
		                        v,
		                        position,
		                        name + " Entry Form",
		                        R.mipmap.ic_pdf_circle);
                        fileDownloader.execute(entryForm, fileName, FileDownloader.ENTRY_FORMS_DIR, fileType);
                    }
                    else {
                        inflater.getContext().startActivity(
                                FileDownloader.openFileActivity(FileDownloader.ENTRY_FORMS_DIR,
		                                fileName,
                                        fileType
                                )
                        );
                    }
                }
                else {
                    Snackbar.make(v, "Entry form not available", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

	    //Handles opening the item in Facebook (or other applications if the user does not have Facebook).
	    final String facebookEvent = current.get("facebook_event");
	    if(!facebookEvent.equals("")) {
		    holder.getEventFacebook().setImageResource(R.mipmap.ic_facebook_square);
	    }
	    else {
		    holder.getEventFacebook().setImageResource(android.R.color.transparent);
	    }

	    holder.getEventFacebook().setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View v) {
				if(!facebookEvent.equals("")) {
					//If Facebook Event exists, open in Facebook app or other recommended apps.
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(facebookEvent));
					inflater.getContext().startActivity(intent);
				}
				else {
					Snackbar.make(v, "Facebook event not available", Snackbar.LENGTH_SHORT).show();
				}
		    }
	    });

	    //Handles the downloading and displaying of the entry form, when available.
	    final String results = current.get("results");
	    if(!results.equals("")) {
		    holder.getEventResults().setImageResource(R.mipmap.ic_excel_square);
	    }
	    else {
		    holder.getEventResults().setImageResource(android.R.color.transparent);
	    }

	    holder.getEventResults().setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View v) {
			    if(!results.equals("")) {
                    /*
                     * If file exists, open file.
                     * If not: download file; open file.
                     */
				    final String fileName = results.substring(results.lastIndexOf("/") + 1);
				    File file = new File(FileDownloader.RESULTS_DIR, fileName);
				    String fileType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

				    if(!file.exists()) {
					    FileDownloader fileDownloader = new FileDownloader(
							    inflater.getContext(),
							    v,
							    position,
							    name + " Results",
							    R.mipmap.ic_excel_circle);
					    fileDownloader.execute(results, fileName, FileDownloader.RESULTS_DIR, fileType);
				    }
				    else {
					    inflater.getContext().startActivity(
							    FileDownloader.openFileActivity(FileDownloader.RESULTS_DIR,
										fileName,
										fileType
							    )
					    );
				    }
			    }
			    else {
				    Snackbar.make(v, "Results not available", Snackbar.LENGTH_SHORT).show();
			    }
		    }
	    });
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }

	/**
	 * Replaces current list with new list.
	 *
	 * @param list The new list to replace the old list.
	 */
    public void setList(List<HashMap<String, String>> list) {
	    eventsList = new ArrayList<>();
	    eventsList.addAll(list);
	    notifyDataSetChanged();
    }

    class EventDetailsHolder extends ViewHolder {

        private TextView eventName;
        private TextView eventDay;
        private TextView eventDayOfMonth;
        private TextView eventMonth;
        private TextView eventYear;
        private TextView eventVenue;
        private ImageButton eventEntryForm;
        private ImageButton eventFacebook;
        private ImageButton eventResults;

        public EventDetailsHolder(View itemView) {
            super(itemView);

            eventName = (TextView) itemView.findViewById(R.id.event_name);
            eventDay = (TextView) itemView.findViewById(R.id.event_day);
            eventDayOfMonth = (TextView) itemView.findViewById(R.id.event_day_of_month);
            eventMonth = (TextView) itemView.findViewById(R.id.event_month);
            eventYear = (TextView) itemView.findViewById(R.id.event_year);
            eventVenue = (TextView) itemView.findViewById(R.id.event_venue);
            eventEntryForm = (ImageButton) itemView.findViewById(R.id.event_entry_form);
	        eventFacebook = (ImageButton) itemView.findViewById(R.id.event_facebook);
	        eventResults = (ImageButton) itemView.findViewById(R.id.event_results);
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
    }
}
