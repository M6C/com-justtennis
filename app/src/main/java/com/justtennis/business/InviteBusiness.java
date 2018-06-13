package com.justtennis.business;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.cameleon.common.tool.StringTool;
import com.justtennis.ApplicationConfig;
import com.justtennis.R;
import com.justtennis.activity.InviteActivity;
import com.justtennis.db.service.InviteService;
import com.justtennis.db.service.MessageService;
import com.justtennis.db.service.PlayerService;
import com.justtennis.db.service.RankingService;
import com.justtennis.db.service.SaisonService;
import com.justtennis.db.service.ScoreSetService;
import com.justtennis.db.service.UserService;
import com.justtennis.domain.Address;
import com.justtennis.domain.Club;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Invite.SCORE_RESULT;
import com.justtennis.domain.Invite.STATUS;
import com.justtennis.domain.Player;
import com.justtennis.domain.Ranking;
import com.justtennis.domain.Saison;
import com.justtennis.domain.ScoreSet;
import com.justtennis.domain.Tournament;
import com.justtennis.domain.User;
import com.justtennis.helper.GCalendarHelper;
import com.justtennis.helper.GCalendarHelper.EVENT_STATUS;
import com.justtennis.manager.SmsManager;
import com.justtennis.manager.TypeManager;
import com.justtennis.parser.LocationParser;
import com.justtennis.parser.SmsParser;
import com.justtennis.ui.common.CommonEnum;

public class InviteBusiness {

	private static final String TAG = InviteBusiness.class.getSimpleName();

	private Context context;
	private INotifierMessage notification;
	private InviteService inviteService;
	private UserService userService;
	private PlayerService playerService;
	private ScoreSetService scoreSetService;
	private RankingService rankingService;
	private SaisonService saisonService;
	private GCalendarHelper gCalendarHelper;
	private LocationParser locationParser;
	private TypeManager typeManager;
	private User user;
	private Invite invite;
	private CommonEnum.MODE mode = CommonEnum.MODE.INVITE_SIMPLE;
	private List<Ranking> listRanking;
	private List<String> listTxtRankings;
	private List<Saison> listSaison = new ArrayList<Saison>();
	private List<String> listTxtSaisons = new ArrayList<String>();
	private String[][] scores;


	public InviteBusiness(Context context, INotifierMessage notificationMessage) {
		this.context = context;
		this.notification = notificationMessage;
		inviteService = new InviteService(context, notificationMessage);
		playerService = new PlayerService(context, notificationMessage);
		userService = new UserService(context, notificationMessage);
		rankingService = new RankingService(context, notificationMessage);
		scoreSetService = new ScoreSetService(context, notificationMessage);
		saisonService = new SaisonService(context, notificationMessage);
		gCalendarHelper = GCalendarHelper.getInstance(context);
		locationParser = LocationParser.getInstance(context, notificationMessage);
		typeManager = TypeManager.getInstance(context, notificationMessage);
	}

	public void initializeData(@NonNull Intent intent) {
		user = userService.find();

		initializeData(Objects.requireNonNull(intent.getExtras()));
	}

	public void initializeData(Bundle savedInstanceState) {
		initializeInvite(savedInstanceState);
		initializeData();
	}

	private void initializeInvite(@NonNull Bundle bundle) {
		if (bundle.containsKey(InviteActivity.EXTRA_MODE)) {
			mode = (CommonEnum.MODE) bundle.getSerializable(InviteActivity.EXTRA_MODE);
		}

		if (bundle.containsKey(InviteActivity.EXTRA_USER)) {
			user = (User) bundle.getSerializable(InviteActivity.EXTRA_USER);
		}
		if (bundle.containsKey(InviteActivity.EXTRA_INVITE)) {
			invite = (Invite) bundle.getSerializable(InviteActivity.EXTRA_INVITE);
			if (getIdRanking()==null) {
				setIdRanking(rankingService.getRanking(getPlayer(), true).getId());
			}
			initializeScores();
		} else {
			invite = new Invite();
			invite.setUser(getUser());
			invite.setType(typeManager.getType());
		}
		if (bundle.containsKey(InviteActivity.EXTRA_PLAYER_ID)) {
			long id = bundle.getLong(InviteActivity.EXTRA_PLAYER_ID, PlayerService.ID_EMPTY_PLAYER);
			if (id != PlayerService.ID_EMPTY_PLAYER) {
				invite.setPlayer(playerService.find(id));
				setIdRanking(rankingService.getRanking(getPlayer(), true).getId());
			}
		}
	}

