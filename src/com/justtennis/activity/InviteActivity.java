package com.justtennis.activity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.justtennis.business.InviteBusiness;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Invite.INVITE_TYPE;
import com.justtennis.domain.Invite.STATUS;
import com.justtennis.domain.Player;
import com.justtennis.domain.Ranking;
import com.justtennis.listener.action.TextWatcherFieldScoreSetBold;
import com.justtennis.manager.ContactManager;
import com.justtennis.notifier.NotifierMessageLogger;

public class InviteActivity extends Activity {

	@SuppressWarnings("unused")
	private static final String TAG = InviteActivity.class.getSimpleName();

	public enum MODE {
		INVITE_MODIFY
	};
	public static final String EXTRA_MODE = "MODE";
	public static final String EXTRA_INVITE = "INVITE";
	public static final String EXTRA_PLAYER_ID = "PLAYER_ID";
	private static final int RESULT_PLAYER = 1;

	private final Integer[] drawableStatus = new Integer[] {R.drawable.check_green, R.drawable.check_red, R.drawable.check_yellow};
//	private final Integer[] drawableType = new Integer[] {R.drawable.check_green, R.drawable.check_red, R.drawable.check_yellow};
	private final Integer[] drawableType = new Integer[] {R.layout.element_invite_type_entrainement, R.layout.element_invite_type_match};

	private InviteBusiness business;

	private LinearLayout llInviteModify;
	private TextView tvFirstname;
	private TextView tvLastname;
	private TextView edDate;
	private TextView edTime;
	private ImageView ivPhoto;
	private Spinner spStatus;
	private Spinner spType;
	private Spinner spRanking;
	private Bundle savedInstanceState;
	private BaseImageAdapter adapterStatus;
	private BaseViewAdapter adapterType;
	
	// SCORE
	private EditText etScore11;
	private EditText etScore21;
	private EditText etScore12;
	private EditText etScore22;
	private EditText etScore13;
	private EditText etScore23;
	private EditText etScore14;
	private EditText etScore24;
	private EditText etScore15;
	private EditText etScore25;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (this.savedInstanceState==null) {
			this.savedInstanceState = savedInstanceState;
		}

		setContentView(R.layout.invite);

		llInviteModify = (LinearLayout)findViewById(R.id.ll_invite_modify);
		tvFirstname = (TextView)findViewById(R.id.tv_firstname);
		tvLastname = (TextView)findViewById(R.id.tv_lastname);
		edDate = ((TextView)findViewById(R.id.inviteDate));
		edTime = ((TextView)findViewById(R.id.inviteTime));
		ivPhoto = (ImageView)findViewById(R.id.iv_photo);
		spStatus = (Spinner)findViewById(R.id.sp_main_status);
		spType = (Spinner)findViewById(R.id.sp_main_type);
		spRanking = (Spinner)findViewById(R.id.sp_main_ranking);

		etScore11 = ((EditText)findViewById(R.id.et_score1_1));
		etScore21 = ((EditText)findViewById(R.id.et_score2_1));
		etScore12 = ((EditText)findViewById(R.id.et_score1_2));
		etScore22 = ((EditText)findViewById(R.id.et_score2_2));
		etScore13 = ((EditText)findViewById(R.id.et_score1_3));
		etScore23 = ((EditText)findViewById(R.id.et_score2_3));
		etScore14 = ((EditText)findViewById(R.id.et_score1_4));
		etScore24 = ((EditText)findViewById(R.id.et_score2_4));
		etScore15 = ((EditText)findViewById(R.id.et_score1_5));
		etScore25 = ((EditText)findViewById(R.id.et_score2_5));

		etScore11.addTextChangedListener(new TextWatcherFieldScoreSetBold(etScore11, etScore21));
		etScore21.addTextChangedListener(new TextWatcherFieldScoreSetBold(etScore21, etScore11));
		etScore12.addTextChangedListener(new TextWatcherFieldScoreSetBold(etScore12, etScore22));
		etScore22.addTextChangedListener(new TextWatcherFieldScoreSetBold(etScore22, etScore12));
		etScore13.addTextChangedListener(new TextWatcherFieldScoreSetBold(etScore13, etScore23));
		etScore23.addTextChangedListener(new TextWatcherFieldScoreSetBold(etScore23, etScore13));
		etScore14.addTextChangedListener(new TextWatcherFieldScoreSetBold(etScore14, etScore24));
		etScore24.addTextChangedListener(new TextWatcherFieldScoreSetBold(etScore24, etScore14));
		etScore15.addTextChangedListener(new TextWatcherFieldScoreSetBold(etScore15, etScore25));
		etScore25.addTextChangedListener(new TextWatcherFieldScoreSetBold(etScore25, etScore15));

		business = new InviteBusiness(this, NotifierMessageLogger.getInstance());

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

	public void onClickModify(View view) {
		saveScores();

		business.modify();
		
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
		initializeDataScore();
		initializeRankingList();
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
		spStatus.setSelection(getStatusPosition());
	}

	private void initializeDataType() {
		spType.setSelection(getTypePosition());
	}

	private void initializeRankingList() {
		spRanking.setVisibility(View.VISIBLE);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, business.getListTxtRankings());
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spRanking.setAdapter(dataAdapter);

		spRanking.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				Ranking ranking = business.getListRanking().get(position);
				business.setIdRanking(ranking.getId());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		initializeRanking(business.getIdRanking());
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

	private void initializeDataMode() {
		switch (business.getMode()) {
			default:
			case INVITE_MODIFY:
				llInviteModify.setVisibility(View.VISIBLE);
				edDate.setEnabled(true);
				edTime.setEnabled(true);
				break;
		}
	}

	private void initializeDataScore() {

		String[][] scores = business.getScores();
		if (scores!=null) {
			int len = scores.length;
			for(int row = 1 ; row <= len ; row++) {
				String[] score = scores[row-1];
				switch(row) {
					case 1:
					default: {
						etScore11.setText(score[0]);
						etScore21.setText(score[1]);
					}
					break;
					case 2: {
						etScore12.setText(score[0]);
						etScore22.setText(score[1]);
					}
					break;
					case 3: {
						etScore13.setText(score[0]);
						etScore23.setText(score[1]);
					}
					break;
					case 4: {
						etScore14.setText(score[0]);
						etScore24.setText(score[1]);
					}
					break;
					case 5: {
						etScore15.setText(score[0]);
						etScore25.setText(score[1]);
					}
					break;
				}
			}
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

	private void saveScores() {
		String[][] scores = new String[][]{
				{etScore11.getText().toString(), etScore21.getText().toString()},
				{etScore12.getText().toString(), etScore22.getText().toString()},
				{etScore13.getText().toString(), etScore23.getText().toString()},
				{etScore14.getText().toString(), etScore24.getText().toString()},
				{etScore15.getText().toString(), etScore25.getText().toString()}
			};
		business.setScores(scores);
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