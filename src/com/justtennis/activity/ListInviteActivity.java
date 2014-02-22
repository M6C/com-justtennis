package com.justtennis.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import com.cameleon.common.android.factory.FactoryDialog;
import com.justtennis.R;
import com.justtennis.adapter.ListInviteAdapter;
import com.justtennis.business.ListInviteBusiness;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Player;
import com.justtennis.listener.itemclick.OnItemClickListInvite;
import com.justtennis.listener.ok.OnClickInviteDeleteListenerOk;
import com.justtennis.notifier.NotifierMessageLogger;

public class ListInviteActivity extends Activity {

	@SuppressWarnings("unused")
	private static final String TAG = ListInviteActivity.class.getSimpleName();

	private ListInviteBusiness business;

	private ListView list;
	private ListInviteAdapter adapter;

	private LinearLayout llFilterPlayer;
	private Spinner spFilterPlayer;
	private Filter filter;
	private CharSequence filterPlayerValue = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.list_invite);
//		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.list_invite_title);

		business = new ListInviteBusiness(this, NotifierMessageLogger.getInstance());
		adapter = new ListInviteAdapter(this, business.getList());

		filter = adapter.getFilter();
		spFilterPlayer = (Spinner)findViewById(R.id.sp_filter_player);
		llFilterPlayer = (LinearLayout)findViewById(R.id.ll_filter_player);
		
		list = (ListView)findViewById(R.id.list);
		list.setOnItemClickListener(new OnItemClickListInvite(this));
		list.setAdapter(adapter);

		
		business.onCreate();
		
		initializePlayerList();
	}

	@Override
	protected void onResume() {
		super.onResume();
		business.onResume();
		refresh();
	}

	public void refresh() {
		adapter.setValue(business.getList());
		filter.filter(filterPlayerValue);
	}

	public void onClickClose(View view) {
		finish();
	}

	public void onClickButtonFilter(View view) {
		if (llFilterPlayer.getVisibility() == View.GONE) {
			llFilterPlayer.setVisibility(View.VISIBLE);
		} else {
			llFilterPlayer.setVisibility(View.GONE);
			filterPlayerValue = null;
			filter.filter(filterPlayerValue);
		}
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
				if (filter!=null) {
					Player player = business.getPlayerNotEmpty(position);
					if (player != null) {
						filterPlayerValue = player.getId().toString();
					} else {
						filterPlayerValue = null;
					}
					filter.filter(filterPlayerValue);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}
}