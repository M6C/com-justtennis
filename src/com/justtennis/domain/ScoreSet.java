package com.justtennis.domain;

import java.io.Serializable;

public class ScoreSet extends GenericDBPojo<Long> implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private Invite invite;
	private Integer order;
	private Integer value1;
	private Integer value2;

	public ScoreSet() {
		super();
	}

	public ScoreSet(Long id) {
		super(id);
	}

	public ScoreSet(Invite invite, Integer order, Integer value1, Integer value2) {
		super();
		this.invite = invite;
		this.order = order;
		this.value1 = value1;
		this.value2 = value2;
	}

	public Invite getInvite() {
		return invite;
	}

	public void setInvite(Invite invite) {
		this.invite = invite;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public Integer getValue1() {
		return value1;
	}

	public void setValue1(Integer value1) {
		this.value1 = value1;
	}

	public Integer getValue2() {
		return value2;
	}

	public void setValue2(Integer value2) {
		this.value2 = value2;
	}
}