package com.justtennis.db.service;

import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.sqlite.datasource.DBClubDataSource;
import com.justtennis.domain.Club;

public class ClubService extends GenericService<Club> {

	public ClubService(Context context, INotifierMessage notificationMessage) {
		super(context, new DBClubDataSource(context, notificationMessage), notificationMessage);
	}
}
