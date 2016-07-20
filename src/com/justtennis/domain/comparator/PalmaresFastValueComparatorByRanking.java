package com.justtennis.domain.comparator;

import java.util.Comparator;

import com.justtennis.domain.PalmaresFastValue;
import com.justtennis.domain.Ranking;

public class PalmaresFastValueComparatorByRanking implements Comparator<Object> {

	@Override
	public int compare(Object lhs, Object rhs) {
		int ret = -1;
		Ranking lRank = getRanking(lhs);
		Ranking rRank = getRanking(rhs);

		if (lRank == null) {
			ret = (rRank == null) ? 0 : 1;
		}
		else if (rRank != null) {
			ret = lRank.equals(rRank) ? 0 : -1;
		}

		return ret;
	};

	private Ranking getRanking(Object o) {
		if (o instanceof PalmaresFastValue && o != null) {
			return ((PalmaresFastValue)o).getRanking();
		}
		else if (o instanceof Ranking) {
			return (Ranking)o;
		}
		return null;
	}
}
