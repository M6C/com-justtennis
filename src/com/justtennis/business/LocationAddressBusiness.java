package com.justtennis.business;

import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.R;
import com.justtennis.db.service.AddressService;
import com.justtennis.db.service.GenericService;
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
	protected void initializeSubBusiness(Context context, INotifierMessage notificationMessage) {
	}

//	public Address addAddress(String name, String line1, String postalCode, String city, String gps) {
//		Address address = null;
//		if (getAddress() != null && getAddress().getId()!=null && service.isRealAddress(getAddress())) {
//			address = getAddress();
//		} else {
//			address = new Address();
//		}
//		address.setName(name);
//		address.setLine1(line1);
//		address.setPostalCode(postalCode);
//		address.setCity(city);
//		address.setGps(gps);
//		return super.add(address);
//	}
//	
//	public void deleteAddress() {
//		Address address = getAddress();
//		if (address!=null && !service.isEmptyAddress(address)) {
//			service.delete(address);
//		}
//	}
//	
//	public Address getAddress() {
//		return invite.getAddress() == null ? null : invite.getAddress();
//	}
}