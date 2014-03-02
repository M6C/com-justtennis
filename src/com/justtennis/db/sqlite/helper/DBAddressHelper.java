package com.justtennis.db.sqlite.helper;

import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierMessage;

public class DBAddressHelper extends GenericDBHelper {

	private static final String TAG = DBAddressHelper.class.getCanonicalName();

	public static final String TABLE_NAME = "ADDRESS";

	public static final String COLUMN_NAME = "NAME";
	public static final String COLUMN_LINE1 = "LINE1";
	public static final String COLUMN_POSTAL_CODE = "POSTAL_CODE";
	public static final String COLUMN_CITY = "CITY";
	public static final String COLUMN_GPS = "GPS";

	private static final String DATABASE_NAME = "Address.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME + "(" + 
		COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
		COLUMN_NAME + " TEXT NULL, " + 
		COLUMN_LINE1 + " TEXT NULL, " + 
		COLUMN_POSTAL_CODE + " TEXT NULL, " + 
		COLUMN_CITY + " TEXT NULL, " + 
		COLUMN_GPS + " TEXT NULL " + 
	");";

	public DBAddressHelper(Context context, INotifierMessage notificationMessage) {
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