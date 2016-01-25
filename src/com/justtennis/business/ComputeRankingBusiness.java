package com.justtennis.business;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.activity.ComputeRankingActivity;
import com.justtennis.business.sub.ComputeRankSubService;
import com.justtennis.db.service.InviteService;
import com.justtennis.db.service.PlayerService;
import com.justtennis.db.service.ScoreSetService;
import com.justtennis.db.service.UserService;
import com.justtennis.domain.ComputeDataRanking;
import com.justtennis.domain.Invite;

public class ComputeRankingBusiness {

	@SuppressWarnings("unused")
	private static final String TAG = ComputeRankingBusiness.class.getSimpleName();
	
	private ComputeRankingActivity context;

	private ComputeRankSubService computeRankService;

	private UserService userService;
	private InviteService inviteService;
	private ScoreSetService scoreService;
	private PlayerService playerService;

	private ComputeDataRanking computeDataRanking;

	private List<Invite> list = new ArrayList<Invite>();
	private Long idRanking;

	public ComputeRankingBusiness(ComputeRankingActivity context, INotifierMessage notificationMessage) {
		this.context = context;
		computeRankService = new ComputeRankSubService(context, notificationMessage);

		userService = new UserService(context, notificationMessage);
		playerService = new PlayerService(context, notificationMessage);
		inviteService = new InviteService(context, notificationMessage);
		scoreService = new ScoreSetService(context, notificationMessage);
	
		idRanking = userService.find().getIdRankingEstimate();
		if (idRanking == null) {
			idRanking = userService.find().getIdRanking();
		}
	}

	public void onCreate() {
		refreshData();
	}
	
	public void onResume() {
		refreshInvite();
	}

	public List<Invite> getList() {
		return list;
	}

	public Context getContext() {
		return context;
	}

	public void refreshData() {
		refreshComputeDataRanking();
		refreshInvite();
	}

	private void refreshComputeDataRanking() {
		computeDataRanking = computeRankService.computeDataRanking(idRanking, true);

		computeDataRanking.setListInviteCalculed(inviteService.sortInviteByPoint(computeDataRanking.getListInviteCalculed()));
		computeDataRanking.setListInviteNotUsed(inviteService.sortInviteByDate(computeDataRanking.getListInviteNotUsed()));
	}

	private void refreshInvite() {
		list.clear();
		list.addAll(computeDataRanking.getListInviteCalculed());
		list.addAll(computeDataRanking.getListInviteNotUsed());

		for (Invite invite : list) {
			invite.setPlayer(playerService.find(invite.getPlayer().getId()));
			invite.setListScoreSet(scoreService.getByIdInvite(invite.getId()));
		}
	}

	public void setIdRanking(Long idRanking) {
		this.idRanking = idRanking;
	}

	public Long getIdRanking() {
		return idRanking;
	}

	public int getPointCalculate() {
		return computeDataRanking.getPointCalculate();
	}

	public int getPointObjectif() {
		return computeDataRanking.getPointObjectif();
	}

	public int getPointBonus() {
		return computeDataRanking.getPointBonus();
	}

	public int getNbVictoryCalculate() {
		return computeDataRanking.getNbVictoryCalculate();
	}

	public int getNbVictoryAdditional() {
		return computeDataRanking.getNbVictoryAdditional();
	}

	public int getNbVictorySum() {
		return computeDataRanking.getNbVictoryCalculate() + computeDataRanking.getNbVictory() + computeDataRanking.getNbVictoryAdditional();
	}
	public int getVE2I5G() {
		return computeDataRanking.getVE2I5G();
	}
}