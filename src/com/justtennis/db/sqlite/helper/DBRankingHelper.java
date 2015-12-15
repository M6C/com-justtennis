package com.justtennis.db.sqlite.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.domain.Ranking;

public class DBRankingHelper extends GenericJustTennisDBHelper {

	private static final String TAG = DBRankingHelper.class.getCanonicalName();

	public static final String TABLE_NAME = "RANKING";

	public static final String COLUMN_RANKING = "RANKING";
	public static final String COLUMN_SERIE = "SERIE";
	public static final String COLUMN_POSITION = "POSITION";
	public static final String COLUMN_RANK_POINT_MAN = "RANK_POINT_MAN";
	public static final String COLUMN_RANK_POINT_WOMAN = "RANK_POINT_WOMAN";
	public static final String COLUMN_VICTORY_MAN = "RANK_VICTORY_MAN";
	public static final String COLUMN_VICTORY_WOMAN = "RANK_VICTORY_WOMAN";

	public static final String RANKING_NC = "NC";

	private static final String DATABASE_NAME = "Ranking.db";
	private static final int DATABASE_VERSION = 3;

	private static final Class<?> CLASS_TYPE = Ranking.class;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME + "(" + 
		COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
		COLUMN_RANKING + " TEXT NULL, " + 
		COLUMN_SERIE + " INTEGER NULL, " + 
		COLUMN_POSITION + " INTEGER NULL, " + 
		COLUMN_RANK_POINT_MAN + " INTEGER NULL, " + 
		COLUMN_RANK_POINT_WOMAN + " INTEGER NULL, " + 
		COLUMN_VICTORY_MAN + " INTEGER NULL, " + 
		COLUMN_VICTORY_WOMAN + " INTEGER NULL " + 
	");";

	public DBRankingHelper(Context context, INotifierMessage notificationMessage) {
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
			if (oldVersion <= 2) {
//				addColumn(database, COLUMN_RANK_POINT_MAN, " INTEGER NULL ");
//				addColumn(database, COLUMN_RANK_POINT_WOMAN, " INTEGER NULL ");
//				addColumn(database, COLUMN_VICTORY_MAN, " INTEGER NULL ");
//				addColumn(database, COLUMN_VICTORY_WOMAN, " INTEGER NULL ");
//				feed(database);
		        database.execSQL("DROP TABLE " + TABLE_NAME + ";");
		        onCreate(database);
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
			{RANKING_NC, "4", "0", "0", "0", "6", "6"},
			{"40", "4", "1", "0", "0", "6", "6"},
			{"30/5", "4", "2", "6", "6", "6", "6"},
			{"30/4", "4", "3", "70", "70", "6", "6"},
			{"30/3", "4", "4", "120", "120", "6", "6"},
			{"30/2", "4", "5", "170", "170", "6", "6"},
			{"30/1", "4", "6", "210", "210", "6", "6"},
			{"30", "3", "7", "280", "260", "8", "8"},
			{"15/5", "3", "8", "300", "290", "8", "8"},
			{"15/4", "3", "9", "310", "300", "8", "8"},
			{"15/3", "3", "10", "320", "310", "8", "8"},
			{"15/2", "3", "11", "340", "330", "8", "8"},
			{"15/1", "3", "12", "370", "350", "8", "8"},
			{"15", "2", "13", "430", "400", "9", "9"},
			{"5/6", "2", "14", "430", "400", "9", "9"},
			{"4/6", "2", "15", "430", "430", "9", "9"},
			{"3/6", "2", "16", "430", "490", "10", "10"},
			{"2/6", "2", "17", "490", "550", "10", "11"},
			{"1/6", "2", "18", "540", "600", "11", "12"}
		};

		try {
			database.beginTransaction();
			database.delete(TABLE_NAME, null, null);
			for (int row = 0 ; row < rows.length ; row++) {
				logMe("insert row:" + rows[row]);
				ContentValues values = new ContentValues();
				values.put(COLUMN_RANKING, rows[row][0]);
				values.put(COLUMN_SERIE, rows[row][1]);
				values.put(COLUMN_POSITION, rows[row][2]);
				values.put(COLUMN_RANK_POINT_MAN, rows[row][3]);
				values.put(COLUMN_RANK_POINT_WOMAN, rows[row][4]);
				values.put(COLUMN_VICTORY_MAN, rows[row][5]);
				values.put(COLUMN_VICTORY_WOMAN, rows[row][6]);
	
				database.insert(TABLE_NAME, null, values);
			}
			database.setTransactionSuccessful();
		}
		finally {
			database.endTransaction();
		}
	}
}