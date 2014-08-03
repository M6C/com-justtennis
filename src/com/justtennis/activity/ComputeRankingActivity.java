package com.justtennis.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.justtennis.R;
import com.justtennis.adapter.ComputeRankingListInviteAdapter;
import com.justtennis.adapter.manager.RankingListManager;
import com.justtennis.adapter.manager.RankingListManager.IRankingListListener;
import com.justtennis.business.ComputeRankingBusiness;
import com.justtennis.domain.Ranking;
import com.justtennis.listener.itemclick.OnItemClickListInvite;
import com.justtennis.manager.TypeManager;
import com.justtennis.notifier.NotifierMessageLogger;

public class ComputeRankingActivity extends GenericActivity {

	@SuppressWarnings("unused")
	private static final String TAG = ComputeRankingActivity.class.getSimpleName();

	private ComputeRankingBusiness business;

	private ListView list;
	private ComputeRankingListInviteAdapter adapter;
	private TextView tvSumPoint;
	private TextView tvNbVictory;

	private RankingListManager rankingListManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.compute_ranking_list_invite);

		NotifierMessageLogger notifier = NotifierMessageLogger.getInstance();
		business = new ComputeRankingBusiness(this, notifier);
		adapter = new ComputeRankingListInviteAdapter(this, business.getList());
		rankingListManager = RankingListManager.getInstance(this, notifier);

		tvSumPoint = (TextView) findViewById(R.id.tv_sum_point);
		tvNbVictory = (TextView) findViewById(R.id.tv_nb_victory);

		adapter.setValue(business.getList());

		list = (ListView)findViewById(R.id.list);
		list.setOnItemClickListener(new OnItemClickListInvite(this));
		list.setAdapter(adapter);

		business.onCreate();
	
		initializeRankingList();
		TypeManager.getInstance().initializeActivity(findViewById(R.id.layout_main), false);
	}

	@Override
	protected void onResume() {
		super.onResume();
		business.onResume();
		refresh();
	}

	public void refresh() {
		initializePalmaresPoint();
		initializePalmaresNbVictory();
	}

	public void onClickClose(View view) {
		finish();
	}

	private void initializeRankingList() {
		IRankingListListener listener = new IRankingListListener() {
			@Override
			public void onRankingSelected(Ranking ranking) {
				business.setIdRanking(ranking.getId());
				business.refreshData();

				adapter.notifyDataSetChanged();
				refresh();
			}
		};
		rankingListManager.manageRanking(this, listener, business.getIdRanking(), false);
	}

	private void initializePalmaresPoint() {
		tvSumPoint.setText(business.getPointCalculate() + "/" + business.getPointObjectif());
		tvSumPoint.setVisibility(View.VISIBLE);
	}

	private void initializePalmaresNbVictory() {
		tvNbVictory.setText(business.getNbVictoryCalculate() + "/" + business.getNbVictorySum());
		tvNbVictory.setVisibility(View.VISIBLE);
	}
}