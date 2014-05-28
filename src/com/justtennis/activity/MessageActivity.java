package com.justtennis.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;

import com.cameleon.common.android.factory.FactoryDialog;
import com.justtennis.R;
import com.justtennis.business.MessageBusiness;
import com.justtennis.manager.TypeManager;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.parser.SmsParser;

public class MessageActivity extends GenericActivity {

	private static final String TAG = MessageActivity.class.getSimpleName();

	private MessageBusiness business;
	private EditText etMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message);

		etMessage = (EditText)findViewById(R.id.et_message);

		business = new MessageBusiness(this, NotifierMessageLogger.getInstance());
		TypeManager.getInstance().initializeActivity(findViewById(R.id.layout_main), false);
	}

	@Override
	protected void onResume() {
		initialize();
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

	private void initialize() {
		Intent intent = getIntent();
		business.initialize(intent);

		etMessage.setText(business.getMessage());
	}

	public void onClickCreate(View view) {
		business.save(etMessage.getText().toString());

		finish();
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
}