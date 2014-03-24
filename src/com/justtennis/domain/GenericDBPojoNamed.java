package com.justtennis.domain;

import com.justtennis.domain.interfaces.IPojoNamed;


public class GenericDBPojoNamed extends GenericDBPojo<Long> implements IPojoNamed {
	
	private static final long serialVersionUID = 1L;

	private String name;

	@Override
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}