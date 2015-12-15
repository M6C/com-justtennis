package com.justtennis.db.sqlite.helper;

import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.service.SaisonService;
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

		feed(database);
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
		Saison[] rows = new Saison[] {
			feedBuild()
		};

		try {
			database.beginTransaction();
			database.delete(TABLE_NAME, null, null);
			for (int row = 0 ; row < rows.length ; row++) {
				Saison saison = rows[row];
				logMe("insert row:" + saison);
				ContentValues values = new ContentValues();
				values.put(COLUMN_NAME, saison.getName());
				values.put(COLUMN_BEGIN, saison.getBegin().getTime());
				values.put(COLUMN_END, saison.getEnd().getTime());
				values.put(COLUMN_ACTIVE, saison.isActive() ? 1 : 0);
	
				long id = database.insert(TABLE_NAME, null, values);
				saison.setId(id);
			}
			database.setTransactionSuccessful();
		}
		finally {
			database.endTransaction();
		}
	}

	private Saison feedBuild() {
		return SaisonService.build(Calendar.getInstance());
	}
}