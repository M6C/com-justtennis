package com.justtennis.db.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.sqlite.datasource.DBInviteDataSource;
import com.justtennis.domain.Invite;
import com.justtennis.domain.comparator.InviteComparatorByDate;
import com.justtennis.manager.TypeManager;

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
	
	public List<Invite> getByIdTournament(long idTournament) {
		try {
			dbDataSource.open();
			return ((DBInviteDataSource)dbDataSource).getByIdTournament(idTournament);
		}
		finally {
			dbDataSource.close();
		}
	}

	public int countByIdPlayer(long idPlayer) {
    	try {
    		dbDataSource.open();
    		return ((DBInviteDataSource)dbDataSource).countByIdPlayer(idPlayer);
    	}
    	finally {
    		dbDataSource.close();
    	}
	}

	public HashMap<String,Double> countByTypeGroupByRanking(TypeManager.TYPE type, Invite.SCORE_RESULT scoreResult) {
		try {
			dbDataSource.open();
			return ((DBInviteDataSource)dbDataSource).countByTypeGroupByRanking(type, scoreResult);
		}
		finally {
			dbDataSource.close();
		}
	}

	public HashMap<Long, List<Invite>> getGroupByIdTournament() {
		List<Invite> listInvite = null;
		try {
			dbDataSource.open();
			listInvite = ((DBInviteDataSource)dbDataSource).getAll();
		}
		finally {
			dbDataSource.close();
		}
		return groupByIdTournament(listInvite);
	}

	public List<Invite> getByScoreResult(Invite.SCORE_RESULT scoreResult) {
		List<Invite> listInvite = null;
		try {
			dbDataSource.open();
			listInvite = ((DBInviteDataSource)dbDataSource).getByScoreResult(scoreResult);
		}
		finally {
			dbDataSource.close();
		}
		return listInvite;
	}

	public HashMap<Long, List<Invite>> getGroupByIdRanking(Invite.SCORE_RESULT scoreResult) {
		List<Invite> listPlayer = getByScoreResult(scoreResult);
		if (listPlayer != null) {
			return groupByIdRanking(listPlayer);
		}
		return null;
	}

	public HashMap<Long, List<Invite>> groupByIdTournament(List<Invite> listInvite) {
		HashMap<Long, List<Invite>> ret = new HashMap<Long, List<Invite>>();
		if (listInvite != null) {
			for(Invite invite : listInvite) {
				Long key = invite.getIdTournament();
				List<Invite> list = ret.get(key);
				if (list == null) {
					list = new ArrayList<Invite>();
					ret.put(key, list);
				}
				list.add(invite);
			}
		}
		return ret;
	}

	public List<Invite> sortInviteByDate(List<Invite> listInvite) {
		Invite[] arrayInvite = listInvite.toArray(new Invite[0]);
		Arrays.sort(arrayInvite, new InviteComparatorByDate(true));
		return Arrays.asList(arrayInvite);
	}

	private HashMap<Long,List<Invite>> groupByIdRanking(List<Invite> listPlayer) {
		HashMap<Long, List<Invite>> ret = new HashMap<Long, List<Invite>>();
		for(Invite invite : listPlayer) {
			Long key = invite.getIdRanking();
			List<Invite> list = ret.get(key);
			if (list == null) {
				list = new ArrayList<Invite>();
				ret.put(key, list);
			}
			list.add(invite);
		}
		return ret;
	}
}