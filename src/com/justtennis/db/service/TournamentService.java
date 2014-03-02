package com.justtennis.db.service;

import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.sqlite.datasource.DBTournamentDataSource;
import com.justtennis.domain.Tournament;

public class TournamentService extends GenericService<Tournament> {

	public TournamentService(Context context, INotifierMessage notificationMessage) {
		super(context, new DBTournamentDataSource(context, notificationMessage), notificationMessage);
	}
}
