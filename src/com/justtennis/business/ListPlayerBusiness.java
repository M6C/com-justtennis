package com.justtennis.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.gdocument.gtracergps.launcher.log.Logger;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.activity.ListPlayerActivity;
import com.justtennis.activity.ListPlayerActivity.MODE;
import com.justtennis.db.service.InviteService;
import com.justtennis.db.service.PlayerService;
import com.justtennis.db.service.UserService;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Player;
import com.justtennis.domain.User;
import com.justtennis.domain.comparator.PlayerComparatorByName;
import com.justtennis.manager.SmsManager;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.parser.SmsParser;

public class ListPlayerBusiness {

	private static final String TAG = ListPlayerBusiness.class.getSimpleName();

	private PlayerService playerService;
	private UserService userService;
	private InviteService inviteService;
	private List<Player> list = new ArrayList<Player>();
	private ListPlayerActivity context;
	private User user;
	private Bundle extraIn = null;

	private MODE mode;

	public ListPlayerBusiness(ListPlayerActivity context, INotifierMessage notificationMessage) {
		this.context = context;
		playerService = new PlayerService(context, notificationMessage);
		userService = new UserService(context, NotifierMessageLogger.getInstance());
		inviteService = new InviteService(context, NotifierMessageLogger.getInstance());
		user = userService.find();
		extraIn = context.getIntent().getExtras();
	}

	public void initialize() {

		Intent intent = context.getIntent();
		mode = MODE.EDIT;

		if (intent.hasExtra(ListPlayerActivity.EXTRA_MODE)) {
			mode = (MODE) intent.getSerializableExtra(ListPlayerActivity.EXTRA_MODE);
		}

		refreshData();
	}

	public List<Player> getList() {
		return list;
	}
	
	public Context getContext() {
		return context;
	}

	public MODE getMode() {
		return mode;
	}

	public Bundle getExtraIn() {
		return extraIn;
	}

	public void delete(Player player) {
		Logger.logMe(TAG, "Delete Button !!!");
		playerService.delete(player);

		refreshData();
		context.refresh();
	}

	public void send(Player player) {
		Invite invite = new Invite(user, player, new Date());
		String message = SmsParser.getInstance(context).toMessageAdd(invite);
		SmsManager.getInstance().send(context, player.getPhonenumber(), message);
	}

	public boolean isUnknownPlayer(Player player) {
		return PlayerService.isUnknownPlayer(player);
	}

	public int getInviteCount(Player player) {
		return inviteService.countByIdPlayer(player.getId());
	}

	private void refreshData() {
		list.clear();
		list.add(playerService.getUnknownPlayer());
		list.addAll(sortPlayer(playerService.getList()));
	}

	private List<Player> sortPlayer(List<Player> listPlayer) {
		Player[] arrayPlayer = listPlayer.toArray(new Player[0]);
		Arrays.sort(arrayPlayer, new PlayerComparatorByName(true));
		return Arrays.asList(arrayPlayer);
	}
}