package com.justtennis.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.activity.ListCompetitionActivity;
import com.justtennis.business.sub.ComputeRankSubService;
import com.justtennis.db.service.InviteService;
import com.justtennis.db.service.PlayerService;
import com.justtennis.db.service.RankingService;
import com.justtennis.db.service.ScoreSetService;
import com.justtennis.db.service.TournamentService;
import com.justtennis.db.service.UserService;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Ranking;
import com.justtennis.domain.Tournament;
import com.justtennis.domain.User;
import com.justtennis.notifier.NotifierMessageLogger;

public class ListCompetitionBusiness {

	public enum TYPE {
		ALL,
		PALMARES
	}

	@SuppressWarnings("unused")
	private static final String TAG = ListCompetitionBusiness.class.getSimpleName();
	
	private ListCompetitionActivity context;

	private UserService userService;
	private InviteService inviteService;
	private ScoreSetService scoreService;
	private PlayerService playerService;
	private TournamentService tournamentService;
	private ComputeRankSubService computeRankService;
	private RankingService rankingService;

	private List<Tournament> listTournament;
	private HashMap<Tournament, List<Invite>> tableInviteByTournament;
	private HashMap<Long,List<Invite>> mapInviteByTournament;

	private TYPE type = TYPE.ALL;

	public ListCompetitionBusiness(ListCompetitionActivity context, INotifierMessage notificationMessage) {
		this.context = context;
		userService = new UserService(context, notificationMessage);
		playerService = new PlayerService(context, notificationMessage);
		tournamentService = new TournamentService(context, notificationMessage);
		inviteService = new InviteService(context, notificationMessage);
		scoreService = new ScoreSetService(context, notificationMessage);
		computeRankService = new ComputeRankSubService(context, notificationMessage);
		rankingService = new RankingService(context, NotifierMessageLogger.getInstance());
	}

	public void onCreate() {
		listTournament = new ArrayList<Tournament>();;
		tableInviteByTournament = new HashMap<Tournament, List<Invite>>(); 
		refreshData();
	}

	public void onResume() {
	}

	public Context getContext() {
		return context;
	}

	public void refreshData() {
		listTournament.clear();
		tableInviteByTournament.clear();
		if (type == TYPE.PALMARES) {
//			mapInviteByTournament = computeRankService.getListInvite();
			mapInviteByTournament = computeRankService.getListInvite(true);
		}

		List<Tournament> list = tournamentService.sortTournamentByDate(tournamentService.getList());
		for (Tournament tournament : list) {
			List<Invite> listInvite = findInvite(tournament);
			if (listInvite != null && listInvite.size() > 0) {
				listTournament.add(tournament);
				listInvite = inviteService.sortInviteByDate(listInvite);
				for(Invite invite : listInvite) {
					invite.setPlayer(playerService.find(invite.getPlayer().getId()));
					invite.setListScoreSet(scoreService.getByIdInvite(invite.getId()));
				}
				tableInviteByTournament.put(tournament, listInvite);
			}
		}
	}

	private List<Invite> findInvite(Tournament tournament) {
		switch(type) {
			case PALMARES: {
				return mapInviteByTournament.get(tournament.getId());
			}
			case ALL:
			default: {
				return inviteService.getByIdTournament(tournament.getId());
			}
		}
	}

	public int getSumPoint() {
		int ret = 0;
		if (mapInviteByTournament != null && mapInviteByTournament.size() > 0) {
			for(List<Invite> list : mapInviteByTournament.values()) {
				for(Invite invite : list) {
					ret += invite.getPoint();
				}
			}
			
		}
		return ret;
	}

	public int getRankingPoint() {
		int ret = 0;
		User user = userService.find();
		Ranking userRanking = rankingService.find(user.getIdRanking());
		if (userRanking != null) {
			ret = userRanking.getRankingPointMan();
		}
		return ret;
	}
	
	public List<Tournament> getListTournament() {
		return listTournament;
	}

	public HashMap<Tournament, List<Invite>> getTableInviteByTournament() {
		return tableInviteByTournament;
	}

	public TYPE getType() {
		return type;
	}

	public void setType(TYPE type) {
		this.type = type;
	}
}