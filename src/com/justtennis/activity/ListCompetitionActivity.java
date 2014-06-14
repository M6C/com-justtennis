package com.justtennis.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.justtennis.R;
import com.justtennis.adapter.ListCompetitionAdapter;
import com.justtennis.business.ListCompetitionBusiness;
import com.justtennis.business.ListCompetitionBusiness.TYPE;
import com.justtennis.listener.itemclick.OnItemClickListCompetition;
import com.justtennis.manager.TypeManager;
import com.justtennis.notifier.NotifierMessageLogger;

public class ListCompetitionActivity extends GenericActivity {

	@SuppressWarnings("unused")
	private static final String TAG = ListCompetitionActivity.class.getSimpleName();

	private ListCompetitionBusiness business;

	private ExpandableListView list;
	private ListCompetitionAdapter adapter;
	private TextView tvSumPoint;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.list_competition);

		business = new ListCompetitionBusiness(this, NotifierMessageLogger.getInstance());
		business.onCreate();

		adapter = new ListCompetitionAdapter(this, business.getListTournament(), business.getTableInviteByTournament());

		tvSumPoint = (TextView) findViewById(R.id.tv_sum_point);
		list = (ExpandableListView) findViewById(R.id.list);
		list.setOnChildClickListener(new OnItemClickListCompetition(this));
		list.setAdapter(adapter);

		TypeManager.getInstance().initializeActivity(findViewById(R.id.layout_main), false);
	}

	@Override
	protected void onResume() {
		super.onResume();
		business.onResume();

		initializePalmaresPoint();

		expandAll();
	}

	public void onClickClose(View view) {
		finish();
	}

	public void onClickInviteAll(View view) {
		updateInviteType(TYPE.ALL);
	}
	
	public void onClickInvitePalmares(View view) {
		updateInviteType(TYPE.PALMARES);
	}
	
	private void expandAll() {
		int count = adapter.getGroupCount();
		for (int i = 0; i < count; i++) {
			list.expandGroup(i);
		}
	}

	// method to collapse all groups
	private void collapseAll() {
		int count = adapter.getGroupCount();
		for (int i = 0; i < count; i++) {
			list.collapseGroup(i);
		}
	}

	private void updateInviteType(TYPE type) {
		business.setType(type);
		business.refreshData();
		adapter.notifyDataSetChanged();
		expandAll();
		initializePalmaresPoint();
	}

	private void initializePalmaresPoint() {
		if (business.getType() == TYPE.PALMARES) {
			tvSumPoint.setText(business.getSumPoint() + "/" + business.getRankingPoint());
			tvSumPoint.setVisibility(View.VISIBLE);
		} else {
			tvSumPoint.setVisibility(View.GONE);
		}
	}
}