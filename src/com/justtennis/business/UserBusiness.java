package com.justtennis.business;

import android.content.Context;
import android.content.Intent;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.activity.PlayerActivity.MODE;
import com.justtennis.db.service.GenericService;
import com.justtennis.db.service.MessageService;
import com.justtennis.db.service.UserService;
import com.justtennis.domain.Message;
import com.justtennis.domain.Player;

public class UserBusiness extends PlayerBusiness {

	private UserService service;
	private MessageService messageService;

	public UserBusiness(Context context, INotifierMessage notificationMessage) {
		super(context, notificationMessage);
		messageService = new MessageService(context, notificationMessage);
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

	public void saveMessage(String message) {
		// Save in database
		messageService.createOrUpdate(new Message(message));
	}
	
	public String getMessage() {
		Message message = messageService.getCommon();
		return (message == null) ? "" : message.getMessage();
	}
}