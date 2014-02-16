package com.justtennis.tool;

import android.database.Cursor;


public class DbTool {

	private static DbTool instance = null;
	
	private DbTool() {
		
	}
	
	public static DbTool getInstance() {
		if (instance==null) {
			instance = new DbTool();
		}
		return instance;
	}

	public Long toLong(Cursor cursor, int i) {
		if (cursor.isNull(i)) {
			return null;
		}
		return Long.valueOf(cursor.getLong(i));
	}

	public Integer toInteger(Cursor cursor, int i) {
		if (cursor.isNull(i)) {
			return null;
		}
		return Integer.valueOf(cursor.getString(i));
	}

	public String toString(Cursor cursor, int i) {
		return toString(cursor, i, null);
	}

	public String toString(Cursor cursor, int i, String valueIfNull) {
		if (cursor.isNull(i)) {
			return valueIfNull;
		}
		return cursor.getString(i);
	}

	public Object toOnject(Cursor cursor, int i) {
		if (!cursor.isNull(i)) {
			int type = cursor.getType(i);
			switch(type) {
				case Cursor.FIELD_TYPE_BLOB:
					return cursor.getBlob(i);
				case Cursor.FIELD_TYPE_FLOAT:
					return cursor.getFloat(i);
				case Cursor.FIELD_TYPE_INTEGER:
					return cursor.getInt(i);
				case Cursor.FIELD_TYPE_STRING:
					return cursor.getString(i);
			}
		}
		return null;
	}
}