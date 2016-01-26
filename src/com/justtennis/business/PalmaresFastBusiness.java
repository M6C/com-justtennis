package com.justtennis.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.gdocument.gtracergps.launcher.log.Logger;

import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.R;
import com.justtennis.activity.PalmaresFastActivity;
import com.justtennis.business.sub.ComputeRankSubService;
import com.justtennis.db.service.PlayerService;
import com.justtennis.db.service.RankingService;
import com.justtennis.db.service.UserService;
import com.justtennis.domain.ComputeDataRanking;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Invite.SCORE_RESULT;
import com.justtennis.domain.PalmaresFastValue;
import com.justtennis.domain.Player;
import com.justtennis.domain.Ranking;
import com.justtennis.domain.comparator.RankingComparatorByOrder;
import com.justtennis.notifier.NotifierMessageLogger;

public class PalmaresFastBusiness {

	private static final String TAG = PalmaresFastBusiness.class.getSimpleName();
	
	private PalmaresFastActivity context;

	private ComputeRankSubService computeRankService;

	private UserService userService;
	private PlayerService playerService;
	private RankingService rankingService;

	private ComputeDataRanking computeDataRanking;

	private List<PalmaresFastValue> list = new ArrayList<PalmaresFastValue>();
	private Long idRanking;
	private int pointBonus = 0;


	public PalmaresFastBusiness(PalmaresFastActivity context, INotifierMessage notificationMessage) {
		this.context = context;

		computeRankService = new ComputeRankSubService(context, notificationMessage);
		userService = new UserService(context, notificationMessage);
		rankingService = new RankingService(context, notificationMessage);
		playerService = new PlayerService(context, NotifierMessageLogger.getInstance());
	
		idRanking = userService.find().getIdRankingEstimate();
		if (idRanking == null) {
			idRanking = userService.find().getIdRanking();
		}
	}

	public void onCreate() {
		initializePalmaresFastValue();
		refreshData();
	}

	public void onResume() {
	}

	public List<PalmaresFastValue> getList() {
		return list;
	}

	public Context getContext() {
		return context;
	}

	public void refreshData() {
Logger.logMe(TAG, "PALMARES FAST - PalmaresFastBusiness - refreshData");
		refreshComputeDataRanking();
	}

	private void refreshComputeDataRanking() {
		Ranking rankingNC = rankingService.getNC();
		Player playerUnknow = playerService.getUnknownPlayer();
		List<Invite> listVictory = new ArrayList<Invite>();
		List<Invite> listDefeat = new ArrayList<Invite>();
		for(PalmaresFastValue value : list) {
			addInviteFromPalmares(listVictory, value.getNbVictory(), value.getRanking().getId(), rankingNC, SCORE_RESULT.VICTORY, SCORE_RESULT.WO_VICTORY, playerUnknow);
			addInviteFromPalmares(listDefeat, value.getNbDefeat(), value.getRanking().getId(), rankingNC, SCORE_RESULT.DEFEAT, SCORE_RESULT.WO_DEFEAT, playerUnknow);
		}
		computeDataRanking = computeRankService.computeDataRanking(listVictory, listDefeat, idRanking, true);
	}

	private void addInviteFromPalmares(List<Invite> listInvite, int nb, Long idRanking, Ranking rankingNC, SCORE_RESULT result, SCORE_RESULT resultWO, Player player) {
		for(int i=0 ; i<nb ; i++) {
			Invite invite = new Invite();
			invite.setIdRanking(idRanking == null ? rankingNC.getId() : idRanking);
			invite.setScoreResult(idRanking == null ? resultWO : result);
			invite.setPlayer(player);
			invite.setDate(new Date());
			listInvite.add(invite);
		}
	}

	private void initializePalmaresFastValue() {
Logger.logMe(TAG, "PALMARES FAST - PalmaresFastBusiness - initializePalmaresFastValue");
		list.clear();

		SortedSet<Ranking> setRanking = new TreeSet<Ranking>(new RankingComparatorByOrder(true));
		List<Ranking> listRanking = rankingService.getList();
		setRanking.addAll(listRanking);

		Ranking rankingWO = new Ranking();
		rankingWO.setRanking(context.getString(R.string.txt_wo));
		list.add(new PalmaresFastValue(rankingWO, 0, 0));

		for(Ranking ranking : setRanking) {
			list.add(new PalmaresFastValue(ranking, 0, 0));
		}
	}

	public int getRankingPosition() {
		int ret = 1;
		for(PalmaresFastValue value : list) {
			if (value.getRanking().getId() !=null && value.getRanking().getId().equals(idRanking)) {
Logger.logMe(TAG, "PALMARES FAST - PalmaresFastBusiness - getRankingPosition:" + ret);
				break;
			}
			ret++;
		}
		return ret;
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

	public int getPointBonus() {
		return pointBonus;
	}

	public void setPointBonus(int pointBonus) {
		this.pointBonus = pointBonus;
	}
}