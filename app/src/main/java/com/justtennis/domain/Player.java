package com.justtennis.domain;

import android.graphics.Bitmap;

import com.justtennis.manager.TypeManager;


public class Player extends Person {
	
	private static final long serialVersionUID = 1L;

	private Long idSaison;
	private Long idTournament;
	private Long idClub;
	private Long idAddress;
	private Long idRanking;
	private Long idRankingEstimate;
	private Long idExternal;
	private Long idGoogle;
	private Bitmap photo;
	private TypeManager.TYPE type = TypeManager.TYPE.TRAINING;

	public Player() {
		super();
	}

	public Player(Long id) {
		super(id);
	}

	public Long getIdSaison() {
		return idSaison;
	}

	public void setIdSaison(Long idSaison) {
		this.idSaison = idSaison;
	}

	public Long getIdTournament() {
		return idTournament;
	}
	public void setIdTournament(Long idTournament) {
		this.idTournament = idTournament;
	}
	public Long getIdClub() {
		return idClub;
	}
	public void setIdClub(Long idClub) {
		this.idClub = idClub;
	}
	public Long getIdAddress() {
		return idAddress;
	}

	public void setIdAddress(Long idAddress) {
		this.idAddress = idAddress;
	}

	public Long getIdRanking() {
		return idRanking;
	}
	public void setIdRanking(Long idRanking) {
		this.idRanking = idRanking;
	}
	public Long getIdRankingEstimate() {
		return idRankingEstimate;
	}

	public void setIdRankingEstimate(Long idRankingEstimate) {
		this.idRankingEstimate = idRankingEstimate;
	}

	public Long getIdExternal() {
		return idExternal;
	}
	public void setIdExternal(Long idExternal) {
		this.idExternal = idExternal;
	}
	public Long getIdGoogle() {
		return idGoogle;
	}
	public void setIdGoogle(Long idGoogle) {
		this.idGoogle = idGoogle;
	}
	public Bitmap getPhoto() {
		return photo;
	}
	public void setPhoto(Bitmap photo) {
		this.photo = photo;
	}
	public TypeManager.TYPE getType() {
		return type;
	}
	public void setType(TypeManager.TYPE type) {
		this.type = type;
	}
}