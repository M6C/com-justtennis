package com.justtennis.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.cameleon.common.android.factory.FactoryDialog;
import com.justtennis.R;
import com.justtennis.adapter.ListPlayerAdapter;
import com.justtennis.business.ListPlayerBusiness;
import com.justtennis.db.service.PlayerService;
import com.justtennis.domain.Player;
import com.justtennis.listener.itemclick.OnItemClickListPlayer;
import com.justtennis.listener.itemclick.OnItemClickListPlayerForResult;
import com.justtennis.listener.itemclick.OnItemClickListPlayerInvite;
import com.justtennis.listener.ok.OnClickPlayerDeleteListenerOk;
import com.justtennis.listener.ok.OnClickPlayerSendListenerOk;
import com.justtennis.manager.TypeManager;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.parser.PlayerParser;

public class ListPlayerActivity extends GenericActivity {

	private static final String TAG = ListPlayerActivity.class.getSimpleName();
	public static final String EXTRA_MODE = "MODE";
	public static final String EXTRA_PLAYER_ID = "EXTRA_PLAYER_ID";
	private static final int RESULT_PLAYER = 1;
	private static final int RESULT_PLAYER_FOR_INFO = 2;

	public enum MODE {
		EDIT,
		FOR_RESULT,
		INVITE
	};

	private ListPlayerBusiness business;
	
	private ListView list;
	private ListPlayerAdapter adapter;

	private LinearLayout llFilterType;
	private Spinner spFilterType;
	private Filter filter;
	private TypeManager.TYPE filterTypeValue = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_player);
		
		business = new ListPlayerBusiness(this, NotifierMessageLogger.getInstance());
		adapter = new ListPlayerAdapter(this, business.getList());
		
		spFilterType = (Spinner)findViewById(R.id.sp_filter_type);
		llFilterType = (LinearLayout)findViewById(R.id.ll_filter_type);
		filter = adapter.getFilter();

		list = (ListView)findViewById(R.id.list);
		list.setAdapter(adapter);

		initializeTypeList();
		TypeManager.getInstance().initializeActivity(findViewById(R.id.layout_main), false);
	}

	@Override
	protected void onResume() {
		super.onResume();

		initialize();

		switch (business.getMode()) {
			case EDIT:
				list.setOnItemClickListener(new OnItemClickListPlayer(this));
				break;
			case INVITE:
				list.setOnItemClickListener(new OnItemClickListPlayerInvite(this));
				break;
			case FOR_RESULT:
				list.setOnItemClickListener(new OnItemClickListPlayerForResult(this));
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case RESULT_PLAYER:
				if (data!=null) {
					Intent intent = new Intent();
					long id = data.getLongExtra(PlayerActivity.EXTRA_PLAYER_ID, PlayerService.ID_EMPTY_PLAYER);
					if (id != PlayerService.ID_EMPTY_PLAYER) {
						intent.putExtra(ListPlayerActivity.EXTRA_PLAYER_ID, id);
					}
					setResult(Activity.RESULT_OK, intent);
					finish();
				}
				break;
			case RESULT_PLAYER_FOR_INFO:
				if (data!=null) {
					long id = data.getLongExtra(InviteDemandeActivity.EXTRA_PLAYER_ID, PlayerService.ID_EMPTY_PLAYER);
					if (id != PlayerService.ID_EMPTY_PLAYER) {
						Intent intent = new Intent(this, InviteDemandeActivity.class);
						intent.putExtra(InviteActivity.EXTRA_PLAYER_ID, id);
						startActivity(intent);
					}
				}
				break;
			default:
				super.onActivityResult(requestCode, resultCode, data);
				break;
		}
	}

	@Override
	public void onBackPressed() {
		finish();
	}

	private void initialize() {
		business.initialize();

		refresh();
	}

	public void refresh() {
//		adapter.notifyDataSetChanged();
		adapter.setValue(business.getList());
		filter.filter(filterTypeValue == null ? null : filterTypeValue.toString());
	}

	public void onClickAdd(View view) {
		Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
		if (business.getExtraIn() != null) {
			intent.putExtras(business.getExtraIn());
		}
		if (business.getMode() == MODE.FOR_RESULT) {
			intent.putExtra(PlayerActivity.EXTRA_MODE, PlayerActivity.MODE.FOR_RESULT);
			startActivityForResult(intent, RESULT_PLAYER);
		} else if (business.getMode() == MODE.INVITE) {
			intent.putExtra(PlayerActivity.EXTRA_MODE, PlayerActivity.MODE.FOR_RESULT);
			startActivityForResult(intent, RESULT_PLAYER_FOR_INFO);
		} else {
			intent.removeExtra(EXTRA_MODE);
			startActivity(intent);
		}
	}

	public void onClickClose(View view) {
		finish();
	}

	public void onClickDelete(View view) {
		Player player = (Player)view.getTag();
		if (business.getInviteCount(player) > 0) {
			Toast.makeText(this, R.string.dialog_player_error_delete_have_invite, Toast.LENGTH_LONG).show();
		} else {
			OnClickPlayerDeleteListenerOk listener = new OnClickPlayerDeleteListenerOk(business, player);
			FactoryDialog.getInstance()
				.buildOkCancelDialog(business.getContext(), listener, R.string.dialog_player_delete_title, R.string.dialog_player_delete_message)
				.show();
		}
	}

	public void onClickQRCode(View view) {
		Player player = (Player)view.getTag();
		String qrcodeData = PlayerParser.getInstance().toDataText(player);
		Intent intent = new Intent(getApplicationContext(), QRCodeActivity.class);
		intent.putExtra(QRCodeActivity.EXTRA_QRCODE_DATA, qrcodeData);
		startActivity(intent);
	}

	public void onClickPlay(View view) {
		Player player = (Player)view.getTag();

		Intent intent = new Intent(getApplicationContext(), InviteActivity.class);
		intent.putExtra(InviteActivity.EXTRA_PLAYER_ID, player.getId());
		startActivity(intent);
		
//		finish();
	}

	public void onClickSend(View view) {
		Player player = (Player)view.getTag();
		OnClickPlayerSendListenerOk listener = new OnClickPlayerSendListenerOk(business, player);
		FactoryDialog.getInstance()
			.buildOkCancelDialog(business.getContext(), listener, R.string.dialog_player_send_title, R.string.dialog_player_send_message)
			.show();
	}

	public void onClickButtonFilter(View view) {
		if (llFilterType.getVisibility() == View.GONE) {
			llFilterType.setVisibility(View.VISIBLE);
		} else {
			llFilterType.setVisibility(View.GONE);
			filterTypeValue = null;
			filter.filter(null);
		}
	}

	public MODE getMode() {
		return business.getMode();
	}
	
	public ListPlayerBusiness getBusiness() {
		return business;
	}

	private void initializeTypeList() {
		String[] listTypeName = new String[]{"", TypeManager.TYPE.ENTRAINEMENT.toString(), TypeManager.TYPE.COMPETITION.toString()};
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
							filterTypeValue = TypeManager.TYPE.ENTRAINEMENT;
							break;
						case 2:
							filterTypeValue = TypeManager.TYPE.COMPETITION;
							break;
					}
					filter.filter(filterTypeValue == null ? null : filterTypeValue.toString());
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}
}