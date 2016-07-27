package com.justtennis.business.sub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gdocument.gtracergps.launcher.log.Logger;

import android.annotation.SuppressLint;
import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.ApplicationConfig;
import com.justtennis.db.service.InviteService;
import com.justtennis.db.service.PlayerService;
import com.justtennis.db.service.RankingService;
import com.justtennis.db.service.UserService;
import com.justtennis.domain.ComputeDataRanking;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Invite.SCORE_RESULT;
import com.justtennis.domain.Player;
import com.justtennis.domain.Ranking;
import com.justtennis.domain.User;
import com.justtennis.notifier.NotifierMessageLogger;

public class ComputeRankSubService {

	private static final String TAG = ComputeRankSubService.class.getCanonicalName();

	private static final int NB_RANKING_ORDER_LOWER = 3;
	public static final int BONUS_POINT_LIMIT = 45;
	protected Context context;
	private InviteService inviteService;
	private UserService userService;
	private PlayerService playerService;
	private RankingService rankingService;

	public ComputeRankSubService(Context context, INotifierMessage notificationMessage) {
		this.context = context;
		inviteService = new InviteService(context, notificationMessage);
		playerService = new PlayerService(context, NotifierMessageLogger.getInstance());
		userService = new UserService(context, NotifierMessageLogger.getInstance());
		rankingService = new RankingService(context, NotifierMessageLogger.getInstance());
	}
//
//	public HashMap<Long,List<Invite>> getListInvite() {
//		List<Invite> listInvite = new ArrayList<Invite>();
//		int sumPoint = 0, nbVictory = 0;
//		User user = userService.find();
//		Ranking userRanking = rankingService.find(user.getIdRanking());
////		HashMap<Long,List<Player>> mapPlayer = playerService.getGroupByIdRanking();
//		HashMap<Long,List<Invite>> mapPlayer = inviteService.getGroupByIdRanking(Invite.SCORE_RESULT.VICTORY);
//		if (userRanking != null && mapPlayer.keySet().size() > 0) {
//			int rankingPositionMin = userRanking.getOrder() - NB_RANKING_ORDER_LOWER;
//			if (rankingPositionMin < 0) {
//				rankingPositionMin = 0;
//			}
//
//			int idx = 0;
//			Set<Long> keySet = mapPlayer.keySet();
//			String[] keyArray = new String[keySet.size()];
//			for(Long id : keySet) {
//				keyArray[idx++] = id.toString();
//			}
//			boolean doBreak = false;
//			List<Ranking> listKeyRanking = rankingService.getList(keyArray);
//			rankingService.order(listKeyRanking, true);
//			nbVictory = userRanking.getVictoryMan();
//			logMe("USER RANKING " + userRanking.getRanking() + " NB VICTORY:" + nbVictory);
//			for(Ranking ranking : listKeyRanking) {
//				List<Invite> list = mapPlayer.get(ranking.getId());
//				int nb = list.size();
//				if (ranking.getOrder() >= rankingPositionMin) {
//					int rankingDif = ranking.getOrder() - userRanking.getOrder();
//					int point = rankingService.getNbPointDifference(rankingDif);
//					if (nbVictory <= nb) {
//						list = list.subList(0, nbVictory);
//						nb = nbVictory;
//						doBreak = true;
//					}
//					for(Invite inv : inviteService.sortInviteByDate(list)) {
//						inv.setPoint(point);
//						listInvite.add(inv);
//					}
//					sumPoint += point * nb;
//					nbVictory -= nb;
//					logMe("RANKING " + ranking.getRanking() + " NB:" + nb + " POINT:" + point + " SUM:" + sumPoint + " NB VICTORY:" + nbVictory);
//					if (doBreak) {
//						break;
//					}
//				}
//			}
//			logMe("USER RANKING " + userRanking.getRanking() + " TOTAL:" + sumPoint);
//		}
//		return inviteService.groupByIdTournament(listInvite);
//	}

//	public HashMap<Long,List<Invite>> getListInvite() {
//		HashMap<Long,List<Invite>> mapInvite = inviteService.getGroupByIdRanking(Invite.SCORE_RESULT.VICTORY);
//		return getListInvite(mapInvite);
//	}

