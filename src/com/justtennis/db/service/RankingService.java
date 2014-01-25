package com.justtennis.db.service;

import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.sqlite.datasource.DBRankingDataSource;
import com.justtennis.domain.Ranking;

public class RankingService extends GenericService<Ranking> {

	public RankingService(Context context, INotifierMessage notificationMessage) {
		super(context, new DBRankingDataSource(context, notificationMessage), notificationMessage);
	}
}
