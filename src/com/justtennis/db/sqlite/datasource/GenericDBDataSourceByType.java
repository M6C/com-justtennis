package com.justtennis.db.sqlite.datasource;

import java.util.Locale;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.sqlite.helper.GenericDBHelper;
import com.justtennis.domain.GenericDBPojo;
import com.justtennis.manager.TypeManager;

public abstract class GenericDBDataSourceByType<POJO extends GenericDBPojo<Long>> extends GenericDBDataSource<POJO> {

	public GenericDBDataSourceByType(GenericDBHelper dbHelper, INotifierMessage notificationMessage) {
		super(dbHelper, notificationMessage);
	}
	
	@Override
	protected String customizeWhere(String where) {
		if (where == null) {
			where = "";
		} else {
//			String WHERE_UP = " WHERE ";
//			String whereUp = where.toUpperCase(Locale.FRANCE);
//			int idx = whereUp.indexOf(WHERE_UP);
//			if (idx >= 0) {
				where += " AND ";
//			}
		}
		where += " TYPE = '" + TypeManager.getInstance().getType() + "'";
		return where;
	}
}