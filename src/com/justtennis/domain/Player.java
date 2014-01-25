package com.justtennis.domain;

import android.graphics.Bitmap;


public class Player extends Person {
	
	private static final long serialVersionUID = 1L;

	private Long idClub;
	private Long idRanking;
	private Long idExternal;
	private Long idGoogle;
	private Bitmap photo;

	public Long getIdClub() {
		return idClub;
	}
	public void setIdClub(Long idClub) {
		this.idClub = idClub;
	}
	public Long getIdRanking() {
		return idRanking;
	}
	public void setIdRanking(Long idRanking) {
		this.idRanking = idRanking;
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
}