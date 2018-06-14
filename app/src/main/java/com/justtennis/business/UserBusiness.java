package com.justtennis.business;

import android.content.Context;

import com.cameleon.common.android.db.sqlite.service.GenericService;
import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.service.MessageService;
import com.justtennis.db.service.UserService;
import com.justtennis.domain.Message;
import com.justtennis.domain.Player;
import com.justtennis.domain.Saison;
import com.justtennis.domain.User;
import com.justtennis.manager.TypeManager;

public class UserBusiness extends PlayerBusiness {

	private MessageService messageService;

	public UserBusiness(Context context, INotifierMessage notificationMessage) {
		super(context, notificationMessage);
		messageService = new MessageService(context, notificationMessage);
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

	public void saveMessage(String text) {
		Message message = messageService.getCommon();
		if (message == null) {
			message = new Message(text);
		} else {
			message.setMessage(text);
		}
		// Save in database
		messageService.createOrUpdate(message);
	}

	public String getMessage() {
		Message message = messageService.getCommon();
		return (message == null) ? "" : message.getMessage();
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