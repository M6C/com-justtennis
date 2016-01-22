package com.justtennis.business;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.cameleon.common.android.db.sqlite.service.GenericService;
import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.activity.PlayerActivity;
import com.justtennis.activity.PlayerActivity.MODE;
import com.justtennis.db.service.PlayerService;
import com.justtennis.db.service.RankingService;
import com.justtennis.db.service.SaisonService;
import com.justtennis.db.service.UserService;
import com.justtennis.domain.Address;
import com.justtennis.domain.Club;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Player;
import com.justtennis.domain.Ranking;
import com.justtennis.domain.Saison;
import com.justtennis.domain.Tournament;
import com.justtennis.domain.User;
import com.justtennis.domain.comparator.RankingComparatorByOrder;
import com.justtennis.manager.SmsManager;
import com.justtennis.manager.TypeManager;
import com.justtennis.manager.TypeManager.TYPE;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.parser.LocationParser;
import com.justtennis.parser.PlayerParser;
import com.justtennis.parser.SmsParser;

public class PlayerBusiness {

	protected Player player;
	protected MODE mode;
	protected GenericService<Player> playerService;

	private Context context;
	private UserService userService;
	private PlayerParser playerParser;
	private LocationParser locationParser;
	private SaisonService saisonService;
	private User user;
	private Invite invite;
	private List<Invite> list = new ArrayList<Invite>();
	private List<Ranking> listRanking;
	private String[] listTxtRankings;
	private TypeManager typeManager;
	private List<Saison> listSaison = new ArrayList<Saison>();
	private List<String> listTxtSaisons = new ArrayList<String>();

	public PlayerBusiness(Context context, INotifierMessage notificationMessage) {
		this.context = context;
		userService = new UserService(context, notificationMessage);
		playerService = createPlayerService(context, notificationMessage);
		saisonService = new SaisonService(context, notificationMessage);
		playerParser = PlayerParser.getInstance();
		locationParser = LocationParser.getInstance(context, notificationMessage);
		typeManager = TypeManager.getInstance();
	}

	public void initialize(Intent intent) {
		user = userService.find();

		initializePlayer(intent);
		initializeInvite(intent);
		initializeMode(intent);

		if (intent.hasExtra(PlayerActivity.EXTRA_TYPE)) {
			player.setType((TypeManager.TYPE) intent.getSerializableExtra(PlayerActivity.EXTRA_TYPE));
		}
		
		if (intent.hasExtra(PlayerActivity.EXTRA_RANKING)) {
			long idRanking = intent.getLongExtra(PlayerActivity.EXTRA_RANKING, -1);
			if (idRanking != -1)  {
				player.setIdRanking(idRanking);
			}
		}
		
		initializeDataRanking();
		initializeDataSaison();
		initializePlayerSaison();
	}

	public void initialize(Bundle savedInstanceState) {
		mode = (MODE) savedInstanceState.getSerializable(PlayerActivity.EXTRA_MODE);
		invite = (Invite) savedInstanceState.getSerializable(PlayerActivity.EXTRA_INVITE);
		player = (Player) savedInstanceState.getSerializable(PlayerActivity.EXTRA_PLAYER);

		initializeDataRanking();
		initializeDataSaison();
	}

	protected void initializeDataRanking() {
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
	}

