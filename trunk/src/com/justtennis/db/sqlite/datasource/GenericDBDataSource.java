package com.justtennis.db.sqlite.datasource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.gdocument.gtracergps.launcher.log.Logger;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.sqlite.helper.DBPlayerHelper;
import com.justtennis.db.sqlite.helper.GenericDBHelper;
import com.justtennis.domain.GenericDBPojo;

public abstract class GenericDBDataSource<POJO extends GenericDBPojo<Long>> {

	protected SQLiteDatabase db;
	protected GenericDBHelper dbHelper;

	private INotifierMessage notificationMessage;

	public GenericDBDataSource(GenericDBHelper dbHelper, INotifierMessage notificationMessage) {
		this.notificationMessage = notificationMessage;
		this.dbHelper = dbHelper;
	}

	protected abstract String[] getAllColumns();
	protected abstract void putContentValue(ContentValues contentValue, POJO pojo);
	protected abstract POJO cursorToPojo(Cursor cursor);
	protected abstract String getTag();
	
	public void open() throws SQLException {
		db = dbHelper.getWritableDatabase();
	}

	public void close() {
		/**
		 * Close Location DB
		 */
		try {
			dbHelper.close();
		}
		catch(Exception ex) {
			notificationMessage.notifyError(ex);
		}
	}

	public void recreateTable() {
		dbHelper.recreateTable(db);
	}

	public POJO create(POJO pojo) {
		Date dateStart = new Date();
		ContentValues values = new ContentValues();

//		values.put(GenericDBHelper.COLUMN_ID, pojo.getId().toString());

		putContentValue(values, pojo);

		pojo.setId(db.insert(dbHelper.getTableName(), null, values));

		logMe("create(pojo.id:" + pojo.getId() + ")", dateStart);
		return pojo;
	}

	public void update (POJO pojo) {
		Date dateStart = new Date();
		ContentValues values = new ContentValues();

//		values.put(GenericDBHelper.COLUMN_ID, pojo.getId().toString());

		putContentValue(values, pojo);

		db.update(dbHelper.getTableName(), values, GenericDBHelper.COLUMN_ID+"=?", new String[]{pojo.getId().toString()});

		logMe("update(pojo.id:" + pojo.getId() + ")", dateStart);
	}

	public void delete(POJO pojo) {
		Date dateStart = new Date();
		String id = pojo.getId().toString();
		logMe("Localisation deleted with id: " + id);
		db.delete(dbHelper.getTableName(), GenericDBHelper.COLUMN_ID + " = " + id, null);
		logMe("pojo.id:" + pojo.getId(), dateStart);
	}

	public POJO getById(long id) {
		Date dateStart = new Date();
		POJO ret = null;
		Cursor cursor = db.query(dbHelper.getTableName(),
				getAllColumns(), DBPlayerHelper.COLUMN_ID + " = " + id, null,
				null, null, null);
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			ret = cursorToPojo(cursor);
		}
		cursor.close();
		logMe("getById(id:" + id + ")", dateStart);
		return ret;
	}

	public List<POJO> getAll() {
		Date dateStart = new Date();
		List<POJO> pojos = new ArrayList<POJO>();

		Cursor cursor = db.query(dbHelper.getTableName(),
				getAllColumns(), null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			POJO pojo = cursorToPojo(cursor);
			pojos.add(pojo);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();

		logMe("getAll()", dateStart);
		return pojos;
	}

	/**
	 * Return table count
	 * @return Table count
	 */
	public long count() {
		Cursor mCount= db.rawQuery("select count(*) from "+dbHelper.getTableName(), null);
		mCount.moveToFirst();
		long ret = mCount.getLong(0);
		mCount.close();
		return ret;
	}

	public void backupDbToSdcard() {
		try {
			dbHelper.backupDbToSdcard();
	    } catch (Exception ex) {
	    	notificationMessage.notifyError(ex);
	    }
	}

	public void restoreDbFromSdcard() {
		try {
			dbHelper.restoreDbFromSdcard();
	    } catch (Exception ex) {
	    	notificationMessage.notifyError(ex);
	    }
	}

	protected List<POJO> query(String sqlWhere) {
		Date dateStart = new Date();
		List<POJO> ret = new ArrayList<POJO>();

		Cursor cursor = db.query(dbHelper.getTableName(), getAllColumns(), 
				sqlWhere, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			ret.add(cursorToPojo(cursor));
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();

		logMe("query(where:" + sqlWhere + ")", dateStart);
		return ret;
	}

	protected void logMe(String msg, Date dateStart) {
		logMe("DB Execution time:" + (new Date().getTime() - dateStart.getTime()) + "millisecond - " + msg);
    }

	private void logMe(String msg) {
		Logger.logMe(getTag(), msg);
    }
}