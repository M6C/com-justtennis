package com.justtennis.business;

import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.service.MessageService;
import com.justtennis.domain.Message;

public class SmsMessageBusiness {

	private MessageService messageService;

	public SmsMessageBusiness(Context context, INotifierMessage notificationMessage) {
		messageService = new MessageService(context, notificationMessage);
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
}