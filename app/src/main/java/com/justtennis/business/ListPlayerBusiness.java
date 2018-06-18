package com.justtennis.business;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.activity.ListPlayerActivity;
import com.justtennis.db.service.InviteService;
import com.justtennis.db.service.PlayerService;
import com.justtennis.db.service.UserService;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Player;
import com.justtennis.domain.RechercheResult;
import com.justtennis.domain.User;
import com.justtennis.domain.comparator.PlayerComparatorByName;
import com.justtennis.drawer.business.INavigationDrawerRechercheBusiness;
import com.justtennis.manager.SmsManager;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.parser.SmsParser;
import com.justtennis.ui.common.CommonEnum;
import com.justtennis.ui.rxjava.RxListPlayer;

import org.gdocument.gtracergps.launcher.log.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;

public class ListPlayerBusiness implements INavigationDrawerRechercheBusiness {

	private static final String TAG = ListPlayerBusiness.class.getSimpleName();

	private PlayerService playerService;
	private UserService userService;
	private InviteService inviteService;
	private List<Player> list = new ArrayList<Player>();
	private Activity context;
	private User user;
	private Bundle extraIn = null;

	private CommonEnum.LIST_PLAYER_MODE mode;

	private String findText;

	public ListPlayerBusiness(Activity context, INotifierMessage notificationMessage) {
		this.context = context;
		playerService = new PlayerService(context, notificationMessage);
		userService = new UserService(context, NotifierMessageLogger.getInstance());
		inviteService = new InviteService(context, NotifierMessageLogger.getInstance());
		user = userService.find();
		extraIn = context.getIntent().getExtras();
	}

	public void initialize() {

		Intent intent = context.getIntent();
		mode = CommonEnum.LIST_PLAYER_MODE.EDIT;

		if (intent.hasExtra(ListPlayerActivity.EXTRA_MODE)) {
			mode = (CommonEnum.LIST_PLAYER_MODE) intent.getSerializableExtra(ListPlayerActivity.EXTRA_MODE);
		}

		refreshData();
	}

	@Override
	public String getFindText() {
		return findText;
	}

	@Override
	public void setFindText(String findText) {
		this.findText = findText;
	}

	@Override
	public Collection<? extends RechercheResult> find(String text) {
		return playerService.find(text);
	}

	public List<Player> getList() {
		return list;
	}
	
	public Context getContext() {
		return context;
	}

	public CommonEnum.LIST_PLAYER_MODE getMode() {
		return mode;
	}

	public Bundle getExtraIn() {
		return extraIn;
	}

	public void delete(Player player) {
		Logger.logMe(TAG, "Delete Button !!!");
		playerService.delete(player);

		refreshData();
		RxListPlayer.publish(RxListPlayer.SUBJECT_REFRESH, Observable.empty());
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

	public void refreshData() {
		list.clear();
		if (!CommonEnum.LIST_PLAYER_MODE.EDIT.equals(mode)) {
			list.add(playerService.getUnknownPlayer());
		}
		list.addAll(sortPlayer(playerService.getList()));
	}

	private List<Player> sortPlayer(List<Player> listPlayer) {
		Player[] arrayPlayer = listPlayer.toArray(new Player[0]);
		Arrays.sort(arrayPlayer, new PlayerComparatorByName(true));
		return Arrays.asList(arrayPlayer);
	}
}