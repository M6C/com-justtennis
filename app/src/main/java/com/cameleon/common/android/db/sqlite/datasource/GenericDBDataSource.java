package com.cameleon.common.android.db.sqlite.datasource;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.cameleon.common.android.db.sqlite.helper.GenericDBHelper;
import com.cameleon.common.android.inotifier.INotifierMessage;
import com.cameleon.common.android.model.GenericDBPojo;
import com.cameleon.common.tool.DbTool;

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
		String id = pojo.getId().toString();
		logMe("Localisation deleted with id: " + id);
		delete(GenericDBHelper.COLUMN_ID + " = " + id);
	}

	public void delete(String sqlWhere) {
		Date dateStart = new Date();
		logMe("Localisation deleted where: " + sqlWhere);
		db.delete(dbHelper.getTableName(), sqlWhere, null);
		logMe("pojo.where:" + sqlWhere, dateStart);
	}

	public POJO getById(long id) {
		Date dateStart = new Date();
		POJO ret = null;
		Cursor cursor = db.query(dbHelper.getTableName(),
				getAllColumns(), GenericDBHelper.COLUMN_ID + " = " + id, null,
				null, null, null);
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			ret = cursorToPojo(cursor);
		}
		cursor.close();
		logMe("getById(id:" + id + ")", dateStart);
		return ret;
	}

	public List<POJO> getInId(String[] id) {
		Date dateStart = new Date();
		List<POJO> pojos = new ArrayList<POJO>();

		String sqlWhere = buildInQuery(GenericDBHelper.COLUMN_ID, id.length);

		Cursor cursor = db.query(dbHelper.getTableName(),
				getAllColumns(), sqlWhere, id, null, null, null);

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
	
	public List<POJO> getAll() {
		Date dateStart = new Date();
		List<POJO> pojos = new ArrayList<POJO>();
		
		String where = customizeWhere(null);
		Cursor cursor = db.query(dbHelper.getTableName(), getAllColumns(), where, null, null, null, null);
		
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
		String where = customizeWhere(null);
		Cursor mCount= db.rawQuery("select count(*) from "+dbHelper.getTableName() + " WHERE " + where, null);
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

	public static String buildInQuery(String columnName, int size) {
		if (size<=0)
			return null;

		String ret = columnName + " IN (";
		boolean first = true;
		for(int i=0 ; i<size ; i++) {
			if (!first) {
				ret += ",";
			}
			ret += "?";
			first = false;
		}
		ret += ")";

		return ret;
	}

	protected List<POJO> query(String sqlWhere) {
		return query(sqlWhere, null);
	}
	
	protected List<POJO> query(String sqlWhere, String[] params) {
		Date dateStart = new Date();
		List<POJO> ret = new ArrayList<POJO>();
		
		Cursor cursor = db.query(dbHelper.getTableName(), getAllColumns(), 
				customizeWhere(sqlWhere), params, null, null, null);
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			ret.add(cursorToPojo(cursor));
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		
		logMe("query(where:" + sqlWhere + ", params:" + params + ")", dateStart);
		return ret;
	}

	protected List<HashMap<String, Object>> rawQuery(String sql) {
		Date dateStart = new Date();
		List<HashMap<String, Object>> ret = new ArrayList<HashMap<String, Object>>();

		Cursor cursor = db.rawQuery(sql, null);
		String[] columnName = cursor.getColumnNames();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			HashMap<String, Object> row = new HashMap<String, Object>();
			for(int i = 0 ; i < columnName.length ; i++) {
				String name = columnName[i];
				row.put(name, DbTool.getInstance().toOnject(cursor, i));
			}
			ret.add(row);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();

		logMe("query(sql:" + sql + ")", dateStart);
		return ret;
	}

	protected String customizeWhere(String where) {
		return (where == null) ? "" : where;
	}

	protected String customizeWhere(String where, String close) {
		where = (where == null) ? "" : where;
		if (!where.equals("")) {
//			String WHERE_UP = " WHERE ";
//			String whereUp = where.toUpperCase(Locale.FRANCE);
//			int idx = whereUp.indexOf(WHERE_UP);
//			if (idx >= 0) {
//				where += " " + close + " ";
//			}
			where += " " + close + " ";
		}
		return where;
	}

	protected void logMe(String msg, Date dateStart) {
		logMe("DB Execution time:" + (new Date().getTime() - dateStart.getTime()) + "millisecond - " + msg);
    }

	protected void logMe(String msg) {
//		Logger.logMe(getTag(), msg);
    }
}