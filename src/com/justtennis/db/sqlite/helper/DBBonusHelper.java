package com.justtennis.db.sqlite.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.domain.Bonus;

public class DBBonusHelper extends GenericJustTennisDBHelper {

	private static final String TAG = DBBonusHelper.class.getCanonicalName();

	public static final String TABLE_NAME = "BONUS";

	public static final String COLUMN_POINT = "POINT";

	private static final String DATABASE_NAME = "Bonus.db";
	private static final int DATABASE_VERSION = 1;

	private static final Class<?> CLASS_TYPE = Bonus.class;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME + "(" + 
		COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
		COLUMN_POINT + " INTEGER NULL " + 
	");";

	public DBBonusHelper(Context context, INotifierMessage notificationMessage) {
		super(context, notificationMessage, DATABASE_NAME, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		super.onCreate(database);

		feed(database);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		if (newVersion>oldVersion) {
			logMe("UPGRADE DATABASE VERSION:" + oldVersion + " TO " + newVersion);
			if (oldVersion <= 1) {
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

	@Override
	public Class<?> getClassType() {
		return CLASS_TYPE;
	}
	
	private void feed(SQLiteDatabase database) {
		String[][] rows = new String[][] {
			{"0"},
			{"15"}
		};

		try {
			database.beginTransaction();
			database.delete(TABLE_NAME, null, null);
			for (int row = 0 ; row < rows.length ; row++) {
				logMe("insert row:" + rows[row]);
				ContentValues values = new ContentValues();
				values.put(COLUMN_POINT, rows[row][0]);
	
				database.insert(TABLE_NAME, null, values);
			}
			database.setTransactionSuccessful();
		}
		finally {
			database.endTransaction();
		}
	}
}