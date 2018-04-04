package com.cameleon.common.android.factory.singleton;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.cameleon.common.android.inotifier.INotifierMessage;


public class SingletonNotifierToast implements INotifierMessage {

	private static SingletonNotifierToast instance;
	private static Context context;
    private Handler mainThreadHandler = null;

	private SingletonNotifierToast() {
		mainThreadHandler = new Handler();
	}

	public static SingletonNotifierToast getInstance(Context context) {
		SingletonNotifierToast.context = context;
		if (instance==null)
			instance = new SingletonNotifierToast();
		return instance;
	}

	public void notifyError(Exception ex) {
		toastMe(ex.getMessage());
	}

	public void notifyMessage(String msg) {
		toastMe(msg);
	}

	private void toastMe(final String msg) {
		mainThreadHandler.post(new Runnable() {
			public void run() {
				Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
			}
		});
	}

	private Context getContext() {
		return context;
	}
}
