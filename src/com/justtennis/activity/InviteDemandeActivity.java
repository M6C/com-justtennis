package com.justtennis.activity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.cameleon.common.android.factory.FactoryDialog;
import com.cameleon.common.android.factory.listener.OnClickViewListener;
import com.justtennis.ApplicationConfig;
import com.justtennis.R;
import com.justtennis.adapter.CustomArrayAdapter;
import com.justtennis.business.InviteDemandeBusiness;
import com.justtennis.db.service.PlayerService;
import com.justtennis.domain.Invite.STATUS;
import com.justtennis.domain.Player;
import com.justtennis.domain.Ranking;
import com.justtennis.manager.ContactManager;
import com.justtennis.manager.TypeManager;
import com.justtennis.notifier.NotifierMessageLogger;

public class InviteDemandeActivity extends GenericActivity {

	@SuppressWarnings("unused")
	private static final String TAG = InviteDemandeActivity.class.getSimpleName();

	public enum MODE {
		INVITE_DEMANDE,
		INVITE_CONFIRM
	};
	public static final String EXTRA_MODE = "MODE";
	public static final String EXTRA_INVITE = "INVITE";
	public static final String EXTRA_PLAYER_ID = "PLAYER_ID";
	private static final int RESULT_PLAYER = 1;

	private InviteDemandeBusiness business;

	private LinearLayout llInviteDemande;
	private LinearLayout llInviteConfirm;
	private TextView tvFirstname;
	private TextView tvLastname;
	private TextView edDate;
	private TextView edTime;
	private ImageView ivPhoto;
	private Switch swType;
	private Bundle savedInstanceState;
	private Spinner spRanking;
	private Long idPlayerForResult = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (this.savedInstanceState==null) {
			this.savedInstanceState = savedInstanceState;
		}

		setContentView(R.layout.invite_demande);

		llInviteDemande = (LinearLayout)findViewById(R.id.ll_invite_demande);
		llInviteConfirm = (LinearLayout)findViewById(R.id.ll_invite_confirm);
		tvFirstname = (TextView)findViewById(R.id.tv_firstname);
		tvLastname = (TextView)findViewById(R.id.tv_lastname);
		edDate = ((TextView)findViewById(R.id.inviteDate));
		edTime = ((TextView)findViewById(R.id.inviteTime));
		ivPhoto = (ImageView)findViewById(R.id.iv_photo);
		swType = (Switch)findViewById(R.id.sw_type);
		spRanking = (Spinner)findViewById(R.id.sp_ranking);

		spRanking.setVisibility(View.VISIBLE);

		business = new InviteDemandeBusiness(this, NotifierMessageLogger.getInstance());

