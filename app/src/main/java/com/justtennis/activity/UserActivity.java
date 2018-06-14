
package com.justtennis.activity;

import android.support.v4.app.Fragment;

import com.justtennis.ui.fragment.UserFragment;


public class UserActivity extends PlayerActivity {

	@Override
	protected Fragment createFragment() {
		return new UserFragment();
	}
}