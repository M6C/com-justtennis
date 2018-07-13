
package com.justtennis.ui.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.cameleon.common.android.factory.FactoryDialog;
import com.justtennis.R;
import com.justtennis.activity.GenericSpinnerFormActivity;
import com.justtennis.activity.LocationActivity;
import com.justtennis.activity.LocationAddressActivity;
import com.justtennis.business.PlayerBusiness;
import com.justtennis.business.UserBusiness;
import com.justtennis.domain.Address;
import com.justtennis.domain.User;
import com.justtennis.listener.action.TextWatcherFieldEnableView;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.parser.SmsParser;
import com.justtennis.ui.common.CommonEnum;
import com.justtennis.ui.rxjava.RxFragment;

import java.io.Serializable;


public class UserFragment extends PlayerFragment {

	private static final String TAG = UserFragment.class.getSimpleName();

	private static final int RESULT_LOCATION_DETAIL = 3;

	private TextView tvMessage;
	private EditText etMessage;
	private LinearLayout llType;
	private LinearLayout llMessage;
	private Spinner spSaison;
	private ImageView ivAjoutChamp;
	private UserBusiness business;

	private Serializable addressFromResult;

	public static UserFragment build() {
		UserFragment fragment = new UserFragment();
		Bundle args = new Bundle();
		args.putSerializable(PlayerFragment.EXTRA_MODE, CommonEnum.PLAYER_MODE.FOR_RESULT);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onResume() {
		super.onResume();
		etMessage.setText(business.getMessage());
		RxFragment.publish(RxFragment.SUBJECT_ON_SHOW, TAG);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
	protected void initializeViewById() {
		super.initializeViewById();
		tvMessage = rootView.findViewById(R.id.tv_message);
		etMessage = rootView.findViewById(R.id.et_message);
		spSaison = rootView.findViewById(R.id.sp_saison);
		llType = rootView.findViewById(R.id.ll_type);
		llMessage = rootView.findViewById(R.id.ll_message);
		ivAjoutChamp = rootView.findViewById(R.id.iv_ajout_champ);
	}

	@Override
	protected void initializeView() {
		super.initializeView();
		llType.setVisibility(View.GONE);
		llMessage.setVisibility(View.VISIBLE);
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

	@Override
	protected PlayerBusiness createBusiness() {
		business = new UserBusiness(context, NotifierMessageLogger.getInstance());
		return business;
	}

	@Override
	protected void initializeListener() {
		super.initializeListener();
		etMessage.addTextChangedListener(new TextWatcherFieldEnableView(tvMessage, View.GONE));
		ivAjoutChamp.setOnClickListener(this::onClickMenuAjoutChamp);
	}

	@Override
	public void onClickModify(View view) {
		super.onClickModify(view);
		business.saveMessage(etMessage.getText().toString());
	}

	@Override
	public void onClickLocationDetail(View view) {
		Intent intent = new Intent(context, LocationAddressActivity.class);
		User user = (User) business.getPlayer();
		if (user.getIdAddress() != null) {
			intent.putExtra(GenericSpinnerFormActivity.EXTRA_DATA, new Address(user.getIdAddress()));
		}
		startActivityForResult(intent, RESULT_LOCATION_DETAIL);
	}

	public void onClickMenuAjoutChamp(View view) {
		String[] listPhoneNumber = new String[] {
				getString(R.string.message_field_date),
				getString(R.string.message_field_date_relative),
				getString(R.string.message_field_time),
				getString(R.string.message_field_player_firstname),
				getString(R.string.message_field_player_lastname),
				getString(R.string.message_field_user_firstname),
				getString(R.string.message_field_user_lastname)
		};
		Dialog dialog = FactoryDialog.getInstance().buildListView(activity, R.string.message_field_title, listPhoneNumber, this::onItemClickMenuAjoutChamp);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.show();
	}

	private void onItemClickMenuAjoutChamp(AdapterView<?> parent, View view1, int position, long id) {
		String tag;
		switch (position) {
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
			default:
				tag = null;
		}
		if (tag != null) {
			int start = Math.max(etMessage.getSelectionStart(), 0);
			int end = Math.max(etMessage.getSelectionEnd(), 0);
			etMessage.getText().replace(
					Math.min(start, end), Math.max(start, end), tag, 0, tag.length()
			);
		}
	}
}