package com.justtennis.listener.ok;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class OnClickExitListenerOk implements OnClickListener {

	private Activity context;

	public OnClickExitListenerOk(Activity context) {
		this.context = context;
	}

	public void onClick(DialogInterface dialog, int which) {
		if (which == DialogInterface.BUTTON_POSITIVE) {
        	context.finish();
		}
	}

}