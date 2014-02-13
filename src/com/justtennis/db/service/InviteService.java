package com.justtennis.db.service;

import java.util.HashMap;
import java.util.List;

import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.sqlite.datasource.DBInviteDataSource;
import com.justtennis.domain.Invite;

public class InviteService extends GenericService<Invite> {

	public InviteService(Context context, INotifierMessage notificationMessage) {
		super(context, new DBInviteDataSource(context, notificationMessage), notificationMessage);
	}

	public List<Invite> getByIdPlayer(long idPlayer) {
    	try {
    		dbDataSource.open();
    		return ((DBInviteDataSource)dbDataSource).getByIdPlayer(idPlayer);
    	}
    	finally {
    		dbDataSource.close();
    	}
	}

	public HashMap<String, Double> countGroupByRanking() {
    	try {
    		dbDataSource.open();
    		return ((DBInviteDataSource)dbDataSource).countGroupByRanking();
    	}
    	finally {
    		dbDataSource.close();
    	}
	}
	
	public HashMap<String,Double> countByTypeGroupByRanking(Invite.INVITE_TYPE type) {
		try {
			dbDataSource.open();
			return ((DBInviteDataSource)dbDataSource).countByTypeGroupByRanking(type);
		}
		finally {
			dbDataSource.close();
		}
	}
}
