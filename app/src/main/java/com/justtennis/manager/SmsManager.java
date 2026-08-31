package com.justtennis.manager;

import java.util.ArrayList;
import java.util.List;

import org.gdocument.gtracergps.launcher.log.Logger;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.justtennis.ApplicationConfig;

public class SmsManager {

	private final static String TAG = SmsManager.class.getSimpleName();

	private static SmsManager instance = null;
	private android.telephony.SmsManager smsManager;

	private SmsManager() {
		smsManager = android.telephony.SmsManager.getDefault();

	}

	public static SmsManager getInstance() {
		if (instance == null) {
			instance = new SmsManager();
		}
		return instance;
	}

	public void send(Context context, String phonenumber, String message) {
		message = cutMessage(message);
		try {
			logMe(message);

			ArrayList<String> divideMessage = smsManager.divideMessage(message);
			logSms("send message", divideMessage);
			ArrayList<PendingIntent> listOfIntents = new ArrayList<PendingIntent>();
			for (int i = 0; i < divideMessage.size(); i++) {
				int id = (int) System.currentTimeMillis();//0;
				PendingIntent pi = PendingIntent.getBroadcast(context, id, new Intent(), 0);
				listOfIntents.add(pi);
			}
			smsManager.sendMultipartTextMessage(phonenumber, null, divideMessage, listOfIntents, null);
		} catch (RuntimeException ex) {
			ex.printStackTrace();
		}
	}

	private String cutMessage(String message) {
		return message;
	}

	private void logMe(String message) {
		Logger.logMe(TAG, message);
	}

	private void logSms(String title, List<String> list) {
		if (ApplicationConfig.SHOW_LOG_SMS_PROCESS) {
			for (int i = 0; i < list.size(); i++) {
				logMe("SHOW_LOG_SMS_PROCESS "+title + "[" + i + "]:" + list.get(i));
			}
		}
	}
}