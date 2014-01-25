package com.justtennis.business;

import android.content.Context;
import android.content.Intent;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.service.MessageService;
import com.justtennis.domain.Message;

public class MessageBusiness {

	private MessageService messageService;
	private Message message;

	public MessageBusiness(Context context, INotifierMessage notificationMessage) {
		messageService = new MessageService(context, notificationMessage);
	}

	public void initialize(Intent intent) {
		message = messageService.getCommon();
		if (message==null) {
			message = new Message();
		}
	}

	public void save(String message) {
		this.message.setMessage(message);

		// Save in database
		messageService.createOrUpdate(this.message);
	}

	public String getMessage() {
		return message.getMessage();
	}
}
