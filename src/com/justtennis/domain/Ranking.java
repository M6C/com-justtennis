package com.justtennis.domain;

import com.cameleon.common.android.model.GenericDBPojo;


public class Ranking extends GenericDBPojo<Long> {
	
	private static final long serialVersionUID = 1;
	private String ranking;
	private Integer serie;
	private Integer order;
	private Integer rankingPointMan;
	private Integer rankingPointWoman;
	private Integer VictoryMan;
	private Integer VictoryWoman;

	public Ranking() {
		super();
	}

	public Ranking(Long id) {
		super(id);
	}

	public String getRanking() {
		return ranking;
	}

	public void setRanking(String ranking) {
		this.ranking = ranking;
	}

	public Integer getSerie() {
		return serie;
	}

	public void setSerie(Integer serie) {
		this.serie = serie;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public Integer getRankingPointMan() {
		return rankingPointMan;
	}

	public void setRankingPointMan(Integer rankingPointMan) {
		this.rankingPointMan = rankingPointMan;
	}

	public Integer getRankingPointWoman() {
		return rankingPointWoman;
	}

	public void setRankingPointWoman(Integer rankingPointWoman) {
		this.rankingPointWoman = rankingPointWoman;
	}

	public Integer getVictoryMan() {
		return VictoryMan;
	}

	public void setVictoryMan(Integer victoryMan) {
		VictoryMan = victoryMan;
	}

	public Integer getVictoryWoman() {
		return VictoryWoman;
	}

	public void setVictoryWoman(Integer victoryWoman) {
		VictoryWoman = victoryWoman;
	}
}