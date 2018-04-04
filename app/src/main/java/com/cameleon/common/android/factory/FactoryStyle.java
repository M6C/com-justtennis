package com.cameleon.common.android.factory;

import android.app.Activity;
import android.view.Gravity;
import android.widget.TextView;

public class FactoryStyle {

	private static FactoryStyle instance;

	private FactoryStyle() {
	}

	public static FactoryStyle getInstance() {
		if (instance==null)
			instance = new FactoryStyle();
		return instance;
	}

	public void centerTitle(Activity activity) {
		((TextView)activity.findViewById(android.R.id.title)).setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
	}
}
