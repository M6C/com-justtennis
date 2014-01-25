package com.justtennis.domain;


public class Ranking extends GenericDBPojo<Long> {
	
	private static final long serialVersionUID = 1;
	private String ranking;
	private Integer serie;
	private Integer order;

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
}