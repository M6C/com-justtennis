package com.justtennis.db.service;

import android.content.Context;

import com.cameleon.common.android.db.sqlite.service.GenericService;
import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.sqlite.datasource.DBMessageDataSource;
import com.justtennis.domain.Message;

public class MessageService extends GenericService<Message> {

	public MessageService(Context context, INotifierMessage notificationMessage) {
		super(context, new DBMessageDataSource(context, notificationMessage), notificationMessage);
	}

	public Message getCommon() {
		Message ret = null;
    	try {
    		dbDataSource.open();
			ret = ((DBMessageDataSource)dbDataSource).getCommon();
    	}
    	finally {
    		dbDataSource.close();
    	}
    	return ret;
	}
}