	public HashMap<Ranking,List<Invite>> getListInviteGroupByRanking(SCORE_RESULT score_result, boolean estimate) {
		List<Invite> listInvite = inviteService.getByScoreResult(score_result);
		return getInviteGroupByPlayerRanking(listInvite, estimate);
	}

	public HashMap<Long,List<Invite>> getListInvite(boolean estimate) {
		List<Invite> listVictory = inviteService.getByScoreResult(SCORE_RESULT.VICTORY);
		HashMap<Ranking,List<Invite>> mapInvite = getInviteGroupByPlayerRanking(listVictory, estimate);
		return getListInvite(mapInvite);
	}

	public HashMap<Long,List<Invite>> getListInvite(List<Invite> listVictory, List<Invite> listDefeat, boolean estimate) {
		HashMap<Ranking,List<Invite>> mapInvite = getInviteGroupByPlayerRanking(listVictory, estimate);
		return getListInvite(mapInvite);
	}

	private HashMap<Long,List<Invite>> getListInvite(HashMap<Ranking,List<Invite>> mapInvite) {
		List<Invite> listInvite = new ArrayList<Invite>();
		int nbVictory = 0, nbVictoryCalculate = 0;
		int sumPoint = 0, pointObjectif = 0;
		User user = userService.find();
		Ranking userRanking = rankingService.find(user.getIdRanking());
		if (userRanking != null && mapInvite.keySet().size() > 0) {
			int rankingPositionMin = userRanking.getOrder() - NB_RANKING_ORDER_LOWER;
			if (rankingPositionMin < 0) {
				rankingPositionMin = 0;
			}
			
//			int idx = 0;
//			Set<Ranking> keySet = mapInvite.keySet();
//			String[] keyArray = new String[keySet.size()];
//			for(Ranking id : keySet) {
//				keyArray[idx++] = id.getId().toString();
//			}
			boolean doBreak = false;
//			List<Ranking> listKeyRanking = rankingService.getList(keyArray);
			List<Ranking> listKeyRanking = new ArrayList<Ranking>(mapInvite.keySet());
			rankingService.order(listKeyRanking, true);
			nbVictory = userRanking.getVictoryMan();
			pointObjectif = userRanking.getRankingPointMan();
			logMe("USER RANKING " + userRanking.getRanking() + " NB VICTORY:" + nbVictory);
			for(Ranking ranking : listKeyRanking) {
				List<Invite> list = mapInvite.get(ranking.getId());
				int nb = list.size();
				if (ranking.getOrder() >= rankingPositionMin) {
					int rankingDif = ranking.getOrder() - userRanking.getOrder();
					int point = rankingService.getNbPointDifference(rankingDif);
					if (nbVictory <= nb) {
						list = list.subList(0, nbVictory);
						nb = nbVictory;
						doBreak = true;
					}
					for(Invite inv : inviteService.sortInviteByDate(list)) {
						inv.setPoint(point);
						listInvite.add(inv);
					}
					sumPoint += point * nb;
					nbVictory -= nb;
					logMe("RANKING " + ranking.getRanking() + " NB:" + nb + " POINT:" + point + " SUM:" + sumPoint + " NB VICTORY:" + nbVictory);
					if (doBreak) {
						break;
					}
				}
			}
			logMe("USER RANKING " + userRanking.getRanking() + " TOTAL:" + sumPoint);
		}
		nbVictoryCalculate = listInvite.size();

		ComputeDataRanking data = new ComputeDataRanking();
		data.setNbVictory(nbVictory);
		data.setNbVictoryCalculate(nbVictoryCalculate);
		data.setPointObjectif(pointObjectif);
		data.setPointCalculate(sumPoint);
		data.setListInviteCalculed(listInvite);

		return inviteService.groupByIdTournament(listInvite);
	}

//	public ComputeDataRanking computeDataRanking(boolean estimate) {
//		User user = userService.find();
//		Long idRanking = user.getIdRanking();
//		return computeDataRanking(idRanking, estimate);
//	}

	public ComputeDataRanking computeDataRanking(long idRanking, boolean estimate) {
		List<Invite> listVictory = inviteService.getByScoreResult(SCORE_RESULT.VICTORY);
		List<Invite> listDefeat = inviteService.getByScoreResult(SCORE_RESULT.DEFEAT);
		return computeDataRanking(listVictory, listDefeat, idRanking, estimate);
	}

