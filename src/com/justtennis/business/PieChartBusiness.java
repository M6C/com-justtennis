package com.justtennis.business;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.R;
import com.justtennis.db.service.InviteService;
import com.justtennis.db.service.RankingService;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Invite.INVITE_TYPE;
import com.justtennis.domain.Invite.SCORE_RESULT;
import com.justtennis.domain.Ranking;

public class PieChartBusiness {

	@SuppressWarnings("unused")
	private static final String TAG = PieChartBusiness.class.getSimpleName();

	public enum CHART_DATA_TYPE {
		ALL(null, R.string.chart_type_all), 
		ENTRAINEMENT(INVITE_TYPE.ENTRAINEMENT, R.string.chart_type_entrainement),
		MATCH(INVITE_TYPE.MATCH, R.string.chart_type_match);

		public INVITE_TYPE type;
		public int stringId;

		CHART_DATA_TYPE(INVITE_TYPE type, int stringId) {
			this.type = type;
			this.stringId = stringId;
		}
	}
	
	public enum CHART_SCORE_RESULT {
		ALL(null, R.string.chart_result_score_all),
		VICTORY(SCORE_RESULT.VICTORY, R.string.chart_result_score_victory),
		DEFEAT(SCORE_RESULT.DEFEAT, R.string.chart_result_score_defeat),
		UNFINISHED(SCORE_RESULT.UNFINISHED, R.string.chart_result_score_unfinish);

		public SCORE_RESULT scoreResult;
		public int stringId;

		CHART_SCORE_RESULT(SCORE_RESULT scoreResult, int stringId) {
			this.scoreResult = scoreResult;
			this.stringId = stringId;
		}
	}

	private InviteService inviteService;
	private RankingService rankingService;

	private CHART_DATA_TYPE chartDataType = CHART_DATA_TYPE.ALL;
	private CHART_SCORE_RESULT chartScoreResult = CHART_SCORE_RESULT.ALL;

	public PieChartBusiness(Context context, INotifierMessage notificationMessage) {
		this.inviteService = new InviteService(context, notificationMessage);
		this.rankingService = new RankingService(context, notificationMessage);
	}

	public HashMap<String, Double> getData(CHART_DATA_TYPE chartDataType) {
		this.chartDataType = chartDataType;
		return getDataByRanking(chartDataType.type, chartScoreResult.scoreResult);
	}
	
	public HashMap<String, Double> getData(CHART_SCORE_RESULT chartScoreResult) {
		this.chartScoreResult = chartScoreResult;
		return getData(chartDataType);
	}
	
	private HashMap<String, Double> getDataByRanking(INVITE_TYPE type, Invite.SCORE_RESULT scoreResult) {
		HashMap<String,Double> data = inviteService.countByTypeGroupByRanking(type, scoreResult);
		return sortDataByRanking(data);
	}

	private HashMap<String, Double> sortDataByRanking(HashMap<String, Double> data) {
		HashMap<String, Double> ret = new LinkedHashMap<String, Double>();
		if (data!=null) {
			String[] listIdRanking = data.keySet().toArray(new String[0]);

			List<Ranking> listRanking = rankingService.getList(listIdRanking);
			rankingService.order(listRanking);

			for(Ranking ranking : listRanking) {
				ret.put(ranking.getRanking(), data.get(ranking.getId().toString()));
			}
		}
		return ret ;
	}
}