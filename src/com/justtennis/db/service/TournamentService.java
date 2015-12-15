package com.justtennis.db.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;

import com.cameleon.common.android.db.sqlite.service.GenericService;
import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.sqlite.datasource.DBTournamentDataSource;
import com.justtennis.domain.Tournament;
import com.justtennis.domain.comparator.TournamentComparatorByDate;

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
	
	public boolean isRealTournament(Tournament tournament) {
		return tournament.getId()!=null && tournament.getId() > 0;
	}
	
	public List<Tournament> sortTournamentByDate(List<Tournament> listTournament) {
		Tournament[] arrayTournament = listTournament.toArray(new Tournament[0]);
		Arrays.sort(arrayTournament, new TournamentComparatorByDate(true));
		return new ArrayList<Tournament>(Arrays.asList(arrayTournament));
	}
}