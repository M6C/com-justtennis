package com.justtennis.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.ApplicationConfig;
import com.justtennis.R;
import com.justtennis.activity.InviteActivity;
import com.justtennis.activity.InviteDemandeActivity.MODE;
import com.justtennis.activity.PlayerActivity;
import com.justtennis.db.service.InviteService;
import com.justtennis.db.service.MessageService;
import com.justtennis.db.service.PlayerService;
import com.justtennis.db.service.RankingService;
import com.justtennis.db.service.UserService;
import com.justtennis.domain.Club;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Invite.STATUS;
import com.justtennis.domain.Player;
import com.justtennis.domain.Ranking;
import com.justtennis.domain.User;
import com.justtennis.domain.comparator.RankingComparatorByOrder;
import com.justtennis.helper.GCalendarHelper;
import com.justtennis.helper.GCalendarHelper.EVENT_STATUS;
import com.justtennis.manager.SmsManager;
import com.justtennis.manager.TypeManager;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.parser.SmsParser;

public class InviteDemandeBusiness {

	private static final String TAG = InviteDemandeBusiness.class.getSimpleName();

	private Context context;
	private INotifierMessage notification;
	private InviteService inviteService;
	private UserService userService;
	private PlayerService playerService;
	private GCalendarHelper gCalendarHelper;
	private User user;
	private Invite invite;
	private MODE mode = MODE.INVITE_DEMANDE;
	private List<Ranking> listRanking;
	private List<String> listTxtRankings;

	private RankingService rankingService;

	public InviteDemandeBusiness(Context context, INotifierMessage notificationMessage) {
		this.context = context;
		this.notification = notificationMessage;
		inviteService = new InviteService(context, notificationMessage);
		playerService = new PlayerService(context, notificationMessage);
		userService = new UserService(context, notificationMessage);
		gCalendarHelper = GCalendarHelper.getInstance(context);
	}

	public void initializeData(Intent intent) {
		initializeDataRanking();

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
		}
		if (intent.hasExtra(InviteActivity.EXTRA_PLAYER_ID)) {
			long id = intent.getLongExtra(InviteActivity.EXTRA_PLAYER_ID, PlayerService.ID_EMPTY_PLAYER);
			if (id != PlayerService.ID_EMPTY_PLAYER) {
				invite.setPlayer(playerService.find(id));
				if (isUnknownPlayer()) {
					setIdRanking(getListRanking().get(0).getId());
					setType(TypeManager.TYPE.COMPETITION);
				} else {
					setIdRanking(getPlayer().getIdRanking());
					switch (getPlayer().getType()) {
						default:
						case ENTRAINEMENT:
							setType(TypeManager.TYPE.ENTRAINEMENT);
							break;
						case COMPETITION:
							setType(TypeManager.TYPE.COMPETITION);
							break;
					}
				}
				
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
	}

	public void initializeData(Bundle savedInstanceState) {
		mode = (MODE) savedInstanceState.getSerializable(InviteActivity.EXTRA_MODE);
		invite = (Invite) savedInstanceState.getSerializable(PlayerActivity.EXTRA_INVITE);

		initializeDataRanking();
	}

	public void initializeDataRanking() {
		SortedSet<Ranking> setRanking = new TreeSet<Ranking>(new RankingComparatorByOrder());

		rankingService = new RankingService(context, NotifierMessageLogger.getInstance());
		listRanking = rankingService.getList();
		setRanking.addAll(listRanking);
		
		listRanking.clear();
		listRanking.addAll(setRanking);

		int i=0;
		listTxtRankings = new ArrayList<String>();
		for(Ranking ranking : setRanking) {
			listTxtRankings.add(ranking.getRanking());
		}
	}

	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(InviteActivity.EXTRA_MODE, mode);
		outState.putSerializable(InviteActivity.EXTRA_INVITE, invite);
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
	
	public void send(String text, boolean addCalendar) {
//		Date date = invite.getDate();
//		Invite invite = new Invite(user, player, date, getType());
		invite.setStatus(this.invite.getStatus());
		if (this.invite!=null) {
			invite.setId(this.invite.getId());
		}

		if (invite.getType() == TypeManager.TYPE.ENTRAINEMENT) {
			if (invite.getPlayer().getIdClub() != null) {
				invite.setClub(new Club(invite.getPlayer().getIdClub()));
			}
		} else {
		}
		inviteService.createOrUpdate(invite);

		Player player = getPlayer();
		if (addCalendar) {
			calendarAddEvent(invite, EVENT_STATUS.CONFIRMED);
		}

		if (text!=null) {
			if (player.getPhonenumber()!=null && !player.getPhonenumber().equals("")) {
				SmsManager.getInstance().send(context, player.getPhonenumber(), text);
			}
		}
		else {
			Toast.makeText(context, R.string.msg_no_message_to_send, Toast.LENGTH_LONG).show();
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

	public boolean isEmptyRanking(Ranking ranking) {
		return rankingService.isEmptyRanking(ranking);
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

	public TypeManager.TYPE getType() {
		return invite.getType();
	}

	public void setType(TypeManager.TYPE type) {
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
}