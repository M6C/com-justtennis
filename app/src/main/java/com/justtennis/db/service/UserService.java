package com.justtennis.db.service;

import java.util.List;

import android.content.Context;

import com.cameleon.common.android.db.sqlite.service.GenericService;
import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.sqlite.datasource.DBUserDataSource;
import com.justtennis.domain.Saison;
import com.justtennis.domain.User;
import com.justtennis.manager.TypeManager;

public class UserService extends GenericService<User> {

	private TypeManager typeManager;

	public UserService(Context context, INotifierMessage notificationMessage) {
		super(context, new DBUserDataSource(context, notificationMessage), notificationMessage);
		typeManager = TypeManager.getInstance(context, notificationMessage);
	}

	public User find() {
		User ret = null;

		Saison saison = typeManager.getSaison();
		if (saison != null) {
			// Select in database
	    	try {
	    		dbDataSource.open();
				ret = ((DBUserDataSource)dbDataSource).getByIdSaison(saison.getId());
	    	}
	    	finally {
	    		dbDataSource.close();
	    	}
		} else {
			ret = findFirst();
		}
    	return ret;
	}

	public User findFirst() {
		User ret = null;

    	try {
    		dbDataSource.open();
			// Select in database
			List<User> list = dbDataSource.getAll();
			if (list!=null && list.size()>0) {
				ret = list.get(0);
			}
    	}
    	finally {
    		dbDataSource.close();
    	}
    	return ret;
	}

	public User getByIdSaison(Long idSaison) {
		User ret = null;
    	try {
    		dbDataSource.open();
			// Select in database
			ret = ((DBUserDataSource)dbDataSource).getByIdSaison(idSaison);
    	}
    	finally {
    		dbDataSource.close();
    	}
    	return ret;
	}
}