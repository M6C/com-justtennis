package com.justtennis.ui.manager;

import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;

import com.justtennis.R;

public class DrawerManager {

    public DrawerManager() {
    }

    public void initializeDrawerToogle(FragmentActivity activity) {
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.detail_toolbar);
        DrawerLayout drawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);

        if (toolbar != null && drawerLayout != null) {
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
        }
    }
}