package com.cameleon.common.android.db.sqlite.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.cameleon.common.android.inotifier.INotifierMessage;

public abstract class GenericDBHelper extends DBAbstractHelper {

	public static final String COLUMN_ID = "_id";

	public GenericDBHelper(Context context, INotifierMessage notificationMessage, String databaseName, int databaseVersion) {
		super(context, databaseName, null, databaseVersion, notificationMessage);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		String sql = getDatabaseCreate();
		if (notificationMessage != null) {
			notificationMessage.notifyMessage(String.format("create database:%s sql:%s", database.getPath(), sql));
		}
		database.execSQL(sql);
	}
	
//	@Override
//	public void backupDbToSdcard() throws IOException {
//		super.backupDbToSdcard(databaseVersion);
//	}

	@Override
	public abstract String getTableName();
	
	public abstract String getDatabaseCreate();

	protected void addColumn(SQLiteDatabase database, String column, String type) {
		addColumn(database, column, type, null);
	}
	protected void addColumn(SQLiteDatabase database, String column, String type, String defaultValue) {
		logMe(String.format("UPGRADE DATABASE TABLE %s ADD COLUMN:%s BEFORE", getTableName(), column));
		execSQL(database, String.format("ALTER TABLE %s ADD COLUMN %s %s", getTableName(), column, type));

		if (defaultValue != null) {
			updateColumn(database, column, defaultValue, null);
		}
		logMe(String.format("UPGRADE DATABASE TABLE %s ADD COLUMN:%s AFTER", getTableName(), column));
	}

	protected void updateColumn(SQLiteDatabase database, String column, String value, String where) {
		logMe(String.format("UPDATE TABLE %s COLUMN:%s WITH VALUE:%s WHERE:%s", getTableName(), column, value, where));
		String sql = String.format("UPDATE %s SET %s = '%s'", getTableName(), column, value);
		if (where != null) {
			sql += String.format(" WHERE %s", where);
		}
		execSQL(database, sql);
	}

	protected void execSQL(SQLiteDatabase database, String sql) {
		logMe(String.format("execSQL: '%s' BEFORE", sql));
		database.execSQL(sql);
		logMe(String.format("execSQL: '%s' AFTER", sql));
	}

	/**
	 *  Create Other Table in same Database
	 *  @param database
	 */
	protected void createOtherTable(SQLiteDatabase database, GenericDBHelper[] allHelper) {
		for(GenericDBHelper helper : allHelper) {
			if (helper.getDatabaseName().equals(getDatabaseName()) && !helper.getTableName().equals(getTableName())) {
		        database.execSQL(helper.getDatabaseCreate());
			}
		}
	}
}
