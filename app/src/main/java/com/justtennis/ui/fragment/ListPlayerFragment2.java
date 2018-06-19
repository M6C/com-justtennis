package com.justtennis.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.cameleon.common.android.factory.FactoryDialog;
import com.justtennis.R;
import com.justtennis.activity.InviteActivity;
import com.justtennis.activity.InviteDemandeActivity;
import com.justtennis.activity.PlayerActivity;
import com.justtennis.activity.QRCodeActivity;
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
import com.justtennis.tool.ToolPermission;
import com.justtennis.ui.common.CommonEnum;
import com.justtennis.ui.rxjava.RxListPlayer;

import org.gdocument.gtracergps.launcher.log.Logger;

import java.util.Date;

public class ListPlayerFragment2 extends Fragment {

	private static final String TAG = ListPlayerFragment2.class.getSimpleName();
	public static final String EXTRA_MODE = "MODE";
	public static final String EXTRA_PLAYER_ID = "EXTRA_PLAYER_ID";
	private static final int RESULT_PLAYER = 1;
	private static final int RESULT_PLAYER_FOR_INFO = 2;

	private ListPlayerBusiness business;

	private ListView list;
	private ListPlayerAdapter adapter;

	private LinearLayout llFilterType;
	private Spinner spFilterType;
	private Filter filter;
	private TypeManager.TYPE filterTypeValue = null;
	private Date dateStart = new Date();
	boolean checkPermission = true;
	private View rootView;
	private Activity activity;
	private Context context;
    private Button btnPlayerAdd;

    public static ListPlayerFragment2 buildForEdit() {
		ListPlayerFragment2 fragment = new ListPlayerFragment2();
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_MODE, CommonEnum.LIST_PLAYER_MODE.EDIT);
		fragment.setArguments(args);
		return fragment;
	}

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_list_player, container, false);
		activity = getActivity();
		context = activity.getApplicationContext();

		NotifierMessageLogger notifier = NotifierMessageLogger.getInstance();

		business = new ListPlayerBusiness(activity, notifier);
		adapter = new ListPlayerAdapter(activity, business.getList());
		filter = adapter.getFilter();

        iniitializeViewId();
        initializeListener();
        initializeTypeList();

        list.setAdapter(adapter);