	protected void initializeDataSaison() {
		listSaison.clear();
		listSaison.add(SaisonService.getEmpty());
		listSaison.addAll(saisonService.getList());

		listTxtSaisons.clear();
		listTxtSaisons.addAll(saisonService.getListName(listSaison));
	}

	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(PlayerActivity.EXTRA_MODE, mode);
		outState.putSerializable(PlayerActivity.EXTRA_INVITE, invite);
		outState.putSerializable(PlayerActivity.EXTRA_PLAYER, player);
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
		return PlayerService.isUnknownPlayer(player);
	}

	public boolean isEmptySaison(Saison saison) {
		return SaisonService.isEmpty(saison);
	}

	public boolean sendMessageConfirmation() {
		return typeManager.getType() == TYPE.TRAINING;
	}
	
	public void setSaison(Saison saison) {
		player.setIdSaison(saison == null || isEmptySaison(saison) ? null : saison.getId());
	}

	public Saison getSaison() {
		return (player.getIdSaison() == null) ? null : new Saison(player.getIdSaison());
	}

	public MODE getMode() {
		return mode;
	}

	public List<Invite> getList() {
		return list;
	}

	public Player getPlayer() {
		switch (mode) {
			case FOR_RESULT:
			case CREATE:
			case MODIFY:
			default:
				return player;
			case DEMANDE_ADD:
				return invite == null ? null : invite.getPlayer();
		}
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

	public Player buildPlayer() {
		if (player==null) {
			player = new Player();
			player.setType(typeManager.getType());
		}
		return player;
	}

	public String[] getListTxtRankings() {
		return listTxtRankings;
	}

	public List<Ranking> getListRanking() {
		return listRanking;
	}

	public List<Saison> getListSaison() {
		return listSaison;
	}

	public void setListSaison(List<Saison> listSaison) {
		this.listSaison = listSaison;
	}

	public List<String> getListTxtSaisons() {
		return listTxtSaisons;
	}

	public void setListTxtSaisons(List<String> listTxtSaisons) {
		this.listTxtSaisons = listTxtSaisons;
	}

	public void setAddress(Address address) {
		locationParser.setAddress(invite, address);
	}

	public void setLocation(Serializable location) {
		if (getPlayerType() == TypeManager.TYPE.COMPETITION) {
			player.setIdTournament(((Tournament)location).getId());
		} else {
			player.setIdClub(((Club)location).getId());
		}
	}

	public String[] getLocationLine() {
		return locationParser.toAddress(player);
	}

	@SuppressWarnings("unchecked")
	protected <P extends Player> GenericService<P> createPlayerService(Context context, INotifierMessage notificationMessage) {
		return (GenericService<P>) new PlayerService(context, notificationMessage);
	}

	protected void initializeMode(Intent intent) {
		mode = MODE.CREATE;

		if (player!=null && player.getId()!=null) {
			mode = MODE.MODIFY;
		}

		if (intent.hasExtra(PlayerActivity.EXTRA_MODE)) {
			mode = (MODE) intent.getSerializableExtra(PlayerActivity.EXTRA_MODE);
		}
	}

	protected void initializeInvite(Intent intent) {
		invite = null;
		if (intent.hasExtra(PlayerActivity.EXTRA_INVITE)) {
			invite = (Invite) intent.getSerializableExtra(PlayerActivity.EXTRA_INVITE);
		}
	}

	protected void initializePlayer(Intent intent) {
		if (intent.hasExtra(PlayerActivity.EXTRA_PLAYER_ID)) {
			long playerId = intent.getLongExtra(PlayerActivity.EXTRA_PLAYER_ID, PlayerService.ID_EMPTY_PLAYER);
			if (playerId != PlayerService.ID_EMPTY_PLAYER) {
				player = findPlayer(playerId);
			}
		}
		if (intent.hasExtra(PlayerActivity.EXTRA_PLAYER)) {
			player = (Player) intent.getSerializableExtra(PlayerActivity.EXTRA_PLAYER);
		}
		if (player == null) {
			player = buildPlayer();
		}
	}

	private void initializePlayerSaison() {
		if (player != null && player.getId() == null && player.getIdSaison() == null) {
			initializePlayerSaison(player);
		}
	}

	public void initializePlayerSaison(Player player) {
		if (player != null) {
			Saison saison = saisonService.getSaisonActiveOrFirst();
			if (saison != null) {
				player.setIdSaison(saison.getId());
			}
		}
	}

//	private void initializeDataInvite() {
//		list.clear();
//		if (player!=null) {
//			List<Invite> listInvite = sortInvite(inviteService.getByIdPlayer(player.getId()));
//			for(Invite invite : listInvite) {
//				invite.setPlayer(playerService.find(invite.getPlayer().getId()));
//			}
//			list.addAll(listInvite);
//		}
//	}

//	private List<Invite> sortInvite(List<Invite> listInvite) {
//		Invite[] arrayInvite = listInvite.toArray(new Invite[0]);
//		Arrays.sort(arrayInvite, new InviteComparatorByDate(true));
//		return Arrays.asList(arrayInvite);
//	}

	private Player findPlayer(long id) {
		return playerService.find(id);
	}

	public TYPE getPlayerType() {
		return getPlayer() == null ? typeManager.getType() : getPlayer().getType();
//		return TypeManager.TYPE.TRAINING;
	}
}