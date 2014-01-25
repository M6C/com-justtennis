package com.justtennis.domain.comparator;

import java.util.Comparator;

import com.justtennis.domain.Invite;

public class InviteComparatorByDate implements Comparator<Invite> {

	private boolean inverse;

	public InviteComparatorByDate(boolean inverse) {
		this.inverse = inverse;
	}

	@Override
	public int compare(Invite lhs, Invite rhs) {
		int order = (lhs.getDate().after(rhs.getDate()) ? 1 : -1);
		if (inverse) {
			order = -order;
		}
		return order;
	}
}
