package com.justtennis.business;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.service.InviteService;
import com.justtennis.db.service.PlayerService;
import com.justtennis.db.service.SaisonService;
import com.justtennis.db.service.UserService;
import com.justtennis.domain.Saison;

public class MainBusiness {

	@SuppressWarnings("unused")
	private static final String TAG = MainBusiness.class.getSimpleName();

	private UserService userService;
	private PlayerService playerService;
	private SaisonService saisonService;
	private InviteService inviteService;

	private long userCount;
	private Context context;

	private List<Saison> listSaison = new ArrayList<Saison>();
	private List<String> listTxtSaisons = new ArrayList<String>();

	public MainBusiness(Context context, INotifierMessage notificationMessage) {
		this.context = context;
		userService = new UserService(context, notificationMessage);
		playerService = new PlayerService(context, notificationMessage);
		saisonService = new SaisonService(context, notificationMessage);
		inviteService = new InviteService(context, notificationMessage);
	}

	public void onResume() {
		initializeData();
	}

	public Long getUnknownPlayerId() {
		return playerService.getUnknownPlayer().getId();
	}

	public void initializeData() {
		userCount = userService.getCount();
		initializeDataSaison();
	}

	public void initializeDataSaison() {
		listSaison.clear();
		listSaison.add(SaisonService.getEmpty());
		listSaison.addAll(saisonService.getList());

		listTxtSaisons.clear();
		listTxtSaisons.addAll(saisonService.getListName(listSaison));
	}

	public long getUserCount() {
		return userCount;
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

	public boolean isEmptySaison(Saison saison) {
		return SaisonService.isEmpty(saison);
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
}