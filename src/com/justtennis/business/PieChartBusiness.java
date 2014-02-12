package com.justtennis.business;

import java.util.HashMap;
import java.util.List;

import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.service.InviteService;
import com.justtennis.db.service.RankingService;
import com.justtennis.domain.Invite.INVITE_TYPE;
import com.justtennis.domain.Ranking;

public class PieChartBusiness {

	@SuppressWarnings("unused")
	private static final String TAG = PieChartBusiness.class.getSimpleName();

	private InviteService inviteService;
	private RankingService rankingService;

	public PieChartBusiness(Context context, INotifierMessage notificationMessage) {
		this.inviteService = new InviteService(context, notificationMessage);
		this.rankingService = new RankingService(context, notificationMessage);
	}

	public HashMap<String, Double> getDataByRanking() {
		HashMap<String, Double> ret = new HashMap<String, Double>();
		List<HashMap<String,Object>> data = inviteService.countGroupByRanking();
		if (data!=null) {
			for(HashMap<String,Object> row : data) {
				Ranking ranking = rankingService.find(Long.parseLong(row.get("ID_RANKING").toString()));
				ret.put(ranking.getRanking(), Double.parseDouble(row.get("NB").toString()));
			}
		}
		return ret ;
	}

	public HashMap<String, Double> getDataByRanking(INVITE_TYPE type) {
		HashMap<String, Double> ret = new HashMap<String, Double>();
		List<HashMap<String,Object>> data = inviteService.countByTypeGroupByRanking(type);
		if (data!=null) {
			for(HashMap<String,Object> row : data) {
				Ranking ranking = rankingService.find(Long.parseLong(row.get("ID_RANKING").toString()));
				ret.put(ranking.getRanking(), Double.parseDouble(row.get("NB").toString()));
			}
		}
		return ret ;
	}
}