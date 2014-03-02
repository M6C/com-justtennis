package com.justtennis.domain.comparator;

import java.util.Comparator;

import com.justtennis.domain.interfaces.IPojoNamed;

public class PojoNamedComparator implements Comparator<IPojoNamed> {

	@Override
	public int compare(IPojoNamed lhs, IPojoNamed rhs) {
		if (lhs.getName() == null && rhs.getName() == null) {
			return 0;
		} else if (lhs.getName() == null) {
			return 1;
		} else if (rhs.getName() == null) {
			return -1;
		} else {
			return lhs.getName().compareTo(rhs.getName());
		}
	}
}
