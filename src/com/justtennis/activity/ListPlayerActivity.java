package com.justtennis.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

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
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.parser.PlayerParser;

public class ListPlayerActivity extends Activity {

	private static final String TAG = ListPlayerActivity.class.getSimpleName();
	public static final String EXTRA_MODE = "MODE";
	public static final String EXTRA_PLAYER_ID = "EXTRA_PLAYER_ID";
	private static final int RESULT_PLAYER = 1;

	public enum MODE {
		EDIT,
		FOR_RESULT,
		INVITE
	};

	private ListPlayerBusiness business;
	
	private ListView list;
	private ListPlayerAdapter adapter;
	private Button btnAdd;
	private Button btnClose;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_player);
		
		business = new ListPlayerBusiness(this, NotifierMessageLogger.getInstance());
		adapter = new ListPlayerAdapter(this, business.getList());

		btnAdd = (Button)findViewById(R.id.btn_player_add);
		btnClose = (Button)findViewById(R.id.btn_player_close);
		list = (ListView)findViewById(R.id.list);
		list.setAdapter(adapter);
	}

	@Override
	protected void onResume() {
		super.onResume();

		initialize();

		switch (business.getMode()) {
			case EDIT:
				list.setOnItemClickListener(new OnItemClickListPlayer(this));
				btnAdd.setVisibility(View.VISIBLE);
				btnClose.setVisibility(View.VISIBLE);
				break;
			case INVITE:
				list.setOnItemClickListener(new OnItemClickListPlayerInvite(this));
				btnAdd.setVisibility(View.GONE);
				btnClose.setVisibility(View.VISIBLE);
				break;
			case FOR_RESULT:
				list.setOnItemClickListener(new OnItemClickListPlayerForResult(this));
				btnAdd.setVisibility(View.VISIBLE);
				btnClose.setVisibility(View.VISIBLE);
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
		adapter.notifyDataSetChanged();
	}

	public void onClickAdd(View view) {
		Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
		if (business.getMode() == MODE.FOR_RESULT) {
			intent.putExtra(PlayerActivity.EXTRA_MODE, PlayerActivity.MODE.FOR_RESULT);
			startActivityForResult(intent, RESULT_PLAYER);
		} else {
			startActivity(intent);
		}
	}

	public void onClickClose(View view) {
		finish();
	}

	public void onClickDelete(View view) {
		Player player = (Player)view.getTag();
		OnClickPlayerDeleteListenerOk listener = new OnClickPlayerDeleteListenerOk(business, player);
		FactoryDialog.getInstance()
			.buildOkCancelDialog(business.getContext(), listener, R.string.dialog_player_delete_title, R.string.dialog_player_delete_message)
			.show();
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

	public MODE getMode() {
		return business.getMode();
	}
	
	public ListPlayerBusiness getBusiness() {
		return business;
	}
}