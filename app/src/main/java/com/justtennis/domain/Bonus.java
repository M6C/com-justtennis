package com.justtennis.domain;

import com.cameleon.common.android.model.GenericDBPojo;

public class Bonus extends GenericDBPojo<Long> {
	private int point;

	public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		this.point = point;
	}
}