	public void updateData() {
		if (this.invite!=null && this.invite.getId() != null) {
			Invite invite = inviteService.find(this.invite.getId());
			if (invite != null) {
				this.invite = invite;
				initializeData();
			}
		}
	}
	
	private void initializeData() {
		if (invite.getDate()==null) {
			Calendar calendar = GregorianCalendar.getInstance(ApplicationConfig.getLocal());
			calendar.setTime(new Date());
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.add(Calendar.HOUR_OF_DAY, 1);
			invite.setDate(calendar.getTime());
		}

		if (invite.getId() == null && invite.getSaison() == null) {
			invite.setSaison(saisonService.getSaisonActiveOrFirst());
		}

		initializeDataRanking();
		initializeDataSaison();
	}

	public void initializeDataRanking() {

		listRanking = rankingService.getList();
		rankingService.order(listRanking);

		listTxtRankings = new ArrayList<String>();
		for(Ranking ranking : listRanking) {
			listTxtRankings.add(ranking.getRanking());
		}
	}

	public void initializeDataSaison() {
		listSaison.clear();
		listSaison.add(SaisonService.getEmpty());
		listSaison.addAll(saisonService.getList());

		listTxtSaisons.clear();
		listTxtSaisons.addAll(saisonService.getListName(listSaison));
	}

	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(InviteActivity.EXTRA_MODE, mode);
		outState.putSerializable(InviteActivity.EXTRA_INVITE, invite);
		outState.putSerializable(InviteActivity.EXTRA_USER, user);
	}

	private void initializeScores() {
		if (getInvite().getId() != null && scores == null) {
			scores = scoreSetService.getTableByIdInvite(getInvite().getId());
		}
	}

	public String buildText() {
//		Date date = invite.getDate();
//		Invite invite = new Invite(user, player, date);

		if (getPlayer().getIdExternal()==null) {
			MessageService messageService = new MessageService(context, notification);
			return SmsParser.getInstance(context).toMessageCommon(messageService.getCommon(), invite);
		}
		else {
			return SmsParser.getInstance(context).toMessageInvite(invite);
		}
	}

	public boolean isUnknownPlayer() {
		return getPlayer() != null && PlayerService.isUnknownPlayer(getPlayer());
	}
	
	public void send(String text) {
//		Date date = invite.getDate();
//		Invite invite = new Invite(user, player, date, getType());
		invite.setStatus(this.invite.getStatus());
		if (this.invite!=null) {
			invite.setId(this.invite.getId());
		}

		inviteService.createOrUpdate(invite);

		saveScoreSet();

		Player player = getPlayer();
		calendarAddEvent(invite, EVENT_STATUS.CONFIRMED);
		
		if (text!=null) {
			if (player.getPhonenumber()!=null && !player.getPhonenumber().equals("")) {
				SmsManager.getInstance().send(context, player.getPhonenumber(), text);
			}
		}
		else {
			Toast.makeText(context, R.string.msg_no_message_to_send, Toast.LENGTH_LONG).show();
		}
	}

	public void modify() {
		if (invite.getId() != null) {
			Invite inv = inviteService.find(invite.getId());
			if (inv != null && inv.getIdCalendar() != null && 
				inv.getIdCalendar() != GCalendarHelper.EVENT_ID_NO_CREATED) {
				gCalendarHelper.deleteCalendarEntry(inv.getIdCalendar());
			}
		}

		inviteService.createOrUpdate(invite);
		
		saveScoreSet();

		EVENT_STATUS status = gCalendarHelper.toEventStatus(invite.getStatus());
		calendarAddEvent(invite, status);
	}
	
	public void confirmYes() {
		Invite invite = doConfirm(STATUS.ACCEPT);

		String message = SmsParser.getInstance(context).toMessageInviteConfirmYes(invite);
		SmsManager.getInstance().send(context, this.invite.getUser().getPhonenumber(), message);
	}
	
	public void confirmNo() {
		Invite invite = doConfirm(STATUS.REFUSE);

		String message = SmsParser.getInstance(context).toMessageInviteConfirmNo(invite);
		SmsManager.getInstance().send(context, this.invite.getUser().getPhonenumber(), message);
	}

	public boolean isEmptyRanking(Ranking ranking) {
		return rankingService.isEmptyRanking(ranking);
	}

	public boolean isEmptySaison(Saison saison) {
		return SaisonService.isEmpty(saison);
	}

	public User getUser() {
		return user;
	}

	public void setDate(Date date) {
		invite.setDate(date);
	}
	
	public Date getDate() {
		return invite.getDate();
	}

	public void setIdRanking(Long idRanking) {
		invite.setIdRanking(idRanking);
	}

	public Long getIdRanking() {
		return invite.getIdRanking();
	}

	public void setSaison(Saison saison) {
		invite.setSaison(saison);
	}

	public Saison getSaison() {
		return invite.getSaison();
	}

	public Address getAddress() {
		return invite.getAddress() == null ? null : invite.getAddress();
	}

	public Club getClub() {
		return invite.getClub() == null ? null : invite.getClub();
	}
	
	public Tournament getTournament() {
		return invite.getTournament() == null ? null : invite.getTournament();
	}

	public Invite getInvite() {
		return invite;
	}

	public void setInvite(Invite invite) {
		this.invite = invite;
	}

	public TypeManager.TYPE getType() {
		return invite.getType();
	}

	public void setType(TypeManager.TYPE type) {
		invite.setType(type);
	}

	public Player getPlayer() {
		return invite.getPlayer();
	}

	public void setPlayer(Player player) {
		this.invite.setPlayer(player);
	}

	public void setLocation(Serializable location) {
		if (getType() == TypeManager.TYPE.COMPETITION) {
			setTournament((Tournament)location);
		} else {
			setClub((Club)location);
		}
	}
	
	public void setLocationClub(Serializable location) {
		setClub((Club)location);
	}

	public CommonEnum.MODE getMode() {
		return mode;
	}

	public void setMode(CommonEnum.MODE mode) { 
		this.mode = mode;
	}

	public void setPlayer(long id) {
		this.invite.setPlayer(playerService.find(id));

		if (isUnknownPlayer()) {
			setIdRanking(getListRanking().get(0).getId());
			setType(TypeManager.TYPE.COMPETITION);
		} else {
			setIdRanking(rankingService.getRanking(getPlayer(), true).getId());
			switch (getPlayer().getType()) {
			default:
			case TRAINING:
				setType(TypeManager.TYPE.TRAINING);
				break;
			case COMPETITION:
				setType(TypeManager.TYPE.COMPETITION);
				break;
			}
		}
	}

	public String getScoresText() {
		return scoreSetService.buildTextScore(invite);
	}

	public String[][] getScores() {
		return scores;
	}

	public void setScores(String[][] scores) {
		this.scores = scores;
	}

	public List<Ranking> getListRanking() {
		return listRanking;
	}

	public void setListRanking(List<Ranking> listRanking) {
		this.listRanking = listRanking;
	}

	public List<String> getListTxtRankings() {
		return listTxtRankings;
	}

	public void setListTxtRankings(List<String> listTxtRankings) {
		this.listTxtRankings = listTxtRankings;
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

	private Invite doConfirm(STATUS status) {
		this.invite.setStatus(status);

		User user = userService.find();
		EVENT_STATUS eventStatus = toEventStatus(status);
		calendarAddEvent(invite, eventStatus);

		inviteService.createOrUpdate(invite);

		Invite invite = new Invite(user, this.invite.getUser(), this.invite.getDate(), status);
		invite.getPlayer().setId(getPlayer().getId());
		invite.getPlayer().setIdExternal(getPlayer().getIdExternal());
		invite.setId(this.invite.getId());
		invite.setIdExternal(this.invite.getIdExternal());
		invite.setIdCalendar(this.invite.getIdCalendar());
		invite.setType(typeManager.getType());
		return invite;
	}

	private void calendarAddEvent(Invite invite, EVENT_STATUS status) {
		Date date = invite.getDate();
		Player player = invite.getPlayer();
		String text = "";
		if (ApplicationConfig.SHOW_ID) {
			text += " [invite:" + invite.getId() + "|user:" + invite.getUser().getId() + "|player:" + invite.getPlayer().getId() + "|calendar:" + invite.getIdCalendar() + "]";
		}
		String title = null;
		if (getType()==TypeManager.TYPE.COMPETITION) {
			title = "Just Tennis Match vs " + player.getFirstName() + " " + player.getLastName();
		} else {
			title = "Just Tennis Entrainement vs " + player.getFirstName() + " " + player.getLastName();
		}

		boolean hasAlarm = (status != EVENT_STATUS.CANCELED);
		long idEvent = gCalendarHelper.addEvent(
			title, text, invite.getPlayer().getAddress(),
			date.getTime(), date.getTime() + Invite.PLAY_DURATION_DEFAULT,
			false, hasAlarm, GCalendarHelper.DEFAULT_CALENDAR_ID, 60,
			status
		);

		invite.setIdCalendar(idEvent);
		inviteService.createOrUpdate(invite);
	}

	private EVENT_STATUS toEventStatus(STATUS status) {
		switch(status) {
			case ACCEPT:
				return EVENT_STATUS.CONFIRMED;
			case REFUSE:
				return EVENT_STATUS.CANCELED;
			case UNKNOW:
			default:
				return EVENT_STATUS.UNKNOW;
		}
	}

	private boolean checkScoreSet(String score1, String score2, boolean last) {
		boolean ret = false;
		if (score1 != null && score2 != null &&
			!"".equals(score1) && !"".equals(score2)) {
			try {
				int num1 = Integer.parseInt(score1);
				int num2 = Integer.parseInt(score2);

				if (last || (num1 <= 7 && num2 <= 7)) {
					ret = true;
				}
			} catch (NumberFormatException ex) {
				Log.e(TAG, "Number Format Exception", ex);
			}
			
		}
		return ret;
	}
	
	private ScoreSet newScoreSet(Integer order, String score1, String score2, boolean last) {
		ScoreSet ret = null;
		if (checkScoreSet(score1, score2, last)) {
			ret = new ScoreSet();
			ret.setInvite(invite);
			ret.setOrder(order);
			ret.setValue1(Integer.parseInt(score1));
			ret.setValue2(Integer.parseInt(score2));
		}
		return ret;
	}

	public List<ScoreSet> computeScoreSet(String[][] scores) {
		List<ScoreSet> ret = new ArrayList<ScoreSet>();
		int len = (scores == null) ? 0 : scores.length;
		for(int row = 1 ; row <= len ; row++) {
			String[] col = scores[row-1];
			if (!StringTool.getInstance().isEmpty(col[0]) ||
				!StringTool.getInstance().isEmpty(col[1])) {
				if (StringTool.getInstance().isEmpty(col[0])) {
					col[0] = "0";
				}
				if (StringTool.getInstance().isEmpty(col[1])) {
					col[1] = "0";
				}
				ScoreSet scoreSet = newScoreSet(row, col[0], col[1], row==len);
				if (scoreSet != null) {
					ret.add(scoreSet);
				}
			}
		}
		return ret;
	}

	public SCORE_RESULT computeScoreResult(List<ScoreSet> listScoreSet) {
		Invite.SCORE_RESULT ret = Invite.SCORE_RESULT.UNFINISHED;
		int size = listScoreSet.size();
		if (size > 0) {
			ScoreSet scoreLast = listScoreSet.get(size-1);

			int iCol0 = (scoreLast.getValue1() == null  ? 0 : scoreLast.getValue1().intValue());
			int iCol1 = (scoreLast.getValue2() == null  ? 0 : scoreLast.getValue2().intValue());
			if (iCol0 == -1) {
				ret = Invite.SCORE_RESULT.WO_VICTORY;
			} else if (iCol1 == -1) {
				ret = Invite.SCORE_RESULT.WO_DEFEAT;
			} else if (iCol0 > iCol1) {
				ret = Invite.SCORE_RESULT.VICTORY;
			} else if (iCol0 < iCol1) {
				ret = Invite.SCORE_RESULT.DEFEAT;
			}
		}
		return ret;
	}

	private void saveScoreSet() {
		if (invite.getId() != null) {
			scoreSetService.deleteByIdInvite(invite.getId());
		}

		List<ScoreSet> listScoreSet = computeScoreSet(getScores());
		int size = listScoreSet.size();
		if (size > 0) {
			for(ScoreSet scoreSet : listScoreSet) {
				scoreSetService.createOrUpdate(scoreSet);
			}
		}
		Invite.SCORE_RESULT scoreResult = computeScoreResult(listScoreSet);

		invite.setScoreResult(scoreResult);
		inviteService.createOrUpdate(invite);
	}

	public void setAddress(Address address) {
		locationParser.setAddress(invite, address);
	}

	public String[] getLocationLine() {
		return locationParser.toAddress(invite);
	}
	
	public String[] getLocationLineUser() {
		return locationParser.toAddress(user);
	}

	public void setClub(Club club) {
		invite.setClub(club);
	}
	
	public void setTournament(Tournament tournament) {
		invite.setTournament(tournament);
	}
}