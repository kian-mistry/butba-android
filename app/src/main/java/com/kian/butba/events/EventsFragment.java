package com.kian.butba.events;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.kian.butba.R;

/**
 * Created by Kian Mistry on 04/12/16.
 */

public class EventsFragment extends Fragment {

    private ActionBar toolbar;

    private SharedPreferences prefShownEvents;
    private boolean studentEvents;
    private boolean exStudentEvents;
    private boolean btbaEvents;

    private RecyclerView recyclerView;


    public EventsFragment() {
        //Required: Empty public constructor.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);

        //Obtain toolbar.
        toolbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        toolbar.setTitle("Events");
        toolbar.invalidateOptionsMenu();
        setHasOptionsMenu(true);

        //Initialise recycler view.
        recyclerView = (RecyclerView) view.findViewById(R.id.event_cards_container);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        //Obtain which events are shown from the shared preferences.
        prefShownEvents = getActivity().getSharedPreferences("events_shown", Context.MODE_PRIVATE);
        studentEvents = prefShownEvents.getBoolean("student_events", true);
        exStudentEvents = prefShownEvents.getBoolean("ex_student_events", true);
        btbaEvents = prefShownEvents.getBoolean("btba_events", true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.clear();
        inflater.inflate(R.menu.menu_events, menu);

        //Initialise the checked values of the menu items.
        menu.getItem(0).setChecked(studentEvents);
        menu.getItem(1).setChecked(exStudentEvents);
        menu.getItem(2).setChecked(btbaEvents);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        boolean status = toggleCheckBox(item);

        Editor editor = prefShownEvents.edit();

        switch(id) {
            case R.id.cb_students:
                editor.putBoolean("student_events", status);
                Snackbar.make(getView(), "Students", Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.cb_ex_students:
                editor.putBoolean("ex_student_events", status);
                Snackbar.make(getView(), "Ex-Students", Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.cb_btba:
                editor.putBoolean("btba_events", status);
                Snackbar.make(getView(), "BTBA", Snackbar.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

        editor.commit();

        return super.onOptionsItemSelected(item);
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
}
