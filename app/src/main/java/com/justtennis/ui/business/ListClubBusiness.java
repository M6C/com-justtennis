package com.justtennis.ui.business;

import android.app.Activity;
import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.service.ClubService;
import com.justtennis.db.service.InviteService;
import com.justtennis.domain.Club;
import com.justtennis.domain.RechercheResult;
import com.justtennis.domain.comparator.ClubComparatorByName;
import com.justtennis.drawer.business.INavigationDrawerRechercheBusiness;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ListClubBusiness implements INavigationDrawerRechercheBusiness {

	private static final String TAG = ListClubBusiness.class.getSimpleName();

	private ClubService clubService;
	private InviteService inviteService;
	private List<Club> list = new ArrayList<>();
	private Activity activity;

	private String findText;

	public ListClubBusiness(Activity activity, INotifierMessage notificationMessage) {
		this.activity = activity;
		clubService = new ClubService(activity, notificationMessage);
		inviteService = new InviteService(activity, notificationMessage);
	}

	public void initialize() {
	}

	@Override
	public String getFindText() {
		return findText;
	}

	@Override
	public void setFindText(String findText) {
		this.findText = findText;
	}

	@Override
	public Collection<? extends RechercheResult> find(String text) {
		return new ArrayList();// clubService.find(text);
	}

	public List<Club> getList() {
		return list;
	}
	
	public Context getActivity() {
		return activity;
	}

	public void refreshData() {
		list.clear();
		list.addAll(sort(clubService.getList()));
	}

	private List<Club> sort(List<Club> list) {
		Club[] arrayPlayer = list.toArray(new Club[0]);
		Arrays.sort(arrayPlayer, new ClubComparatorByName(true));
		return Arrays.asList(arrayPlayer);
	}

    public int getInviteCount(Club club) {
		return inviteService.countByIdClub(club.getId());
    }


	public void delete(Club club) {
		clubService.delete(club);
	}
}