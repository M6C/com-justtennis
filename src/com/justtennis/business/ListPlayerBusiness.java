package com.justtennis.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.gdocument.gtracergps.launcher.log.Logger;

import android.content.Context;
import android.content.Intent;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.R;
import com.justtennis.activity.ListPlayerActivity;
import com.justtennis.activity.ListPlayerActivity.MODE;
import com.justtennis.db.service.PlayerService;
import com.justtennis.db.service.UserService;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Player;
import com.justtennis.domain.User;
import com.justtennis.listener.itemclick.OnItemClickListPlayer;
import com.justtennis.listener.itemclick.OnItemClickListPlayerInvite;
import com.justtennis.manager.SmsManager;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.parser.SmsParser;

public class ListPlayerBusiness {

	private static final String TAG = ListPlayerBusiness.class.getSimpleName();

	private PlayerService playerService;
	private UserService userService;
	private List<Player> list = new ArrayList<Player>();
	private ListPlayerActivity context;
	private User user;

	private MODE mode;

	public ListPlayerBusiness(ListPlayerActivity context, INotifierMessage notificationMessage) {
		this.context = context;
		playerService = new PlayerService(context, notificationMessage);
		userService = new UserService(context, NotifierMessageLogger.getInstance());
		user = userService.find();
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

	public void delete(Player player) {
		Logger.logMe(TAG, "Delete Button !!!");
		playerService.delete(player);

		refreshData();
		context.refresh();
	}

	private void refreshData() {
		list.clear();
		list.add(playerService.getUnknownPlayer());
		list.addAll(playerService.getList());
		
//		for(Player player : list) {
//			if (player.getIdGoogle()!=null && player.getIdGoogle().longValue()>0l) {
//				player.setPhoto(ContactManager.getInstance().getPhoto(context, player.getIdGoogle()));
//			}
//		}
	}

	public void send(Player player) {
		Invite invite = new Invite(user, player, new Date());
		String message = SmsParser.getInstance(context).toMessageAdd(invite);
		SmsManager.getInstance().send(context, player.getPhonenumber(), message);
	}

	public boolean isUnknownPlayer(Player player) {
		return playerService.isUnknownPlayer(player);
	}
}