package com.justtennis.db.sqlite.helper;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.sqlite.datasource.DBInviteDataSource;
import com.justtennis.db.sqlite.datasource.DBSaisonDataSource;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Saison;

public class DBSaisonHelper extends GenericDBHelper {

	private static final String TAG = DBSaisonHelper.class.getCanonicalName();

	public static final String TABLE_NAME = "SAISON";

	public static final String COLUMN_NAME = "NAME";
	public static final String COLUMN_BEGIN = "BEGIN";
	public static final String COLUMN_END = "END";
	public static final String COLUMN_ACTIVE = "ACTIVE";

	private static final String DATABASE_NAME = "Saison.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME + "(" + 
		COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
		COLUMN_NAME + " TEXT NULL, " + 
		COLUMN_BEGIN + " INTEGER NULL, " + 
		COLUMN_END + " INTEGER NULL, " + 
		COLUMN_ACTIVE + " INTEGER NULL " + 
	");";
//
//	private Context context;
//
//	private INotifierMessage notificationMessage;

	public DBSaisonHelper(Context context, INotifierMessage notificationMessage) {
		super(context, notificationMessage, DATABASE_NAME, DATABASE_VERSION);
//		this.context = context;
//		this.notificationMessage = notificationMessage;
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		super.onCreate(database);

		Saison[] saisons = feed(database);
//
//		updateInvite(database, saisons);
	}
//
//	@Override
//	public void onOpen(SQLiteDatabase db) {
//		super.onOpen(db);
//
//		logMe("onOpen");
//		DBSaisonDataSource saisonDataSource = new DBSaisonDataSource(context, notificationMessage);
//		updateInvite(db, saisonDataSource.getAll().toArray(new Saison[0]));
//	}

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

	private Saison[] feed(SQLiteDatabase database) {
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
		return rows;
	}

	private Saison feedBuild() {
		Saison ret = new Saison();

		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);

		if (month < 10) {
			year--;
		}
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.MONTH, 10);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		ret.setBegin(new Date(cal.getTimeInMillis()));

		cal.set(Calendar.DAY_OF_MONTH, 30);
		cal.set(Calendar.MONTH, 9);
		cal.set(Calendar.YEAR, year + 1);

		ret.setEnd(new Date(cal.getTimeInMillis()));

		ret.setName(year + "/" + (year + 1));

		ret.setActive(true);
		return ret;
	}
//
//	public void updateInvite(SQLiteDatabase database, Saison[] saisons) {
//		if (saisons != null && saisons.length > 1) {
//			// Just to be sure to create Invite Table before
//			List<Invite> invites = new DBInviteDataSource(context, notificationMessage).getAll();
//			if (invites != null && invites.size() > 0) {
//				Long id = null;
//				for(Saison saison : saisons) {
//					if (saison.isActive()) {
//						id = saison.getId();
//						break;
//					}
//				}
//				if (id == null) {
//					id = saisons[0].getId();
//				}
//				String sql = 
//					"UPDATE " + DBInviteHelper.TABLE_NAME + 
//					" SET " + DBInviteHelper.COLUMN_ID_SAISON + " = '" + id + "'" +
//					" WHERE " + DBInviteHelper.COLUMN_ID_SAISON + " IS NULL";
//				logMe(sql);
//				execSQL(database, sql);
//			} else {
//				logMe("NO INVITE TO UPDATE");
//			}
//		} else {
//			logMe("NO SAISON !! NO INVITE UPDATED");
//		}
//	}
}