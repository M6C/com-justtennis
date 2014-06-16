package com.justtennis.db.service;

import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import android.content.Context;
import android.util.SparseIntArray;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.sqlite.datasource.DBRankingDataSource;
import com.justtennis.domain.Player;
import com.justtennis.domain.Ranking;
import com.justtennis.domain.comparator.RankingComparatorByOrder;

public class RankingService extends GenericService<Ranking> {

	private static final int MAX_POINT = 120;

	public RankingService(Context context, INotifierMessage notificationMessage) {
		super(context, new DBRankingDataSource(context, notificationMessage), notificationMessage);
	}

	public void order(List<Ranking> listRanking) {
		order(listRanking, false);
	}

	public Ranking getNC() {
		try {
			dbDataSource.open();
			return ((DBRankingDataSource)dbDataSource).getNC();
		}
		finally {
			dbDataSource.close();
		}
	}

	public Ranking getRanking(Player player, boolean estimate) {
		Ranking ret = null;
		Long idRanking = null;
		if (estimate) {
			idRanking = (player.getIdRankingEstimate() != null ? player.getIdRankingEstimate() : player.getIdRanking());
		} else {
			idRanking = (player.getIdRanking() != null ? player.getIdRanking() : player.getIdRankingEstimate());
		}
		if (idRanking != null) {
			ret = find(idRanking);
		}
		if (idRanking == null) {
			ret = getNC();
		}
		return ret;
	}
	
	public void order(List<Ranking> listRanking, boolean inverse) {
		SortedSet<Ranking> setRanking = new TreeSet<Ranking>(new RankingComparatorByOrder(inverse));
		setRanking.addAll(listRanking);
		listRanking.clear();
		listRanking.addAll(setRanking);
	}

	public HashMap<Long, Ranking> getMapById() {
		HashMap<Long, Ranking> ret = new HashMap<Long, Ranking>();
		List<Ranking> lisRanking = getList();
		for(Ranking ranking : lisRanking) {
			ret.put(ranking.getId(), ranking);
		}
		return ret;
	}

	public boolean isEmptyRanking(Ranking ranking) {
		return ranking.getOrder()==0;
	}

	public int getNbPointDifference(int orderDifference) {
		int ret = 0;
		SparseIntArray nbPointByOrderDifference = getNbPointByOrderDifference();
		if (nbPointByOrderDifference.indexOfKey(orderDifference) >= 0) {
			ret = nbPointByOrderDifference.get(orderDifference);
		} else {
			if (orderDifference > 0) {
				ret = MAX_POINT;
			}
		}
		return ret;
	}

	private SparseIntArray getNbPointByOrderDifference() {
		SparseIntArray ret = new SparseIntArray(6);
		ret.put(-3, 15);
		ret.put(-2, 20);
		ret.put(-1, 30);
		ret.put(0, 60);
		ret.put(1, 90);
		ret.put(2, MAX_POINT);
		return ret;
	}
}