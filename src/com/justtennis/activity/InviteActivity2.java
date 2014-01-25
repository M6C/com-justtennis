package com.justtennis.activity;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.cameleon.common.android.factory.FactoryDialog;
import com.cameleon.common.android.factory.listener.OnClickViewListener;
import com.justtennis.ApplicationConfig;
import com.justtennis.R;
import com.justtennis.business.InviteBusiness;
import com.justtennis.listener.changed.InviteDateChangedListener;
import com.justtennis.listener.changed.InviteTimeChangedListener;
import com.justtennis.notifier.NotifierMessageLogger;

public class InviteActivity2 extends Activity {

	private static final String TAG = InviteActivity2.class.getSimpleName();

	public enum MODE {
		INVITE_DEMANDE,
		INVITE_CONFIRM
	};
	public static final String EXTRA_MODE = "MODE";
	public static final String EXTRA_INVITE = "INVITE";

	private InviteBusiness business;
	private DatePicker datePicker;
	private TimePicker timePicker;
	private LinearLayout llInviteDemande;
	private LinearLayout llInviteConfirm;

	public static final String EXTRA_PLAYER_ID = "PLAYER_ID";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.invite2);

		datePicker = (DatePicker)findViewById(R.id.datePicker);
		timePicker = (TimePicker)findViewById(R.id.timePicker);
		llInviteDemande = (LinearLayout)findViewById(R.id.ll_invite_demande);
		llInviteConfirm = (LinearLayout)findViewById(R.id.ll_invite_confirm);
		
		business = new InviteBusiness(this, NotifierMessageLogger.getInstance());
	}

	@Override
	protected void onResume() {
		super.onResume();
		iniData();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

		onClickCancel(null);
	}

	private void iniData() {
		Intent intent = getIntent();

		business.initializeData(intent);

		MODE mode = MODE.INVITE_DEMANDE;

		if (intent.hasExtra(InviteActivity2.EXTRA_MODE)) {
			mode = (MODE) intent.getSerializableExtra(EXTRA_MODE);
		}

		switch (mode) {
			case INVITE_CONFIRM: {
				llInviteDemande.setVisibility(View.GONE);
				llInviteConfirm.setVisibility(View.VISIBLE);
				datePicker.setEnabled(false);
				timePicker.setEnabled(false);

				Calendar calendar = GregorianCalendar.getInstance();
				calendar.setTime(business.getInvite().getDate());
				datePicker.init(calendar.get(
					Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 
					new InviteDateChangedListener(business)
				);
				timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
				timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
				timePicker.setOnTimeChangedListener(new InviteTimeChangedListener(business));
				break;
			}
			case INVITE_DEMANDE:
			default: {
				llInviteDemande.setVisibility(View.VISIBLE);
				llInviteConfirm.setVisibility(View.GONE);
				datePicker.setEnabled(true);
				timePicker.setEnabled(true);

				Calendar calendar = GregorianCalendar.getInstance();
				datePicker.init(calendar.get(
					Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 
					new InviteDateChangedListener(business)
				);
				timePicker.setOnTimeChangedListener(new InviteTimeChangedListener(business));
				
				business.setDate(calendar.getTime());
				break;
			}
		}
	}

	public void onClickOk(View view) {
		
		String text = business.buildText();

		if (ApplicationConfig.SHOW_CONFIRM_DIALOG_TEXT_SMS && 
			business.getPlayer().getIdExternal()==null) {
			AlertDialog dialog = (AlertDialog) FactoryDialog.getInstance().buildEditTextDialog(this, new OnClickViewListener() {
				
				@Override
				public void onClick(DialogInterface dialog, View view, int which) {
					TextView textView = (TextView)view;
					business.send(textView.getText().toString());
	
					dialog.dismiss();
	
					Intent intent = new Intent(InviteActivity2.this, ListInviteActivity.class);
					InviteActivity2.this.startActivity(intent);
					InviteActivity2.this.finish();
				}
			}, R.string.dialog_player_send_title, text);
			dialog.show();
		}
		else {
			business.send(text);

			Intent intent = new Intent(InviteActivity2.this, ListInviteActivity.class);
			InviteActivity2.this.startActivity(intent);
			InviteActivity2.this.finish();
		}
	}

	public void onClickCancel(View view) {
		Intent intent = new Intent(this, ListPlayerActivity.class);
		intent.putExtra(ListPlayerActivity.EXTRA_MODE, ListPlayerActivity.MODE.INVITE);
		startActivity(intent);

		finish();
	}
	
	public void onClickInviteConfirmeYes(View view) {
		business.confirmYes();
		finish();
	}
	
	public void onClickInviteConfirmeNo(View view) {
		business.confirmNo();
		finish();
	}
}