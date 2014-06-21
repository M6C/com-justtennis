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
import com.justtennis.domain.ComputeDataRanking;
import com.justtennis.domain.Invite;

public class ComputeRankingBusiness {

	@SuppressWarnings("unused")
	private static final String TAG = ComputeRankingBusiness.class.getSimpleName();
	
	private ComputeRankingActivity context;

	private ComputeRankSubService computeRankService;

	private InviteService inviteService;
	private ScoreSetService scoreService;
	private PlayerService playerService;

	private List<Invite> list = new ArrayList<Invite>();

	private ComputeDataRanking computeDataRanking;

	public ComputeRankingBusiness(ComputeRankingActivity context, INotifierMessage notificationMessage) {
		this.context = context;
		computeRankService = new ComputeRankSubService(context, notificationMessage);

		playerService = new PlayerService(context, notificationMessage);
		inviteService = new InviteService(context, notificationMessage);
		scoreService = new ScoreSetService(context, notificationMessage);
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

	private void refreshData() {
		refreshComputeDataRanking();
		refreshInvite();
	}

	private void refreshComputeDataRanking() {
		computeDataRanking = computeRankService.computeDataRanking(true);

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

	public int getPointCalculate() {
		return computeDataRanking.getPointCalculate();
	}

	public int getPointObjectif() {
		return computeDataRanking.getPointObjectif();
	}
}