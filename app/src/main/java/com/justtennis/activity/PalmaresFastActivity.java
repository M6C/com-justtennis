package com.justtennis.activity;

import android.support.v4.app.Fragment;

import com.justtennis.ui.activity.AbsctractFragmentActivity;
import com.justtennis.ui.fragment.PalmaresFastFragment;

public class PalmaresFastActivity extends AbsctractFragmentActivity {

	@Override
	protected Fragment createFragment() {
		return new PalmaresFastFragment();
	}
}