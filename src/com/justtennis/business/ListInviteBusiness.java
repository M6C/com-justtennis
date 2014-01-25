package com.justtennis.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gdocument.gtracergps.launcher.log.Logger;

import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.activity.ListInviteActivity;
import com.justtennis.db.service.InviteService;
import com.justtennis.db.service.PlayerService;
import com.justtennis.domain.Invite;
import com.justtennis.domain.comparator.InviteComparatorByDate;

public class ListInviteBusiness {

	private static final String TAG = ListInviteBusiness.class.getSimpleName();

	private InviteService inviteService;
	private List<Invite> list = new ArrayList<Invite>();
	private PlayerService playerService;
	private ListInviteActivity context;

	public ListInviteBusiness(ListInviteActivity context, INotifierMessage notificationMessage) {
		this.context = context;
		playerService = new PlayerService(context, notificationMessage);
		inviteService = new InviteService(context, notificationMessage);
	}

	public void onResume() {
		refreshData();
	}

	public List<Invite> getList() {
		return list;
	}
	
	public Context getContext() {
		return context;
	}
	
	public void delete(Invite invite) {
		Logger.logMe(TAG, "Delete Button !!!");
		inviteService.delete(invite);
		
		refreshData();
		context.refresh();
	}

	private void refreshData() {
		List<Invite> listInvite = sortInvite(inviteService.getList());
		
		for(Invite invite : listInvite) {
			invite.setPlayer(playerService.find(invite.getPlayer().getId()));
		}

		list.clear();
		list.addAll(listInvite);
	}

	private List<Invite> sortInvite(List<Invite> listInvite) {
		Invite[] arrayInvite = listInvite.toArray(new Invite[0]);
		Arrays.sort(arrayInvite, new InviteComparatorByDate(true));
		return Arrays.asList(arrayInvite);
	}
}