package com.justtennis.activity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.cameleon.common.android.adapter.BaseImageAdapter;
import com.cameleon.common.android.adapter.BaseImageAdapter.ViewBinder;
import com.cameleon.common.android.adapter.BaseViewAdapter;
import com.cameleon.common.android.factory.FactoryDialog;
import com.cameleon.common.android.factory.listener.OnClickViewListener;
import com.justtennis.ApplicationConfig;
import com.justtennis.R;
import com.justtennis.adapter.ListInviteAdapter;
import com.justtennis.adapter.ListInviteAdapter.ADAPTER_INVITE_MODE;
import com.justtennis.business.InviteBusiness;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Invite.INVITE_TYPE;
import com.justtennis.domain.Invite.STATUS;
import com.justtennis.domain.Player;
import com.justtennis.manager.ContactManager;
import com.justtennis.notifier.NotifierMessageLogger;

public class InviteActivity extends Activity {

	private static final String TAG = InviteActivity.class.getSimpleName();

	public enum MODE {
		INVITE_DEMANDE,
		INVITE_MODIFY,
		INVITE_CONFIRM
	};
	public static final String EXTRA_MODE = "MODE";
	public static final String EXTRA_INVITE = "INVITE";
	public static final String EXTRA_PLAYER_ID = "PLAYER_ID";
	private static final int RESULT_PLAYER = 1;

	private final Integer[] drawableStatus = new Integer[] {R.drawable.check_green, R.drawable.check_red, R.drawable.check_yellow};
//	private final Integer[] drawableType = new Integer[] {R.drawable.check_green, R.drawable.check_red, R.drawable.check_yellow};
	private final Integer[] drawableType = new Integer[] {R.layout.element_invite_type_entrainement, R.layout.element_invite_type_match};

	private InviteBusiness business;
	private ListInviteAdapter adapter;

	private LinearLayout llInviteDemande;
	private LinearLayout llInviteModify;
	private LinearLayout llInviteConfirm;
	private Button btnInviteSend;
	private TextView tvFirstname;
	private TextView tvLastname;
	private TextView edDate;
	private TextView edTime;
	private ImageView ivPhoto;
	private Spinner spStatus;
	private Spinner spType;
	private ListView list;
	private Bundle savedInstanceState;
	private BaseImageAdapter adapterStatus;
	private BaseViewAdapter adapterType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (this.savedInstanceState==null) {
			this.savedInstanceState = savedInstanceState;
		}

		setContentView(R.layout.invite);

		llInviteDemande = (LinearLayout)findViewById(R.id.ll_invite_demande);
		llInviteModify = (LinearLayout)findViewById(R.id.ll_invite_modify);
		llInviteConfirm = (LinearLayout)findViewById(R.id.ll_invite_confirm);
		btnInviteSend = (Button)findViewById(R.id.btn_invite_confirm_send);
		tvFirstname = (TextView)findViewById(R.id.tv_firstname);
		tvLastname = (TextView)findViewById(R.id.tv_lastname);
		edDate = ((TextView)findViewById(R.id.inviteDate));
		edTime = ((TextView)findViewById(R.id.inviteTime));
		ivPhoto = (ImageView)findViewById(R.id.iv_photo);
//		ivStatus = (ImageView)findViewById(R.id.iv_main_status);
		spStatus = (Spinner)findViewById(R.id.sp_main_status);
		spType = (Spinner)findViewById(R.id.sp_main_type);
		list = (ListView)findViewById(R.id.lvInvite);

		business = new InviteBusiness(this, NotifierMessageLogger.getInstance());
		adapter = new ListInviteAdapter(this, business.getList(), ADAPTER_INVITE_MODE.READ);
		list.setAdapter(adapter);

		initializeListStatus();
		initializeListType();
	}

	@Override
	protected void onResume() {
		super.onResume();
		initializeData();
		initializeListener();
	}

	@Override
	public void onBackPressed() {
		onClickCancel(null);
		super.onBackPressed();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case RESULT_PLAYER:
				if (data!=null) {
					long id = data.getLongExtra(ListPlayerActivity.EXTRA_PLAYER_ID, -1);
					business.setPlayer(id);
				}
				break;
	
			default:
				super.onActivityResult(requestCode, resultCode, data);
				break;
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		business.onSaveInstanceState(outState);
		super.onSaveInstanceState(outState);
	}

