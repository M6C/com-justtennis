package com.justtennis.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.justtennis.R;
import com.justtennis.activity.GenericSpinnerFormActivity.MODE;
import com.justtennis.business.GenericSpinnerFormBusiness;
import com.justtennis.business.LocationClubBusiness;
import com.justtennis.domain.Address;
import com.justtennis.domain.Club;
import com.justtennis.notifier.NotifierMessageLogger;

public class LocationClubActivity extends GenericSpinnerFormActivity<Club> {

	@SuppressWarnings("unused")
	private static final String TAG = LocationClubActivity.class.getSimpleName();

	private static final int RESULT_FORM_LIST = 1;

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
		Intent intent = new Intent(this, LocationAddressActivity.class);
		intent.putExtra(LocationClubActivity.EXTRA_MODE, MODE.ADD);

		if (business.getData().getIdAddress() != null) {
			@SuppressWarnings("unchecked")
			List<Address> list = (List<Address>) business.getSubBusiness().getService().getList(new String[]{business.getData().getIdAddress().toString()});
			if (list != null && list.size() > 0) {
				intent.putExtra(LocationAddressActivity.EXTRA_DATA, list.get(0));
			}
		}

		startActivityForResult(intent, RESULT_FORM_LIST);
	}

	@Override
	protected GenericSpinnerFormBusiness<Club> getBusiness() {
		if (business==null) {
			business = new LocationClubBusiness(this, NotifierMessageLogger.getInstance());
		}
		return business;
	}

	@Override
	protected View buildFormAdd() {
		return null;
	}

	private void returnResult() {
		Intent data = new Intent();
		data.putExtra(EXTRA_OUT_LOCATION, business.getClub());
		setResult(Activity.RESULT_OK, data);

		finish();
	}
	
	public void onClickCancel(View view) {
		finish();
	}
}