package com.cameleon.common.android.factory.singleton;

import org.gdocument.gtracergps.launcher.log.Logger;

import android.app.ProgressDialog;
import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierSessionProgress;


public class SingletonNotifierSendSessionProgress implements INotifierSessionProgress {
	private static final String TAG = SingletonNotifierSendSessionProgress.class.getCanonicalName();

	private static SingletonNotifierSendSessionProgress instance;
	private static Context context;
	private ProgressDialog mProgressDialog;

	private SingletonNotifierSendSessionProgress() {
	}

	public static SingletonNotifierSendSessionProgress getInstance(Context context) {
		SingletonNotifierSendSessionProgress.context = context;
		if (instance==null)
			instance = new SingletonNotifierSendSessionProgress();
		return instance;
	}


	public void notifySendSession(int messageId) {
		if (mProgressDialog!=null) {
			logMe("Dismiss not close old ProgressDialog !!!");
			mProgressDialog.dismiss();
		}
    	mProgressDialog = ProgressDialog.show(getContext(), "Please wait", getContext().getResources().getString(messageId), true);
		mProgressDialog.setIndeterminate(true);
	}

	public void notifySendLocation(int messageId, int count, int total) {
		if (mProgressDialog!=null) {
			if (mProgressDialog.isIndeterminate() || mProgressDialog.getMax()!=total) {
				mProgressDialog.setIndeterminate(false);
				mProgressDialog.setMax(total);
			}
			mProgressDialog.setProgress(count);
	    	mProgressDialog.setMessage(getContext().getResources().getString(messageId, count, total));
		}
		else {
			logWa("ProgressDialog is null (notifySendLocation) !!!");
		}
	}

	public void notifySendEnd(int messageId) {
		if (mProgressDialog!=null) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
		else {
			logWa("ProgressDialog is null (notifySendEnd) !!!");
		}
	}

	private Context getContext() {
		return context;
	}

	/**
     * Log Error methodes
     * @param msg
     */
    private void logEr(String msg) {
		Logger.logEr(TAG, msg);
    }

	/**
     * Log Warning methodes
     * @param msg
     */
    private void logWa(String msg) {
		Logger.logWa(TAG, msg);
    }

	/**
     * Log methodes
     * @param msg
     */
    private void logMe(String msg) {
		Logger.logMe(TAG, msg);
    }
}
