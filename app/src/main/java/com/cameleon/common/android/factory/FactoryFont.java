package com.cameleon.common.android.factory;

import android.content.Context;
import android.graphics.Typeface;

public class FactoryFont {

	private static FactoryFont instance;

	public static FactoryFont getInstance() {
		if (instance==null)
			instance = new FactoryFont();
		return instance;
	}

	public Typeface buildDigital(Context context) {
		Typeface ret = null;
		try {
			ret = Typeface.createFromAsset(context.getAssets(),"font/Clock_Digital_Regular.ttf");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return ret;
	}
}
