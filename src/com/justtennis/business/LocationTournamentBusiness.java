package com.justtennis.business;

import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.R;
import com.justtennis.db.service.GenericService;
import com.justtennis.db.service.TournamentService;
import com.justtennis.domain.Club;
import com.justtennis.domain.Tournament;

public class LocationTournamentBusiness extends GenericSpinnerFormBusiness<Tournament, Club>{

	@SuppressWarnings("unused")
	private static final String TAG = LocationTournamentBusiness.class.getSimpleName();

	private TournamentService service;

	public LocationTournamentBusiness(Context context, INotifierMessage notificationMessage) {
		super(context, notificationMessage);
	}

	@Override
	protected Tournament getNewData() {
		return new Tournament();
	}
	
	@Override
	protected Tournament getEmptyData() {
		Tournament tournament = service.getEmptyTournament();
		tournament.setName(getContext().getString(R.string.txt_tournament));
		return tournament;
	}

	@Override
	public boolean isEmptyData(Tournament pojo) {
		return service.isEmptyTournament(pojo);
	}

	@Override
	protected GenericService<Tournament> initializeService(Context context, INotifierMessage notificationMessage) {
		if (service == null) {
			service = new TournamentService(context, notificationMessage);
		}
		return service;
	}

	@Override
	protected GenericSpinnerFormBusiness<Club, ?> initializeSubBusiness(Context context, INotifierMessage notificationMessage) {
		return new LocationClubBusiness(context, notificationMessage);
	}
}