		initializeListType();
		TypeManager.getInstance().initializeActivity(findViewById(R.id.layout_main), false);
	}

	@Override
	protected void onResume() {
		super.onResume();
		initializeData();
		initializeListener();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case RESULT_PLAYER:
				if (data!=null) {
//					long id = data.getLongExtra(PlayerActivity.EXTRA_PLAYER_ID, PlayerService.ID_EMPTY_PLAYER);
					long id = data.getLongExtra(ListPlayerActivity.EXTRA_PLAYER_ID, PlayerService.ID_EMPTY_PLAYER);
					if (id != PlayerService.ID_EMPTY_PLAYER) {
						idPlayerForResult  = Long.valueOf(id);
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

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		business.onSaveInstanceState(outState);
		super.onSaveInstanceState(outState);
	}

	public void onClickOk(View view) {
		final boolean addCalendar = Calendar.getInstance().getTime().before(business.getDate());
		if (business.isUnknownPlayer()) {
			business.setStatus(STATUS.ACCEPT);
			business.send(null, addCalendar);

			Intent intent = new Intent(InviteDemandeActivity.this, ListInviteActivity.class);
			InviteDemandeActivity.this.startActivity(intent);
			InviteDemandeActivity.this.finish();
		} else {
			String text = business.buildText();
	
			if (business.sendMessageConfirmation()) {
				AlertDialog dialog = (AlertDialog) FactoryDialog.getInstance().buildEditTextDialog(this, new OnClickViewListener() {
					
					@Override
					public void onClick(DialogInterface dialog, View view, int which) {
						TextView textView = (TextView)view;
						business.setStatus(STATUS.ACCEPT);
						business.send(textView.getText().toString(), addCalendar);
		
						dialog.dismiss();
		
						Intent intent = new Intent(InviteDemandeActivity.this, ListInviteActivity.class);
						InviteDemandeActivity.this.startActivity(intent);
						InviteDemandeActivity.this.finish();
					}
				}, new OnClickViewListener() {
					
					@Override
					public void onClick(DialogInterface dialog, View view, int which) {
						business.send(null, addCalendar);
		
						dialog.dismiss();
		
						Intent intent = new Intent(InviteDemandeActivity.this, ListInviteActivity.class);
						InviteDemandeActivity.this.startActivity(intent);
						InviteDemandeActivity.this.finish();
					}
				}, R.string.dialog_player_send_title, text);
				dialog.show();
			}
			else {
				business.send(text, addCalendar);
	
				Intent intent = new Intent(InviteDemandeActivity.this, ListInviteActivity.class);
				InviteDemandeActivity.this.startActivity(intent);
				InviteDemandeActivity.this.finish();
			}
		}
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
	
	public void onClickDetail(View view) {
		Intent intent = new Intent(this, InviteActivity.class);
		intent.putExtra(InviteActivity.EXTRA_MODE, InviteActivity.MODE.INVITE_DETAIL);
		intent.putExtra(InviteActivity.EXTRA_INVITE, business.getInvite());
		startActivityForResult(intent, RESULT_PLAYER);
		finish();
	}
	
	public void onClickPlayer(View view) {
		if (business.isUnknownPlayer()) {
			Intent intent = new Intent(this, ListPlayerActivity.class);
			intent.putExtra(ListPlayerActivity.EXTRA_MODE, ListPlayerActivity.MODE.FOR_RESULT);
//			Intent intent = new Intent(this, PlayerActivity.class);
//			intent.putExtra(PlayerActivity.EXTRA_MODE, PlayerActivity.MODE.FOR_RESULT);
			startActivityForResult(intent, RESULT_PLAYER);
		}
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

		if (idPlayerForResult != null) {
			business.setPlayer(idPlayerForResult);
			business.setIdRanking(business.getPlayer().getIdRanking());
			idPlayerForResult = null;
		}

		initializeDataMode();
		initializeDataType();
		initializeDataDateTime();
		initializeDataPlayer();
//		initializeRankingText();
		initializeRankingList();
		initializeRanking();
	}

	private void initializeListType() {
		swType.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				business.setType(isChecked ? TypeManager.TYPE.TRAINING : TypeManager.TYPE.COMPETITION);
			}
		});
	}

	private void initializeRankingList() {
		spRanking.setVisibility(View.VISIBLE);
		CustomArrayAdapter<String> dataAdapter = new CustomArrayAdapter<String>(this, business.getListTxtRankings());
		spRanking.setAdapter(dataAdapter);

		spRanking.setOnItemSelectedListener(dataAdapter.new OnItemSelectedListener<Ranking>() {
			@Override
			public Ranking getItem(int position) {
				return business.getListRanking().get(position);
			}

			@Override
			public boolean isHintItemSelected(Ranking item) {
				return business.isEmptyRanking(item);
			}

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id, Ranking item) {
				business.setIdRanking(item.getId());
			}
		});
	}

	private void initializeRanking() {
		Long id = business.getIdRanking();
		int position = 0;
		List<Ranking> listRanking = business.getListRanking();
		for(Ranking ranking : listRanking) {
			if (ranking.getId().equals(id)) {
				spRanking.setSelection(position, true);
				break;
			} else {
				position++;
			}
		}
	}

	private void initializeDataPlayer() {
		Player player = business.getPlayer();
		if (player!=null) {
			tvFirstname.setText(player.getFirstName());
			tvLastname.setText(player.getLastName());
			if (player.getIdGoogle()!=null && player.getIdGoogle().longValue()>0l) {
				ivPhoto.setImageBitmap(ContactManager.getInstance(this).getPhoto(player.getIdGoogle()));
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

	private void initializeDataType() {
		swType.setChecked(getTypePosition()==0);
	}

	private void initializeDataMode() {
		switch (business.getMode()) {
			case INVITE_CONFIRM:
				llInviteDemande.setVisibility(View.GONE);
				llInviteConfirm.setVisibility(View.VISIBLE);
				edDate.setEnabled(false);
				edTime.setEnabled(false);
				break;
			case INVITE_DEMANDE:
			default:
				llInviteDemande.setVisibility(View.VISIBLE);
				llInviteConfirm.setVisibility(View.GONE);
				edDate.setEnabled(true);
				edTime.setEnabled(true);
				break;
		}
	}

	private void initializeListener() {
		edDate.setOnFocusChangeListener(new OnFocusChangeListener() {
			private boolean first = true;
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (first) {
					first = false;
					return;
				}
				if (hasFocus) {
					onClickInviteDate(v);
				}
			}
		});
		edTime.setOnFocusChangeListener(new OnFocusChangeListener() {
			private boolean first = true;
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (first) {
					first = false;
					return;
				}
				if (hasFocus) {
					onClickInviteTime(v);
				}
			}
		});
	}
	
	private int getTypePosition() {
		switch(business.getInvite().getType()) {
			case TRAINING:
				return 0;
			case COMPETITION:
			default:
				return 1;
		}
	}
}