package com.cameleon.common.android.factory;

import android.content.Context;

import com.cameleon.common.android.factory.singleton.SingletonNotifierSendSessionProgress;
import com.cameleon.common.android.factory.singleton.SingletonNotifierToast;
import com.cameleon.common.android.inotifier.INotifierMessage;
import com.cameleon.common.android.inotifier.INotifierSessionProgress;


public class FactoryNotifier {

	private static FactoryNotifier instance;

	private FactoryNotifier() {
	}

	public static FactoryNotifier getInstance() {
		if (instance==null)
			instance = new FactoryNotifier();
		return instance;
	}

	public INotifierMessage getNotifierToast(Context context) {
		return SingletonNotifierToast.getInstance(context);
	}

	public INotifierSessionProgress getNotifierSendSessionProgress(Context context) {
		return SingletonNotifierSendSessionProgress.getInstance(context);
	}
}