//	@Override
//	protected void onRestoreInstanceState(Bundle savedInstanceState) {
//		super.onRestoreInstanceState(savedInstanceState);
//		this.savedInstanceState = savedInstanceState;
//	}

	public void onClickOk(View view) {
		
//		if (business.getType()==INVITE_TYPE.MATCH) {
		if (business.isUnknownPlayer()) {
			business.send(null);
			
			Intent intent = new Intent(InviteActivity.this, ListInviteActivity.class);
			InviteActivity.this.startActivity(intent);
			InviteActivity.this.finish();
		} else {
			String text = business.buildText();
	
			if (ApplicationConfig.SHOW_CONFIRM_DIALOG_TEXT_SMS && 
				business.getPlayer().getIdExternal()==null) {
				AlertDialog dialog = (AlertDialog) FactoryDialog.getInstance().buildEditTextDialog(this, new OnClickViewListener() {
					
					@Override
					public void onClick(DialogInterface dialog, View view, int which) {
						TextView textView = (TextView)view;
						business.send(textView.getText().toString());
		
						dialog.dismiss();
		
						Intent intent = new Intent(InviteActivity.this, ListInviteActivity.class);
						InviteActivity.this.startActivity(intent);
						InviteActivity.this.finish();
					}
				}, new OnClickViewListener() {
					
					@Override
					public void onClick(DialogInterface dialog, View view, int which) {
						business.send(null);
		
						dialog.dismiss();
		
						Intent intent = new Intent(InviteActivity.this, ListInviteActivity.class);
						InviteActivity.this.startActivity(intent);
						InviteActivity.this.finish();
					}
				}, R.string.dialog_player_send_title, text);
				dialog.show();
			}
			else {
				business.send(text);
	
				Intent intent = new Intent(InviteActivity.this, ListInviteActivity.class);
				InviteActivity.this.startActivity(intent);
				InviteActivity.this.finish();
			}
		}
	}

	public void onClickModify(View view) {
		business.modify();
		
		Intent intent = new Intent(InviteActivity.this, ListInviteActivity.class);
		InviteActivity.this.startActivity(intent);
		InviteActivity.this.finish();
	}
	
	public void onClickInviteConfirmeYes(View view) {
		business.confirmYes();
		finish();
	}
	
	public void onClickInviteConfirmeNo(View view) {
		business.confirmNo();
		finish();
	}

	public void onClickInviteDate(final View view) {
		FactoryDialog.getInstance().buildDatePickerDialog(this, new OnClickViewListener() {
			
			@Override
			public void onClick(DialogInterface dialog, View view2, int which) {
				DatePicker datePicker = (DatePicker)view2;

				Calendar calendar = GregorianCalendar.getInstance(ApplicationConfig.getLocal());
				calendar.setTime(business.getDate());
				calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
				business.setDate(calendar.getTime());
				
				DateFormat sdf = new SimpleDateFormat(getString(R.string.msg_common_format_date), ApplicationConfig.getLocal());
				((EditText)view).setText(sdf.format(business.getDate()));
			}
		}, -1, business.getDate()).show();
	}

	public void onClickInviteTime(final View view) {

		FactoryDialog.getInstance().buildTimePickerDialog(this, new OnClickViewListener() {
			
			@Override
			public void onClick(DialogInterface dialog, View view2, int which) {
				TimePicker timePicker = (TimePicker)view2;

				Calendar calendar = GregorianCalendar.getInstance(ApplicationConfig.getLocal());
				calendar.setTime(business.getDate());
				calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
				calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
				business.setDate(calendar.getTime());

				DateFormat sdf = new SimpleDateFormat(getString(R.string.msg_common_format_time), ApplicationConfig.getLocal());
				((EditText)view).setText(sdf.format(business.getDate()));
			}
		}, -1, business.getDate()).show();
	}
	
	public void onClickCancel(View view) {
		finish();
		Intent intent = null;
		if (business.getMode()==MODE.INVITE_DEMANDE) {
			intent = new Intent(this, ListPlayerActivity.class);
		} else {
			intent = new Intent(this, ListInviteActivity.class);
		}
		intent.putExtra(ListPlayerActivity.EXTRA_MODE, ListPlayerActivity.MODE.INVITE);
		startActivity(intent);
	}
	
	public void onClickPlayer(View view) {
		Intent intent = new Intent(this, ListPlayerActivity.class);
		intent.putExtra(ListPlayerActivity.EXTRA_MODE, ListPlayerActivity.MODE.FOR_RESULT);
		startActivityForResult(intent, RESULT_PLAYER);
	}

	private void initializeData() {
		Intent intent = getIntent();
		if (savedInstanceState!=null) {
			business.initializeData(savedInstanceState);
			savedInstanceState = null;
		}
		else {
			business.initializeData(intent);
		}

		initializeDataMode();
		initializeDataType();
		initializeDataStatus();
		initializeDataDateTime();
		initializeDataPlayer();
		
		adapter.notifyDataSetChanged();
	}

	private void initializeListStatus() {
		adapterStatus = new BaseImageAdapter(this, R.layout.list_status_row, R.id.iv_row_status, drawableStatus);
		adapterStatus.setViewBinder(new ViewBinder() {
			
			@Override
			public boolean setViewValue(int position, View view) {
				view.setTag(drawableStatus[position]);
				return true;
			}
		});
		spStatus.setAdapter(adapterStatus);

		spStatus.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//				business.setStatus((STATUS)spStatus.getSelectedView().getTag());
				business.setStatus(getStatus(position));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

	private void initializeListType() {
		adapterType = new BaseViewAdapter(this, drawableType);
		adapterType.setViewBinder(new BaseViewAdapter.ViewBinder() {
			
			@Override
			public boolean setViewValue(int position, View view) {
				view.setTag(getType(position));
				return true;
			}
		});
		spType.setAdapter(adapterType);

		spType.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				business.setType((INVITE_TYPE) view.getTag());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

	private void initializeDataPlayer() {
		Player player = business.getPlayer();
		if (player!=null) {
			tvFirstname.setText(player.getFirstName());
			tvLastname.setText(player.getLastName());
			if (player.getIdGoogle()!=null && player.getIdGoogle().longValue()>0l) {
				ivPhoto.setImageBitmap(ContactManager.getInstance().getPhoto(this, player.getIdGoogle()));
			}
		}
	}

	private void initializeDataDateTime() {
		Date date = business.getDate();

		DateFormat sdfD = new SimpleDateFormat(getString(R.string.msg_common_format_date), ApplicationConfig.getLocal());
		DateFormat sdfT = new SimpleDateFormat(getString(R.string.msg_common_format_time), ApplicationConfig.getLocal());
		edDate.setText(sdfD.format(date));
		edTime.setText(sdfT.format(date));
	}

	private void initializeDataStatus() {
//		switch(business.getInvite().getStatus()) {
//			case ACCEPT:
//				ivStatus.setBackgroundResource(R.drawable.check_green);
//				break;
//			case REFUSE:
//				ivStatus.setBackgroundResource(R.drawable.check_red);
//				break;
//			default:
//				ivStatus.setBackgroundResource(R.drawable.check_yellow);
//				break;
//		}

		spStatus.setSelection(getStatusPosition());
	}

	private void initializeDataType() {
//		switch(business.getInvite().getType()) {
//			case ENTRAINEMENT:
//				vTypeEntrainement.setVisibility(View.VISIBLE);
//				vTypeMatch.setVisibility(View.GONE);
//				break;
//			case MATCH:
//			default:
//				vTypeEntrainement.setVisibility(View.GONE);
//				vTypeMatch.setVisibility(View.VISIBLE);
//				break;
//		}
		spType.setSelection(getTypePosition());
	}

	private void initializeDataMode() {
		switch (business.getMode()) {
			case INVITE_CONFIRM:
				llInviteDemande.setVisibility(View.GONE);
				llInviteModify.setVisibility(View.GONE);
				llInviteConfirm.setVisibility(View.VISIBLE);
				edDate.setEnabled(false);
				edTime.setEnabled(false);
				break;
			case INVITE_MODIFY:
				llInviteDemande.setVisibility(View.GONE);
				llInviteModify.setVisibility(View.VISIBLE);
				llInviteConfirm.setVisibility(View.GONE);
				edDate.setEnabled(true);
				edTime.setEnabled(true);
				break;
			case INVITE_DEMANDE:
			default:
				llInviteDemande.setVisibility(View.VISIBLE);
				llInviteModify.setVisibility(View.GONE);
				llInviteConfirm.setVisibility(View.GONE);
				edDate.setEnabled(true);
				edTime.setEnabled(true);
				break;
		}
	}

	private void initializeListener() {
		edDate.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					onClickInviteDate(v);
				}
			}
		});
		edTime.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					onClickInviteTime(v);
				}
			}
		});
	}
	
	private int getStatusPosition() {
		switch(business.getInvite().getStatus()) {
			case ACCEPT:
				return 0;
			case REFUSE:
				return 1;
			default:
				return 2;
		}
	}

	private STATUS getStatus(Integer position) {
		switch(position) {
			case 0:
				return STATUS.ACCEPT;
			case 1:
				return STATUS.REFUSE;
			default:
				return STATUS.UNKNOW;
		}
	}
	
	private int getTypePosition() {
		switch(business.getInvite().getType()) {
			case ENTRAINEMENT:
				return 0;
			case MATCH:
			default:
				return 1;
		}
	}

	private INVITE_TYPE getType(Integer position) {
		switch(position) {
			case 0:
				return Invite.INVITE_TYPE.ENTRAINEMENT;
			case 1:
			default:
				return Invite.INVITE_TYPE.MATCH;
		}
	}
}