package com.justtennis.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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
import com.justtennis.business.NewLook01ListInviteBusiness;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Invite.INVITE_TYPE;
import com.justtennis.domain.Player;
import com.justtennis.listener.itemclick.OnItemClickListInvite;
import com.justtennis.notifier.NotifierMessageLogger;

public class NewLook01ListInviteActivity extends Activity {

	@SuppressWarnings("unused")
	private static final String TAG = NewLook01ListInviteActivity.class.getSimpleName();

	private NewLook01ListInviteBusiness business;

	private ListView list;
	private ListInviteAdapter adapter;

	private LinearLayout llFilterPlayer;
	private Spinner spFilterPlayer;
	private Spinner spFilterType;
	private Filter filter;
	private CharSequence filterPlayerValue = null;
	private INVITE_TYPE filterTypeValue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.list_invite);
//		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.list_invite_title);

		business = new NewLook01ListInviteBusiness(this, NotifierMessageLogger.getInstance());
		adapter = new ListInviteAdapter(this, business.getList());

		filter = adapter.getFilter();
		spFilterPlayer = (Spinner)findViewById(R.id.sp_filter_player);
		llFilterPlayer = (LinearLayout)findViewById(R.id.ll_filter_player);
		spFilterType = (Spinner)findViewById(R.id.sp_filter_type);
		
		list = (ListView)findViewById(R.id.list);
		list.setOnItemClickListener(new OnItemClickListInvite(this));
		list.setAdapter(adapter);

		
		business.onCreate();
		
		initializePlayerList();
		initializeTypeList();
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
		final Invite invite = (Invite)view.getTag();
		OnClickListener listener = new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (which == DialogInterface.BUTTON_POSITIVE) {
		        	business.delete(invite);
				}
			}
		};
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

					filter();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

	private void initializeTypeList() {
		String[] listTypeName = new String[]{"", INVITE_TYPE.ENTRAINEMENT.toString(), INVITE_TYPE.MATCH.toString()};
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listTypeName);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spFilterType.setAdapter(dataAdapter);
		spFilterType.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (filter!=null) {
					switch(position) {
						case 0:
						default:
							filterTypeValue = null;
							break;
						case 1:
							filterTypeValue = INVITE_TYPE.ENTRAINEMENT;
							break;
						case 2:
							filterTypeValue = INVITE_TYPE.MATCH;
							break;
					}

					filter();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}
	
	private void filter() {
		String filterValue = "";
		if (filterPlayerValue != null) {
			filterValue += filterPlayerValue;
		}
		filterValue += ";";
		if (filterTypeValue != null) {
			filterValue += filterTypeValue.toString();
		}
		filter.filter(";".equals(filterValue) ? null : filterValue);
	}
}