package com.justtennis.db.service;

import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.sqlite.datasource.DBInviteDataSource;
import com.justtennis.db.sqlite.datasource.DBSaisonDataSource;
import com.justtennis.db.sqlite.helper.DBInviteHelper;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Saison;

public class SaisonService extends GenericNamedService<Saison> {

	public static final long ID_EMPTY = -1l;
	private INotifierMessage notificationMessage;

	public SaisonService(Context context, INotifierMessage notificationMessage) {
		super(context, new DBSaisonDataSource(context, notificationMessage), notificationMessage);
		this.notificationMessage = notificationMessage;
	}
	
	public Saison getEmpty() {
		return new Saison(ID_EMPTY);
	}

	public boolean isEmpty(Saison saison) {
		return saison.getId()!=null && ID_EMPTY==saison.getId();
	}
}
