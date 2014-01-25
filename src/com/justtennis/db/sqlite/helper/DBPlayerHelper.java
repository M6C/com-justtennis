package com.justtennis.db.sqlite.helper;

import java.io.IOException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.cameleon.common.android.inotifier.INotifierMessage;

public class DBPlayerHelper extends GenericDBHelper {

	private static final String TAG = DBPlayerHelper.class.getCanonicalName();

	public static final String TABLE_NAME = "PLAYER";

	public static final String COLUMN_ID_CLUB = "ID_CLUB";
	public static final String COLUMN_ID_RANKING = "ID_RANKING";
	public static final String COLUMN_FIRSTNAME = "FIRSTNAME";
	public static final String COLUMN_LASTNAME = "LASTNAME";
	public static final String COLUMN_BIRTHDAY = "BIRTHDAY";
	public static final String COLUMN_PHONENUMBER = "PHONENUMBER";
	public static final String COLUMN_ADDRESS = "ADDRESS";
	public static final String COLUMN_POSTALCODE = "POSTALCODE";
	public static final String COLUMN_LOCALITY = "LOCALITY";
	public static final String COLUMN_ID_EXTERNAL = "ID_EXTERNAL";
	public static final String COLUMN_ID_GOOGLE = "ID_GOOGLE";

	private static final String DATABASE_NAME = "Player.db";
	private static final int DATABASE_VERSION = 6;

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
		COLUMN_LOCALITY + " TEXT NULL, " + 
		COLUMN_ID_EXTERNAL + " INTEGER NULL, " + 
		COLUMN_ID_GOOGLE + " INTEGER NULL " + 
	");";

	public DBPlayerHelper(Context context, INotifierMessage notificationMessage) {
		super(context, notificationMessage, DATABASE_NAME, DATABASE_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		if (oldVersion==5 && newVersion>oldVersion) {
			try {
				super.backupDbToSdcard(oldVersion);
				
				database.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_ID_GOOGLE + " INTEGER NULL ");
			} catch (IOException e) {
				e.printStackTrace();
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
}