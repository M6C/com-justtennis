package com.justtennis.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.justtennis.R;
import com.justtennis.business.GenericSpinnerFormBusiness;
import com.justtennis.business.LocationAddressBusiness;
import com.justtennis.domain.Address;
import com.justtennis.notifier.NotifierMessageLogger;

public class LocationAddressActivity extends GenericSpinnerFormActivity<Address> {

	@SuppressWarnings("unused")
	private static final String TAG = LocationAddressActivity.class.getSimpleName();

	private LocationAddressBusiness business;

	@Override
	public IGenericSpinnerFormResource getResource() {
		return new IGenericSpinnerFormResource() {
			@Override
			public int getTitleStringId() {
				return R.string.txt_address;
			}

			@Override
			public int getNameHintStringId() {
				return R.string.hint_address_name;
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
	protected GenericSpinnerFormBusiness<Address> getBusiness() {
		if (business==null) {
			business = new LocationAddressBusiness(this, NotifierMessageLogger.getInstance());
		}
		return business;
	}

	@Override
	protected View buildFormAdd() {
		return null;
	}

	private void returnResult() {
		Intent data = new Intent();
		data.putExtra(EXTRA_OUT_LOCATION, business.getAddress());
		setResult(Activity.RESULT_OK, data);

		finish();
	}
	
	public void onClickCancel(View view) {
		finish();
	}
}