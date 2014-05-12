package com.justtennis.business;

import android.content.Context;
import android.content.Intent;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.activity.PlayerActivity.MODE;
import com.justtennis.db.service.GenericService;
import com.justtennis.db.service.UserService;
import com.justtennis.domain.Player;

public class UserBusiness extends PlayerBusiness {

	private UserService service;

	public UserBusiness(Context context, INotifierMessage notificationMessage) {
		super(context, notificationMessage);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <P extends Player> GenericService<P> createPlayerService(Context context, INotifierMessage notificationMessage) {
		service = new UserService(context, notificationMessage);
		return (GenericService<P>) service;
	}

	@Override
	protected void initializePlayer(Intent intent) {
		player = service.find();
	}
	
	@Override
	protected void initializeMode(Intent intent) {
		mode = (player == null || player.getId() == null) ? MODE.CREATE : MODE.MODIFY;
	}

	@Override
	public boolean isUnknownPlayer(Player player) {
		return false;
	}
}