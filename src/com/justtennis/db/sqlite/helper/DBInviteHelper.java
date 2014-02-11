package com.justtennis.db.sqlite.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.cameleon.common.android.inotifier.INotifierMessage;

public class DBInviteHelper extends GenericDBHelper {

	private static final String TAG = DBInviteHelper.class.getCanonicalName();

	public static final String TABLE_NAME = "INVITE";

	public static final String COLUMN_ID_PLAYER = "ID_PLAYER";
	public static final String COLUMN_TIME = "TIME";
	public static final String COLUMN_STATUS = "STATUS";
	public static final String COLUMN_TYPE = "TYPE";
	public static final String COLUMN_ID_EXTERNAL = "ID_EXTERNAL";
	public static final String COLUMN_ID_CALENDAR = "ID_CALENDAR";
	public static final String COLUMN_ID_RANKING = "ID_RANKING";

	public static final String DATABASE_NAME = "Invite.db";
	public static final int DATABASE_VERSION = 8;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME + "(" + 
		COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
		COLUMN_ID_PLAYER + " INTEGER NULL, " + 
		COLUMN_TIME + " INTEGER NULL, " + 
		COLUMN_STATUS + " INTEGER NULL, " + 
		COLUMN_TYPE + " INTEGER NULL, " + 
		COLUMN_ID_EXTERNAL + " INTEGER NULL, " + 
		COLUMN_ID_CALENDAR + " INTEGER NULL, " + 
		COLUMN_ID_RANKING + " INTEGER NULL " + 
	");";

	public DBInviteHelper(Context context, INotifierMessage notificationMessage) {
		super(context, notificationMessage, DATABASE_NAME, DATABASE_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		if (newVersion>oldVersion) {
			logMe("UPGRADE DATABASE VERSION:" + oldVersion + " TO " + newVersion);
			if (oldVersion==5) {
				addColumn(database, COLUMN_ID_CALENDAR, "INTEGER NULL");
			}
			else if (oldVersion==6) {
				addColumn(database, COLUMN_TYPE, "INTEGER NULL");
			}else if (oldVersion==7) {
				addColumn(database, COLUMN_ID_RANKING, "INTEGER NULL");
			}
		}
		else {
			super.onUpgrade(database, oldVersion, newVersion);
		}
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

	private void addColumn(SQLiteDatabase database, String column, String type) {
		logMe("UPGRADE DATABASE TABLE " + TABLE_NAME + " ADD COLUMN:" + column + " BEFORE");
		database.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + column + " " + type);
		logMe("UPGRADE DATABASE TABLE " + TABLE_NAME + " ADD COLUMN:" + column + " AFTER");
	}
}