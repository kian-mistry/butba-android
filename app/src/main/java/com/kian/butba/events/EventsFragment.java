package com.kian.butba.events;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.view.ViewGroup;

import com.kian.butba.R;
import com.kian.butba.file.JSONHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Kian Mistry on 04/12/16.
 */

public class EventsFragment extends Fragment implements OnMenuItemClickListener {

    private ActionBar toolbar;
    private PopupMenu popupMenu;

    private SharedPreferences prefShownEvents;
    private boolean studentEvents;
    private boolean exStudentEvents;
    private boolean btbaEvents;

    private RecyclerView recyclerView;
    private EventCardsAdapter eventDetailsAdapter;
	private ArrayList<HashMap<String, String>> events;


    public EventsFragment() {
        //Required: Empty public constructor.
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Obtain which events are shown from the shared preferences.
        prefShownEvents = getActivity().getSharedPreferences("events_shown", Context.MODE_PRIVATE);
        studentEvents = prefShownEvents.getBoolean("student_events", true);
        exStudentEvents = prefShownEvents.getBoolean("ex_student_events", true);
        btbaEvents = prefShownEvents.getBoolean("btba_events", true);

        getEventDetails(new boolean[]{studentEvents, exStudentEvents, btbaEvents});
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
        eventDetailsAdapter = new EventCardsAdapter(getActivity(), getEvents());
        recyclerView.setAdapter(eventDetailsAdapter);
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        boolean status = toggleCheckBox(item);

        //Prevent popup menu from closing when an option is selected.
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        item.setActionView(new View(getContext()));

        Editor editor = prefShownEvents.edit();

        switch(id) {
            case R.id.cb_students:
                editor.putBoolean("student_events", status);
                getEventDetails(new boolean[]{status, exStudentEvents, btbaEvents});
                break;
            case R.id.cb_ex_students:
                editor.putBoolean("ex_student_events", status);
                getEventDetails(new boolean[]{studentEvents, status, btbaEvents});
                break;
            case R.id.cb_btba:
                editor.putBoolean("btba_events", status);
                getEventDetails(new boolean[]{studentEvents, exStudentEvents, status});
                break;
            default:
                break;
        }

        editor.commit();

        //Update recycler view with list of events if a checkbox has been altered.
        eventDetailsAdapter.setEventsList(getEvents());
        eventDetailsAdapter.notifyDataSetChanged();

        return false;
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

    private ArrayList<HashMap<String, String>> getEvents() {
        return events;
    }

    private void getEventDetails(boolean[] eventTypes) {
        boolean studentEvents = eventTypes[0];
        boolean exStudentEvents = eventTypes[1];
        boolean btbaEvents = eventTypes[2];

        try {
            JSONObject jsonObject = JSONHandler.loadJson(getContext(), "butba_events.json");
            JSONArray jsonArray = jsonObject.getJSONArray("events1617");

            events = new ArrayList<>();
            HashMap<String, String> eventDetails;

            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject event = jsonArray.getJSONObject(i);

                String tags = event.getString("tags");
                if(
                        (tags.contains("S") && studentEvents) ||
                        (tags.contains("X") && exStudentEvents) ||
                        (tags.contains("BTBA") && btbaEvents)
                ) {
                    String name = event.getString("name");

                    SimpleDateFormat oldDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    SimpleDateFormat newDateFormat = new SimpleDateFormat("EEE dd MMM yyyy");
                    String date = event.getString("date");
                    Date parsedDate = oldDateFormat.parse(date);
                    String formattedDate = newDateFormat.format(parsedDate);

                    String venue = event.getString("venue");
                    String entryForm = event.getString("entryForm");
                    String facebookEvent = event.getString("facebookEvent");
                    String results = event.getString("results");

                    eventDetails = new HashMap<>();
                    eventDetails.put("name", name);
                    eventDetails.put("date", formattedDate);
                    eventDetails.put("venue", venue);
                    eventDetails.put("entry_form", entryForm);
                    eventDetails.put("facebook_event", facebookEvent);
                    eventDetails.put("results", results);

                    events.add(eventDetails);
                }
            }
        }
        catch(JSONException | ParseException e) {
            e.printStackTrace();
        }
    }
}
