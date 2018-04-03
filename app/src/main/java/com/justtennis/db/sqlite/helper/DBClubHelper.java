package com.justtennis.db.sqlite.helper;

import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.domain.Club;

public class DBClubHelper extends GenericJustTennisDBHelper {

	private static final String TAG = DBClubHelper.class.getCanonicalName();

	public static final String TABLE_NAME = "CLUB";

	public static final String COLUMN_NAME = "NAME";
	public static final String COLUMN_ID_ADDRESS = "ID_ADDRESS";

	private static final String DATABASE_NAME = "Club.db";
	private static final int DATABASE_VERSION = 1;

	private static final Class<?> CLASS_TYPE = Club.class;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME + "(" + 
		COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
		COLUMN_NAME + " TEXT, " + 
		COLUMN_ID_ADDRESS + " INTEGER NULL " + 
	");";

	public DBClubHelper(Context context, INotifierMessage notificationMessage) {
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
	
	@Override
	public Class<?> getClassType() {
		return CLASS_TYPE;
	}
}