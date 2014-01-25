package com.justtennis.activity;

import java.util.List;

import org.gdocument.gtracergps.launcher.log.Logger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.R;
import com.justtennis.business.UserBusiness;
import com.justtennis.domain.Ranking;
import com.justtennis.domain.User;

public class UserActivity extends Activity implements INotifierMessage {

	private static final String TAG = UserActivity.class.getSimpleName();

	private UserBusiness business;
	private EditText etFirstname;
	private EditText etLastname;
	private EditText etBirthday;
	private EditText etPhonenumber;
	private Spinner spRanking;

	private User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user);

		etFirstname = (EditText)findViewById(R.id.et_firstname);
		etLastname = (EditText)findViewById(R.id.et_lastname);
		etBirthday = (EditText)findViewById(R.id.et_birthday);
		etPhonenumber = (EditText)findViewById(R.id.et_phonenumber);
		spRanking = (Spinner)findViewById(R.id.sp_ranking);

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
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		if (business.getUserCount()==0) {
			finish();
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

		business.submit(user);

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
	
	private void buildUser() {
		if (user==null)
			user = new User();

		user.setFirstName(etFirstname.getText().toString());
		user.setLastName(etLastname.getText().toString());
		user.setBirthday(etBirthday.getText().toString());
		user.setPhonenumber(etPhonenumber.getText().toString());
	}
}