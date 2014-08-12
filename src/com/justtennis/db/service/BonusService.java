package com.justtennis.db.service;

import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.sqlite.datasource.DBBonusDataSource;
import com.justtennis.domain.Bonus;

public class BonusService extends GenericService<Bonus> {

	public BonusService(Context context, INotifierMessage notificationMessage) {
		super(context, new DBBonusDataSource(context, notificationMessage), notificationMessage);
	}
}