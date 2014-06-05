package com.justtennis.domain.comparator;

import java.util.Comparator;

import com.justtennis.domain.Tournament;

public class TournamentComparatorByDate implements Comparator<Tournament> {

	private boolean inverse;

	public TournamentComparatorByDate(boolean inverse) {
		this.inverse = inverse;
	}

	@Override
	public int compare(Tournament lhs, Tournament rhs) {
		if (lhs.getId().equals(rhs.getId())) {
			return 0;
		}
		int order = (lhs.getId().longValue() > rhs.getId().longValue() ? 1 : -1);
		if (inverse) {
			order = -order;
		}
		return order;
	}
}
