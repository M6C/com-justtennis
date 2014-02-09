package com.justtennis.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.justtennis.db.service.UserService;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Invite.INVITE_TYPE;
import com.justtennis.domain.Invite.STATUS;
import com.justtennis.domain.Player;
import com.justtennis.domain.User;
import com.justtennis.helper.GCalendarHelper;
import com.justtennis.helper.GCalendarHelper.EVENT_STATUS;
import com.justtennis.manager.SmsManager;
import com.justtennis.parser.SmsParser;

public class InviteBusiness {

	private Context context;
	private INotifierMessage notification;
	private InviteService inviteService;
	private UserService userService;
	private PlayerService playerService;
	private GCalendarHelper gCalendarHelper;
//	private User user;
//	private Player player;
	private Invite invite;
	private List<Invite> list = new ArrayList<Invite>();
	private MODE mode = MODE.INVITE_DEMANDE;

	public InviteBusiness(Context context, INotifierMessage notificationMessage) {
		this.context = context;
		this.notification = notificationMessage;
		inviteService = new InviteService(context, notificationMessage);
		playerService = new PlayerService(context, notificationMessage);
		userService = new UserService(context, notificationMessage);
		gCalendarHelper = GCalendarHelper.getInstance(context);
	}

	public void initializeData(Intent intent) {
		invite = new Invite();
		invite.setUser(userService.find());

		if (intent.hasExtra(InviteActivity.EXTRA_MODE)) {
			mode = (MODE) intent.getSerializableExtra(InviteActivity.EXTRA_MODE);
		}

		if (intent.hasExtra(InviteActivity.EXTRA_INVITE)) {
			invite = (Invite) intent.getSerializableExtra(PlayerActivity.EXTRA_INVITE);
		}
		if (intent.hasExtra(InviteActivity.EXTRA_PLAYER_ID)) {
			long id = intent.getLongExtra(InviteActivity.EXTRA_PLAYER_ID, -1);
			invite.setPlayer(playerService.find(id));
		}

		if (invite.getDate()==null) {
			Calendar calendar = GregorianCalendar.getInstance(ApplicationConfig.getLocal());
			calendar.setTime(new Date());
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.add(Calendar.HOUR_OF_DAY, 1);
			invite.setDate(calendar.getTime());
		}
		
		initializeDataInvite();
	}

	public void initializeData(Bundle savedInstanceState) {
		mode = (MODE) savedInstanceState.getSerializable(InviteActivity.EXTRA_MODE);
		invite = (Invite) savedInstanceState.getSerializable(PlayerActivity.EXTRA_INVITE);
		initializeDataInvite();
	}

	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(InviteActivity.EXTRA_MODE, mode);
		outState.putSerializable(InviteActivity.EXTRA_INVITE, invite);
	}

	public List<Invite> getList() {
		return list;
	}

	private void initializeDataInvite() {
		list.clear();
		if (getPlayer()!=null) {
			List<Invite> listInvite = inviteService.getByIdPlayer(getPlayer().getId());
			for(Invite inv : listInvite) {
				inv.setPlayer(playerService.find(inv.getPlayer().getId()));
			}
			list.addAll(listInvite);
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
		return playerService.isUnknownPlayer(getPlayer());
	}
	
	public void send(String text) {
//		Date date = invite.getDate();
//		Invite invite = new Invite(user, player, date, getType());
		invite.setStatus(this.invite.getStatus());
		if (this.invite!=null) {
			invite.setId(this.invite.getId());
		}

		inviteService.createOrUpdate(invite);

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

		if (inv != null && inv.getIdCalendar() != null && 
			inv.getIdCalendar() != GCalendarHelper.EVENT_ID_NO_CREATED && 
			inv.getStatus() != invite.getStatus()) {
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

	public void setDate(Date date) {
		invite.setDate(date);
	}
	
	public Date getDate() {
		return invite.getDate();
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
}