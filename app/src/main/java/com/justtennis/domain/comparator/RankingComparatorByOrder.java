package com.justtennis.domain.comparator;

import java.util.Comparator;

import com.justtennis.domain.Ranking;

public class RankingComparatorByOrder implements Comparator<Ranking> {

	private int sens = 1;

	public RankingComparatorByOrder() {
	}
	
	public RankingComparatorByOrder(boolean inverse) {
		sens = (inverse) ? -1 : 1;
	}

	@Override
	public int compare(Ranking lhs, Ranking rhs) {
		return (lhs.getOrder()>rhs.getOrder() ? (sens*1) : (sens*-1));
	}
}
