package com.justtennis.activity;

import android.app.Activity;
import android.content.res.Resources.Theme;
import android.support.v7.app.AppCompatActivity;

public abstract class GenericAppCompatActivity extends AppCompatActivity {

	@SuppressWarnings("unused")
	private static final String TAG = GenericAppCompatActivity.class.getSimpleName();

	@Override
	protected void onApplyThemeResource(Theme theme, int resid, boolean first) {
//		resid = TypeManager.getThemeResource();
//
//		theme.applyStyle(resid, true);

		super.onApplyThemeResource(theme, resid, first);
	}
}