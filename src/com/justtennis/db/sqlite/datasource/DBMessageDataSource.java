package com.justtennis.db.sqlite.datasource;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.sqlite.helper.DBMessageHelper;
import com.justtennis.domain.Message;
import com.justtennis.tool.DbTool;

public class DBMessageDataSource extends GenericDBDataSource<Message> {

	private static final String TAG = DBMessageDataSource.class.getCanonicalName();

	// Database fields
	private String[] allColumns = {
		DBMessageHelper.COLUMN_ID,
		DBMessageHelper.COLUMN_MESSAGE,
		DBMessageHelper.COLUMN_ID_PLAYER
	};

	public DBMessageDataSource(Context context, INotifierMessage notificationMessage) {
		super(new DBMessageHelper(context, notificationMessage), notificationMessage);
	}

	@Override
	protected String[] getAllColumns() {
		return allColumns;
	}

	@Override
	protected void putContentValue(ContentValues values, Message message) {
		values.put(DBMessageHelper.COLUMN_MESSAGE, message.getMessage());
		values.put(DBMessageHelper.COLUMN_ID_PLAYER, message.getIdPlayer());
	}

	@Override
	protected Message cursorToPojo(Cursor cursor) {
		int col = 0;
		Message message = new Message();
		message.setId(DbTool.getInstance().toLong(cursor, col++));
		message.setMessage(cursor.getString(col++));
		message.setIdPlayer(DbTool.getInstance().toLong(cursor, col++));
		return message;
	}
	
	@Override
	protected String getTag() {
		return TAG;
	}

	public Message getCommon() {
		Date dateStart = new Date();
		Message ret = null;
		Cursor cursor = db.query(dbHelper.getTableName(),
			getAllColumns(), DBMessageHelper.COLUMN_ID_PLAYER + " is null", null,
			null, null, null);
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			ret = cursorToPojo(cursor);
		}
		cursor.close();
		logMe("getCommon()", dateStart);
		return ret;
	}
}