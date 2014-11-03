package com.justtennis.db.sqlite.datasource;

import java.util.Date;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.sqlite.helper.DBSaisonHelper;
import com.justtennis.domain.Saison;
import com.justtennis.tool.DbTool;

public class DBSaisonDataSource extends GenericDBDataSource<Saison> {

	private static final String TAG = DBSaisonDataSource.class.getCanonicalName();

	// Database fields
	private String[] allColumns = {
		DBSaisonHelper.COLUMN_ID,
		DBSaisonHelper.COLUMN_NAME,
		DBSaisonHelper.COLUMN_BEGIN,
		DBSaisonHelper.COLUMN_END,
		DBSaisonHelper.COLUMN_ACTIVE
	};

	public DBSaisonDataSource(Context context, INotifierMessage notificationMessage) {
		super(new DBSaisonHelper(context, notificationMessage), notificationMessage);
//		SQLiteDatabase database = ((DBSaisonHelper)dbHelper).getWritableDatabase();
//		((DBSaisonHelper)dbHelper).updateInvite(database, getAll().toArray(new Saison[0]));
	}

	@Override
	protected String[] getAllColumns() {
		return allColumns;
	}

	@Override
	protected void putContentValue(ContentValues values, Saison saison) {
		values.put(DBSaisonHelper.COLUMN_NAME, saison.getName());
		values.put(DBSaisonHelper.COLUMN_BEGIN, saison.getBegin() == null ? 0 : saison.getBegin().getTime());
		values.put(DBSaisonHelper.COLUMN_END, saison.getEnd() == null ? 0 : saison.getEnd().getTime());
		values.put(DBSaisonHelper.COLUMN_ACTIVE, saison.isActive() ? 1 : 0);
	}

	@Override
	protected Saison cursorToPojo(Cursor cursor) {
		int col = 0;
		Saison saison = new Saison();
		saison.setId(DbTool.getInstance().toLong(cursor, col++));
		saison.setName(cursor.getString(col++));
		String begin = cursor.getString(col++);
		saison.setBegin(begin==null || "null".equals(begin.toLowerCase(Locale.FRANCE)) ? null : new Date(Long.parseLong(begin)));
		String end = cursor.getString(col++);
		saison.setEnd(begin==null || "null".equals(end.toLowerCase(Locale.FRANCE)) ? null : new Date(Long.parseLong(end)));
		saison.setActive(cursor.getInt(col++) == 1);
		return saison;
	}
	
	@Override
	protected String getTag() {
		return TAG;
	}
}