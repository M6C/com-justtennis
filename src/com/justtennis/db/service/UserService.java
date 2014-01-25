package com.justtennis.db.service;

import java.util.List;

import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.sqlite.datasource.DBUserDataSource;
import com.justtennis.domain.User;

public class UserService extends GenericService<User> {

	public UserService(Context context, INotifierMessage notificationMessage) {
		super(context, new DBUserDataSource(context, notificationMessage), notificationMessage);
	}

	public User find() {
		User ret = null;
    	try {
    		dbDataSource.open();
			// Save in database
			List<User> list = dbDataSource.getAll();
			if (list!=null && list.size()>0)
				ret = list.get(0);
    	}
    	finally {
    		dbDataSource.close();
    	}
    	return ret;
	}
}