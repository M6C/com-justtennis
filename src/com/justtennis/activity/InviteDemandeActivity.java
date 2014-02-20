package com.justtennis.activity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.cameleon.common.android.adapter.BaseViewAdapter;
import com.cameleon.common.android.factory.FactoryDialog;
import com.cameleon.common.android.factory.listener.OnClickViewListener;
import com.justtennis.ApplicationConfig;
import com.justtennis.R;
import com.justtennis.business.InviteDemandeBusiness;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Ranking;
import com.justtennis.domain.Invite.INVITE_TYPE;
import com.justtennis.domain.Player;
import com.justtennis.manager.ContactManager;
import com.justtennis.notifier.NotifierMessageLogger;

public class InviteDemandeActivity extends Activity {

	@SuppressWarnings("unused")
	private static final String TAG = InviteDemandeActivity.class.getSimpleName();

	public enum MODE {
		INVITE_DEMANDE,
		INVITE_CONFIRM
	};
	public static final String EXTRA_MODE = "MODE";
	public static final String EXTRA_INVITE = "INVITE";
	public static final String EXTRA_PLAYER_ID = "PLAYER_ID";

	private final Integer[] drawableType = new Integer[] {R.layout.element_invite_type_entrainement, R.layout.element_invite_type_match};

	private InviteDemandeBusiness business;

	private LinearLayout llInviteDemande;
	private LinearLayout llInviteConfirm;
	private TextView tvFirstname;
	private TextView tvLastname;
	private TextView edDate;
	private TextView edTime;
	private ImageView ivPhoto;
	private Spinner spType;
	private Bundle savedInstanceState;
	private BaseViewAdapter adapterType;
	private Spinner spStatus;
	private Spinner spRanking;
	private TextView tvRanking;
	private TextView tvStatus;
	
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
		spType = (Spinner)findViewById(R.id.sp_main_type);
		spStatus = (Spinner)findViewById(R.id.sp_status);
		spRanking = (Spinner)findViewById(R.id.sp_ranking);
		tvStatus = (TextView)findViewById(R.id.tv_status);
		tvRanking = (TextView)findViewById(R.id.tv_ranking);

		findViewById(R.id.ll_status).setVisibility(View.GONE);
		spStatus.setVisibility(View.GONE);
		spRanking.setVisibility(View.GONE);
		tvStatus.setVisibility(View.GONE);
		tvRanking.setVisibility(View.VISIBLE);

		business = new InviteDemandeBusiness(this, NotifierMessageLogger.getInstance());

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
	protected void onSaveInstanceState(Bundle outState) {
		business.onSaveInstanceState(outState);
		super.onSaveInstanceState(outState);
	}

	public void onClickOk(View view) {
		
		if (business.isUnknownPlayer()) {
			business.send(null);

			Intent intent = new Intent(InviteDemandeActivity.this, ListInviteActivity.class);
			InviteDemandeActivity.this.startActivity(intent);
			InviteDemandeActivity.this.finish();
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
		
						Intent intent = new Intent(InviteDemandeActivity.this, ListInviteActivity.class);
						InviteDemandeActivity.this.startActivity(intent);
						InviteDemandeActivity.this.finish();
					}
				}, new OnClickViewListener() {
					
					@Override
					public void onClick(DialogInterface dialog, View view, int which) {
						business.send(null);
		
						dialog.dismiss();
		
						Intent intent = new Intent(InviteDemandeActivity.this, ListInviteActivity.class);
						InviteDemandeActivity.this.startActivity(intent);
						InviteDemandeActivity.this.finish();
					}
				}, R.string.dialog_player_send_title, text);
				dialog.show();
			}
			else {
				business.send(text);
	
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
	
	public void onClickCancel(View view) {
		finish();
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
		initializeDataDateTime();
		initializeDataPlayer();
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

			if (business.isUnknownPlayer()) {
				List<Ranking> listRanking = business.getListRanking();
				tvRanking.setText(getString(R.string.txt_ranking_value, listRanking.get(0).getRanking()));
			} else {
				Long idRanking = player.getIdRanking();
				List<Ranking> listRanking = business.getListRanking();
				for(Ranking ranking : listRanking) {
					if (ranking.getId().equals(idRanking)) {
						tvRanking.setText(getString(R.string.txt_ranking_value, ranking.getRanking()));
					}
				}
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
		spType.setSelection(getTypePosition());
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