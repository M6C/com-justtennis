package com.justtennis.db.sqlite.datasource;

import android.database.Cursor;

import com.cameleon.common.android.db.sqlite.datasource.GenericDBDataSource;
import com.cameleon.common.android.db.sqlite.helper.GenericDBHelper;
import com.cameleon.common.android.inotifier.INotifierMessage;
import com.cameleon.common.android.model.GenericDBPojo;
import com.cameleon.common.tool.DbTool;
import com.justtennis.manager.TypeManager;

public abstract class GenericDBDataSourceByType<POJO extends GenericDBPojo<Long>> extends GenericDBDataSource<POJO> {

	public GenericDBDataSourceByType(GenericDBHelper dbHelper, INotifierMessage notificationMessage) {
		super(dbHelper, notificationMessage);
	}
	
	@Override
	protected String customizeWhere(String where) {
		where = super.customizeWhere(where, "AND");

		where += " TYPE = '" + TypeManager.getInstance().getType() + "'";
		return where;
	}

	protected int cursorToPojo(Cursor cursor, POJO pojo, int col) {
		pojo.setId(DbTool.getInstance().toLong(cursor, col++));
		return col;
	}
}