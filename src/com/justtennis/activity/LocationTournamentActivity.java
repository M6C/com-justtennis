package com.justtennis.activity;

import android.view.View;
import android.view.ViewGroup;

import com.justtennis.R;
import com.justtennis.business.LocationTournamentBusiness;
import com.justtennis.domain.Tournament;
import com.justtennis.notifier.NotifierMessageLogger;

public class LocationTournamentActivity extends GenericSpinnerFormActivity<Tournament> {

	@SuppressWarnings("unused")
	private static final String TAG = LocationTournamentActivity.class.getSimpleName();

	private LocationTournamentBusiness business;

	@Override
	public IGenericSpinnerFormResource getResource() {
		return new IGenericSpinnerFormResource() {
			@Override
			public int getTitleStringId() {
				return R.string.txt_tournament;
			}

			@Override
			public int getNameHintStringId() {
				return R.string.hint_tournament_name;
			}

			@Override
			public Class<LocationClubActivity> getFormListActivityClass() {
				return LocationClubActivity.class;
			}
		};
	}

	@Override
	protected LocationTournamentBusiness getBusiness() {
		if (business==null) {
			business = new LocationTournamentBusiness(this, NotifierMessageLogger.getInstance());
		}
		return business;
	}

	@Override
	protected View buildFormAdd(ViewGroup llForm) {
		return null;
	}
}