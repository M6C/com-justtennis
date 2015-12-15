package com.justtennis.db.sqlite.helper;

import android.content.Context;

import com.cameleon.common.android.db.sqlite.helper.GenericDBHelper;
import com.cameleon.common.android.inotifier.INotifierMessage;

public abstract class GenericJustTennisDBHelper extends GenericDBHelper {

	private static final String PACKAGE_NAME = "com.justtennis";

	public GenericJustTennisDBHelper(Context context, INotifierMessage notificationMessage, String databaseName, int databaseVersion) {
		super(context, notificationMessage, databaseName, databaseVersion);
	}

	@Override
	protected String getPackagename() {
		return PACKAGE_NAME;
	}

	public abstract Class<?> getClassType();
}
