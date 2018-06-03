package com.justtennis.drawer.manager.business;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.service.InviteService;
import com.justtennis.db.service.SaisonService;
import com.justtennis.domain.Saison;

public class DrawerSaisonBusiness {

	@SuppressWarnings("unused")
	private static final String TAG = DrawerSaisonBusiness.class.getSimpleName();

	private SaisonService saisonService;
	private InviteService inviteService;

	private Context context;

	private List<Saison> listSaison = new ArrayList<Saison>();
	private List<String> listTxtSaisons = new ArrayList<String>();
	private Saison saisonActive;
	private int saisonActivePosition = 0;


	public DrawerSaisonBusiness(Context context, INotifierMessage notificationMessage) {
		this.context = context;
		saisonService = new SaisonService(context, notificationMessage);
		inviteService = new InviteService(context, notificationMessage);
	}

	public void onResume() {
		initializeData();
	}

	public void initializeData() {
		initializeDataSaison();
	}

	public void initializeDataSaison() {
		listSaison.clear();
		listSaison.add(SaisonService.getEmpty());
		listSaison.addAll(saisonService.getList());

		listTxtSaisons.clear();
		listTxtSaisons.addAll(saisonService.getListName(listSaison));

		saisonActive = saisonService.getSaisonActiveOrFirst();
		saisonActivePosition = listSaison.indexOf(saisonActive);
		if (saisonActivePosition < 0) {
			saisonActivePosition = 0;
		}
	}

	public boolean isEmptySaison(Saison saison) {
		return SaisonService.isEmpty(saison);
	}

	public Saison getEmptySaison() {
		return SaisonService.getEmpty();
	}

	public boolean isExistSaison(int year) {
		return saisonService.exist(year);
	}

	public boolean isExistInviteSaison(Saison saison) {
		return inviteService.countByIdSaison(saison.getId()) > 0;
	}

	public Saison createSaison(int year, boolean active) {
		return saisonService.create(year, active);
	}

	public void deleteSaison(Saison saison) {
		saisonService.delete(saison);
	}

	public Context getContext() {
		return context;
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

	public Saison getSaisonActive() {
		return saisonActive;
	}

	public int getSaisonActivePosition() {
		return saisonActivePosition;
	}
}