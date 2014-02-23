package com.justtennis.business;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.ApplicationConfig;
import com.justtennis.R;
import com.justtennis.activity.InviteActivity;
import com.justtennis.activity.InviteActivity.MODE;
import com.justtennis.activity.PlayerActivity;
import com.justtennis.db.service.InviteService;
import com.justtennis.db.service.MessageService;
import com.justtennis.db.service.PlayerService;
import com.justtennis.db.service.RankingService;
import com.justtennis.db.service.ScoreSetService;
import com.justtennis.db.service.UserService;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Invite.INVITE_TYPE;
import com.justtennis.domain.Invite.STATUS;
import com.justtennis.domain.Player;
import com.justtennis.domain.Ranking;
import com.justtennis.domain.ScoreSet;
import com.justtennis.domain.User;
import com.justtennis.helper.GCalendarHelper;
import com.justtennis.helper.GCalendarHelper.EVENT_STATUS;
import com.justtennis.manager.SmsManager;
import com.justtennis.parser.SmsParser;

public class InviteBusiness {

	private static final String TAG = InviteBusiness.class.getSimpleName();

	private Context context;
	private INotifierMessage notification;
	private InviteService inviteService;
	private UserService userService;
	private PlayerService playerService;
	private ScoreSetService scoreSetService;
	private GCalendarHelper gCalendarHelper;
	private User user;
	private Invite invite;
	private MODE mode = MODE.INVITE_MODIFY;
	private List<Ranking> listRanking;
	private String[] listTxtRankings;
	private String[][] scores;

	private RankingService rankingService;


	public InviteBusiness(Context context, INotifierMessage notificationMessage) {
		this.context = context;
		this.notification = notificationMessage;
		inviteService = new InviteService(context, notificationMessage);
		playerService = new PlayerService(context, notificationMessage);
		userService = new UserService(context, notificationMessage);
		rankingService = new RankingService(context, notificationMessage);
		scoreSetService = new ScoreSetService(context, notificationMessage);
		gCalendarHelper = GCalendarHelper.getInstance(context);
	}

	public void initializeData(Intent intent) {
		user = userService.find();

		invite = new Invite();
		invite.setUser(getUser());

		if (intent.hasExtra(InviteActivity.EXTRA_MODE)) {
			mode = (MODE) intent.getSerializableExtra(InviteActivity.EXTRA_MODE);
		}

		if (intent.hasExtra(InviteActivity.EXTRA_INVITE)) {
			invite = (Invite) intent.getSerializableExtra(PlayerActivity.EXTRA_INVITE);
			if (getIdRanking()==null) {
				setIdRanking(getPlayer().getIdRanking());
			}
			initializeScores();
		}
		if (intent.hasExtra(InviteActivity.EXTRA_PLAYER_ID)) {
			long id = intent.getLongExtra(InviteActivity.EXTRA_PLAYER_ID, PlayerService.ID_EMPTY_PLAYER);
			if (id != PlayerService.ID_EMPTY_PLAYER) {
				invite.setPlayer(playerService.find(id));
				setIdRanking(getPlayer().getIdRanking());
			}
		}

		if (invite.getDate()==null) {
			Calendar calendar = GregorianCalendar.getInstance(ApplicationConfig.getLocal());
			calendar.setTime(new Date());
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.add(Calendar.HOUR_OF_DAY, 1);
			invite.setDate(calendar.getTime());
		}

		initializeDataRanking();
	}

	public void initializeData(Bundle savedInstanceState) {
		mode = (MODE) savedInstanceState.getSerializable(InviteActivity.EXTRA_MODE);
		invite = (Invite) savedInstanceState.getSerializable(PlayerActivity.EXTRA_INVITE);

		initializeDataRanking();
	}

