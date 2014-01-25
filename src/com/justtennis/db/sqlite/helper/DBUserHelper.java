package com.justtennis.db.sqlite.helper;

import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierMessage;

public class DBUserHelper extends GenericDBHelper {

	private static final String TAG = DBUserHelper.class.getCanonicalName();

	public static final String TABLE_NAME = "USER";

	public static final String COLUMN_ID_CLUB = "ID_CLUB";
	public static final String COLUMN_ID_RANKING = "ID_RANKING";
	public static final String COLUMN_FIRSTNAME = "FIRSTNAME";
	public static final String COLUMN_LASTNAME = "LASTNAME";
	public static final String COLUMN_BIRTHDAY = "BIRTHDAY";
	public static final String COLUMN_PHONENUMBER = "PHONENUMBER";
	public static final String COLUMN_ADDRESS = "ADDRESS";
	public static final String COLUMN_POSTALCODE = "POSTALCODE";
	public static final String COLUMN_LOCALITY = "LOCALITY";

	private static final String DATABASE_NAME = "User.db";
	private static final int DATABASE_VERSION = 5;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME + "(" + 
		COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
		COLUMN_ID_CLUB + " INTEGER NULL, " + 
		COLUMN_ID_RANKING + " INTEGER NULL, " + 
		COLUMN_FIRSTNAME + " TEXT NULL, " + 
		COLUMN_LASTNAME + " TEXT NULL, " + 
		COLUMN_BIRTHDAY + " INTEGER NULL, " + 
		COLUMN_PHONENUMBER + " TEXT NULL, " + 
		COLUMN_ADDRESS + " TEXT NULL, " + 
		COLUMN_POSTALCODE + " TEXT NULL, " + 
		COLUMN_LOCALITY + " TEXT NULL " + 
	");";

	public DBUserHelper(Context context, INotifierMessage notificationMessage) {
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