	public ComputeDataRanking computeDataRanking(List<Invite> listVictory, List<Invite> listDefeat, long idRanking, boolean estimate) {
//		List<Invite> listInvite = inviteService.getByScoreResult(SCORE_RESULT.VICTORY);
		listVictory = inviteService.sortInviteByRanking(playerService, rankingService, estimate, true, listVictory);
		List<Invite> listInviteCalculed = new ArrayList<Invite>();
		List<Invite> listInviteNotUsed = new ArrayList<Invite>();
		int nbVictory = 0, nbVictoryCalculate = 0;
		int sumPoint = 0, pointObjectif = 0;
		int sumPointBonus = 0;
		Ranking userRanking = rankingService.find(idRanking);
		if (userRanking == null) {
			userRanking = rankingService.getNC();
		}
		int rankingPositionMin = userRanking.getOrder() - NB_RANKING_ORDER_LOWER;
		if (rankingPositionMin < 0) {
			rankingPositionMin = 0;
		}

		List<Ranking> listKeyRanking = rankingService.getWithPostionEqualUpper(rankingPositionMin);
		rankingService.order(listKeyRanking, true);
		nbVictory = userRanking.getVictoryMan();
		pointObjectif = userRanking.getRankingPointMan();
		logMe("USER RANKING " + userRanking.getRanking() + " NB VICTORY:" + nbVictory + " POINT OBJECTIF:" + pointObjectif);
		for(Invite invite : listVictory) {
			Player player = playerService.find(invite.getPlayer().getId());
			Ranking ranking = rankingService.getRanking(invite, player, estimate);
			if (sumPointBonus >= BONUS_POINT_LIMIT) {
				invite.setBonusPoint(0);
			} else {
				sumPointBonus += invite.getBonusPoint();
			}
			if (ranking.getOrder() >= rankingPositionMin && nbVictory > 0) {
				listInviteCalculed.add(invite);
				int rankingDif = ranking.getOrder() - userRanking.getOrder();
				int point = rankingService.getNbPointDifference(rankingDif);
				invite.setPoint(point);
				sumPoint += point;
				nbVictory--;
				logMe("RANKING " + ranking.getRanking() + " POINT:" + point + " SUM:" + sumPoint + " SUM BONUS:" + sumPointBonus + " NB VICTORY:" + nbVictory);
			} else {
				logMe("RANKING " + ranking.getRanking() + " NOT USED SUM BONUS:" + sumPointBonus);
				listInviteNotUsed.add(invite);
			}
		}
		inviteService.sortInviteByPoint(listInviteCalculed);
		inviteService.sortInviteByDate(listInviteNotUsed);
		nbVictoryCalculate = listInviteCalculed.size();
		logMe("USER RANKING " + userRanking.getRanking() + " TOTAL:" + sumPoint + " TOTAL BONUS:" + sumPointBonus + " NB VICTORY:" + nbVictoryCalculate);

		ComputeDataRanking data = new ComputeDataRanking();
		data.setNbMatch(inviteService.countByScoreResult(null));
		data.setNbVictory(nbVictory);
		data.setNbVictoryCalculate(nbVictoryCalculate);
		data.setPointObjectif(pointObjectif);
		data.setPointCalculate(sumPoint);
		data.setPointBonus(sumPointBonus);
		data.setListInviteCalculed(listInviteCalculed);
		data.setListInviteNotUsed(listInviteNotUsed);

		computeVE2I5G(data, listVictory, listDefeat, idRanking, estimate);
		computeNbVitoryAdditional(data, idRanking, estimate);

		return data;
	}

	private void computeVE2I5G(ComputeDataRanking data, List<Invite> listVictory, List<Invite> listDefeat, long idRanking, boolean estimate) {
		int iE = 0, i2I = 0, i5G = 0;
//		List<Invite> listInvite = inviteService.getByScoreResult(SCORE_RESULT.DEFEAT);
		Ranking userRanking = rankingService.find(idRanking);
		if (userRanking == null) {
			userRanking = rankingService.getNC();
		}
		if (listDefeat.size() > 0) {
			int rankingPosition = userRanking.getOrder();
			for(Invite invite : listDefeat) {
				if (!SCORE_RESULT.WO_DEFEAT.equals(invite.getScoreResult())) {
					Player player = playerService.find(invite.getPlayer().getId());
					Ranking ranking = rankingService.getRanking(invite, player, estimate);
					int rankingPositionDiff = rankingPosition - ranking.getOrder();
					if (rankingPositionDiff > 0) {
						switch(rankingPositionDiff) {
							case 0:
								iE++;
								break;
							case 1:
								i2I++;
								break;
							default:
								i5G++;
						}
					}
				}
			}
		}
		data.setVE2I5G(listVictory.size() - iE - (i2I*2) - (i5G*5));
		logMe("USER RANKING " + userRanking.getRanking() + " VE2I5G:" + data.getVE2I5G());
	}

