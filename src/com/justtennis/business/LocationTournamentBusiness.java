package com.justtennis.business;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;

import com.cameleon.common.android.db.sqlite.service.GenericService;
import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.R;
import com.justtennis.db.service.SaisonService;
import com.justtennis.db.service.TournamentService;
import com.justtennis.domain.Club;
import com.justtennis.domain.Saison;
import com.justtennis.domain.Tournament;

public class LocationTournamentBusiness extends GenericSpinnerFormBusiness<Tournament, Club>{

	@SuppressWarnings("unused")
	private static final String TAG = LocationTournamentBusiness.class.getSimpleName();

	private TournamentService service;
	private SaisonService saisonService;

	private List<Saison> listSaison = new ArrayList<Saison>();
	private List<String> listTxtSaisons = new ArrayList<String>();

	public LocationTournamentBusiness(Context context, INotifierMessage notificationMessage) {
		super(context, notificationMessage);
		saisonService = new SaisonService(context, notificationMessage);
	}

	@Override
	protected Tournament getNewData() {
		Tournament tournament = new Tournament();
		tournament.setSaison(saisonService.getSaisonActiveOrFirst());
		return tournament;
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

	@Override
	public void initializeData(Intent intent) {
		super.initializeData(intent);
		initializeDataSaison();
	}

	protected void initializeDataSaison() {
		Saison saisonEmpty = SaisonService.getEmpty();
		saisonEmpty.setName(getContext().getString(R.string.txt_saison));

		listSaison.clear();
		listSaison.add(saisonEmpty);
		listSaison.addAll(saisonService.getList());

		listTxtSaisons.clear();
		listTxtSaisons.addAll(saisonService.getListName(listSaison));
	}

	public boolean isEmptySaison(Saison saison) {
		return SaisonService.isEmpty(saison);
	}

	public void setSaison(Saison saison) {
		data.setSaison(saison == null || isEmptySaison(saison) ? null : saison);
	}

	public Saison getSaison() {
		return data.getSaison();
	}

	public List<Saison> getListSaison() {
		return listSaison;
	}

	public void setListSaison(List<Saison> listSaison) {
		this.listSaison = listSaison;
	}

	public List<String> getListTxtSaisons() {
		return listTxtSaisons;
	}

	public void setListTxtSaisons(List<String> listTxtSaisons) {
		this.listTxtSaisons = listTxtSaisons;
	}
}