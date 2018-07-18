package com.justtennis.business;

import android.content.Context;

import com.cameleon.common.android.db.sqlite.service.GenericService;
import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.service.UserService;
import com.justtennis.domain.Player;
import com.justtennis.domain.Saison;
import com.justtennis.domain.User;
import com.justtennis.manager.TypeManager;

public class UserBusiness extends PlayerBusiness {

	public UserBusiness(Context context, INotifierMessage notificationMessage) {
		super(context, notificationMessage);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <P extends Player> GenericService<P> createPlayerService(Context context, INotifierMessage notificationMessage) {
		return (GenericService<P>) new UserService(context, notificationMessage);
	}

	@Override
	public Player buildPlayer() {
		if (player==null) {

			player = getUserService().find();
			if (player == null) {
				player = getUserService().findFirst();
				if (player == null) {
					player = new User();
				}
			}

			Saison saison = TypeManager.getInstance().getSaison();
			if (saison == null) {
				saison = getSaisonService().getSaisonActiveOrFirst();
			}
			if (saison != null) {
				player.setIdSaison(saison.getId());
			}
		}
		return player;
	}

	@Override
	public boolean isUnknownPlayer(Player player) {
		return false;
	}

	@Override
	public boolean sendMessageConfirmation() {
		return false;
	}

	private UserService getUserService() {
		return (UserService)super.getPlayerService();
	}
}