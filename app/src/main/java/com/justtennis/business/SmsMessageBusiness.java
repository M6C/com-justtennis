package com.justtennis.business;

import android.content.Context;
import android.os.Bundle;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.activity.PlayerActivity;
import com.justtennis.db.service.MessageService;
import com.justtennis.domain.Message;
import com.justtennis.ui.common.CommonEnum;

public class SmsMessageBusiness {

	private MessageService messageService;
	private CommonEnum.PLAYER_MODE mode;

	public SmsMessageBusiness(Context context, INotifierMessage notificationMessage) {
		messageService = new MessageService(context, notificationMessage);
	}

	public void initialize(Bundle bundle) {
		initializeMode(bundle);
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

	protected void initializeMode(Bundle bundle) {
		if (bundle != null && bundle.containsKey(PlayerActivity.EXTRA_MODE)) {
			mode = (CommonEnum.PLAYER_MODE) bundle.getSerializable(PlayerActivity.EXTRA_MODE);
		}
	}

	public CommonEnum.PLAYER_MODE getMode() {
		return mode;
	}
}