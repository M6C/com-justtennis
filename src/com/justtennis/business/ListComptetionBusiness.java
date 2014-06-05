package com.justtennis.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.activity.ListCompetitionActivity;
import com.justtennis.db.service.InviteService;
import com.justtennis.db.service.PlayerService;
import com.justtennis.db.service.ScoreSetService;
import com.justtennis.db.service.TournamentService;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Tournament;
import com.justtennis.domain.comparator.InviteComparatorByDate;
import com.justtennis.domain.comparator.TournamentComparatorByDate;

public class ListComptetionBusiness {

	@SuppressWarnings("unused")
	private static final String TAG = ListComptetionBusiness.class.getSimpleName();
	
	private ListCompetitionActivity context;

	private InviteService inviteService;
	private ScoreSetService scoreService;
	private PlayerService playerService;
	private TournamentService tournamentService;

	private List<Tournament> listTournament;
	private HashMap<Tournament, List<Invite>> tableInviteByTournament;

	public ListComptetionBusiness(ListCompetitionActivity context, INotifierMessage notificationMessage) {
		this.context = context;
		playerService = new PlayerService(context, notificationMessage);
		tournamentService = new TournamentService(context, notificationMessage);
		inviteService = new InviteService(context, notificationMessage);
		scoreService = new ScoreSetService(context, notificationMessage);
	}

	public void onCreate() {
		refreshData();
	}

	public void onResume() {
	}

	public Context getContext() {
		return context;
	}

	private void refreshData() {
		listTournament = sortTournament(tournamentService.getList());
		tableInviteByTournament = new HashMap<Tournament, List<Invite>>(); 

		List<Tournament> list = new ArrayList<Tournament>(listTournament);
		for (Tournament tournament : list) {
			List<Invite> listInvite = inviteService.getByIdTournament(tournament.getId());
			if (listInvite != null && listInvite.size() > 0) {
				listInvite = sortInvite(listInvite);
				for(Invite invite : listInvite) {
					invite.setPlayer(playerService.find(invite.getPlayer().getId()));
					invite.setListScoreSet(scoreService.getByIdInvite(invite.getId()));
				}
				tableInviteByTournament.put(tournament, listInvite);
			} else {
				listTournament.remove(tournament);
			}
		}
	}

	private List<Invite> sortInvite(List<Invite> listInvite) {
		Invite[] arrayInvite = listInvite.toArray(new Invite[0]);
		Arrays.sort(arrayInvite, new InviteComparatorByDate(true));
		return Arrays.asList(arrayInvite);
	}
	
	private List<Tournament> sortTournament(List<Tournament> listTournament) {
		Tournament[] arrayTournament = listTournament.toArray(new Tournament[0]);
		Arrays.sort(arrayTournament, new TournamentComparatorByDate(true));
		return new ArrayList<Tournament>(Arrays.asList(arrayTournament));
	}

	public List<Tournament> getListTournament() {
		return listTournament;
	}

	public HashMap<Tournament, List<Invite>> getTableInviteByTournament() {
		return tableInviteByTournament;
	}
}