package com.justtennis.db.service;

import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.sqlite.datasource.DBTournamentDataSource;
import com.justtennis.domain.Tournament;

public class TournamentService extends GenericService<Tournament> {

	public static final long ID_EMPTY_TOURNAMENT = -2l;

	public TournamentService(Context context, INotifierMessage notificationMessage) {
		super(context, new DBTournamentDataSource(context, notificationMessage), notificationMessage);
	}

	public Tournament getEmptyTournament() {
		Tournament ret = new Tournament();
		ret.setId(ID_EMPTY_TOURNAMENT);
		return ret;
	}
	
	public boolean isEmptyTournament(Tournament tournament) {
		return tournament.getId()!=null && ID_EMPTY_TOURNAMENT==tournament.getId();
	}
}