	@SuppressLint("UseSparseArrays")
	private void computeNbVitoryAdditional(ComputeDataRanking data, long idRanking, boolean estimate) {
		if (data.getListInviteCalculed().size() == 0) {
			return;
		}
		HashMap<Integer, int[][]> victory = new HashMap<Integer, int[][]>();
		victory.put(4, new int[][]{
			{0, 4, 1},
			{5, 9, 2},
			{10, 14, 3},
			{15, 16, 4},
			{20, 24, 5},
			{25, 999, 6}
		});
		victory.put(3, new int[][]{
			{0, 7, 1},
			{8, 14, 2},
			{15, 22, 3},
			{23, 29, 4},
			{30, 39, 5},
			{40, 999, 6}
		});
		victory.put(2, new int[][]{
			{-999, -41, -3},
			{-40, -31, -2},
			{-30, -21, -1},
			{-20, -1, 0},
			{0, 7, 1},
			{8, 14, 2},
			{15, 22, 3},
			{23, 29, 4},
			{30, 39, 5},
			{40, 999, 6}
		});
		int iVE2I5G = data.getVE2I5G();
		Ranking userRanking = rankingService.find(idRanking);
		logMe("USER RANKING " + userRanking.getRanking() + " NB VICTORY BEFORE VE2I5G:" + data.getNbVictoryCalculate());
		if (userRanking != null) {
			int[][] nbVitoryVE2I5G = victory.get(userRanking.getSerie());
			if (nbVitoryVE2I5G != null) {
				int[] nb = null;
				int size = nbVitoryVE2I5G.length - 1;
				for(int i=0 ; i<=size ; i++) {
					nb = nbVitoryVE2I5G[i];
					if (
						(i == size && iVE2I5G >= nb[0]) ||
						(iVE2I5G >= nb[0] && iVE2I5G <= nb[1])) {
						logMe("USER RANKING " + userRanking.getRanking() + " VE2I5G FIND:" + nb);
						data.setNbVictoryAdditional(data.getNbVictoryAdditional() + nb[2]);
						break;
					}
				}
			}
		}
		logMe("USER RANKING " + userRanking.getRanking() + " NB VICTORY AFTER VE2I5G:" + data.getNbVictoryCalculate());
	}

	@SuppressLint("UseSparseArrays")
	private HashMap<Ranking,List<Invite>> getInviteGroupByPlayerRanking(List<Invite> listVictory, boolean estimate) {
		HashMap<Ranking, List<Invite>> ret = new HashMap<Ranking, List<Invite>>();
		User user = userService.find();
		Ranking userRanking = rankingService.find(user.getIdRanking());
//		List<Invite> listVictory = inviteService.getByScoreResult(Invite.SCORE_RESULT.VICTORY);
		if (userRanking != null && listVictory.size() > 0) {
			int rankingPositionMin = userRanking.getOrder() - NB_RANKING_ORDER_LOWER;
			if (rankingPositionMin < 0) {
				rankingPositionMin = 0;
			}

			for(Invite victory : listVictory) {
				Player player = playerService.find(victory.getPlayer().getId());
				Ranking ranking = rankingService.getRanking(victory, player, estimate);
				List<Invite> listInvite = null;
				if (ret.containsKey(ranking)) {
					listInvite = ret.get(ranking);
				} else {
					listInvite = new ArrayList<Invite>();
					ret.put(ranking, listInvite);
				}
				listInvite.add(victory);
			}
		}
		return ret;
	}

	private void logMe(String msg) {
		if (ApplicationConfig.SHOW_LOG_COMPUTER_RANK) {
			Logger.logMe(TAG, "COMPUTE RANKING - " + msg);
		}
    }
}