package com.justtennis.activity;

import android.app.Activity;
import android.content.res.Resources.Theme;

import com.justtennis.manager.TypeManager;

public abstract class GenericActivity extends Activity {

	@SuppressWarnings("unused")
	private static final String TAG = GenericActivity.class.getSimpleName();

	@Override
	protected void onApplyThemeResource(Theme theme, int resid, boolean first) {
		resid = TypeManager.getThemeResource();

		theme.applyStyle(resid, true);

		super.onApplyThemeResource(theme, resid, first);
	}
}