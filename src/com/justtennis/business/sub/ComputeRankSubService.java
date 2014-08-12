package com.justtennis.business.sub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.gdocument.gtracergps.launcher.log.Logger;

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

	public HashMap<Long,List<Invite>> getListInvite() {
		HashMap<Long,List<Invite>> mapInvite = inviteService.getGroupByIdRanking(Invite.SCORE_RESULT.VICTORY);
		return getListInvite(mapInvite);
	}
	
	public HashMap<Long,List<Invite>> getListInvite(boolean estimate) {
		HashMap<Long,List<Invite>> mapInvite = getInviteGroupByPlayerRanking(estimate);
		return getListInvite(mapInvite);
	}

	private HashMap<Long,List<Invite>> getListInvite(HashMap<Long,List<Invite>> mapInvite) {
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
			
			int idx = 0;
			Set<Long> keySet = mapInvite.keySet();
			String[] keyArray = new String[keySet.size()];
			for(Long id : keySet) {
				keyArray[idx++] = id.toString();
			}
			boolean doBreak = false;
			List<Ranking> listKeyRanking = rankingService.getList(keyArray);
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

	public ComputeDataRanking computeDataRanking(boolean estimate) {
		User user = userService.find();
		Long idRanking = user.getIdRanking();
		return computeDataRanking(idRanking, estimate);
	}

	public ComputeDataRanking computeDataRanking(long idRanking, boolean estimate) {
		List<Invite> listInvite = inviteService.getByScoreResult(SCORE_RESULT.VICTORY);
		List<Invite> listInviteCalculed = new ArrayList<Invite>();
		List<Invite> listInviteNotUsed = new ArrayList<Invite>();
		int nbVictory = 0, nbVictoryCalculate = 0;
		int sumPoint = 0, pointObjectif = 0;
		int sumPointBonus = 0;
		Ranking userRanking = rankingService.find(idRanking);
		if (userRanking != null && listInvite.size() > 0) {
			int rankingPositionMin = userRanking.getOrder() - NB_RANKING_ORDER_LOWER;
			if (rankingPositionMin < 0) {
				rankingPositionMin = 0;
			}

			List<Ranking> listKeyRanking = rankingService.getWithPostionEqualUpper(rankingPositionMin);
			rankingService.order(listKeyRanking, true);
			nbVictory = userRanking.getVictoryMan();
			pointObjectif = userRanking.getRankingPointMan();
			logMe("USER RANKING " + userRanking.getRanking() + " NB VICTORY:" + nbVictory);
			for(Invite invite : listInvite) {
				Player player = playerService.find(invite.getPlayer().getId());
				Ranking ranking = rankingService.getRanking(player, estimate);
				if (ranking.getOrder() >= rankingPositionMin) {
					listInviteCalculed.add(invite);
					int rankingDif = ranking.getOrder() - userRanking.getOrder();
					int point = rankingService.getNbPointDifference(rankingDif);
					invite.setPoint(point);
					sumPoint += point;
					sumPointBonus += invite.getBonusPoint();
					nbVictory--;
					logMe("RANKING " + ranking.getRanking() + " POINT:" + point + " SUM:" + sumPoint + " NB VICTORY:" + nbVictory);
				} else {
					listInviteNotUsed.add(invite);
				}
			}
			inviteService.sortInviteByPoint(listInviteCalculed);
			inviteService.sortInviteByDate(listInviteNotUsed);
			logMe("USER RANKING " + userRanking.getRanking() + " TOTAL:" + sumPoint);
		}
		nbVictoryCalculate = listInviteCalculed.size();

		ComputeDataRanking data = new ComputeDataRanking();
		data.setNbVictory(nbVictory);
		data.setNbVictoryCalculate(nbVictoryCalculate);
		data.setPointObjectif(pointObjectif);
		data.setPointCalculate(sumPoint);
		data.setPointBonus(sumPointBonus);
		data.setListInviteCalculed(listInviteCalculed);
		data.setListInviteNotUsed(listInviteNotUsed);

		return data;
	}
	
	private HashMap<Long,List<Invite>> getInviteGroupByPlayerRanking(boolean estimate) {
		HashMap<Long, List<Invite>> ret = new HashMap<Long, List<Invite>>();
		User user = userService.find();
		Ranking userRanking = rankingService.find(user.getIdRanking());
		List<Invite> listVictory = inviteService.getByScoreResult(Invite.SCORE_RESULT.VICTORY);
		if (userRanking != null && listVictory.size() > 0) {
			int rankingPositionMin = userRanking.getOrder() - NB_RANKING_ORDER_LOWER;
			if (rankingPositionMin < 0) {
				rankingPositionMin = 0;
			}

			for(Invite victory : listVictory) {
				Player player = playerService.find(victory.getPlayer().getId());
				Long idRanking = rankingService.getRanking(player, estimate).getId();
				List<Invite> listInvite = null;
				if (ret.containsKey(idRanking)) {
					listInvite = ret.get(idRanking);
				} else {
					listInvite = new ArrayList<Invite>();
					ret.put(idRanking, listInvite);
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