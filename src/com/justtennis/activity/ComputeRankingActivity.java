package com.justtennis.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.justtennis.R;
import com.justtennis.adapter.ComputeRankingListInviteAdapter;
import com.justtennis.business.ComputeRankingBusiness;
import com.justtennis.business.ListCompetitionBusiness.TYPE;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.compute_ranking_list_invite);

		business = new ComputeRankingBusiness(this, NotifierMessageLogger.getInstance());
		adapter = new ComputeRankingListInviteAdapter(this, business.getList());

		tvSumPoint = (TextView) findViewById(R.id.tv_sum_point);
		list = (ListView)findViewById(R.id.list);
		list.setOnItemClickListener(new OnItemClickListInvite(this));
		list.setAdapter(adapter);

		
		business.onCreate();
		
		TypeManager.getInstance().initializeActivity(findViewById(R.id.layout_main), false);
	}

	@Override
	protected void onResume() {
		super.onResume();
		business.onResume();
		refresh();
	}

	public void refresh() {
		adapter.setValue(business.getList());
		initializePalmaresPoint();
	}

	public void onClickClose(View view) {
		finish();
	}

	private void initializePalmaresPoint() {
		tvSumPoint.setText(business.getPointCalculate() + "/" + business.getPointObjectif());
		tvSumPoint.setVisibility(View.VISIBLE);
	}
}