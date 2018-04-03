package com.justtennis.activity;

import android.view.View;
import android.view.ViewGroup;

import com.justtennis.R;
import com.justtennis.business.LocationClubBusiness;
import com.justtennis.domain.Club;
import com.justtennis.notifier.NotifierMessageLogger;

public class LocationClubActivity extends GenericSpinnerFormActivity<Club> {

	@SuppressWarnings("unused")
	private static final String TAG = LocationClubActivity.class.getSimpleName();

	private LocationClubBusiness business;

	@Override
	public IGenericSpinnerFormResource getResource() {
		return new IGenericSpinnerFormResource() {
			@Override
			public int getTitleStringId() {
				return R.string.txt_club;
			}

			@Override
			public int getNameHintStringId() {
				return R.string.hint_club_name;
			}

			@Override
			public Class<LocationAddressActivity> getFormListActivityClass() {
				return LocationAddressActivity.class;
			}
		};
	}

	@Override
	protected LocationClubBusiness getBusiness() {
		if (business==null) {
			business = new LocationClubBusiness(this, NotifierMessageLogger.getInstance());
		}
		return business;
	}

	@Override
	protected View buildFormAdd(ViewGroup llForm) {
		return null;
	}
}