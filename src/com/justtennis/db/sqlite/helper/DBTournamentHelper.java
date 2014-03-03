package com.justtennis.db.sqlite.helper;

import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierMessage;

public class DBTournamentHelper extends GenericDBHelper {

	private static final String TAG = DBTournamentHelper.class.getCanonicalName();

	public static final String TABLE_NAME = "TOURNAMENT";

	public static final String COLUMN_NAME = "NAME";
	public static final String COLUMN_ID_CLUB = "ID_CLUB";

	private static final String DATABASE_NAME = "Tournament.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME + "(" + 
		COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
		COLUMN_NAME + " TEXT, " + 
		COLUMN_ID_CLUB + " INTEGER NULL " + 
	");";

	public DBTournamentHelper(Context context, INotifierMessage notificationMessage) {
		super(context, notificationMessage, DATABASE_NAME, DATABASE_VERSION);
	}

	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	@Override
	public String getDatabaseCreate() {
		return DATABASE_CREATE;
	}
}