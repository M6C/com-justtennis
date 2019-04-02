package com.justtennis.db.sqlite.datasource;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.cameleon.common.android.db.sqlite.datasource.GenericDBDataSource;
import com.cameleon.common.android.inotifier.INotifierMessage;
import com.cameleon.common.tool.DbTool;
import com.justtennis.db.sqlite.helper.DBBonusHelper;
import com.justtennis.domain.Bonus;

public class DBBonusDataSource extends GenericDBDataSource<Bonus> {

	private static final String TAG = DBBonusDataSource.class.getCanonicalName();

	// Database fields
	private String[] allColumns = {
		DBBonusHelper.COLUMN_ID,
		DBBonusHelper.COLUMN_POINT
	};

	public DBBonusDataSource(Context context, INotifierMessage notificationMessage) {
		super(new DBBonusHelper(context, notificationMessage), notificationMessage);
	}

	/**
	 * Return all Ranking
	 * @return Ranking list
	 */
	public List<Bonus> getAll() {
		return query(null);
	}

	@Override
	public String[] getAllColumns() {
		return allColumns;
	}

	@Override
	protected void putContentValue(ContentValues values, Bonus bonus) {
		values.put(DBBonusHelper.COLUMN_POINT, bonus.getPoint());
	}

	@Override
	protected Bonus cursorToPojo(Cursor cursor) {
		int col = 0;
		Bonus pojo = new Bonus();
		pojo.setId(DbTool.getInstance().toLong(cursor, col++));
		pojo.setPoint(DbTool.getInstance().toInteger(cursor, col++));
		return pojo;
	}
	
	@Override
	protected String getTag() {
		return TAG;
	}
}