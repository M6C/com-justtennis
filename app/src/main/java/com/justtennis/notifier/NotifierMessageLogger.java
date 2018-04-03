package com.justtennis.notifier;

import org.gdocument.gtracergps.launcher.log.Logger;

import com.cameleon.common.android.inotifier.INotifierMessage;

public class NotifierMessageLogger implements INotifierMessage {

	private static final String TAG = NotifierMessageLogger.class.getSimpleName();
	private static NotifierMessageLogger instance = null;
	
	protected NotifierMessageLogger() {
		
	}

	public static NotifierMessageLogger getInstance() {
		if (instance == null) {
			instance = new NotifierMessageLogger();
		}
		return instance;
	}

	@Override
	public void notifyError(Exception ex) {
		Logger.logEr(TAG, ex.getMessage());
	}

	@Override
	public void notifyMessage(String msg) {
		Logger.logMe(TAG, msg);
	}
}
