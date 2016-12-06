package com.kian.butba.events;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        //Add custom buttons to the toolbar.
        menu.clear();
        inflater.inflate(R.menu.toolbar_events_items, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //Obtain which events are shown from the shared preferences.
        prefShownEvents = getActivity().getSharedPreferences("events_shown", Context.MODE_PRIVATE);
        studentEvents = prefShownEvents.getBoolean("student_events", true);
        exStudentEvents = prefShownEvents.getBoolean("ex_student_events", true);
        btbaEvents = prefShownEvents.getBoolean("btba_events", true);

        switch(id) {
            case R.id.action_filter:
                //Add popup menu.
                View menuActionFilter = getActivity().findViewById(R.id.action_filter);
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
}
