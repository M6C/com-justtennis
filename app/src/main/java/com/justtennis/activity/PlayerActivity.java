
package com.justtennis.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.BaseAdapter;

import com.justtennis.business.PlayerBusiness;
import com.justtennis.domain.Player;
import com.justtennis.domain.RechercheResult;
import com.justtennis.drawer.adapter.NavigationDrawerRechercheAdapter;
import com.justtennis.drawer.business.INavigationDrawerRechercheBusiness;
import com.justtennis.drawer.data.NavigationDrawerData;
import com.justtennis.drawer.data.NavigationDrawerRechercheData;
import com.justtennis.drawer.manager.DrawerManager;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.ui.activity.AbsctractFragmentActivity;
import com.justtennis.ui.fragment.PlayerFragment;

import java.util.ArrayList;
import java.util.List;

public class PlayerActivity extends AbsctractFragmentActivity {

	private static final String TAG = PlayerActivity.class.getSimpleName();
	public static final String EXTRA_PLAYER = "PLAYER";
	public static final String EXTRA_PLAYER_ID = "PLAYER_ID";
	public static final String EXTRA_INVITE = "INVITE";
	public static final String EXTRA_MODE = "MODE";
	public static final String EXTRA_TYPE = "TYPE";
	public static final String EXTRA_RANKING = "RANKING";
	public static final String EXTRA_FIND = "EXTRA_FIND";

	private PlayerFragment fragment;
	private DrawerManager drawerManager;
	private List<NavigationDrawerData> navigationDrawer = new ArrayList<NavigationDrawerData>();
	private PlayerBusiness business;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		navigationDrawer.add(new NavigationDrawerRechercheData(0, new NavigationDrawerRechercheNotifer()));
		NotifierMessageLogger notifier = NotifierMessageLogger.getInstance();
		drawerManager = new DrawerManager(this, notifier);
	}
	@Override
	public void onResume() {
		super.onResume();

		drawerManager.onResume();
		drawerManager.setDrawerLayoutSaisonNotifier(fragment);
		drawerManager.setDrawerLayoutTypeNotifier(fragment);
		if (navigationDrawer != null) {
			drawerManager.setValue(navigationDrawer);
		}
	}

	@Override
	protected Fragment createFragment() {
		fragment = new PlayerFragment();
		business = fragment.getBusiness();
		return fragment;
	}

	public class NavigationDrawerRechercheNotifer implements com.justtennis.drawer.notifier.NavigationDrawerRechercheNotifer.INavigationDrawerRechercheNotifer {

		@Override
		public BaseAdapter getAdapter(Context context, List<RechercheResult> list) {
			return new NavigationDrawerRechercheAdapter(context, list, new NavigationDrawerRechercheItemOnClickListener());
		}

		@Override
		public INavigationDrawerRechercheBusiness getBusiness() {
			return business;
		}
	}

	public class NavigationDrawerRechercheItemOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			RechercheResult item = (RechercheResult) v.getTag();
			Player player = business.getPlayer();
			player.setIdTournament(0L);
			player.setIdClub(0L);
			player.setIdAddress(0L);

			switch (item.getType()) {
				case TOURNAMENT:
					player.setIdTournament(item.getId());
					break;

				case CLUB:
					player.setIdClub(item.getId());
					break;

				case ADDRESS:
					player.setIdAddress(item.getId());
					break;
			}
// TODO With RxJava To PlayerActivity
//			PlayerFragment.this.initializeLocation();
			drawerManager.close();
		}
	}
}