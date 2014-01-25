package com.justtennis.db.sqlite.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.cameleon.common.android.inotifier.INotifierMessage;

public class DBRankingHelper extends GenericDBHelper {

	private static final String TAG = DBRankingHelper.class.getCanonicalName();

	public static final String TABLE_NAME = "RANKING";

	public static final String COLUMN_RANKING = "RANKING";
	public static final String COLUMN_SERIE = "SERIE";
	public static final String COLUMN_POSITION = "POSITION";

	private static final String DATABASE_NAME = "Ranking.db";
	private static final int DATABASE_VERSION = 2;


	// Database creation sql statement
	private static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME + "(" + 
		COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
		COLUMN_RANKING + " TEXT NULL, " + 
		COLUMN_SERIE + " INTEGER NULL, " + 
		COLUMN_POSITION + " INTEGER NULL " + 
	");";

	public DBRankingHelper(Context context, INotifierMessage notificationMessage) {
		super(context, notificationMessage, DATABASE_NAME, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		super.onCreate(database);

		String[][] rows = new String[][] {
			{"NC", "4", "0"},
			{"40", "4", "1"},
			{"30/5", "4", "2"},
			{"30/4", "4", "3"},
			{"30/3", "4", "4"},
			{"30/2", "4", "5"},
			{"30/1", "4", "6"},
			{"30", "3", "7"},
			{"15/5", "3", "8"},
			{"15/4", "3", "9"},
			{"15/3", "3", "10"},
			{"15/2", "3", "11"},
			{"15/1", "3", "12"},
			{"15", "2", "13"},
			{"5/6", "2", "14"},
			{"4/6", "2", "15"},
			{"3/6", "2", "16"},
			{"2/6", "2", "17"},
			{"1/6", "2", "18"}
		};

		try {
			database.beginTransaction();
			for (int row = 0 ; row < rows.length ; row++) {
				logMe("insert row:" + rows[row]);
				ContentValues values = new ContentValues();
				values.put(COLUMN_RANKING, rows[row][0]);
				values.put(COLUMN_SERIE, rows[row][1]);
				values.put(COLUMN_POSITION, rows[row][2]);
	
				database.insert(TABLE_NAME, null, values);
			}
			database.setTransactionSuccessful();
		}
		finally {
			database.endTransaction();
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