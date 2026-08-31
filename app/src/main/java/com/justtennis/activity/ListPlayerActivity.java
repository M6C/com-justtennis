package com.justtennis.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.justtennis.R;
import com.justtennis.adapter.ListPlayerAdapter;
import com.justtennis.business.ListPlayerBusiness;
import com.justtennis.domain.Player;
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

import java.util.ArrayList;
import java.util.List;

public class ListPlayerActivity extends AbsctractFragmentActivity {

	public static final String EXTRA_MODE = "INVITE_MODE";
	public static final String EXTRA_PLAYER_ID = "EXTRA_PLAYER_ID";

	private DrawerManager drawerManager;
	private List<NavigationDrawerData> navigationDrawer = new ArrayList<>();
	private ListPlayerBusiness business;
	private ListPlayerAdapter adapter;

	@Override
	protected Fragment createFragment() {
		// Cet ecran n'utilise pas le mecanisme fragment de AbsctractFragmentActivity : le layout
		// est entierement remplace juste apres par drawerManager.setContentView(R.layout.list_player)
		// (ListView + DrawerManager, pattern legacy). Retourner un fragment ici l'attacherait a un
		// container (R.id.main_container) qui n'existe plus une fois ce remplacement effectue,
		// provoquant un crash systematique a l'ouverture de l'ecran.
		return null;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		NotifierMessageLogger notifier = NotifierMessageLogger.getInstance();
		drawerManager = new DrawerManager(this, notifier);
		drawerManager.setContentView(R.layout.list_player);

		business = new ListPlayerBusiness(this, notifier);
		business.initialize(getIntent().getExtras());
		navigationDrawer.add(new NavigationDrawerRechercheData(0, new NavigationDrawerRecherchePlayerNotifer()));

		// La liste n'etait jamais cablee a un adapter (ni business.initialize()/refreshData()
		// jamais appeles) : ListPlayerAdapter existait deja (utilise par ListPlayerFragment2,
		// code mort) mais n'etait jamais instancie ici.
		adapter = new ListPlayerAdapter(this, business.getList());
		ListView list = findViewById(R.id.list);
		list.setAdapter(adapter);
		list.setOnItemClickListener((parent, view, position, id) -> {
			Player player = (Player) parent.getItemAtPosition(position);
			Intent intent = new Intent(this, PlayerActivity.class);
			intent.putExtra(PlayerActivity.EXTRA_MODE, CommonEnum.PLAYER_MODE.MODIFY);
			intent.putExtra(PlayerActivity.EXTRA_PLAYER_ID, player.getId());
			startActivity(intent);
		});
	}
	@Override
	public void onResume() {
		super.onResume();

		drawerManager.onResume();
		drawerManager.setValue(navigationDrawer);

		business.refreshData();
		adapter.notifyDataSetChanged();
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