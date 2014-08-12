package com.justtennis.adapter.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.justtennis.R;
import com.justtennis.db.service.RankingService;
import com.justtennis.domain.Player;
import com.justtennis.domain.Ranking;
import com.justtennis.domain.comparator.RankingComparatorByOrder;
import com.justtennis.notifier.NotifierMessageLogger;

public class RankingListManager {
	
	private static RankingListManager instance;

	private RankingService rankingService;
	private List<Ranking> listRanking;
	private String[] listTxtRankings;
	private Ranking rankingNC;

	private RankingListManager(Context context, NotifierMessageLogger notifier) {
		rankingService = new RankingService(context, notifier);
		initializeDataRanking();
	}

	public static RankingListManager getInstance(Context context, NotifierMessageLogger notifier) {
		if (instance == null) {
			instance = new RankingListManager(context, notifier);
		}
		return instance;
	}

	public void manageRanking(Activity context, final Player player, final boolean estimate) {
		IRankingListListener listener = new IRankingListListener() {
			@Override
			public void onRankingSelected(Ranking ranking) {
				if (estimate) {
					if (ranking.equals(rankingNC)) {
						player.setIdRankingEstimate(null);
					} else {
						player.setIdRankingEstimate(ranking.getId());
					}
				} else {
					if (ranking.equals(rankingNC)) {
						player.setIdRanking(null);
					} else {
						player.setIdRanking(ranking.getId());
					}
				}
			}
		};
		Long idRanking = estimate ? player.getIdRankingEstimate() : player.getIdRanking();
		manageRanking(context, listener, idRanking, estimate);
	}

	public void manageRanking(Activity context, final IRankingListListener listener, Long idRanking, boolean estimate) {
		Spinner spRanking = (Spinner)context.findViewById(estimate ? R.id.sp_ranking_estimate : R.id.sp_ranking);

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, listTxtRankings);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spRanking.setAdapter(dataAdapter);

		spRanking.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				Ranking ranking = listRanking.get(position);
				listener.onRankingSelected(ranking);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		if (idRanking != null) {
			initializeRanking(spRanking, idRanking);
		}
	}
	
	private void initializeRanking(Spinner spRanking, Long idRanking) {
		int rankingPosition = getRankingPosition(idRanking);
		if (rankingPosition < listRanking.size()) {
			spRanking.setSelection(rankingPosition, true);
		}
	}

	private int getRankingPosition(Long idRanking) {
		int position = 0;
		for(Ranking r : listRanking) {
			if (r.getId().equals(idRanking)) {
				break;
			} else {
				position++;
			}
		}
		return position;
	}

	/**
	 * BUSINESS
	 */
	private void initializeDataRanking() {
		SortedSet<Ranking> setRanking = new TreeSet<Ranking>(new RankingComparatorByOrder());

		setRanking.addAll(rankingService.getList());
		
		listRanking = new ArrayList<Ranking>(setRanking);

		int i=0;
		listTxtRankings = new String[setRanking.size()];
		for(Ranking ranking : setRanking) {
			listTxtRankings[i++] = ranking.getRanking();
		}

		rankingNC = rankingService.getNC();
	}

	/**
	 * INNER INTERFACE
	 */
	public interface IRankingListListener {
		void onRankingSelected(Ranking ranking);
	}
}