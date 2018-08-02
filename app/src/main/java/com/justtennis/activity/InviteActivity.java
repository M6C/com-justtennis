package com.justtennis.activity;

import android.support.v4.app.Fragment;

import com.justtennis.ui.activity.AbsctractFragmentActivity;
import com.justtennis.ui.fragment.InviteFragment;

public class InviteActivity extends AbsctractFragmentActivity {

	public static final String EXTRA_MODE = "INVITE_MODE";
	public static final String EXTRA_INVITE = "INVITE";
	public static final String EXTRA_USER = "USER";
	public static final String EXTRA_PLAYER_ID = "PLAYER_ID";
	public static final String EXTRA_VIEW_MODEL = "EXTRA_VIEW_MODEL";

	@Override
	protected Fragment createFragment() {
		return new InviteFragment();
	}
}