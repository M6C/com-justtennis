package com.justtennis.domain;

import com.justtennis.domain.interfaces.IPojoNamed;


public class Tournament extends GenericDBPojo<Long> implements IPojoNamed {
	
	private static final long serialVersionUID = 1L;

	private String name;
	private Long idClub;

	@Override
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getIdClub() {
		return idClub;
	}
	public void setIdClub(Long idClub) {
		this.idClub = idClub;
	}
}