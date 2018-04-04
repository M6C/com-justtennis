package com.cameleon.common.android.model;



public class GenericDBPojoNamedSubId extends GenericDBPojoNamed {
	
	private static final long serialVersionUID = 1L;

	private Long subId;

	public GenericDBPojoNamedSubId() {
		super();
	}

	public GenericDBPojoNamedSubId(Long id) {
		super(id);
	}

	public GenericDBPojoNamedSubId(Long id, String name) {
		super(id, name);
	}

	public Long getSubId() {
		return subId;
	}

	public void setSubId(Long subId) {
		this.subId = subId;
	}
}