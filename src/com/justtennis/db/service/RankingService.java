package com.justtennis.db.service;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.sqlite.datasource.DBRankingDataSource;
import com.justtennis.domain.Ranking;
import com.justtennis.domain.comparator.RankingComparatorByOrder;

public class RankingService extends GenericService<Ranking> {

	public RankingService(Context context, INotifierMessage notificationMessage) {
		super(context, new DBRankingDataSource(context, notificationMessage), notificationMessage);
	}

	public void order(List<Ranking> listRanking) {
		SortedSet<Ranking> setRanking = new TreeSet<Ranking>(new RankingComparatorByOrder());
		setRanking.addAll(listRanking);
		listRanking.clear();
		listRanking.addAll(setRanking);
	}
}