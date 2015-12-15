package com.justtennis.business;

import android.content.Context;

import com.cameleon.common.android.db.sqlite.service.GenericService;
import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.R;
import com.justtennis.db.service.AddressService;
import com.justtennis.domain.Address;

public class LocationAddressBusiness extends GenericSpinnerFormBusiness<Address, Address>{

	@SuppressWarnings("unused")
	private static final String TAG = LocationAddressBusiness.class.getSimpleName();

	private AddressService service;

	public LocationAddressBusiness(Context context, INotifierMessage notificationMessage) {
		super(context, notificationMessage);
	}

	@Override
	protected Address getNewData() {
		return new Address();
	}
	
	@Override
	protected Address getEmptyData() {
		Address address = service.getEmptyAddress();
		address.setName(getContext().getString(R.string.txt_address));
		return address;
	}

	@Override
	public boolean isEmptyData(Address pojo) {
		return service.isEmptyAddress(pojo);
	}

	@Override
	protected GenericService<Address> initializeService(Context context, INotifierMessage notificationMessage) {
		if (service == null) {
			service = new AddressService(context, notificationMessage);
		}
		return service;
	}

	@Override
	protected GenericSpinnerFormBusiness<Address, ?> initializeSubBusiness(Context context, INotifierMessage notificationMessage) {
		return null;
	}
}