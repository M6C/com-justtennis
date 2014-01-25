package com.justtennis.domain;

import java.io.Serializable;
import java.util.Date;

public class Invite extends GenericDBPojo<Long> implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static final long PLAY_DURATION_DEFAULT = 60 * 60 * 1000;

	public enum STATUS {
		UNKNOW,
		ACCEPT,
		REFUSE
	};

	public enum INVITE_TYPE {
		ENTRAINEMENT,
		MATCH
	};

	private Player player;
	private User user;
	private Date date;
	private STATUS status = STATUS.UNKNOW;
	private Long idExternal;
	private Long idCalendar;
	private INVITE_TYPE type = INVITE_TYPE.ENTRAINEMENT;

	public Invite() {
	}

	public Invite(User user, Player player) {
		this.user = user;
		this.player = player;
	}

	public Invite(User user, Player player, Date date) {
		this.user = user;
		this.player = player;
		this.date = date;
	}

	public Invite(User user, Player player, Date date, INVITE_TYPE type) {
		this.user = user;
		this.player = player;
		this.date = date;
		this.type = type;
	}

	public Invite(User user, Player player, Date date, STATUS status) {
		this.user = user;
		this.player = player;
		this.date = date;
		this.status = status;
	}

	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Player getPlayer() {
		return player;
	}
	public void setPlayer(Player player) {
		this.player = player;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public STATUS getStatus() {
		return status;
	}
	public void setStatus(STATUS status) {
		this.status = status;
	}
	public Long getIdExternal() {
		return idExternal;
	}
	public void setIdExternal(Long idExternal) {
		this.idExternal = idExternal;
	}
	public Long getIdCalendar() {
		return idCalendar;
	}
	public void setIdCalendar(Long idCalendar) {
		this.idCalendar = idCalendar;
	}
	public INVITE_TYPE getType() {
		return type;
	}
	public void setType(INVITE_TYPE type) {
		this.type = type;
	}
}