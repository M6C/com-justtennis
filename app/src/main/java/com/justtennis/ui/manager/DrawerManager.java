package com.justtennis.ui.manager;

import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.justtennis.R;

public class DrawerManager {

    public DrawerManager() {
    }

    public void initializeDrawerToogle(FragmentActivity activity) {
        Toolbar toolbar = activity.findViewById(R.id.detail_toolbar);
        View layout = activity.findViewById(R.id.drawer_layout);

        if (toolbar != null && layout instanceof DrawerLayout) {
            DrawerLayout drawerLayout = (DrawerLayout)layout;
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
        }
    }
}