	public void initializeDataRanking() {

		listRanking = rankingService.getList();
		rankingService.order(listRanking);

		int i=0;
		listTxtRankings = new String[listRanking.size()];
		for(Ranking ranking : listRanking) {
			listTxtRankings[i++] = ranking.getRanking();
		}
	}

	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(InviteActivity.EXTRA_MODE, mode);
		outState.putSerializable(InviteActivity.EXTRA_INVITE, invite);
	}

	private void initializeScores() {
		scores = scoreSetService.getTableByIdInvite(getInvite().getId());
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
		return getPlayer() != null && playerService.isUnknownPlayer(getPlayer());
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
		Invite inv = inviteService.find(invite.getId());
		inviteService.createOrUpdate(invite);
		
		saveScoreSet();

		if (inv != null && inv.getIdCalendar() != null && 
			inv.getIdCalendar() != GCalendarHelper.EVENT_ID_NO_CREATED) {
			EVENT_STATUS status = gCalendarHelper.toEventStatus(invite.getStatus());
//			gCalendarHelper.updateEventStatus(invite.getIdCalendar(), status);
//			gCalendarHelper.recreateEventStatus(invite.getIdCalendar(), status);
			calendarAddEvent(invite, status);
			gCalendarHelper.deleteCalendarEntry(inv.getIdCalendar());
		}
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

	public Invite getInvite() {
		return invite;
	}

	public void setInvite(Invite invite) {
		this.invite = invite;
	}

	public INVITE_TYPE getType() {
		return invite.getType();
	}

	public void setType(INVITE_TYPE type) {
		invite.setType(type);
	}

	public void setStatus(STATUS status) {
		invite.setStatus(status);
	}

	public Player getPlayer() {
		return invite.getPlayer();
	}

	public void setPlayer(Player player) {
		this.invite.setPlayer(player);
	}

	public MODE getMode() {
		return mode;
	}

	public void setPlayer(long id) {
		this.invite.setPlayer(playerService.find(id));
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

	public String[] getListTxtRankings() {
		return listTxtRankings;
	}

	public void setListTxtRankings(String[] listTxtRankings) {
		this.listTxtRankings = listTxtRankings;
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
		if (getType()==INVITE_TYPE.MATCH) {
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
	
	private void addScoreSet(Integer order, String score1, String score2, boolean last) {
		if (checkScoreSet(score1, score2, last)) {
			ScoreSet pojo = new ScoreSet();
			pojo.setInvite(invite);
			pojo.setOrder(order);
			pojo.setValue1(Integer.parseInt(score1));
			pojo.setValue2(Integer.parseInt(score2));
			scoreSetService.createOrUpdate(pojo);
		}
	}

	private void saveScoreSet() {
		String[][] scores = getScores();
		scoreSetService.deleteByIdInvite(invite.getId());

		int len = scores.length;
		String[] colLast = null;
		for(int row = 1 ; row <= len ; row++) {
			String[] col = scores[row-1];
			addScoreSet(row, col[0], col[1], row==len);
			if (col[0]!=null && !col[0].equals("") &&
				col[1]!=null && !col[1].equals("")) {
				colLast = col;
			}
		}

		Invite.SCORE_RESULT scoreResult = Invite.SCORE_RESULT.UNFINISHED;
		if (colLast!=null && colLast.length==2) {
			String col0 = colLast[0];
			String col1 = colLast[1];
			int iCol0 = 0;
			int iCol1 = 0;
			try {
				iCol0 = (col0==null || col0.equals("")) ? 0 : Integer.parseInt(col0);
			} catch(NumberFormatException ex) {
			}
			try {
				iCol1 = (col1==null || col1.equals("")) ? 0 : Integer.parseInt(col1);
			} catch(NumberFormatException ex) {
			}
			int[] iCol = new int[]{iCol0, iCol1}; 

			if (iCol[0] > iCol[1]) {
				scoreResult = Invite.SCORE_RESULT.VICTORY;
			} else if (iCol[0] < iCol[1]) {
				scoreResult = Invite.SCORE_RESULT.DEFEAT;
			}
		}
		invite.setScoreResult(scoreResult);
		inviteService.createOrUpdate(invite);
	}
}