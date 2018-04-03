package com.justtennis.listener.ok;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.justtennis.business.ListCompetitionBusiness;
import com.justtennis.domain.Invite;

public class OnClickCompetitionDeleteListenerOk implements OnClickListener {

	private ListCompetitionBusiness business;
	private Invite invite;

	public OnClickCompetitionDeleteListenerOk(ListCompetitionBusiness business, Invite invite) {
		this.business = business;
		this.invite = invite;
	}

	public void onClick(DialogInterface dialog, int which) {
		if (which == DialogInterface.BUTTON_POSITIVE) {
        	business.delete(invite);
		}
	}

}