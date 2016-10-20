package com.kian.butba.navigation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.kian.butba.ProfileActivity;
import com.kian.butba.R;
import com.kian.butba.committee.CommitteeActivity;
import com.kian.butba.views.DividerItemDecoration;
import com.kian.butba.views.OnItemPressListener;
import com.kian.butba.views.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kian Mistry on 17/10/16.
 */

public class NavigationDrawerFragment extends Fragment {

    //Preference File
    public static final String PREF_FILE_NAME = "nav_drawer_pref";
    public static final String KEY_USER_LEARNED_DRAWER = "user_learned_drawer";

    //Preference File Tags For Navigation Drawer
    private boolean userLearnedDrawer;
    private boolean fromSavedInstanceState;

    //Navigation Drawer Components
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private View view;

    private RecyclerView recyclerView;
    private NavigationAdapter navigationAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userLearnedDrawer = Boolean.valueOf(readFromPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, "false"));

        if(savedInstanceState != null) {
            fromSavedInstanceState = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the layout for this fragment.
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.drawer_list);

        navigationAdapter = new NavigationAdapter(getActivity(), getMenuItems());
        recyclerView.setAdapter(navigationAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new OnItemPressListener() {
            @Override
            public void onTouch(View view, int position) {
                Context context = view.getContext();
                navigationItemsActions(context, position);
            }

            @Override
            public void onLongTouch(View view, int position) {

            }
        }));

        return layout;
    }

    private void navigationItemsActions(Context context, int position) {
        switch(position - 1) {
            case -1:
                Toast.makeText(context, "Profile", Toast.LENGTH_SHORT).show();
                break;
            case 0:
                Intent iProfile = new Intent(context, ProfileActivity.class);
                context.startActivity(iProfile);
                break;
            case 1:
                Intent iCommittee = new Intent(context, CommitteeActivity.class);
                context.startActivity(iCommittee);
            default:
                Toast.makeText(context, "Test", Toast.LENGTH_SHORT).show();
                break;
        }
        closeDrawer();
    }

    public void setup(final DrawerLayout drawerLayout, final Toolbar toolbar, int fragmentId) {
        this.drawerLayout = drawerLayout;
        view = getActivity().findViewById(fragmentId);

        drawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                if(!userLearnedDrawer) {
                    userLearnedDrawer = true;
                    saveToPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, String.valueOf(userLearnedDrawer));
                }
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }
        };

        if(!userLearnedDrawer && !fromSavedInstanceState) {
            this.drawerLayout.openDrawer(view);
        }

        this.drawerLayout.addDrawerListener(drawerToggle);

        //Animates the hamburger icon on the toolbar.
        this.drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                drawerToggle.syncState();
            }
        });
    }

    public boolean isDrawerOpen() {
        return drawerLayout != null && drawerLayout.isDrawerOpen(view);
    }

    public void closeDrawer() {
        drawerLayout.closeDrawer(view);
    }

    public static void saveToPreferences(Context context, String preferenceName, String preferenceValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(preferenceName, preferenceValue);
        editor.apply();
    }

    public static String readFromPreferences(Context context, String preferenceName, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(preferenceName, defaultValue);
    }

    public List<NavigationItem> getMenuItems() {
        List<NavigationItem> navigationItems = new ArrayList<>();

        int[] menuIcons = {
            R.mipmap.ic_action_perm_identity,
            R.mipmap.ic_action_group_work
        };

        String[] menuTitles = getResources().getStringArray(R.array.nav_items);

        for(int i = 0; i < menuIcons.length && i < menuTitles.length; i++) {
            NavigationItem current = new NavigationItem();

            current.setIconId(menuIcons[i]);
            current.setItemTitle(menuTitles[i]);

            navigationItems.add(current);
        }

        return navigationItems;
    }
}
