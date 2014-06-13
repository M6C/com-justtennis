package com.justtennis.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

import com.justtennis.R;
import com.justtennis.adapter.ListCompetitionAdapter;
import com.justtennis.business.ListCompetitionBusiness;
import com.justtennis.listener.itemclick.OnItemClickListCompetition;
import com.justtennis.manager.TypeManager;
import com.justtennis.notifier.NotifierMessageLogger;

public class ListCompetitionActivity extends GenericActivity {

	@SuppressWarnings("unused")
	private static final String TAG = ListCompetitionActivity.class.getSimpleName();

	private ListCompetitionBusiness business;

	private ExpandableListView list;
	private ListCompetitionAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.list_competition);

		business = new ListCompetitionBusiness(this, NotifierMessageLogger.getInstance());
		business.onCreate();

		adapter = new ListCompetitionAdapter(this, business.getListTournament(), business.getTableInviteByTournament());

		list = (ExpandableListView) findViewById(R.id.list);
		list.setOnChildClickListener(new OnItemClickListCompetition(this));
		list.setAdapter(adapter);

		TypeManager.getInstance().initializeActivity(findViewById(R.id.layout_main), false);
	}

	@Override
	protected void onResume() {
		super.onResume();
		business.onResume();

		expandAll();
	}

	public void onClickClose(View view) {
		finish();
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
}