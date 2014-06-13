package com.justtennis.db.service;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.gdocument.gtracergps.launcher.log.Logger;

import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Player;
import com.justtennis.domain.Ranking;
import com.justtennis.domain.User;
import com.justtennis.notifier.NotifierMessageLogger;

public class ComputeRankService {

	private static final String TAG = ComputeRankService.class.getCanonicalName();

	private static final int NB_RANKING_ORDER_LOWER = 3;
	protected Context context;
	private PlayerService playerService;
	private InviteService inviteService;
	private UserService userService;
	private RankingService rankingService;

	public ComputeRankService(Context context, INotifierMessage notificationMessage) {
		this.context = context;
		playerService = new PlayerService(context, notificationMessage);
		inviteService = new InviteService(context, notificationMessage);
		userService = new UserService(context, NotifierMessageLogger.getInstance());
		rankingService = new RankingService(context, NotifierMessageLogger.getInstance());
	}

	public int compute() {
		int ret = 0, nbVictory = 0;
		User user = userService.find();
		Ranking userRanking = rankingService.find(user.getIdRanking());
//		HashMap<Long,List<Player>> mapPlayer = playerService.getGroupByIdRanking();
		HashMap<Long,List<Invite>> mapPlayer = inviteService.getGroupByIdRanking(Invite.SCORE_RESULT.VICTORY);
		if (userRanking != null && mapPlayer.keySet().size() > 0) {
			int rankingPositionMin = userRanking.getOrder() - NB_RANKING_ORDER_LOWER;
			if (rankingPositionMin < 0) {
				rankingPositionMin = 0;
			}

			int idx = 0;
			Set<Long> keySet = mapPlayer.keySet();
			String[] keyArray = new String[keySet.size()];
			for(Long id : keySet) {
				keyArray[idx++] = id.toString();
			}
			List<Ranking> listKeyRanking = rankingService.getList(keyArray);
			rankingService.order(listKeyRanking, true);
			nbVictory = userRanking.getVictoryMan();
			logMe("USER RANKING " + userRanking.getRanking() + " NB VICTORY:" + nbVictory);
			for(Ranking ranking : listKeyRanking) {
				int nb = mapPlayer.get(ranking.getId()).size();
				if (ranking.getOrder() >= rankingPositionMin) {
					int rankingDif = ranking.getOrder() - userRanking.getOrder();
					int point = rankingService.getNbPointDifference(rankingDif);
					if (nbVictory > nb) {
						ret += point * nb;
						nbVictory -= nb;
						logMe("RANKING " + ranking.getRanking() + " NB:" + nb + " POINT:" + point + " TOTAL:" + ret + " NB VICTORY:" + nbVictory);
					} else {
						ret += point * nbVictory;
						logMe("RANKING " + ranking.getRanking() + " NB:" + nbVictory + " [" + nb + "] POINT:" + point + " TOTAL:" + ret);
						break;
					}
				}
			}
			logMe("USER RANKING " + userRanking.getRanking() + " TOTAL:" + ret);
		}
		return ret;
	}

	private void logMe(String msg) {
		Logger.logMe(TAG, "COMPUTE RANKING - " + msg);
    }
}