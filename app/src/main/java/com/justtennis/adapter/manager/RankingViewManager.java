package com.justtennis.adapter.manager;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.justtennis.R;
import com.justtennis.db.service.PlayerService;
import com.justtennis.db.service.RankingService;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Player;
import com.justtennis.domain.Ranking;
import com.justtennis.notifier.NotifierMessageLogger;

public class RankingViewManager {
	
	private static RankingViewManager instance;

	private RankingService rankingService;
	private Ranking rankingNC;

	private RankingViewManager(Context context, NotifierMessageLogger notifier) {
		rankingService = new RankingService(context, notifier);
		rankingNC = rankingService.getNC();
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

		Ranking ranking = rankingService.getRanking(invite, false);
		Ranking rankingEstimate = rankingService.getRanking(invite, true);

		if (ranking != null) {
			tvRanking.setText(ranking.getRanking());
			tvRanking.setVisibility(View.VISIBLE);
		} else {
			tvRanking.setVisibility(View.GONE);
		}

		if (ranking != null && ranking.getId() != rankingEstimate.getId() && !rankingNC.equals(rankingEstimate)) {
			tvRankingEstimate.setText(rankingEstimate.getRanking());
			tvRankingEstimate.setVisibility(View.VISIBLE);
		} else {
			tvRankingEstimate.setVisibility(View.GONE);
		}
	}

	public void manageRanking(View convertView, Player player, boolean estimate) {
		TextView tvRanking = (TextView) convertView.findViewById(R.id.tv_ranking);
		TextView tvRankingEstimate = (TextView) convertView.findViewById(R.id.tv_ranking_estimate);

		if (player != null) {
			if (PlayerService.isUnknownPlayer(player)) {
				tvRanking.setVisibility(View.GONE);
				tvRankingEstimate.setVisibility(View.GONE);
			} else {
				Ranking ranking = rankingService.getRanking(player, false);
				Ranking rankingEstimate = rankingService.getRanking(player, true);
				tvRanking.setText(ranking.getRanking());
				tvRanking.setVisibility(View.VISIBLE);
				
				if (ranking.getId() != rankingEstimate.getId() && !rankingNC.equals(rankingEstimate)) {
					tvRankingEstimate.setText(rankingEstimate.getRanking());
					tvRankingEstimate.setVisibility(View.VISIBLE);
				} else {
					tvRankingEstimate.setVisibility(View.GONE);
				}
			}
		}
	}
}