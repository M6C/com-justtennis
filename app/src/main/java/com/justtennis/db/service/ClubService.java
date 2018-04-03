package com.justtennis.db.service;

import android.content.Context;

import com.cameleon.common.android.db.sqlite.service.GenericService;
import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.sqlite.datasource.DBClubDataSource;
import com.justtennis.domain.Club;

public class ClubService extends GenericService<Club> {

	public static final long ID_EMPTY_CLUB = -2l;

	public ClubService(Context context, INotifierMessage notificationMessage) {
		super(context, new DBClubDataSource(context, notificationMessage), notificationMessage);
	}

	public Club getEmptyClub() {
		Club ret = new Club();
		ret.setId(ID_EMPTY_CLUB);
		return ret;
	}

	public boolean isEmptyClub(Club club) {
		return club.getId()!=null && ID_EMPTY_CLUB==club.getId();
	}
	
	public boolean isRealClub(Club club) {
		return club.getId()!=null && club.getId() > 0;
	}
}