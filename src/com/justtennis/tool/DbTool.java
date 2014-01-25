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
}
