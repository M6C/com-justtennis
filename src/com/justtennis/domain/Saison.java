package com.justtennis.domain;

import java.util.Date;

import com.cameleon.common.android.model.GenericDBPojoNamed;

public class Saison extends GenericDBPojoNamed {
	
	private static final long serialVersionUID = 1L;

	private Date begin;
	private Date end;
	private boolean active;

	public Saison() {
		super();
	}

	public Saison(Long id) {
		super(id);
	}

	public Saison(Long id, String name) {
		super(id, name);
	}

	public Date getBegin() {
		return begin;
	}
	public void setBegin(Date begin) {
		this.begin = begin;
	}
	public Date getEnd() {
		return end;
	}
	public void setEnd(Date end) {
		this.end = end;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
}