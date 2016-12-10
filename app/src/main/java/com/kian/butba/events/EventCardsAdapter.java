package com.kian.butba.events;

import android.content.Context;
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
                                fileType));
                    }
                }
                else {
                    Snackbar.make(v, "Entry form not available", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }

    public void setEventsList(List<HashMap<String, String>> eventsList) {
        this.eventsList = eventsList;
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
