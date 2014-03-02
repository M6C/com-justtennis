package com.justtennis.db.sqlite.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.sqlite.helper.DBClubHelper;
import com.justtennis.domain.Club;
import com.justtennis.tool.DbTool;

public class DBClubDataSource extends GenericDBDataSource<Club> {

	private static final String TAG = DBClubDataSource.class.getCanonicalName();

	// Database fields
	private String[] allColumns = {
		DBClubHelper.COLUMN_ID,
		DBClubHelper.COLUMN_NAME,
		DBClubHelper.COLUMN_ID_ADDRESS
	};

	public DBClubDataSource(Context context, INotifierMessage notificationMessage) {
		super(new DBClubHelper(context, notificationMessage), notificationMessage);
	}

	@Override
	protected String[] getAllColumns() {
		return allColumns;
	}

	@Override
	protected void putContentValue(ContentValues values, Club club) {
		values.put(DBClubHelper.COLUMN_NAME, club.getName());
		values.put(DBClubHelper.COLUMN_ID_ADDRESS, club.getIdAddress());
	}

	@Override
	protected Club cursorToPojo(Cursor cursor) {
		int col = 0;
		Club club = new Club();
		club.setId(DbTool.getInstance().toLong(cursor, col++));
		club.setName(cursor.getString(col++));
		club.setIdAddress(DbTool.getInstance().toLong(cursor, col++));
		return club;
	}
	
	@Override
	protected String getTag() {
		return TAG;
	}
}