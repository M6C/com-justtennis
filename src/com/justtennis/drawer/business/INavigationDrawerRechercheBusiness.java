package com.justtennis.drawer.business;

import java.util.Collection;

import com.justtennis.domain.RechercheResult;

public interface INavigationDrawerRechercheBusiness {

	public abstract Collection<? extends RechercheResult> find(String text);

	public String getFindText();

	public void setFindText(String text);
}
