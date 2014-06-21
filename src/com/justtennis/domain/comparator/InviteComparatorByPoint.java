package com.justtennis.domain.comparator;

import java.util.Comparator;

import com.justtennis.domain.Invite;

public class InviteComparatorByPoint implements Comparator<Invite> {

	private boolean inverse;

	public InviteComparatorByPoint(boolean inverse) {
		this.inverse = inverse;
	}

	@Override
	public int compare(Invite lhs, Invite rhs) {
		int order = (lhs.getPoint() > rhs.getPoint() ? 1 : -1);
		if (inverse) {
			order = -order;
		}
		return order;
	}
}
