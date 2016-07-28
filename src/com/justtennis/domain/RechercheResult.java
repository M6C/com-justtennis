package com.justtennis.domain;

import com.justtennis.db.service.RechercheService.TYPE;

public class RechercheResult {

	private long id;
	private TYPE type;
	private String data;

	public RechercheResult(TYPE type, long id, String data) {
		super();
		this.type = type;
		this.id = id;
		this.data = data;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public TYPE getType() {
		return type;
	}

	public void setType(TYPE type) {
		this.type = type;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
