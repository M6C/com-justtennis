package com.justtennis.domain;

import com.cameleon.common.android.model.GenericDBPojoNamedSubId;



public class Club extends GenericDBPojoNamedSubId {
	
	private static final long serialVersionUID = 1L;

	public Club() {
		super();
	}

	public Club(Long id) {
		super(id);
	}

	public Club(Long id, String name) {
		super(id, name);
	}
}