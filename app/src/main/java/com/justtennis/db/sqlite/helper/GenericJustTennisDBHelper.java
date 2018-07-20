package com.justtennis.db.sqlite.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.cameleon.common.android.db.sqlite.helper.GenericDBHelper;
import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.DBDictionary;

public abstract class GenericJustTennisDBHelper extends GenericDBHelper {

	private static final String PACKAGE_NAME = "com.justtennis";

	public GenericJustTennisDBHelper(Context context, INotifierMessage notificationMessage, String databaseName, int databaseVersion) {
		super(context, notificationMessage, databaseName, databaseVersion);
		this.context = context;
		this.notificationMessage = notificationMessage;
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		super.onCreate(database);
		createOtherTable(database, DBDictionary.getInstance(context, notificationMessage).getListHelper());
	}

	@Override
	protected String getPackagename() {
		return PACKAGE_NAME;
	}

	public abstract Class<?> getClassType();
}
