package com.justtennis.business;

import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.R;
import com.justtennis.db.service.GenericService;
import com.justtennis.db.service.TournamentService;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Tournament;

public class LocationTournamentBusiness extends GenericSpinnerFormBusiness<Tournament>{

	@SuppressWarnings("unused")
	private static final String TAG = LocationTournamentBusiness.class.getSimpleName();

	private Invite invite;

	private TournamentService service;

	private LocationClubBusiness locationClubBusiness;

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
	protected void initializeSubBusiness(Context context, INotifierMessage notificationMessage) {
		if (subBusiness == null) {
			subBusiness = new LocationClubBusiness(context, notificationMessage);
			subBusiness.initializeData(null);
		}
	}

	public Tournament addTournament(String name, Long idClub) {
		Tournament tournament = null;
		if (getTournament() != null && getTournament().getId()!=null && service.isRealTournament(getTournament())) {
			tournament = getTournament();
		} else {
			tournament = new Tournament();
		}
		tournament.setName(name);
		tournament.setIdClub(idClub);
		return super.add(tournament);
	}
	
	public void deleteTournament() {
		Tournament address = getTournament();
		if (address!=null && !service.isEmptyTournament(address)) {
			service.delete(address);
		}
	}
	
	public Tournament getTournament() {
		return invite.getTournament() == null ? null : invite.getTournament();
	}
}