package com.justtennis.activity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.justtennis.R;
import com.justtennis.business.LocationAddressBusiness;
import com.justtennis.domain.Address;
import com.justtennis.notifier.NotifierMessageLogger;

public class LocationAddressActivity extends GenericSpinnerFormActivity<Address> {

	@SuppressWarnings("unused")
	private static final String TAG = LocationAddressActivity.class.getSimpleName();

	private LocationAddressBusiness business;

	private EditText etAddressLine1;
	private EditText etAddressPostalCode;
	private EditText etAddressCity;

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

			@Override
			public Class<?> getFormListActivityClass() {
				return null;
			}
		};
	}

	@Override
	protected LocationAddressBusiness getBusiness() {
		if (business==null) {
			business = new LocationAddressBusiness(this, NotifierMessageLogger.getInstance());
		}
		return business;
	}

	@Override
	protected View buildFormAdd(ViewGroup llForm) {
		View view = getLayoutInflater().inflate(R.layout.location_element_address, llForm, false);

		etAddressLine1 = (EditText)view.findViewById(R.id.et_address_line_1);
		etAddressPostalCode = (EditText)view.findViewById(R.id.et_address_postal_code);
		etAddressCity = (EditText)view.findViewById(R.id.et_address_city);

		return view;
	}

	@Override
	protected void initializeField() {
		super.initializeField();
		Address data = business.getData();
		etAddressLine1.setText(data.getLine1());
		etAddressPostalCode.setText(data.getPostalCode());
		etAddressCity.setText(data.getCity());
	}

	@Override
	protected void updateData(Address data) {
		super.updateData(data);
		data.setLine1(etAddressLine1.getText().toString());
		data.setPostalCode(etAddressPostalCode.getText().toString());
		data.setCity(etAddressCity.getText().toString());
		data.setName(business.formatAddressName(data));
	}

	@Override
	protected void manageVisibility() {
		super.manageVisibility();
		getNameEditText().setVisibility(View.GONE);
	}
}