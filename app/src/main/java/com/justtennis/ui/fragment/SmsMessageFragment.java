
package com.justtennis.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cameleon.common.android.factory.FactoryDialog;
import com.justtennis.R;
import com.justtennis.business.SmsMessageBusiness;
import com.justtennis.listener.action.TextWatcherFieldEnableView;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.parser.SmsParser;
import com.justtennis.tool.FragmentTool;
import com.justtennis.ui.common.CommonEnum;


public class SmsMessageFragment extends Fragment {

	public static final String TAG = SmsMessageFragment.class.getSimpleName();

	private TextView tvMessage;
	private EditText etMessage;
	private ImageView ivAjoutChamp;
	private SmsMessageBusiness business;

	private Bundle savedInstanceState;
	private View rootView;
	private FragmentActivity activity;

	public static SmsMessageFragment build() {
		SmsMessageFragment fragment = new SmsMessageFragment();
		Bundle args = new Bundle();
		args.putSerializable(PlayerFragment.EXTRA_MODE, CommonEnum.PLAYER_MODE.FOR_RESULT);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_sms_message, container, false);
		if (this.savedInstanceState==null) {
			this.savedInstanceState = savedInstanceState;
		}

		activity = getActivity();
		Context context = activity.getApplicationContext();

		initializeViewById();

		business = new SmsMessageBusiness(context, NotifierMessageLogger.getInstance());

		initializeListener();

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();

		initializeFab();
		etMessage.setText(business.getMessage());
	}

	protected void initializeViewById() {
		tvMessage = rootView.findViewById(R.id.tv_message);
		etMessage = rootView.findViewById(R.id.et_message);
		ivAjoutChamp = rootView.findViewById(R.id.iv_ajout_champ);
	}

	protected void initializeListener() {
		etMessage.addTextChangedListener(new TextWatcherFieldEnableView(tvMessage, View.GONE));
		ivAjoutChamp.setOnClickListener(this::onClickMenuAjoutChamp);
	}

	private void initializeFab() {
		FragmentTool.initializeFabDrawable(activity, FragmentTool.INIT_FAB_IMAGE.VALIDATE);
		FragmentTool.onClickFab(activity, (View view) -> {
			business.saveMessage(etMessage.getText().toString());
		});
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