package com.justtennis.business;

import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.activity.ComputeRankingActivity;
import com.justtennis.business.sub.ComputeRankSubService;
import com.justtennis.db.service.InviteService;
import com.justtennis.db.service.PlayerService;
import com.justtennis.db.service.RankingService;
import com.justtennis.db.service.ScoreSetService;
import com.justtennis.db.service.UserService;
import com.justtennis.domain.ComputeDataRanking;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Invite.SCORE_RESULT;
import com.justtennis.domain.PalmaresFastValue;
import com.justtennis.domain.Player;
import com.justtennis.domain.Ranking;
import com.justtennis.domain.User;
import com.justtennis.domain.comparator.PalmaresFastValueComparatorByRanking;
import com.justtennis.tool.ListTool;

import java.util.ArrayList;
import java.util.List;

public class ComputeRankingBusiness {

	@SuppressWarnings("unused")
	private static final String TAG = ComputeRankingBusiness.class.getSimpleName();
	
	private ComputeRankingActivity context;

	private ComputeRankSubService computeRankService;

	private InviteService inviteService;
	private ScoreSetService scoreService;
	private PlayerService playerService;
	private RankingService rankingService;

	private ComputeDataRanking computeDataRanking;

	private List<Invite> list = new ArrayList<>();
	private Long idRanking;

	public ComputeRankingBusiness(ComputeRankingActivity context, INotifierMessage notificationMessage) {
		this.context = context;
		computeRankService = new ComputeRankSubService(context, notificationMessage);

		UserService userService = new UserService(context, notificationMessage);
		playerService = new PlayerService(context, notificationMessage);
		inviteService = new InviteService(context, notificationMessage);
		scoreService = new ScoreSetService(context, notificationMessage);
		rankingService = new RankingService(context, notificationMessage);
	
		User user = userService.find();
		if (user != null) {
			idRanking = user.getIdRankingEstimate();
			if (idRanking == null) {
				idRanking = user.getIdRanking();
			}
		}
		if (idRanking == null) {
			idRanking = rankingService.getNC().getId();
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

	public List<PalmaresFastValue> getPalmares() {
		List<PalmaresFastValue> ret = new ArrayList<>();

		List<Invite> listVictory = inviteService.getByScoreResult(SCORE_RESULT.VICTORY);
		List<Invite> listDefeat = inviteService.getByScoreResult(SCORE_RESULT.DEFEAT);

		computePalmares(ret, listVictory, true);
		computePalmares(ret, listDefeat, false);

		return ret;
	}

	private void computePalmares(List<PalmaresFastValue> ret, List<Invite> listVictory, boolean victory) {
		PalmaresFastValueComparatorByRanking comparator = new PalmaresFastValueComparatorByRanking();
		for(Invite invite : listVictory) {
			Player player = playerService.find(invite.getPlayer().getId());
			Ranking ranking = rankingService.getRanking(invite, player, true);
			PalmaresFastValue value = ListTool.get(ret, ranking, comparator);
			if (value == null) {
				value = new PalmaresFastValue(ranking, 0, 0);
				ret.add(value);
			}
			if (victory) {
				value.setNbVictory(value.getNbVictory() + 1);
			} else  {
				value.setNbDefeat(value.getNbDefeat() + 1);
			}
		}
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