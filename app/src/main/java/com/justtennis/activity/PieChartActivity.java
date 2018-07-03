package com.justtennis.activity;

import android.support.v4.app.Fragment;

import com.justtennis.ui.activity.AbsctractFragmentActivity;
import com.justtennis.ui.fragment.PieChartFragment;

//http://code.google.com/p/achartengine/source/browse/trunk/achartengine/
public class PieChartActivity extends AbsctractFragmentActivity {

	@Override
	protected Fragment createFragment() {
		return new PieChartFragment();
	}
}