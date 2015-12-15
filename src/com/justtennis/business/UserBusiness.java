package com.justtennis.business;

import android.content.Context;
import android.content.Intent;

import com.cameleon.common.android.db.sqlite.service.GenericService;
import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.activity.PlayerActivity.MODE;
import com.justtennis.db.service.MessageService;
import com.justtennis.db.service.SaisonService;
import com.justtennis.db.service.UserService;
import com.justtennis.domain.Message;
import com.justtennis.domain.Player;
import com.justtennis.domain.Saison;
import com.justtennis.domain.User;
import com.justtennis.manager.TypeManager;

public class UserBusiness extends PlayerBusiness {

	private UserService service;
	private SaisonService saisonService;
	private MessageService messageService;

	public UserBusiness(Context context, INotifierMessage notificationMessage) {
		super(context, notificationMessage);
		messageService = new MessageService(context, notificationMessage);
		saisonService = new SaisonService(context, notificationMessage);
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
		if (player == null) {
			player = service.findFirst();
			if (player != null) {
				Saison saison = TypeManager.getInstance().getSaison();
				if (saison == null) {
					saison = saisonService.getSaisonActiveOrFirst();
				}
				if (saison != null) {
					player.setId(null);
					player.setIdSaison(saison.getId());
					service.createOrUpdate((User)player);
				}
			}
		}
	}
	
	@Override
	protected void initializeMode(Intent intent) {
		mode = (player == null || player.getId() == null) ? MODE.CREATE : MODE.MODIFY;
	}

//	@Override
//	protected void initializeDataSaison() {
//	}

	@Override
	public boolean isUnknownPlayer(Player player) {
		return false;
	}

	@Override
	public Player buildPlayer() {
		if (player==null) {
			player = new User();
		}
		return player;
	}

	public void saveMessage(String message) {
		// Save in database
		messageService.createOrUpdate(new Message(message));
	}
	
	public String getMessage() {
		Message message = messageService.getCommon();
		return (message == null) ? "" : message.getMessage();
	}
}