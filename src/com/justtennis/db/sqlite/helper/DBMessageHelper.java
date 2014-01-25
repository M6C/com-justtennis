package com.justtennis.db.sqlite.helper;

import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierMessage;

public class DBMessageHelper extends GenericDBHelper {

	private static final String TAG = DBMessageHelper.class.getCanonicalName();

	public static final String TABLE_NAME = "MESSAGE";

	public static final String COLUMN_MESSAGE = "MESSAGE";
	public static final String COLUMN_ID_PLAYER = "ID_PLAYER";

	private static final String DATABASE_NAME = "Message.db";
	private static final int DATABASE_VERSION = 5;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME + "(" + 
		COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
		COLUMN_MESSAGE + " TEXT, " + 
		COLUMN_ID_PLAYER + " INTEGER NULL " + 
	");";

	public DBMessageHelper(Context context, INotifierMessage notificationMessage) {
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