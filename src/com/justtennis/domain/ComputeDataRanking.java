package com.justtennis.domain;

import java.util.List;

public class ComputeDataRanking {
	private Ranking ranking;
	private int nbVictory;
	private int nbVictoryCalculate;
	private int pointObjectif;
	private int pointCalculate;
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
}