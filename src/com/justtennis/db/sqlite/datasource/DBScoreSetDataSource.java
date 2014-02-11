package com.justtennis.db.sqlite.datasource;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.sqlite.helper.DBScoreSetHelper;
import com.justtennis.domain.Invite;
import com.justtennis.domain.ScoreSet;
import com.justtennis.tool.DbTool;

public class DBScoreSetDataSource extends GenericDBDataSource<ScoreSet> {

	private static final String TAG = DBScoreSetDataSource.class.getCanonicalName();

	// Database fields
	private String[] allColumns = {
		DBScoreSetHelper.COLUMN_ID,
		DBScoreSetHelper.COLUMN_ID_INVITE,
		DBScoreSetHelper.COLUMN_NUMBER,
		DBScoreSetHelper.COLUMN_VALUE1,
		DBScoreSetHelper.COLUMN_VALUE2
	};

	public DBScoreSetDataSource(Context context, INotifierMessage notificationMessage) {
		super(new DBScoreSetHelper(context, notificationMessage), notificationMessage);
	}

	/**
	 * Return all Set for an Invite
	 * @param idInvite Invite id
	 * @return Set list
	 */
	public List<ScoreSet> getByIdInvite(long idInvite) {
		String sqlWhere = DBScoreSetHelper.COLUMN_ID_INVITE + " = " + idInvite;
		return query(sqlWhere);
	}
	
	/**
	 * Delete all Set for an Invite
	 * @param idInvite Invite id
	 */
	public void deleteByIdInvite(long idInvite) {
		String sqlWhere = DBScoreSetHelper.COLUMN_ID_INVITE + " = " + idInvite;
		delete(sqlWhere);
	}

	@Override
	protected String[] getAllColumns() {
		return allColumns;
	}

	@Override
	protected void putContentValue(ContentValues values, ScoreSet set) {
		values.put(DBScoreSetHelper.COLUMN_ID_INVITE, set.getInvite().getId());
		values.put(DBScoreSetHelper.COLUMN_NUMBER, set.getOrder());
		values.put(DBScoreSetHelper.COLUMN_VALUE1, set.getValue1());
		values.put(DBScoreSetHelper.COLUMN_VALUE2, set.getValue2());
	}

	@Override
	protected ScoreSet cursorToPojo(Cursor cursor) {
		int col = 0;
		ScoreSet set = new ScoreSet();
		set.setId(DbTool.getInstance().toLong(cursor, col++));
		Invite invite = new Invite();
		invite.setId(DbTool.getInstance().toLong(cursor, col++));
		set.setInvite(invite);
		set.setOrder(DbTool.getInstance().toInteger(cursor, col++));
		set.setValue1(DbTool.getInstance().toInteger(cursor, col++));
		set.setValue2(DbTool.getInstance().toInteger(cursor, col++));
		return set;
	}
	
	@Override
	protected String getTag() {
		return TAG;
	}
}