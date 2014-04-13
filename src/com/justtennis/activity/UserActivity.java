package com.justtennis.activity;

import java.io.Serializable;
import java.util.List;

import org.gdocument.gtracergps.launcher.log.Logger;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.cameleon.common.android.factory.FactoryDialog;
import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.R;
import com.justtennis.business.UserBusiness;
import com.justtennis.domain.Club;
import com.justtennis.domain.Ranking;
import com.justtennis.domain.User;
import com.justtennis.listener.action.TextWatcherFieldEnableView;
import com.justtennis.parser.SmsParser;

public class UserActivity extends Activity implements INotifierMessage {

	private static final String TAG = UserActivity.class.getSimpleName();

	private static final int RESULT_LOCATION = 2;

	private UserBusiness business;
	private TextView tvFirstname;
	private TextView tvLastname;
	private TextView tvBirthday;
	private TextView tvPhonenumber;
	private TextView tvMessage;
	private EditText etFirstname;
	private EditText etLastname;
	private EditText etBirthday;
	private EditText etPhonenumber;
	private Spinner spRanking;
	private EditText etMessage;

	private TextView tvLocation;
	private TextView tvLocationEmpty;
	private LinearLayout llLocationDetail;
	private TextView tvLocationName;
	private TextView tvLocationLine1;
	private TextView tvLocationLine2;

	private User user;
	private Serializable locationFromResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user);

		tvFirstname = (TextView)findViewById(R.id.tv_firstname);
		tvLastname = (TextView)findViewById(R.id.tv_lastname);
		tvBirthday = (TextView)findViewById(R.id.tv_birthday);
		tvPhonenumber = (TextView)findViewById(R.id.tv_phonenumber);
		tvMessage = (TextView)findViewById(R.id.tv_message);
		etFirstname = (EditText)findViewById(R.id.et_firstname);
		etLastname = (EditText)findViewById(R.id.et_lastname);
		etBirthday = (EditText)findViewById(R.id.et_birthday);
		etPhonenumber = (EditText)findViewById(R.id.et_phonenumber);
		spRanking = (Spinner)findViewById(R.id.sp_ranking);
		etMessage = (EditText)findViewById(R.id.et_message);

		tvLocation = ((TextView)findViewById(R.id.tv_location));
		tvLocationEmpty = ((TextView)findViewById(R.id.et_location));
		llLocationDetail = (LinearLayout)findViewById(R.id.ll_location_detail);
		tvLocationName = ((TextView)findViewById(R.id.tv_location_name));
		tvLocationLine1 = ((TextView)findViewById(R.id.tv_location_line1));
		tvLocationLine2 = ((TextView)findViewById(R.id.tv_location_line2));

		initializeListener();

		business = new UserBusiness(this, this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		business.initializeData();

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, business.getListTxtRankings());
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spRanking.setAdapter(dataAdapter);

		spRanking.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				Ranking ranking = business.getListRanking().get(position);
				user.setIdRanking(ranking.getId());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		user = business.findUser();
		if (user!=null) {
			etFirstname.setText(user.getFirstName());
			etLastname.setText(user.getLastName());
			etBirthday.setText(user.getBirthday());
			etPhonenumber.setText(user.getPhonenumber());
			initializeRanking(user.getIdRanking());
		}

		etMessage.setText(business.getMessage());

		initializeLocation();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		if (business.getUserCount()==0) {
			finish();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode==RESULT_LOCATION) {
			if (data != null) {
				locationFromResult = data.getSerializableExtra(LocationActivity.EXTRA_OUT_LOCATION);
			}
		}
	}

	@Override
	public void notifyError(Exception ex) {
		Logger.logEr(TAG, ex.getMessage());
	}

	@Override
	public void notifyMessage(String msg) {
		Logger.logMe(TAG, msg);
	}

	public void onClickSubmit(View view) {
		buildUser();

		business.submit(user, etMessage.getText().toString());

		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		startActivity(intent);
	}

	public void onClickQRCode(View view) {
		buildUser();

		String qrcodeData = business.toQRCode(user);

		Intent intent = new Intent(getApplicationContext(), QRCodeActivity.class);
		intent.putExtra(QRCodeActivity.EXTRA_QRCODE_DATA, qrcodeData);
		startActivity(intent);
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
	}
	
	public void onClickLocation(View view) {
		Intent intent = null;
		switch(user.getType()) {
			case ENTRAINEMENT:
				intent = new Intent(this, LocationClubActivity.class);
				if (user.getIdClub() != null) {
					intent.putExtra(GenericSpinnerFormActivity.EXTRA_DATA, new Club(user.getIdClub()));
				}
				break;
			case MATCH:
//				intent = new Intent(this, LocationTournamentActivity.class);
//				if (business.getInvite().getTournament() != null) {
//					intent.putExtra(GenericSpinnerFormActivity.EXTRA_DATA, business.getInvite().getTournament());
//				}
				break;
		}
		if (intent != null) {
			startActivityForResult(intent, RESULT_LOCATION);
		}
	}

	private void initializeListener() {
		etFirstname.addTextChangedListener(new TextWatcherFieldEnableView(tvFirstname, View.GONE));
		etLastname.addTextChangedListener(new TextWatcherFieldEnableView(tvLastname, View.GONE));
		etBirthday.addTextChangedListener(new TextWatcherFieldEnableView(tvBirthday, View.GONE));
		etPhonenumber.addTextChangedListener(new TextWatcherFieldEnableView(tvPhonenumber, View.GONE));
		etMessage.addTextChangedListener(new TextWatcherFieldEnableView(tvMessage, View.GONE));
	}

	private void initializeRanking(Long id) {
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

	private void initializeLocation() {
		Log.d(TAG, "initializeDataLocation");
		if (locationFromResult != null) {
			setLocation(locationFromResult);
			locationFromResult = null;
		}

		String[] location = getLocationLine();
		tvLocation.setText(getString(R.string.txt_club));
		tvLocationEmpty.setText(getString(R.string.txt_club));

		if (location != null) {
			tvLocationName.setText(location[0]);
			tvLocationLine1.setText(location[1]);
			tvLocationLine2.setText(location[2]);
			tvLocation.setVisibility(View.VISIBLE);
			llLocationDetail.setVisibility(View.VISIBLE);
			tvLocationEmpty.setVisibility(View.GONE);
		} else {
			tvLocation.setVisibility(View.GONE);
			llLocationDetail.setVisibility(View.GONE);
			tvLocationEmpty.setVisibility(View.VISIBLE);
		}
	}
	
	private void buildUser() {
		if (user==null)
			user = new User();

		user.setFirstName(etFirstname.getText().toString());
		user.setLastName(etLastname.getText().toString());
		user.setBirthday(etBirthday.getText().toString());
		user.setPhonenumber(etPhonenumber.getText().toString());
	}

	private void setLocation(Serializable location) {
		user.setIdClub(((Club)location).getId());
	}

	public String[] getLocationLine() {
		return business.getLocationLine(user);
	}
}