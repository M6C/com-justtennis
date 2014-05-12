
package com.justtennis.activity;

import com.justtennis.business.PlayerBusiness;
import com.justtennis.business.UserBusiness;
import com.justtennis.notifier.NotifierMessageLogger;


public class UserActivity extends PlayerActivity {

	@Override
	protected PlayerBusiness createBusiness() {
		return new UserBusiness(this, NotifierMessageLogger.getInstance());
	}
}