package com.justtennis.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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

	public enum SCORE_RESULT {
		VICTORY, DEFEAT, UNFINISHED
	}
	private Player player;
	private User user;
	private Date date;
	private STATUS status = STATUS.UNKNOW;
	private Long idExternal;
	private Long idCalendar;
	private INVITE_TYPE type = INVITE_TYPE.ENTRAINEMENT;
	private Long idRanking;
	private SCORE_RESULT scoreResult = SCORE_RESULT.UNFINISHED;
	private List<ScoreSet> listScoreSet = null;
	private Address address;
	private Club club;
	private Tournament tournament;

	public Invite() {
	}
	
	public Invite(Long id) {
		super(id);
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

	public Long getIdRanking() {
		return idRanking;
	}

	public void setIdRanking(Long idRanking) {
		this.idRanking = idRanking;
	}

	public SCORE_RESULT getScoreResult() {
		return scoreResult;
	}

	public void setScoreResult(SCORE_RESULT scoreResult) {
		this.scoreResult = scoreResult;
	}

	public List<ScoreSet> getListScoreSet() {
		return listScoreSet;
	}

	public void setListScoreSet(List<ScoreSet> listScoreSet) {
		this.listScoreSet = listScoreSet;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Club getClub() {
		return club;
	}

	public void setClub(Club club) {
		this.club = club;
	}

	public Tournament getTournament() {
		return tournament;
	}

	public void setTournament(Tournament tournament) {
		this.tournament = tournament;
	}
	
	public Long getIdAddress() {
		return address == null ? null : address.getId();
	}

	public Long getIdClub() {
		return club == null ? null : club.getId();
	}
	
	public Long getIdTournament() {
		return tournament == null ? null : tournament.getId();
	}
}