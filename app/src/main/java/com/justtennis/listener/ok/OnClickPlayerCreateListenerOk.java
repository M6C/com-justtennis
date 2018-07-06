package com.justtennis.listener.ok;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.justtennis.business.PlayerBusiness;
import com.justtennis.tool.FragmentTool;

public class OnClickPlayerCreateListenerOk implements OnClickListener {

	private PlayerBusiness business;
	private Activity activity;

	public OnClickPlayerCreateListenerOk(Activity activity, PlayerBusiness business) {
		this.activity = activity;
		this.business = business;
	}

	public void onClick(DialogInterface dialog, int which) {
    	business.create(which == DialogInterface.BUTTON_POSITIVE);
    	finish();
	}

	private void finish() {
		FragmentTool.finish(activity);
	}
}