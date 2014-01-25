package com.justtennis.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import android.content.Context;
import android.content.Intent;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.activity.PlayerActivity;
import com.justtennis.activity.PlayerActivity.MODE;
import com.justtennis.db.service.InviteService;
import com.justtennis.db.service.PlayerService;
import com.justtennis.db.service.RankingService;
import com.justtennis.db.service.UserService;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Player;
import com.justtennis.domain.Ranking;
import com.justtennis.domain.User;
import com.justtennis.domain.comparator.InviteComparatorByDate;
import com.justtennis.domain.comparator.RankingComparatorByOrder;
import com.justtennis.manager.SmsManager;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.parser.PlayerParser;
import com.justtennis.parser.SmsParser;

public class PlayerBusiness {

	private Context context;
	private UserService userService;
	private InviteService inviteService;
	private PlayerService playerService;
	private PlayerParser playerParser;
	private User user;
	private Player player;
	private Invite invite;
	private List<Invite> list = new ArrayList<Invite>();
	private List<Ranking> listRanking;
	private String[] listTxtRankings;
	private MODE mode;

	public PlayerBusiness(Context context, INotifierMessage notificationMessage) {
		this.context = context;
		inviteService = new InviteService(context, notificationMessage);
		userService = new UserService(context, notificationMessage);
		playerService = new PlayerService(context, notificationMessage);
		playerParser = PlayerParser.getInstance();
	}

	public void initialize(Intent intent) {
		user = userService.find();
		player = null;
		invite = null;

		if (intent.hasExtra(PlayerActivity.EXTRA_PLAYER_ID)) {
			long playerId = intent.getLongExtra(PlayerActivity.EXTRA_PLAYER_ID, -1000);
			if (playerId!=-1000) {
				player = findPlayer(playerId);
			}
		}
		else if (intent.hasExtra(PlayerActivity.EXTRA_PLAYER)) {
			player = (Player) intent.getSerializableExtra(PlayerActivity.EXTRA_PLAYER);
		}
		else if (intent.hasExtra(PlayerActivity.EXTRA_INVITE)) {
			invite = (Invite) intent.getSerializableExtra(PlayerActivity.EXTRA_INVITE);
		}
		mode = MODE.CREATE;

		if (player!=null && player.getId()!=null) {
			mode = MODE.MODIFY;
		}

		if (intent.hasExtra(PlayerActivity.EXTRA_MODE)) {
			mode = (MODE) intent.getSerializableExtra(PlayerActivity.EXTRA_MODE);
		}

		initializeData();
	}

	public void initializeData() {
		SortedSet<Ranking> setRanking = new TreeSet<Ranking>(new RankingComparatorByOrder());

		listRanking = new RankingService(context, NotifierMessageLogger.getInstance()).getList();
		setRanking.addAll(listRanking);
		
		listRanking.clear();
		listRanking.addAll(setRanking);

		int i=0;
		listTxtRankings = new String[setRanking.size()];
		for(Ranking ranking : setRanking) {
			listTxtRankings[i++] = ranking.getRanking();
		}
		
		initializeDataInvite();
	}

	public long getPlayerCount() {
		return playerService.getCount();
	}

	public void create(boolean sendAddConfirmation) {
		// Save in database
		playerService.createOrUpdate(player);

		if (sendAddConfirmation) {
			Invite invite = new Invite(new UserService(context, NotifierMessageLogger.getInstance()).find(), player, new Date());
			String message = SmsParser.getInstance(context).toMessageAdd(invite);
			SmsManager.getInstance().send(context, player.getPhonenumber(), message);
		}
	}
	
	public void modify() {
		// Save in database
		playerService.createOrUpdate(player);
	}

	public void demandeAddYes() {
		// Save in database
		Player player = PlayerParser.getInstance().fromUser(this.invite.getUser());
		player.setIdExternal(this.invite.getPlayer().getId());
		player.setId(null);
		playerService.createOrUpdate(player);

		Invite invite = new Invite(user, player);
		String message = SmsParser.getInstance(context).toMessageDemandeAddYes(invite);
		SmsManager.getInstance().send(context, invite.getPlayer().getPhonenumber(), message);
	}

	public void demandeAddNo() {
		Invite invite = new Invite(user, this.invite.getUser());
		String message = SmsParser.getInstance(context).toMessageDemandeAddNo(invite);
		SmsManager.getInstance().send(context, invite.getPlayer().getPhonenumber(), message);
	}

	public boolean isUnknownPlayer(Player player) {
		return playerService.isUnknownPlayer(player);
	}

	public MODE getMode() {
		return mode;
	}

	public List<Invite> getList() {
		return list;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Invite getInvite() {
		return invite;
	}

	public void setInvite(Invite invite) {
		this.invite = invite;
	}

	public void setMode(MODE mode) {
		this.mode = mode;
	}

	public String toQRCode() {
		return playerParser.toDataText(player);
	}

	public Player initializePlayer() {
		if (player==null) {
			player = new Player();
		}
		return player;
	}

	public String[] getListTxtRankings() {
		return listTxtRankings;
	}

	public List<Ranking> getListRanking() {
		return listRanking;
	}

	private void initializeDataInvite() {
		list.clear();
		if (player!=null) {
			List<Invite> listInvite = sortInvite(inviteService.getByIdPlayer(player.getId()));
			for(Invite invite : listInvite) {
				invite.setPlayer(playerService.find(invite.getPlayer().getId()));
			}
			list.addAll(listInvite);
		}
	}

	private List<Invite> sortInvite(List<Invite> listInvite) {
		Invite[] arrayInvite = listInvite.toArray(new Invite[0]);
		Arrays.sort(arrayInvite, new InviteComparatorByDate(true));
		return Arrays.asList(arrayInvite);
	}

	private Player findPlayer(long id) {
		return playerService.find(id);
	}
}