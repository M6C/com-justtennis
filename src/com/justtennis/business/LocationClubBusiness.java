package com.justtennis.business;

import android.content.Context;
import android.content.Intent;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.R;
import com.justtennis.activity.GenericSpinnerFormActivity;
import com.justtennis.db.service.ClubService;
import com.justtennis.db.service.GenericService;
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
	protected void initializeSubBusiness(Context context, INotifierMessage notificationMessage) {
		if (subBusiness == null) {
			Intent intent = new Intent();
			intent.putExtra(GenericSpinnerFormActivity.EXTRA_DATA, new Address(getData().getSubId()));
			subBusiness = new LocationAddressBusiness(context, notificationMessage);
			subBusiness.initializeData(intent);
		}
	}
/*
	public Club addClub(String name, Long idAddress) {
		Club club = null;
		if (getClub() != null && getClub().getId()!=null && service.isRealClub(getClub())) {
			club = getClub();
		} else {
			club = new Club();
		}
		club.setName(name);
		club.setIdAddress(idAddress);
		return super.add(club);
	}
	
	public void deleteClub() {
		Club address = getData();
		if (address!=null && !service.isEmptyClub(address)) {
			service.delete(address);
		}
	}
*/
}