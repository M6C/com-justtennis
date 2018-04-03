package com.justtennis.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.cameleon.common.android.factory.FactoryDialog;
import com.justtennis.R;
import com.justtennis.adapter.ListInviteAdapter;
import com.justtennis.business.ListInviteBusiness;
import com.justtennis.domain.Invite;
import com.justtennis.listener.itemclick.OnItemClickListInvite;
import com.justtennis.listener.ok.OnClickInviteDeleteListenerOk;
import com.justtennis.manager.TypeManager;
import com.justtennis.notifier.NotifierMessageLogger;

public class ListInviteActivity extends GenericActivity {

	@SuppressWarnings("unused")
	private static final String TAG = ListInviteActivity.class.getSimpleName();

	private static final int RESULT_ITEM_CLICK = 1;

	private ListInviteBusiness business;

	private ListView list;
	private ListInviteAdapter adapter;

	private LinearLayout llFilterPlayer;
	private EditText tvFilterPlayer;
	private Filter filter;
	private CharSequence filterIdPlayerValue = null;
	private TypeManager.TYPE filterTypeValue;
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
		tvFilterPlayer = (EditText)findViewById(R.id.et_filter_player);
		llFilterPlayer = (LinearLayout)findViewById(R.id.ll_filter_player);
		
		list = (ListView)findViewById(R.id.list);
		list.setOnItemClickListener(new OnItemClickListInvite(this, RESULT_ITEM_CLICK));
		list.setAdapter(adapter);

		
		business.onCreate();
		
		initializePlayerFilter();
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
		adapter.setValue(business.getList());
		filter.filter(filterIdPlayerValue);
	}

	public void onClickButtonFilter(View view) {
		if (llFilterPlayer.getVisibility() == View.GONE) {
			llFilterPlayer.setVisibility(View.VISIBLE);
		} else {
			llFilterPlayer.setVisibility(View.GONE);
			filterIdPlayerValue = null;
			filter.filter(filterIdPlayerValue);
		}
	}

	public void onClickDelete(View view) {
		Invite invite = (Invite)view.getTag();
		OnClickInviteDeleteListenerOk listener = new OnClickInviteDeleteListenerOk(business, invite);
		FactoryDialog.getInstance()
			.buildOkCancelDialog(business.getContext(), listener, R.string.dialog_player_delete_title, R.string.dialog_player_delete_message)
			.show();
	}

	private void initializePlayerFilter() {
		tvFilterPlayer.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (filter!=null) {
					filterPlayerValue = s;

					filter();
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	private void filter() {
		String filterValue = "";
		if (filterIdPlayerValue != null) {
			filterValue += filterIdPlayerValue;
		}
		filterValue += ";";
		if (filterTypeValue != null) {
			filterValue += filterTypeValue.toString();
		}
		filterValue += ";";
		if (filterPlayerValue != null) {
			filterValue += filterPlayerValue;
		}
		filter.filter(";".equals(filterValue) ? null : filterValue);
	}
}