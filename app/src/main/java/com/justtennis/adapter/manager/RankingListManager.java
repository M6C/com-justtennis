package com.justtennis.adapter.manager;

import android.app.Activity;
import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.cameleon.common.android.factory.FactoryDialog;
import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.ApplicationConfig;
import com.justtennis.R;
import com.justtennis.db.service.RankingService;
import com.justtennis.domain.Player;
import com.justtennis.domain.Ranking;
import com.justtennis.domain.comparator.RankingComparatorByOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class RankingListManager {
	
	private static RankingListManager instance;

	private RankingService rankingService;
	private List<Ranking> listRanking;
	private String[] listTxtRankings;
	private Ranking rankingNC;

	private RankingListManager(Context context, INotifierMessage notifier) {
		rankingService = new RankingService(context, notifier);
		initializeDataRanking();
	}

	public static RankingListManager getInstance(Context context, INotifierMessage notifier) {
		if (instance == null) {
			instance = new RankingListManager(context, notifier);
		}
		return instance;
	}

	public void manageRanking(Activity context, Player player, boolean estimate) {
		manageRanking(context, context.getWindow().getDecorView(), player, estimate);
	}

	public void manageRanking(Activity context, IRankingListListener rankingListener, Player player, boolean estimate) {
		manageRanking(context, context.getWindow().getDecorView(), rankingListener, player, estimate);
	}

	public void manageRanking(final ContextThemeWrapper context, View view, final Player player, final boolean estimate) {
		IRankingListListener listener = ranking -> {
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
        };
		manageRanking(context, view, listener, player, estimate);
	}

	public void manageRanking(final ContextThemeWrapper context, View view, IRankingListListener rankingListener, Player player, final boolean estimate) {
		Long idRanking = getRankingId(player, estimate);
		manageRanking(context, view, rankingListener, idRanking, estimate);
	}

	public void manageRanking(Activity context, final IRankingListListener listener, Long idRanking, boolean estimate) {
		manageRanking(context, context.getWindow().getDecorView(), listener, idRanking, estimate);
	}

	public void manageRanking(final ContextThemeWrapper context, View view, final IRankingListListener listener, Long idRanking, boolean estimate) {
		final Spinner spRanking = view.findViewById(estimate ? R.id.sp_ranking_estimate : R.id.sp_ranking);

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(context, R.layout.spinner_item, listTxtRankings);
		dataAdapter.setDropDownViewResource(R.layout.spinner_item);
		spRanking.setAdapter(dataAdapter);
		initializeRankingListener(context, listener, view, idRanking, estimate);
	}

	public void initializeRankingListener(final Activity context, final IRankingListListener listener, final Long idRanking, final boolean estimate) {
		initializeRankingListener(context, listener, context.getWindow().getDecorView(), idRanking, estimate);
	}

	private void initializeRankingListener(final ContextThemeWrapper context, final IRankingListListener listener, final View view, final Long idRanking, final boolean estimate) {
		final Spinner spRanking = view.findViewById(estimate ? R.id.sp_ranking_estimate : R.id.sp_ranking);
		spRanking.setEnabled(listener != null);

		spRanking.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
				TextView tv = spRanking.findViewById(android.R.id.text1);
				if (!spRanking.isEnabled()) {
					tv.setTextColor(context.getResources().getColor(R.color.spinner_color_hint));
				} else {
					tv.setTextColor(context.getResources().getColor(position == 0 ? R.color.spinner_color_hint : android.R.color.black));
				}

				if (listener != null) {
					Ranking ranking = listRanking.get(position);
					listener.onRankingSelected(ranking);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// Nothing to do
			}
		});

		initializeRanking(spRanking, (idRanking == null) ? rankingService.getNC().getId() : idRanking);
	}

	public void manageRankingTextViewDialog(final Activity context, View view, final IRankingListListener listener, boolean estimate) {
		View vwRanking = view.findViewById(estimate ? R.id.tv_ranking_estimate : R.id.tv_ranking);
		vwRanking.setOnClickListener(v -> {
            AdapterView.OnItemClickListener onClickListener = (parent, view1, position, id) -> {
                Ranking ranking = listRanking.get(position);
                listener.onRankingSelected(ranking);
            };
            FactoryDialog.getInstance().buildListView(context, 0, listTxtRankings, onClickListener).show();
        });
	}

	private void initializeRanking(Spinner spRanking, Long idRanking) {
		spRanking.setSelection(getRankingPosition(idRanking), true);
	}

	private Long getRankingId(Player player, boolean estimate) {
		if (player==null) {
			return rankingNC.getId();
		} else {
			return (estimate ? player.getIdRankingEstimate() : player.getIdRanking());
		}
	}

	private int getRankingPosition(Long idRanking) {
		int ret = 0;
		for(int i=0 ; i<listRanking.size() ; i++) {
			Ranking r = listRanking.get(i);
			if (r.getId().equals(idRanking)) {
				ret = i;
				break;
			}
		}
		return ret;
	}

	/**
	 * BUSINESS
	 */
	private void initializeDataRanking() {
		SortedSet<Ranking> setRanking = new TreeSet<>(new RankingComparatorByOrder());

		setRanking.addAll(rankingService.getList());
		
		listRanking = new ArrayList<>(setRanking);

		int i=0;
		listTxtRankings = new String[setRanking.size()];
		for(Ranking ranking : setRanking) {
			listTxtRankings[i] = ranking.getRanking();
			if (ApplicationConfig.SHOW_ID) {
				listTxtRankings[i] = "[" + ranking.getId() + "] " + listTxtRankings[i];
			}
			i++;
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