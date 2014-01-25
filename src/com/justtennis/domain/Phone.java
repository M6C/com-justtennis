package com.justtennis.domain;

public class Phone extends GenericDBPojo<Long> {

	private static final long serialVersionUID = 1L;

	private String number;
	private int type;
	
	public String getNumber() {
		return number;
	}
	
	public void setNumber(String number) {
		this.number = number;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}