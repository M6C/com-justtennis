package com.justtennis.db.sqlite.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.domain.Saison;

public class DBSaisonHelper extends GenericJustTennisDBHelper {

	private static final String TAG = DBSaisonHelper.class.getCanonicalName();

	public static final String TABLE_NAME = "SAISON";

	public static final String COLUMN_NAME = "NAME";
	public static final String COLUMN_BEGIN = "BEGIN";
	public static final String COLUMN_END = "END";
	public static final String COLUMN_ACTIVE = "ACTIVE";

	private static final String DATABASE_NAME = "Saison.db";
	private static final int DATABASE_VERSION = 1;

	private static final Class<?> CLASS_TYPE = Saison.class;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME + "(" + 
		COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
		COLUMN_NAME + " TEXT NULL, " + 
		COLUMN_BEGIN + " INTEGER NULL, " + 
		COLUMN_END + " INTEGER NULL, " + 
		COLUMN_ACTIVE + " INTEGER NULL " + 
	");";

	public DBSaisonHelper(Context context, INotifierMessage notificationMessage) {
		super(context, notificationMessage, DATABASE_NAME, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		super.onCreate(database);
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