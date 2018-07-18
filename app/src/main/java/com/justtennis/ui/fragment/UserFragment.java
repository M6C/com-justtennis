
package com.justtennis.ui.fragment;

import android.os.Bundle;
import android.widget.Spinner;

import com.justtennis.R;
import com.justtennis.business.PlayerBusiness;
import com.justtennis.business.UserBusiness;
import com.justtennis.domain.Address;
import com.justtennis.domain.User;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.ui.common.CommonEnum;

import java.io.Serializable;


public class UserFragment extends PlayerFragment {

	public static final String TAG = UserFragment.class.getSimpleName();

	private Spinner spSaison;
	private UserBusiness business;

	private Serializable addressFromResult;

	public static UserFragment build() {
		UserFragment fragment = new UserFragment();
		Bundle args = new Bundle();
		args.putSerializable(PlayerFragment.EXTRA_MODE, CommonEnum.PLAYER_MODE.FOR_RESULT);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onResume() {
		super.onResume();
		//RxFragment.publish(RxFragment.SUBJECT_ON_SHOW, TAG);
	}

	@Override
	protected void initializeViewById() {
		super.initializeViewById();
		spSaison = rootView.findViewById(R.id.sp_saison);
	}

	@Override
	protected void initializeLocation() {
		super.initializeLocation();
		if (addressFromResult != null) {
			User user = (User) business.getPlayer();
			user.setIdAddress(((Address)addressFromResult).getId());
			addressFromResult = null;
		}
	}

	@Override
	protected void initializeSaisonList() {
		super.initializeSaisonList();
		spSaison.setOnItemSelectedListener(null);
	}

	@Override
	protected PlayerBusiness createBusiness() {
		business = new UserBusiness(context, NotifierMessageLogger.getInstance());
		return business;
	}
}