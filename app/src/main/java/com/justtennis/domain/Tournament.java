package com.justtennis.domain;

import com.cameleon.common.android.model.GenericDBPojoNamedSubId;



public class Tournament extends GenericDBPojoNamedSubId {
	
	private static final long serialVersionUID = 1L;

	private Saison saison;

	public Tournament() {
		super();
	}

	public Tournament(Long id) {
		super(id);
	}

	public Saison getSaison() {
		return saison;
	}

	public void setSaison(Saison saison) {
		this.saison = saison;
	}
}