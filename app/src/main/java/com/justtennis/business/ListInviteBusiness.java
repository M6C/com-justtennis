package com.justtennis.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gdocument.gtracergps.launcher.log.Logger;

import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.activity.interfaces.ICommonListActivity;
import com.justtennis.db.service.InviteService;
import com.justtennis.db.service.PlayerService;
import com.justtennis.db.service.ScoreSetService;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Player;
import com.justtennis.domain.Saison;
import com.justtennis.domain.comparator.PlayerComparatorByName;
import com.justtennis.helper.GCalendarHelper;
import com.justtennis.manager.TypeManager;

public class ListInviteBusiness {

	private static final String TAG = ListInviteBusiness.class.getSimpleName();
	
	private Context context;
	private ICommonListActivity iActivity;

	private InviteService inviteService;
	private ScoreSetService scoreService;
	private PlayerService playerService;
	private TypeManager typeManager;

	private GCalendarHelper calendarHelper;

	private List<Invite> list = new ArrayList<Invite>();
	private List<Player> listPlayer = new ArrayList<Player>();
	private String[] listPlayerName;

	public ListInviteBusiness(Context context, ICommonListActivity iActivity, INotifierMessage notificationMessage) {
		this.context = context;
		this.iActivity = iActivity;
		playerService = new PlayerService(context, notificationMessage);
		inviteService = new InviteService(context, notificationMessage);
		scoreService = new ScoreSetService(context, notificationMessage);
		calendarHelper = GCalendarHelper.getInstance(context);
		typeManager = TypeManager.getInstance(context, notificationMessage);
	}

	public void onCreate() {
		refreshData();
	}
	
	public void onResume() {
		refreshInvite();
	}

	public List<Invite> getList() {
		return list;
	}

	public Context getContext() {
		return context;
	}

	public void delete(Invite invite) {
		Logger.logMe(TAG, "Delete Invite");
		scoreService.deleteByIdInvite(invite.getId());
		inviteService.delete(invite);
		if (invite.getIdCalendar() != null) {
			calendarHelper.deleteCalendarEntry(invite.getIdCalendar());
		}

		refreshInvite();
		iActivity.refresh();
	}

	public void refreshData() {
		refreshInvite();
		refreshPlayer();
	}

	private void refreshInvite() {
		List<Invite> listInvite = inviteService.sortInviteByDate(inviteService.getList());

		for (Invite invite : listInvite) {
			invite.setPlayer(playerService.find(invite.getPlayer().getId()));
			invite.setListScoreSet(scoreService.getByIdInvite(invite.getId()));
		}

		list.clear();
		list.addAll(listInvite);
	}

	private void refreshPlayer() {
		listPlayer.clear();
		listPlayer.add(playerService.getEmptyPlayer());
		listPlayer.add(playerService.getUnknownPlayer());
		listPlayer.addAll(sortPlayer(playerService.getList()));

		listPlayerName = new String[listPlayer.size()];
		for (int i = 0; i < listPlayer.size(); i++) {
			Player player = listPlayer.get(i);
			listPlayerName[i] = player.getFullName();
		}
	}

	private List<Player> sortPlayer(List<Player> listPlayer) {
		Player[] arrayPlayer = listPlayer.toArray(new Player[0]);
		Arrays.sort(arrayPlayer, new PlayerComparatorByName(true));
		return Arrays.asList(arrayPlayer);
	}

	public Player getPlayerNotEmpty(int position) {
		Player player = listPlayer.get(position);
		if (PlayerService.isEmptyPlayer(player)) {
			player = null;
		}
		return player;
	}

	public Long getUnknownPlayerId() {
		return playerService.getUnknownPlayer().getId();
	}

	public List<Player> getListPlayer() {
		return listPlayer;
	}

	public void setListPlayer(List<Player> listPlayer) {
		this.listPlayer = listPlayer;
	}

	public String[] getListPlayerName() {
		return listPlayerName;
	}

	public void setSaison(Saison saison) {
		typeManager.setSaison(saison);
	}
}