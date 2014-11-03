package com.justtennis.business;

import java.util.List;

import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.service.PlayerService;
import com.justtennis.db.service.SaisonService;
import com.justtennis.db.service.UserService;
import com.justtennis.domain.Saison;

public class MainBusiness {

	@SuppressWarnings("unused")
	private static final String TAG = MainBusiness.class.getSimpleName();

	private UserService userService;
	private PlayerService playerService;
	private SaisonService saisonService;

	private long userCount;
	private Context context;

	private List<Saison> listSaison;

	public MainBusiness(Context context, INotifierMessage notificationMessage) {
		this.context = context;
		userService = new UserService(context, notificationMessage);
		playerService = new PlayerService(context, notificationMessage);
		saisonService = new SaisonService(context, notificationMessage);
	}

	public void onResume() {
		initializeData();
	}

	public Long getUnknownPlayerId() {
		return playerService.getUnknownPlayer().getId();
	}

	private void initializeData() {
		userCount = userService.getCount();
		initializeDataSaison();
	}

	private void initializeDataSaison() {
		listSaison = saisonService.getList();
	}

	public long getUserCount() {
		return userCount;
	}
	
	public Context getContext() {
		return context;
	}
}