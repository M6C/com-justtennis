package com.justtennis.db.sqlite.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.sqlite.helper.DBTournamentHelper;
import com.justtennis.domain.Saison;
import com.justtennis.domain.Tournament;
import com.justtennis.tool.DbTool;

public class DBTournamentDataSource extends GenericDBDataSource<Tournament> {

	private static final String TAG = DBTournamentDataSource.class.getCanonicalName();

	// Database fields
	private String[] allColumns = {
		DBTournamentHelper.COLUMN_ID,
		DBTournamentHelper.COLUMN_NAME,
		DBTournamentHelper.COLUMN_ID_CLUB,
		DBTournamentHelper.COLUMN_ID_SAISON
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
		values.put(DBTournamentHelper.COLUMN_ID_SAISON, tournament.getSaison()==null ? null : tournament.getSaison().getId());
	}

	@Override
	protected Tournament cursorToPojo(Cursor cursor) {
		int col = 0;
		Tournament tournament = new Tournament();
		tournament.setId(DbTool.getInstance().toLong(cursor, col++));
		tournament.setName(cursor.getString(col++));
		tournament.setSubId(DbTool.getInstance().toLong(cursor, col++));
		Long idSaison = DbTool.getInstance().toLong(cursor, col++);
		tournament.setSaison(idSaison==null ? null : new Saison(idSaison));
		return tournament;
	}
	
	@Override
	protected String getTag() {
		return TAG;
	}
}