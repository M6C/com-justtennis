package com.justtennis.business;

import android.content.Context;

import com.cameleon.common.android.db.sqlite.service.GenericService;
import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.R;
import com.justtennis.db.service.ClubService;
import com.justtennis.domain.Address;
import com.justtennis.domain.Club;

public class LocationClubBusiness extends GenericSpinnerFormBusiness<Club, Address>{

	@SuppressWarnings("unused")
	private static final String TAG = LocationClubBusiness.class.getSimpleName();

	private ClubService service;

	public LocationClubBusiness(Context context, INotifierMessage notificationMessage) {
		super(context, notificationMessage);
	}

	@Override
	protected Club getNewData() {
		return new Club();
	}
	
	@Override
	protected Club getEmptyData() {
		Club club = service.getEmptyClub();
		club.setName(getContext().getString(R.string.txt_club));
		return club;
	}

	@Override
	public boolean isEmptyData(Club pojo) {
		return service.isEmptyClub(pojo);
	}

	@Override
	protected GenericService<Club> initializeService(Context context, INotifierMessage notificationMessage) {
		if (service == null) {
			service = new ClubService(context, notificationMessage);
		}
		return service;
	}

	@Override
	protected GenericSpinnerFormBusiness<Address, ?> initializeSubBusiness(Context context, INotifierMessage notificationMessage) {
		return new LocationAddressBusiness(context, notificationMessage);
	}
}