
package com.justtennis.activity;

import java.io.Serializable;

import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.cameleon.common.android.factory.FactoryDialog;
import com.justtennis.R;
import com.justtennis.business.PlayerBusiness;
import com.justtennis.business.UserBusiness;
import com.justtennis.domain.Address;
import com.justtennis.domain.User;
import com.justtennis.listener.action.TextWatcherFieldEnableView;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.parser.SmsParser;


public class UserActivity extends PlayerActivity {

	private static final int RESULT_LOCATION_DETAIL = 3;

	private TextView tvMessage;
	private EditText etMessage;
	private LinearLayout llType;
	private Spinner spSaison;
	private UserBusiness business;

	private Serializable addressFromResult;

	@Override
	protected void onResume() {
		super.onResume();
		etMessage.setText(business.getMessage());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode==RESULT_LOCATION_DETAIL) {
			if (data != null) {
				addressFromResult = data.getSerializableExtra(LocationActivity.EXTRA_OUT_LOCATION);
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	protected void initializeLayoutView() {
		setContentView(R.layout.user);
	}

	@Override
	protected void initializeViewById() {
		super.initializeViewById();
		tvMessage = (TextView)findViewById(R.id.tv_message);
		etMessage = (EditText)findViewById(R.id.et_message);
		spSaison = (Spinner)findViewById(R.id.sp_saison);
		llType = (LinearLayout)findViewById(R.id.ll_type);
	}

	@Override
	protected void initializeView() {
		super.initializeView();
		llType.setVisibility(View.GONE);
	}

	@Override
	protected void initializeLocation() {
		super.initializeLocation();
		if (addressFromResult != null) {
			User user = (User) business.getPlayer();
			user.setIdAddress(((Address)addressFromResult).getId());
			addressFromResult = null;
		}
	}

	@Override
	protected void initializeSaisonList() {
		super.initializeSaisonList();
		spSaison.setOnItemSelectedListener(null);
	}

//	@Override
//	protected void initializeSaison() {
//	}

	@Override
	protected PlayerBusiness createBusiness() {
		business = new UserBusiness(this, NotifierMessageLogger.getInstance());
		return business;
	}

	@Override
	protected void initializeListener() {
		super.initializeListener();
		etMessage.addTextChangedListener(new TextWatcherFieldEnableView(tvMessage, View.GONE));
	}

	@Override
	public void onClickModify(View view) {
		super.onClickModify(view);
		business.saveMessage(etMessage.getText().toString());
	}

	@Override
	public void onClickLocationDetail(View view) {
		Intent intent = new Intent(this, LocationAddressActivity.class);
		User user = (User) business.getPlayer();
		if (user.getIdAddress() != null) {
			intent.putExtra(GenericSpinnerFormActivity.EXTRA_DATA, new Address(user.getIdAddress()));
		}
		startActivityForResult(intent, RESULT_LOCATION_DETAIL);
	}

	public void onClickMenuAjoutChamp(View view) {
		String[] listPhonenumber = new String[] {
				getString(R.string.message_field_date),
				getString(R.string.message_field_date_relative),
				getString(R.string.message_field_time),
				getString(R.string.message_field_player_firstname),
				getString(R.string.message_field_player_lastname),
				getString(R.string.message_field_user_firstname),
				getString(R.string.message_field_user_lastname)
		};
		Dialog dialog = FactoryDialog.getInstance().buildListView(this, R.string.message_field_title, listPhonenumber, new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String tag = null;
				switch(position) {
					case 0:
						tag = SmsParser.TAG_DATE;
						break;
					case 1:
						tag = SmsParser.TAG_DATE_RELATIVE;
						break;
					case 2:
						tag = SmsParser.TAG_TIME;
						break;
					case 3:
						tag = SmsParser.TAG_FIRSTNAME;
						break;
					case 4:
						tag = SmsParser.TAG_LASTNAME;
						break;
					case 5:
						tag = SmsParser.TAG_USER_FIRSTNAME;
						break;
					case 6:
						tag = SmsParser.TAG_USER_LASTNAME;
						break;
				}
				if (tag!=null) {
					int start = Math.max(etMessage.getSelectionStart(), 0);
					int end = Math.max(etMessage.getSelectionEnd(), 0);
					etMessage.getText().replace(
						Math.min(start, end), Math.max(start, end), tag, 0, tag.length()
					);
				}
			}
		});
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.show();
	}}