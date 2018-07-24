package com.cameleon.common.android.mapper;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

import com.cameleon.common.android.model.GenericDBPojo;

public abstract class GenericMapper<T extends GenericDBPojo<Long>> {

	private static final String TAG = GenericMapper.class.getName();

	public List<T> mappe(Cursor cursor) {
		List<T> ret = new ArrayList<T>();

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	      T obj = cursorToPojo(cursor);
	      ret.add(obj);
	      cursor.moveToNext();
	    }
		return ret;
	}

	protected abstract T cursorToPojo(Cursor cursor);
	public abstract String[] getListColumn();

	protected String mappeString(Cursor cursor, int columnIndex) {
		logMapper("mappeBoolean columnName:", Integer.toString(columnIndex));
		return cursor.getString(columnIndex);
	}

	protected long mappeLong(Cursor cursor, String columnName) {
		logMapper("mappeLong columnName:", columnName);
		int idx = getColumnIndex(cursor, columnName);
		return (idx==-1) ? null : cursor.getLong(idx);
	}
	
	protected int mappeInt(Cursor cursor, String columnName) {
		logMapper("mappeLong columnName:", columnName);
		int idx = getColumnIndex(cursor, columnName);
		return (idx==-1) ? null : cursor.getInt(idx);
	}
	
	protected boolean mappeBoolean(Cursor cursor, String columnName) {
		logMapper("mappeBoolean columnName:", columnName);
		int idx = getColumnIndex(cursor, columnName);
		return (idx==-1) ? null : "1".equals(cursor.getString(idx));
	}

	protected String mappeString(Cursor cursor, String columnName) {
		logMapper("mappeString columnName:", columnName);
		int idx = getColumnIndex(cursor, columnName);
		return (idx==-1) ? null : cursor.getString(idx);
	}

	protected int getColumnIndex(Cursor cursor, String columnName) {
		int idx = cursor.getColumnIndex(columnName);
		while (idx==-1) {
			String name = getAlternatifColumnNameWhenNoExist(columnName);
			if (name!=null) {
				logMapper("getColumnIndex", "Use Alternative ["+name+"] for ["+columnName+"]");
				idx = cursor.getColumnIndex(name);
			} else {
				logMapper("getColumnIndex", "No Alternative for ["+columnName+"]");
				break;
			}
		}
		return idx;
	}
	
	protected String getAlternatifColumnNameWhenNoExist(String columnName) {
		return null;
	}

	protected boolean showLogTrace() {
		return false;
	}

	protected boolean showLogPojo() {
		return false;
	}

	protected String tagLogTrace() {
		return TAG;
	}

	protected void logMapper(String title, String data) {
		if (showLogTrace()) {
			logMe("SHOW_LOG_TRACE "+tagLogTrace()+" "+title+data);
		}
	}

	protected void logPojo(T pojo) {
		if (showLogPojo()) {
			logMe(pojo.toString());
		}
	}

	protected void logMe(String message) {
		com.crashlytics.android.Crashlytics.log(message);
		System.out.println(message);
	}
}