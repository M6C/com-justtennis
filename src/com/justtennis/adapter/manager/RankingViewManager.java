package com.justtennis.adapter.manager;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.justtennis.R;
import com.justtennis.db.service.RankingService;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Ranking;
import com.justtennis.notifier.NotifierMessageLogger;

public class RankingViewManager {
	
	private static RankingViewManager instance;

	private RankingService rankingService;

	private RankingViewManager(Context context, NotifierMessageLogger notifier) {
		rankingService = new RankingService(context, notifier);
	}

	public static RankingViewManager getInstance(Context context, NotifierMessageLogger notifier) {
		if (instance == null) {
			instance = new RankingViewManager(context, notifier);
		}
		return instance;
	}

	public void manageRanking(View convertView, Invite invite, boolean estimate) {
		TextView tvRanking = (TextView) convertView.findViewById(R.id.tv_ranking);
		TextView tvRankingEstimate = (TextView) convertView.findViewById(R.id.tv_ranking_estimate);

		if (invite.getPlayer() != null) {
			Ranking ranking = rankingService.getRanking(invite.getPlayer(), false);
			Ranking rankingEstimate = rankingService.getRanking(invite.getPlayer(), true);
			tvRanking.setText(ranking.getRanking());

			if (ranking.getId() != rankingEstimate.getId()) {
				tvRankingEstimate.setText(rankingEstimate.getRanking());
				tvRankingEstimate.setVisibility(View.VISIBLE);
			} else {
				tvRankingEstimate.setVisibility(View.GONE);
			}
		} else {
			Ranking r = rankingService.find(invite.getIdRanking());
			tvRanking.setText(r == null ? "" : r.getRanking());
		}
	}
}