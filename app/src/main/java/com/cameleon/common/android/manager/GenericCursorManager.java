package com.cameleon.common.android.manager;

import java.util.List;

import org.gdocument.gtracergps.launcher.log.Logger;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;

import com.cameleon.common.android.db.sqlite.helper.GenericDBHelper;
import com.cameleon.common.android.mapper.GenericMapper;
import com.cameleon.common.android.model.GenericDBPojo;

public abstract class GenericCursorManager<T extends GenericDBPojo<Long>, M extends GenericMapper<T>> {

	private final static String TAG = GenericCursorManager.class.getSimpleName();

	protected abstract CursorLoader buildCursorLoader(Context context, String where, String[] whereParameters);
	protected abstract M getMapper();

	public List<T> getListExcept(Context context, List<T> listTExclude) {
	    String where = buildWhereNotIn(listTExclude); 
	    String[] whereParameters = null;
		return getList(context, where, whereParameters);
	}

	public List<T> getList(Context context) {
	    String where = null;
	    String[] whereParameters = null;
		return getList(context, where, whereParameters);
	}


	protected T get(Context context, String columnNameId, String columnNameMimeType, long id, String mimeType) {
		T ret = null;
	    String where = columnNameId + " = ? AND " + columnNameMimeType + " = ?"; 
	    String[] whereParameters = new String[]{Long.toString(id), mimeType};
		List<T> list = getList(context, where, whereParameters);
		if (list!=null && list.size()>0) {
			ret = list.get(0);
		}
		return ret;
	}

	protected List<T> getList(Context context, String where, String[] whereParameters) {
		// Run query
		CursorLoader cursorLoader = buildCursorLoader(context, where, whereParameters);
		
		List<T> ret = null;
		Cursor cursor = null;
		try {
			cursor = cursorLoader.loadInBackground();
			
			if (cursor!=null) {
				logCursor(cursor);
				ret = getMapper().mappe(cursor);
			}
		} finally {
			if (cursor!=null) {
				cursor.close();
			}
		}
		logManager("getList", ret);
		return ret;
	}

	protected String buildWhereNotIn(List<T> list) {
		return buildWhereNotIn(list, GenericDBHelper.COLUMN_ID);
	}

	protected <E extends GenericDBPojo<Long>> String buildWhereNotIn(List<E> list, String columnName) {
		return buildWhere(list, columnName, " NOT IN (", ")", ",");
	}

	protected String buildWhereIn(List<T> list) {
		return buildWhereIn(list, GenericDBHelper.COLUMN_ID);
	}

	protected <E extends GenericDBPojo<Long>> String buildWhereIn(List<E> list, String columnName) {
		return buildWhere(list, columnName, " IN (", ")", ",");
	}

	protected <E extends GenericDBPojo<Long>> String buildWhere(List<E> list, String columnName, String clauseBengin, String clauseEnd, String dataSeparator) {
		String ret = null;
		if (list != null && list.size() > 0) {
			for(E sms : list) {
				if (ret == null) {
					ret = columnName + clauseBengin;
				} else {
					ret += dataSeparator;
				}
				ret += sms.getId().toString();
			}
			ret += clauseEnd;
		}
		return ret;
	}

	protected boolean showLogTrace() {
		return false;
	}

	protected boolean showLogCursor() {
		return true;
	}

	protected String tagLogTrace() {
		return TAG;
	}
	
	protected void logManager(String title, List<T> ret) {
		if (ret!=null && showLogTrace()) {
			for (int i = 0; i < ret.size(); i++) {
				T pojo = ret.get(i);
				logMe(title + "[" + i + "]:" + pojo.getId());
			}
		}
	}
	

	protected void logCursor(Cursor cursor) {
		if (showLogCursor()) {
			int ROW_LIMIT = 20;
			int STR_LIMIT = 20;
			int columnsQty = cursor.getColumnCount();
			StringBuilder sb = new StringBuilder();
			for(int i=0 ; i<columnsQty ; i++) {
				sb.append(cursor.getColumnName(i));
				if (i < columnsQty - 1)
					sb.append("; ");
			}
			logMe(String.format("Column Names: %s", sb.toString()));
			int rowCnt = ROW_LIMIT;
			if (cursor.moveToFirst()) {
				do {
					sb = new StringBuilder();
					for (int idx = 0; idx < columnsQty; ++idx) {
						String str = cursor.getString(idx);
						if (str != null && str.length() > STR_LIMIT) {
							str = str.substring(0, STR_LIMIT);
						}
						sb.append(cursor.getColumnName(idx)).append(":").append(str);
						if (idx < columnsQty - 1)
							sb.append("; ");
					}
					logMe(String.format("Row: %d, Values: %s", cursor.getPosition(), sb.toString()));
				} while ((rowCnt-- > 0) && cursor.moveToNext());
			}
		}
	}

	protected void logMe(String message) {
		Logger.logMe(tagLogTrace(), message);
	}

	protected void logMe(Exception ex) {
		Logger.logMe(tagLogTrace(), ex);
    }
}