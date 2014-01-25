package com.justtennis.manager;

import java.util.List;

import org.gdocument.gtracergps.launcher.log.Logger;

import android.app.Activity;
import android.content.CursorLoader;
import android.database.Cursor;

import com.justtennis.ApplicationConfig;
import com.justtennis.domain.GenericDBPojo;
import com.justtennis.manager.mapper.GenericMapper;

public abstract class GenericCursorManager<T extends GenericDBPojo<Long>, M extends GenericMapper<T>> {

	private final static String TAG = GenericCursorManager.class.getSimpleName();

	protected List<T> getList(Activity context, String where, String[] whereParameters) {
		// Run query
		CursorLoader cursorLoader = buildCursorLoader(context, where, whereParameters);
		
		List<T> ret = null;
		Cursor cursor = null;
		try {
			cursor = cursorLoader.loadInBackground();
			
			if (cursor!=null) {
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

	protected abstract CursorLoader buildCursorLoader(Activity context, String where, String[] whereParameters);
	protected abstract M getMapper();

	protected boolean logFlag() {
		return ApplicationConfig.SHOW_LOG_CURSOR_PROCESS;
	}

	protected String logFlagName() {
		return "SHOW_LOG_CURSOR_PROCESS";
	}
	protected String tag() {
		return TAG;
	}
	
	protected void logManager(String title, List<T> ret) {
		if (ret!=null && logFlag()) {
			for (int i = 0; i < ret.size(); i++) {
				T pojo = ret.get(i);
				logMe(logFlagName() + title + "[" + i + "]:" + pojo.getId());
			}
		}
	}
	
	protected void logMe(String message) {
		Logger.logMe(tag(), message);
	}
}