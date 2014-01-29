package com.justtennis.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.cameleon.common.android.factory.FactoryDialog;
import com.justtennis.R;
import com.justtennis.adapter.ListInviteAdapter;
import com.justtennis.business.ListInviteBusiness;
import com.justtennis.domain.Invite;
import com.justtennis.listener.itemclick.OnItemClickListInvite;
import com.justtennis.listener.ok.OnClickInviteDeleteListenerOk;
import com.justtennis.notifier.NotifierMessageLogger;

public class ListInviteActivity extends Activity {

	private static final String TAG = ListInviteActivity.class.getSimpleName();

	private ListInviteBusiness business;
	
	private ListView list;
	private ListInviteAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_invite);
		
		business = new ListInviteBusiness(this, NotifierMessageLogger.getInstance());
		adapter = new ListInviteAdapter(this, business.getList());
		
		list = (ListView)findViewById(R.id.list);
		list.setOnItemClickListener(new OnItemClickListInvite(this));
		list.setAdapter(adapter);
	}

	@Override
	
	protected void onResume() {
		super.onResume();

		business.onResume();

		refresh();
	}
	
//	@Override
//	public void onBackPressed() {
//		onClickClose(null);
//		super.onBackPressed();
//	}

	public void refresh() {
		adapter.notifyDataSetChanged();
	}

	public void onClickClose(View view) {
		finish();
//		Intent intent = null;
//		intent = new Intent(this, MainActivity.class);
//		startActivity(intent);
	}

	public void onClickDelete(View view) {
		Invite invite = (Invite)view.getTag();
		OnClickInviteDeleteListenerOk listener = new OnClickInviteDeleteListenerOk(business, invite);
		FactoryDialog.getInstance()
			.buildOkCancelDialog(business.getContext(), listener, R.string.dialog_player_delete_title, R.string.dialog_player_delete_message)
			.show();
	}
}