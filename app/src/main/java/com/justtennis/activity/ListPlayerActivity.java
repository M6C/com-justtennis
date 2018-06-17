package com.justtennis.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.BaseAdapter;

import com.justtennis.R;
import com.justtennis.business.ListPlayerBusiness;
import com.justtennis.domain.RechercheResult;
import com.justtennis.drawer.adapter.NavigationDrawerRechercheListPlayerAdapter;
import com.justtennis.drawer.business.INavigationDrawerRechercheBusiness;
import com.justtennis.drawer.data.NavigationDrawerData;
import com.justtennis.drawer.data.NavigationDrawerRechercheData;
import com.justtennis.drawer.manager.DrawerManager;
import com.justtennis.drawer.notifier.NavigationDrawerRechercheNotifer;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.ui.activity.AbsctractFragmentActivity;
import com.justtennis.ui.common.CommonEnum;
import com.justtennis.ui.fragment.ListPlayerFragment;

import java.util.ArrayList;
import java.util.List;

public class ListPlayerActivity extends AbsctractFragmentActivity {

	public static final String EXTRA_MODE = "MODE";
	public static final String EXTRA_PLAYER_ID = "EXTRA_PLAYER_ID";

	private NotifierMessageLogger notifier;
	private DrawerManager drawerManager;
	private List<NavigationDrawerData> navigationDrawer = new ArrayList<>();
	private ListPlayerBusiness business;

	@Override
	protected Fragment createFragment() {
		return ListPlayerFragment.buildForEdit(this, notifier);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		notifier = NotifierMessageLogger.getInstance();
		drawerManager = new DrawerManager(this, notifier);
		drawerManager.setContentView(R.layout.list_player);

		business = new ListPlayerBusiness(this, notifier);
		navigationDrawer.add(new NavigationDrawerRechercheData(0, new NavigationDrawerRecherchePlayerNotifer()));
	}
	@Override
	public void onResume() {
		super.onResume();

		drawerManager.onResume();
		drawerManager.setValue(navigationDrawer);
	}

	public class NavigationDrawerRecherchePlayerNotifer implements NavigationDrawerRechercheNotifer.INavigationDrawerRechercheNotifer {

		@Override
		public BaseAdapter getAdapter(Context context, List<RechercheResult> list) {
			return new NavigationDrawerRechercheListPlayerAdapter(context, list, new NavigationDrawerRecherchePlayerItemOnClickListener());
		}

		@Override
		public INavigationDrawerRechercheBusiness getBusiness() {
			return business;
		}
	}

	public class NavigationDrawerRecherchePlayerItemOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			drawerManager.close();
		}
	}
}