package com.justtennis.domain.comparator;

import com.justtennis.domain.Club;

import java.util.Comparator;

public class ClubComparatorByName implements Comparator<Club> {

	private boolean inverse;

	public ClubComparatorByName(boolean inverse) {
		this.inverse = inverse;
	}

	@Override
	public int compare(Club lhs, Club rhs) {
		Club p1 = (inverse ? lhs : rhs);
		Club p2 = (inverse ? rhs : lhs);
		return p1.getName().compareTo(p2.getName());
	}
}
