package com.justtennis.db.sqlite.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.sqlite.helper.DBTournamentHelper;
import com.justtennis.domain.Tournament;
import com.justtennis.tool.DbTool;

public class DBTournamentDataSource extends GenericDBDataSource<Tournament> {

	private static final String TAG = DBTournamentDataSource.class.getCanonicalName();

	// Database fields
	private String[] allColumns = {
		DBTournamentHelper.COLUMN_ID,
		DBTournamentHelper.COLUMN_NAME,
		DBTournamentHelper.COLUMN_ID_CLUB
	};

	public DBTournamentDataSource(Context context, INotifierMessage notificationMessage) {
		super(new DBTournamentHelper(context, notificationMessage), notificationMessage);
	}

	@Override
	protected String[] getAllColumns() {
		return allColumns;
	}

	@Override
	protected void putContentValue(ContentValues values, Tournament tournament) {
		values.put(DBTournamentHelper.COLUMN_NAME, tournament.getName());
		values.put(DBTournamentHelper.COLUMN_ID_CLUB, tournament.getSubId());
	}

	@Override
	protected Tournament cursorToPojo(Cursor cursor) {
		int col = 0;
		Tournament tournament = new Tournament();
		tournament.setId(DbTool.getInstance().toLong(cursor, col++));
		tournament.setName(cursor.getString(col++));
		tournament.setSubId(DbTool.getInstance().toLong(cursor, col++));
		return tournament;
	}
	
	@Override
	protected String getTag() {
		return TAG;
	}
}