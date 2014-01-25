package com.justtennis.business;

import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.service.UserService;

public class MainBusiness {

	private static final String TAG = MainBusiness.class.getSimpleName();

	private UserService userService;
	private long userCount;
	private Context context;

	public MainBusiness(Context context, INotifierMessage notificationMessage) {
		this.context = context;
		userService = new UserService(context, notificationMessage);
	}

	public void onResume() {
		userCount = userService.getCount();
	}

	public long getUserCount() {
		return userCount;
	}
	
	public Context getContext() {
		return context;
	}
}