//		TypeManager.getInstance().initializeActivity(rootView.findViewById(R.id.layout_main), false);

		logMe("onCreate End", dateStart);

        initializeSubscribeRefresh();

        return rootView;
	}

	@Override
	public void onPause() {
		RxListPlayer.unregister(this);
		super.onPause();
	}

    private void iniitializeViewId() {
        btnPlayerAdd = rootView.findViewById(R.id.btn_player_add);
        spFilterType = rootView.findViewById(R.id.sp_filter_type);
        llFilterType = rootView.findViewById(R.id.ll_filter_type);
        list = rootView.findViewById(R.id.list);
    }

    private void initializeListener() {
        btnPlayerAdd.setOnClickListener(this::onClickAdd);
    }

    @Override
	public void onResume() {
		super.onResume();

		if (checkPermission && ToolPermission.checkPermissionREAD_CONTACTS(activity, true)) {
			initialize();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case RESULT_PLAYER:
				if (data!=null) {
					Intent intent = new Intent();
					long id = data.getLongExtra(PlayerActivity.EXTRA_PLAYER_ID, PlayerService.ID_EMPTY_PLAYER);
					if (id != PlayerService.ID_EMPTY_PLAYER) {
						intent.putExtra(ListPlayerFragment2.EXTRA_PLAYER_ID, id);
					}
//					setResult(Activity.RESULT_OK, intent);
//					finish();
				}
				break;
			case RESULT_PLAYER_FOR_INFO:
				if (data!=null) {
					long id = data.getLongExtra(InviteDemandeActivity.EXTRA_PLAYER_ID, PlayerService.ID_EMPTY_PLAYER);
					if (id != PlayerService.ID_EMPTY_PLAYER) {
						Intent intent = new Intent(context, InviteActivity.class);
						intent.putExtra(InviteActivity.EXTRA_PLAYER_ID, id);
						intent.putExtra(InviteActivity.EXTRA_MODE, CommonEnum.MODE.INVITE_DETAIL);
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
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case ToolPermission.MY_PERMISSIONS_REQUEST: {
				checkPermission = false;
				initialize();
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				} else {
					logMe("Permission Denied ! Cancel initialization");
				}
				return;
			}
		}
	}

	private void initialize() {
		business.initialize();
		business.refreshData();

		refresh();

		switch (business.getMode()) {
			case EDIT:
				list.setOnItemClickListener(new OnItemClickListPlayer(activity));
				break;
			case INVITE:
				list.setOnItemClickListener(new OnItemClickListPlayerInvite(activity));
				break;
			case FOR_RESULT:
				list.setOnItemClickListener(new OnItemClickListPlayerForResult(activity));
				break;
		}
		logMe("onResume End", dateStart);
	}

    private void initializeSubscribeRefresh() {
        RxListPlayer.subscribe(RxListPlayer.SUBJECT_REFRESH, this, o -> refresh());
    }

	public void refresh() {
		filter.filter(filterTypeValue == null ? null : filterTypeValue.toString());
		adapter.setValue(business.getList());
		list.setAdapter(adapter);
//		adapter.notifyDataSetChanged();
	}

	public void onClickAdd(View view) {
		Intent intent = new Intent(context, PlayerActivity.class);
		if (business.getExtraIn() != null) {
			intent.putExtras(business.getExtraIn());
		}
		if (business.getMode() == CommonEnum.LIST_PLAYER_MODE.FOR_RESULT) {
			intent.putExtra(PlayerActivity.EXTRA_MODE, CommonEnum.LIST_PLAYER_MODE.FOR_RESULT);
			startActivityForResult(intent, RESULT_PLAYER);
		} else if (business.getMode() == CommonEnum.LIST_PLAYER_MODE.INVITE) {
			intent.putExtra(PlayerActivity.EXTRA_MODE, CommonEnum.LIST_PLAYER_MODE.FOR_RESULT);
			startActivityForResult(intent, RESULT_PLAYER_FOR_INFO);
		} else {
			intent.removeExtra(EXTRA_MODE);
			startActivity(intent);
		}
	}

	public void onClickDelete(View view) {
		Player player = (Player)view.getTag();
		if (business.getInviteCount(player) > 0) {
			Toast.makeText(activity, R.string.dialog_player_error_delete_have_invite, Toast.LENGTH_LONG).show();
		} else {
			OnClickPlayerDeleteListenerOk listener = new OnClickPlayerDeleteListenerOk(business, player);
			FactoryDialog.getInstance()
				.buildOkCancelDialog(business.getActivity(), listener, R.string.dialog_player_delete_title, R.string.dialog_player_delete_message)
				.show();
		}
	}

	public void onClickQRCode(View view) {
		Player player = (Player)view.getTag();
		String qrcodeData = PlayerParser.getInstance().toDataText(player);
		Intent intent = new Intent(context, QRCodeActivity.class);
		intent.putExtra(QRCodeActivity.EXTRA_QRCODE_DATA, qrcodeData);
		startActivity(intent);
	}

	public void onClickPlay(View view) {
		Player player = (Player)view.getTag();

		Intent intent = new Intent(context, InviteActivity.class);
		intent.putExtra(InviteActivity.EXTRA_PLAYER_ID, player.getId());
		intent.putExtra(InviteActivity.EXTRA_MODE, CommonEnum.MODE.INVITE_SIMPLE);
		startActivity(intent);
		
//		finish();
	}

	public void onClickSend(View view) {
		Player player = (Player)view.getTag();
		OnClickPlayerSendListenerOk listener = new OnClickPlayerSendListenerOk(business, player);
		FactoryDialog.getInstance()
			.buildOkCancelDialog(business.getActivity(), listener, R.string.dialog_player_send_title, R.string.dialog_player_send_message)
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

	public CommonEnum.LIST_PLAYER_MODE getMode() {
		return business.getMode();
	}
	
	public ListPlayerBusiness getBusiness() {
		return business;
	}

	private void initializeTypeList() {
		String[] listTypeName = new String[]{"", TypeManager.TYPE.TRAINING.toString(), TypeManager.TYPE.COMPETITION.toString()};
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, listTypeName);
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
							filterTypeValue = TypeManager.TYPE.TRAINING;
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

	protected void logMe(String msg, Date dateStart) {
		logMe("ListPlayerActivity time:" + (new Date().getTime() - dateStart.getTime()) + " millisecond - " + msg);
    }

	protected static void logMe(String msg) {
		Logger.logMe(TAG, msg);
    }
}