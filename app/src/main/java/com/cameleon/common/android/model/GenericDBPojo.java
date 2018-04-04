package com.cameleon.common.android.model;

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

	@Override
	public boolean equals(Object o) {
		if (o instanceof GenericDBPojo<?>) {
			GenericDBPojo<?> pojo = (GenericDBPojo<?>)o;
			if (id == null && pojo.getId() == null) {
				return true;
			} else {
				return id.equals(pojo.getId());
			}
		}
		return super.equals(o);
	}

	@Override
	public String toString() {
		return "GenericDBPojo [id=" + id + "]";
	}
}
