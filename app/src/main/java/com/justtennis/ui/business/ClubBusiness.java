package com.justtennis.ui.business;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.service.AddressService;
import com.justtennis.db.service.ClubService;
import com.justtennis.domain.Address;
import com.justtennis.domain.Club;

public class ClubBusiness {

	private static final String TAG = ClubBusiness.class.getSimpleName();

	public static final String EXTRA_DATA = "EXTRA_DATA";

	private Club club;
	private ClubService clubService;
	private AddressService addressService;
	private Address address;

	public ClubBusiness(Context context, INotifierMessage notificationMessage) {
		clubService = new ClubService(context, notificationMessage);
		addressService = new AddressService(context, notificationMessage);
	}

	public void onCreate(Fragment fragment) {
		Bundle bundle = fragment.getArguments();
		assert bundle != null;
		if (bundle.containsKey(EXTRA_DATA)) {
			club = (Club) bundle.getSerializable(EXTRA_DATA);
			assert club != null;
			if (club.getSubId() != null) {
				address = addressService.find(club.getSubId());
			}
		} else {
			club = new Club();
		}
		if (address == null) {
			address = new Address();
		}
	}

	public Club getClub() {
		return club;
	}

	public Address getAddress() {
		return address;
	}

	public void validate() {
		addressService.createOrUpdate(address);
		if (address.getId() != null) {
			club.setSubId(address.getId());
			clubService.createOrUpdate(club);
		}
	}
}