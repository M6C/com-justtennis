package com.justtennis.domain.comparator;

import java.util.Comparator;

import com.justtennis.domain.Player;

public class PlayerComparatorByName implements Comparator<Player> {

	private boolean inverse;

	public PlayerComparatorByName(boolean inverse) {
		this.inverse = inverse;
	}

	@Override
	public int compare(Player lhs, Player rhs) {
		Player p1 = (inverse ? lhs : rhs);
		Player p2 = (inverse ? rhs : lhs);
		return p1.getFullName().compareTo(p2.getFullName());
	}
}
