package com.justtennis.business;

import android.content.Context;
import android.os.Bundle;

import com.cameleon.common.android.db.sqlite.service.GenericService;
import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.activity.PlayerActivity;
import com.justtennis.db.service.PlayerService;
import com.justtennis.db.service.RankingService;
import com.justtennis.db.service.RechercheService;
import com.justtennis.db.service.SaisonService;
import com.justtennis.db.service.UserService;
import com.justtennis.domain.Address;
import com.justtennis.domain.Club;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Player;
import com.justtennis.domain.Ranking;
import com.justtennis.domain.RechercheResult;
import com.justtennis.domain.Saison;
import com.justtennis.domain.Tournament;
import com.justtennis.domain.User;
import com.justtennis.domain.comparator.RankingComparatorByOrder;
import com.justtennis.drawer.business.INavigationDrawerRechercheBusiness;
import com.justtennis.manager.SmsManager;
import com.justtennis.manager.TypeManager;
import com.justtennis.manager.TypeManager.TYPE;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.parser.LocationParser;
import com.justtennis.parser.PlayerParser;
import com.justtennis.parser.SmsParser;
import com.justtennis.ui.common.CommonEnum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class PlayerBusiness implements INavigationDrawerRechercheBusiness {

	private final com.justtennis.db.service.RechercheService.TYPE[] typeRecherche = new com.justtennis.db.service.RechercheService.TYPE[] {
			com.justtennis.db.service.RechercheService.TYPE.CLUB,
			com.justtennis.db.service.RechercheService.TYPE.TOURNAMENT,
			com.justtennis.db.service.RechercheService.TYPE.ADDRESS
	};

	protected Player player;
	protected CommonEnum.PLAYER_MODE mode;
	protected GenericService<Player> playerService;

	private Context context;
	private UserService userService;
	private PlayerParser playerParser;
	private LocationParser locationParser;
	private SaisonService saisonService;
	private RechercheService rechercheService;
	private User user;
	private Invite invite;
	private List<Invite> list = new ArrayList<Invite>();
	private List<Ranking> listRanking;
	private String[] listTxtRankings;
	private TypeManager typeManager;
	private List<Saison> listSaison = new ArrayList<Saison>();
	private List<String> listTxtSaisons = new ArrayList<String>();
	private String findText;

	public PlayerBusiness(Context context, INotifierMessage notificationMessage) {
		this.context = context;
		userService = new UserService(context, notificationMessage);
		playerService = createPlayerService(context, notificationMessage);
		saisonService = new SaisonService(context, notificationMessage);
		rechercheService = new RechercheService(context, notificationMessage);
		playerParser = PlayerParser.getInstance();
		locationParser = LocationParser.getInstance(context, notificationMessage);
		typeManager = TypeManager.getInstance();
	}

	public void initialize(Bundle bundle) {
		user = userService.find();

		initializePlayer(bundle);
		initializeInvite(bundle);
		initializeMode(bundle);

		if (bundle.containsKey(PlayerActivity.EXTRA_TYPE)) {
			player.setType((TypeManager.TYPE) bundle.getSerializable(PlayerActivity.EXTRA_TYPE));
		}
		
		if (bundle.containsKey(PlayerActivity.EXTRA_RANKING)) {
			long idRanking = bundle.getLong(PlayerActivity.EXTRA_RANKING, -1);
			if (idRanking != -1)  {
				player.setIdRanking(idRanking);
			}
		}
		
		initializeDataRanking();
		initializeDataSaison();
		initializePlayerSaison();
	}

	public void initializeSavedState(Bundle savedInstanceState) {
		mode = (CommonEnum.PLAYER_MODE) savedInstanceState.getSerializable(PlayerActivity.EXTRA_MODE);
		invite = (Invite) savedInstanceState.getSerializable(PlayerActivity.EXTRA_INVITE);
		player = (Player) savedInstanceState.getSerializable(PlayerActivity.EXTRA_PLAYER);
		findText = (String) savedInstanceState.getSerializable(PlayerActivity.EXTRA_FIND);

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
		outState.putSerializable(PlayerActivity.EXTRA_FIND, findText);
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
		return rechercheService.find(typeRecherche, text);
	}

	public long getPlayerCount() {
		return playerService.getCount();
	}

	public void create(boolean sendAddConfirmation) {
		System.out.println("PlayerBusiness create:" + player.toString());
		// Save in database
		playerService.createOrUpdate(player);

		if (sendAddConfirmation) {
			Invite invite = new Invite(new UserService(context, NotifierMessageLogger.getInstance()).find(), player, new Date());
			String message = SmsParser.getInstance(context).toMessageAdd(invite);
			SmsManager.getInstance().send(context, player.getPhonenumber(), message);
		}
	}
	
	public void modify() {
		System.out.println("PlayerBusiness modify:" + player.toString());
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

	public CommonEnum.PLAYER_MODE getMode() {
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

	public void setMode(CommonEnum.PLAYER_MODE mode) {
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

	protected void initializeMode(Bundle intent) {
		if (intent != null && intent.containsKey(PlayerActivity.EXTRA_MODE)) {
			mode = (CommonEnum.PLAYER_MODE) intent.getSerializable(PlayerActivity.EXTRA_MODE);
		} else {
			mode = (player == null || player.getId() == null) ? CommonEnum.PLAYER_MODE.CREATE : CommonEnum.PLAYER_MODE.MODIFY;
		}
	}

	protected void initializeInvite(Bundle intent) {
		invite = null;
		if (intent != null && intent.containsKey(PlayerActivity.EXTRA_INVITE)) {
			invite = (Invite) intent.getSerializable(PlayerActivity.EXTRA_INVITE);
		}
	}

	protected void initializePlayer(Bundle intent) {
		if (intent != null) {
			if (intent.containsKey(PlayerActivity.EXTRA_PLAYER_ID)) {
				long playerId = intent.getLong(PlayerActivity.EXTRA_PLAYER_ID, PlayerService.ID_EMPTY_PLAYER);
				if (playerId != PlayerService.ID_EMPTY_PLAYER) {
					player = findPlayer(playerId);
				}
			}
			if (intent.containsKey(PlayerActivity.EXTRA_PLAYER)) {
				player = (Player) intent.getSerializable(PlayerActivity.EXTRA_PLAYER);
			}
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

	public GenericService<? extends Player> getPlayerService() {
		return playerService;
	}

	public SaisonService getSaisonService() {
		return saisonService;
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