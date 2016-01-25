package com.justtennis.activity;

import android.content.Intent;
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

	private static final int RESULT_ITEM_CLICK = 1;

	private ComputeRankingBusiness business;

	private ListView list;
	private ComputeRankingListInviteAdapter adapter;
	private TextView tvSumPoint;
	private TextView tvNbVictory;
	private TextView tvNbVictoryDetail;

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
		tvNbVictoryDetail = (TextView) findViewById(R.id.tv_nb_victory_detail);

		adapter.setValue(business.getList());

		list = (ListView)findViewById(R.id.list);
		list.setOnItemClickListener(new OnItemClickListInvite(this, RESULT_ITEM_CLICK));
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RESULT_ITEM_CLICK) {
			business.refreshData();
			adapter.notifyDataSetChanged();
		}
	}

	public void refresh() {
		initializePalmaresPoint();
		initializePalmaresNbVictory();
	}

	public void onClickPoint(View view) {
	}

	public void refreshData() {
		business.refreshData();

		adapter.notifyDataSetChanged();
		refresh();
	}

	private void initializeRankingList() {
		IRankingListListener listener = new IRankingListListener() {
			@Override
			public void onRankingSelected(Ranking ranking) {
				business.setIdRanking(ranking.getId());
				refreshData();
			}
		};
		rankingListManager.manageRanking(this, listener, business.getIdRanking(), false);
	}

	private void initializePalmaresPoint() {
		int point = business.getPointCalculate() + business.getPointBonus();
		String text = point + "/" + business.getPointObjectif();
		if (business.getPointBonus() > 0) {
			text += " [bonus:" + business.getPointBonus() + "]";
		}
		tvSumPoint.setText(text);
		tvSumPoint.setVisibility(View.VISIBLE);
	}

	private void initializePalmaresNbVictory() {
		tvNbVictory.setText(business.getNbVictoryCalculate() + "/" + business.getNbVictorySum());
		tvNbVictory.setVisibility(View.VISIBLE);
		tvNbVictoryDetail.setText("("+ business.getNbVictoryAdditional() + ") [V-E-2I-5G:" + business.getVE2I5G() + "]");
		tvNbVictoryDetail.setVisibility(View.VISIBLE);
	}
}