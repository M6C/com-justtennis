package com.justtennis.business;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.activity.ListPlayerActivity;
import com.justtennis.db.service.InviteService;
import com.justtennis.db.service.PlayerService;
import com.justtennis.db.service.UserService;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Player;
import com.justtennis.domain.RechercheResult;
import com.justtennis.domain.Saison;
import com.justtennis.domain.User;
import com.justtennis.domain.comparator.PlayerComparatorByName;
import com.justtennis.drawer.business.INavigationDrawerRechercheBusiness;
import com.justtennis.manager.SmsManager;
import com.justtennis.manager.TypeManager;
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
	private InviteService inviteService;
	private TypeManager typeManager;
	private List<Player> list = new ArrayList<>();
	private Activity activity;
	private User user;
	private Bundle extraIn;

	private CommonEnum.LIST_FRAGMENT_MODE mode;

	private String findText;

	public ListPlayerBusiness(Activity activity, INotifierMessage notificationMessage) {
		this.activity = activity;
		playerService = new PlayerService(activity, notificationMessage);
		inviteService = new InviteService(activity, NotifierMessageLogger.getInstance());
		typeManager = TypeManager.getInstance(activity, notificationMessage);
		UserService userService = new UserService(activity, NotifierMessageLogger.getInstance());
		user = userService.find();
		extraIn = activity.getIntent().getExtras();
	}

	public void initialize(Bundle bundle) {

		mode = CommonEnum.LIST_FRAGMENT_MODE.EDIT;

		if (bundle != null && bundle.containsKey(ListPlayerActivity.EXTRA_MODE)) {
			mode = (CommonEnum.LIST_FRAGMENT_MODE) bundle.getSerializable(ListPlayerActivity.EXTRA_MODE);
		}
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
	
	public Context getActivity() {
		return activity;
	}

	public CommonEnum.LIST_FRAGMENT_MODE getMode() {
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
		String message = SmsParser.getInstance(activity).toMessageAdd(invite);
		SmsManager.getInstance().send(activity, player.getPhonenumber(), message);
	}

	public boolean isUnknownPlayer(Player player) {
		return PlayerService.isUnknownPlayer(player);
	}

	public int getInviteCount(Player player) {
		return inviteService.countByIdPlayer(player.getId());
	}

	public void refreshData() {
		list.clear();
		if (!CommonEnum.LIST_FRAGMENT_MODE.EDIT.equals(mode)) {
			list.add(playerService.getUnknownPlayer());
		}
		list.addAll(sortPlayer(playerService.getList()));
	}

	private List<Player> sortPlayer(List<Player> listPlayer) {
		Player[] arrayPlayer = listPlayer.toArray(new Player[0]);
		Arrays.sort(arrayPlayer, new PlayerComparatorByName(true));
		return Arrays.asList(arrayPlayer);
	}

	public void setSaison(Saison saison) {
		typeManager.setSaison(saison);
	}
}