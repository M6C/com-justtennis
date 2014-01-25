package com.justtennis.domain.comparator;

import java.util.Comparator;

import com.justtennis.domain.Ranking;

public class RankingComparatorByOrder implements Comparator<Ranking> {

	@Override
	public int compare(Ranking lhs, Ranking rhs) {
		return (lhs.getOrder()>rhs.getOrder() ? 1 : -1);
	}
}
