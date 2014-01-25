package com.justtennis.listener.ok;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.justtennis.business.InviteBusiness;
import com.justtennis.domain.Player;

public class OnClickSendInviteListenerOk implements OnClickListener {

	private InviteBusiness business;
	private Player player;

	public OnClickSendInviteListenerOk(InviteBusiness business, Player player) {
		this.business = business;
		this.player = player;
	}

	public void onClick(DialogInterface dialog, int which) {
		if (which == DialogInterface.BUTTON_POSITIVE) {
			business.send(business.buildText());
		}
	}

}
