package com.justtennis.domain;

import java.util.List;

public class ComputeDataRanking {
	private Ranking ranking;
	private int nbMatch;
	private int nbVictory;
	private int nbVictoryCalculate;
	private int nbVictoryAdditional;
	private int pointObjectif;
	private int pointCalculate;
	private int pointBonus;
	private int vE2I5G;
	private List<Invite> listInviteCalculed;
	private List<Invite> listInviteNotUsed;

	public ComputeDataRanking() {
	}

	public Ranking getRanking() {
		return ranking;
	}

	public void setRanking(Ranking ranking) {
		this.ranking = ranking;
	}

	public int getNbMatch() {
		return nbMatch;
	}

	public void setNbMatch(int nbMatch) {
		this.nbMatch = nbMatch;
	}

	public int getNbVictory() {
		return nbVictory;
	}

	public void setNbVictory(int nbVictory) {
		this.nbVictory = nbVictory;
	}

	public int getNbVictoryCalculate() {
		return nbVictoryCalculate;
	}

	public void setNbVictoryCalculate(int nbVictoryCalculate) {
		this.nbVictoryCalculate = nbVictoryCalculate;
	}

	/**
	 * @return the nbVictoryAdditional
	 */
	public int getNbVictoryAdditional() {
		return nbVictoryAdditional;
	}

	/**
	 * @param nbVictoryAdditional the nbVictoryAdditional to set
	 */
	public void setNbVictoryAdditional(int nbVictoryAdditional) {
		this.nbVictoryAdditional = nbVictoryAdditional;
	}

	public int getPointObjectif() {
		return pointObjectif;
	}

	public void setPointObjectif(int pointObjectif) {
		this.pointObjectif = pointObjectif;
	}

	public int getPointCalculate() {
		return pointCalculate;
	}

	public void setPointCalculate(int pointCalculate) {
		this.pointCalculate = pointCalculate;
	}

	public int getPointBonus() {
		return pointBonus;
	}

	public void setPointBonus(int pointBonus) {
		this.pointBonus = pointBonus;
	}

	public List<Invite> getListInviteCalculed() {
		return listInviteCalculed;
	}

	public void setListInviteCalculed(List<Invite> listInviteCalculed) {
		this.listInviteCalculed = listInviteCalculed;
	}

	public List<Invite> getListInviteNotUsed() {
		return listInviteNotUsed;
	}

	public void setListInviteNotUsed(List<Invite> listInviteNotUsed) {
		this.listInviteNotUsed = listInviteNotUsed;
	}

	public int getVE2I5G() {
		return vE2I5G;
	}

	public void setVE2I5G(int vE2I5G) {
		this.vE2I5G = vE2I5G;
	}
}