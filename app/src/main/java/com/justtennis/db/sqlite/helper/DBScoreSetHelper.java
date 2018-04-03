package com.justtennis.db.sqlite.helper;

import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.domain.ScoreSet;

public class DBScoreSetHelper extends GenericJustTennisDBHelper {

	private static final String TAG = DBScoreSetHelper.class.getCanonicalName();

	public static final String TABLE_NAME = "SCORE_SET";

	public static final String COLUMN_ID_INVITE = "ID_INVITE";
	public static final String COLUMN_NUMBER = "NUMBER";
	public static final String COLUMN_VALUE1 = "VALUE1";
	public static final String COLUMN_VALUE2 = "VALUE2";

	private static final String DATABASE_NAME = DBInviteHelper.DATABASE_NAME;
	private static final int DATABASE_VERSION = DBInviteHelper.DATABASE_VERSION;

	private static final Class<?> CLASS_TYPE = ScoreSet.class;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME + "(" + 
		COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
		COLUMN_ID_INVITE + " INTEGER NULL, " + 
		COLUMN_NUMBER + " INTEGER NULL, " + 
		COLUMN_VALUE1 + " INTEGER NULL, " + 
		COLUMN_VALUE2 + " INTEGER NULL " + 
	");";

	public DBScoreSetHelper(Context context, INotifierMessage notificationMessage) {
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