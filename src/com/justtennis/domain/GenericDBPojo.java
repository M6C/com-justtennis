package com.justtennis.domain;

import java.io.Serializable;

public class GenericDBPojo<ID> implements Serializable {
	
	private static final long serialVersionUID = 8200368381507195391L;

	private ID id;

	public GenericDBPojo() {
		super();
	}
	
	public GenericDBPojo(ID id) {
		super();
		this.id = id;
	}

	public ID getId() {
		return id;
	}

	public void setId(ID id) {
		this.id = id;
	}
}
