package com.justtennis.drawer.fragment;

import android.support.v4.widget.DrawerLayout;

import com.justtennis.drawer.data.NavigationDrawerData;

import java.util.List;

public interface INavigationDrawer {
    public boolean isDrawerOpen();
    public void close();
    public void setValue(List<NavigationDrawerData> value);
    public void updValue();
    public void setUp(int fragmentId, DrawerLayout drawerLayout);
    public void setHeader(NavigationDrawerData header);
    public void setFooter(NavigationDrawerData footer);
}
