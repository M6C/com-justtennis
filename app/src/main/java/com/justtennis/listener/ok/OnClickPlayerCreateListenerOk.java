package com.justtennis.listener.ok;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.justtennis.business.PlayerBusiness;

public class OnClickPlayerCreateListenerOk implements OnClickListener {

	private PlayerBusiness business;
	private Activity context;

	public OnClickPlayerCreateListenerOk(Activity context, PlayerBusiness business) {
		this.context = context;
		this.business = business;
	}

	public void onClick(DialogInterface dialog, int which) {
		context.finish();
    	business.create(which == DialogInterface.BUTTON_POSITIVE);
	}
}