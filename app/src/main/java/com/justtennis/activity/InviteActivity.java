package com.justtennis.activity;

import android.support.v4.app.Fragment;

import com.justtennis.ui.activity.AbsctractFragmentActivity;
import com.justtennis.ui.fragment.InviteFragment;

public class InviteActivity extends AbsctractFragmentActivity {

	public static final String EXTRA_MODE = "MODE";
	public static final String EXTRA_INVITE = "INVITE";
	public static final String EXTRA_USER = "USER";
	public static final String EXTRA_PLAYER_ID = "PLAYER_ID";

	@Override
	protected Fragment createFragment() {
		return new InviteFragment();
	}
}