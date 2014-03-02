package com.justtennis.domain;

import com.justtennis.domain.interfaces.IPojoNamed;


public class Tournament extends GenericDBPojo<Long> implements IPojoNamed {
	
	private static final long serialVersionUID = 1L;

	private String name;
	private Long idAddress;

	@Override
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getIdAddress() {
		return idAddress;
	}
	public void setIdAddress(Long idAddress) {
		this.idAddress = idAddress;
	}
}