package com.justtennis.listener.ok;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.justtennis.business.ListPlayerBusiness;
import com.justtennis.domain.Player;

public class OnClickPlayerDeleteListenerOk implements OnClickListener {

	private ListPlayerBusiness business;
	private Player player;

	public OnClickPlayerDeleteListenerOk(ListPlayerBusiness business, Player player) {
		this.business = business;
		this.player = player;
	}

	public void onClick(DialogInterface dialog, int which) {
		if (which == DialogInterface.BUTTON_POSITIVE) {
        	business.delete(player);
		}
	}

}