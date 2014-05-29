package com.justtennis.db.sqlite.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.cameleon.common.android.db.sqlite.helper.DBAbstractHelper;
import com.cameleon.common.android.inotifier.INotifierMessage;

public abstract class GenericDBHelper extends DBAbstractHelper {

	private static final String PACKAGE_NAME = "com.justtennis";
	public static final String COLUMN_ID = "_id";

	public GenericDBHelper(Context context, INotifierMessage notificationMessage, String databaseName, int databaseVersion) {
		super(context, databaseName, null, databaseVersion, notificationMessage);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
        notificationMessage.notifyMessage("create database:" + database.getPath());

        database.execSQL(getDatabaseCreate());
	}
	
	@Override
	protected String getPackagename() {
		return PACKAGE_NAME;
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
		logMe("UPGRADE DATABASE TABLE " + getTableName() + " ADD COLUMN:" + column + " BEFORE");
		execSQL(database, "ALTER TABLE " + getTableName() + " ADD COLUMN " + column + " " + type);

		if (defaultValue != null) {
			updateColumn(database, column, defaultValue, null);
		}
		logMe("UPGRADE DATABASE TABLE " + getTableName() + " ADD COLUMN:" + column + " AFTER");
	}

	protected void updateColumn(SQLiteDatabase database, String column, String value, String where) {
		logMe("UPDATE TABLE " + getTableName() + " COLUMN:" + column + " WITH VALUE:" + value + " WHERE:" + where);
		String sql = "UPDATE " + getTableName() + " SET " + column + " = '" + value + "'";
		if (where != null) {
			sql += " WHERE " + where;
		}
		execSQL(database, sql);
	}

	protected void execSQL(SQLiteDatabase database, String sql) {
		logMe("execSQL: '" + sql + "' BEFORE");
		database.execSQL(sql);
		logMe("execSQL: '" + sql + "' AFTER");
	}
}
