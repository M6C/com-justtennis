package com.justtennis.domain;

import java.io.Serializable;

public class PalmaresFastValue implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Ranking ranking;
	private int nbVictory;
	private int nbDefeat;

	public PalmaresFastValue() {
	}

	public PalmaresFastValue(Ranking ranking, int nbVictory, int nbDefeat) {
		this.ranking = ranking;
		this.nbVictory = nbVictory;
		this.nbDefeat = nbDefeat;
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

	public int getNbDefeat() {
		return nbDefeat;
	}

	public void setNbDefeat(int nbDefeat) {
		this.nbDefeat = nbDefeat;
	}
}