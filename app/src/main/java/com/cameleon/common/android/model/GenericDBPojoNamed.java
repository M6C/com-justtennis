package com.cameleon.common.android.model;

import com.cameleon.common.android.model.interfaces.IPojoNamed;


public class GenericDBPojoNamed extends GenericDBPojo<Long> implements IPojoNamed {
	
	private static final long serialVersionUID = 1L;

	private String name;

	public GenericDBPojoNamed() {
		super();
	}

	public GenericDBPojoNamed(Long id) {
		super(id);
	}

	public GenericDBPojoNamed(Long id, String name) {
		super(id);
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}