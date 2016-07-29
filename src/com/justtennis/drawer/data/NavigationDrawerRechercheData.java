package com.justtennis.drawer.data;

import com.justtennis.drawer.notifier.NavigationDrawerRechercheNotifer;
import com.justtennis.drawer.notifier.NavigationDrawerRechercheNotifer.INavigationDrawerRechercheNotifer;
import com.justtennis.R;

public class NavigationDrawerRechercheData extends NavigationDrawerData {

	public NavigationDrawerRechercheData(long id, INavigationDrawerRechercheNotifer navigationDrawerRechercheNotifer) {
		super(id, R.layout.fragment_navigation_player_drawer_element_recherche, new NavigationDrawerRechercheNotifer(navigationDrawerRechercheNotifer));
	}
}
