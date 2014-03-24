package com.justtennis.domain;



public class Tournament extends GenericDBPojoNamed {
	
	private static final long serialVersionUID = 1L;

	private Long idClub;

	public Long getIdClub() {
		return idClub;
	}
	public void setIdClub(Long idClub) {
		this.idClub = idClub;
	}
}