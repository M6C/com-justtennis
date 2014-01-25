package com.justtennis.listener.ok;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.justtennis.business.ListInviteBusiness;
import com.justtennis.domain.Invite;

public class OnClickInviteDeleteListenerOk implements OnClickListener {

	private ListInviteBusiness business;
	private Invite invite;

	public OnClickInviteDeleteListenerOk(ListInviteBusiness business, Invite invite) {
		this.business = business;
		this.invite = invite;
	}

	public void onClick(DialogInterface dialog, int which) {
		if (which == DialogInterface.BUTTON_POSITIVE) {
        	business.delete(invite);
		}
	}

}