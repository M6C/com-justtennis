package com.justtennis.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

import com.cameleon.common.android.factory.FactoryDialog;
import com.justtennis.R;
import com.justtennis.adapter.ListInviteAdapter;
import com.justtennis.adapter.ListPlayerAdapter;
import com.justtennis.business.ListInviteBusiness;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Player;
import com.justtennis.domain.Ranking;
import com.justtennis.listener.itemclick.OnItemClickListInvite;
import com.justtennis.listener.ok.OnClickInviteDeleteListenerOk;
import com.justtennis.notifier.NotifierMessageLogger;

public class ListInviteActivity extends Activity {

	private static final String TAG = ListInviteActivity.class.getSimpleName();

	private ListInviteBusiness business;
	
	private ListView list;
	private Spinner spFilterPlayer;
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

		spFilterPlayer = (Spinner)findViewById(R.id.sp_filter_player);
	}

	@Override
	
	protected void onResume() {
		super.onResume();

		business.onResume();

		initializePlayerList();
		refresh();
	}
	
//	@Override
//	public void onBackPressed() {
//		onClickClose(null);
//		super.onBackPressed();
//	}

	public void refresh() {
		adapter.setValue(business.getList());
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

	private void initializePlayerList() {
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, business.getListPlayerName());
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spFilterPlayer.setAdapter(dataAdapter);

		spFilterPlayer.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				Player player  = business.getListPlayer().get(position);
				Filter filter = adapter.getFilter();
				if (filter!=null) {
					filter.filter(player.getId().toString());
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}
}