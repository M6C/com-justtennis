package com.justtennis.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.justtennis.R;
import com.justtennis.business.GenericSpinnerFormBusiness;
import com.justtennis.business.LocationTournamentBusiness;
import com.justtennis.domain.Club;
import com.justtennis.domain.Tournament;
import com.justtennis.notifier.NotifierMessageLogger;

public class LocationTournamentActivity extends GenericSpinnerFormActivity<Tournament> {

	@SuppressWarnings("unused")
	private static final String TAG = LocationTournamentActivity.class.getSimpleName();

	private static final int RESULT_FORM_LIST = 1;

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
		};
	}

	@Override
	public void onBackPressed() {
		onClickCancel(null);
	}

	@Override
	public void onClickValidate(View view) {
	}

	@Override
	public void onClickAddValidate(View view) {
		super.onClickAddValidate(view);
		returnResult();
	}

	@Override
	public void onClickAddFormList(View view) {
		Intent intent = new Intent(this, LocationClubActivity.class);
		intent.putExtra(LocationClubActivity.EXTRA_MODE, MODE.ADD);

		if (business.getData().getIdClub() != null) {
			@SuppressWarnings("unchecked")
			List<Club> listClub = (List<Club>) business.getSubBusiness().getService().getList(new String[]{business.getData().getIdClub().toString()});
			if (listClub != null && listClub.size() > 0) {
				intent.putExtra(LocationClubActivity.EXTRA_DATA, listClub.get(0));
			}
		}

		startActivityForResult(intent, RESULT_FORM_LIST);
	}

	@Override
	protected GenericSpinnerFormBusiness<Tournament> getBusiness() {
		if (business==null) {
			business = new LocationTournamentBusiness(this, NotifierMessageLogger.getInstance());
		}
		return business;
	}

	@Override
	protected View buildFormAdd() {
		return null;
	}

	private void returnResult() {
		Intent data = new Intent();
		data.putExtra(EXTRA_OUT_LOCATION, business.getTournament());
		setResult(Activity.RESULT_OK, data);

		finish();
	}
	
	public void onClickCancel(View view) {
		finish();
	}
}