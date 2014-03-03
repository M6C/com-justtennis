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
import com.justtennis.business.InviteBusiness;
import com.justtennis.db.service.PlayerService;
import com.justtennis.domain.Address;
import com.justtennis.domain.Club;
import com.justtennis.domain.Invite.INVITE_TYPE;
import com.justtennis.domain.Player;
import com.justtennis.domain.Player.PLAYER_TYPE;
import com.justtennis.domain.Ranking;
import com.justtennis.domain.Tournament;
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

	private InviteBusiness business;
	private Long idPlayerForResult = null;
	private int visibilityAddressContent = View.GONE;
	private int visibilityClubContent = View.GONE;
	private int visibilityTournamentContent = View.GONE;
	private int visibilityScoreContent = View.GONE;

	private LinearLayout llInviteModify;
	private LinearLayout llAddressContent;
	private LinearLayout llClubContent;
	private LinearLayout llTournamentContent;
	private LinearLayout llScoreContent;
	private TextView tvFirstname;
	private TextView tvLastname;
	private TextView edDate;
	private TextView edTime;
	private ImageView ivPhoto;
	private Switch swType;
	private Spinner spRanking;
	private Bundle savedInstanceState;
	
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

	// ADDRESS
	private Spinner spAddress;
	private Spinner spClub;
	private Spinner spClubAddress;
	private Spinner spTournament;
	private Spinner spTournamentClub;
	private EditText etAddressLine1;
	private EditText etAddressPostalCode;
	private EditText etAddressCity;
	private View llTournamentAdd;
	private View llTournamentSelection;
	private View llClubAdd;
	private View llClubSelection;
	private View llAddressAdd;
	private View llAddressSelection;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (this.savedInstanceState==null) {
			this.savedInstanceState = savedInstanceState;
		}

		setContentView(R.layout.invite2);

		llInviteModify = (LinearLayout)findViewById(R.id.ll_invite_modify);
		tvFirstname = (TextView)findViewById(R.id.tv_firstname);
		tvLastname = (TextView)findViewById(R.id.tv_lastname);
		edDate = ((TextView)findViewById(R.id.inviteDate));
		edTime = ((TextView)findViewById(R.id.inviteTime));
		ivPhoto = (ImageView)findViewById(R.id.iv_photo);
		swType = (Switch)findViewById(R.id.sw_type);
		spRanking = (Spinner)findViewById(R.id.sp_main_ranking);

		etScore11 = (EditText)findViewById(R.id.et_score1_1);
		etScore21 = (EditText)findViewById(R.id.et_score2_1);
		etScore12 = (EditText)findViewById(R.id.et_score1_2);
		etScore22 = (EditText)findViewById(R.id.et_score2_2);
		etScore13 = (EditText)findViewById(R.id.et_score1_3);
		etScore23 = (EditText)findViewById(R.id.et_score2_3);
		etScore14 = (EditText)findViewById(R.id.et_score1_4);
		etScore24 = (EditText)findViewById(R.id.et_score2_4);
		etScore15 = (EditText)findViewById(R.id.et_score1_5);
		etScore25 = (EditText)findViewById(R.id.et_score2_5);

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

		llAddressAdd = findViewById(R.id.ll_address_add);
		llAddressSelection = findViewById(R.id.ll_address_selection);
		spAddress = (Spinner)findViewById(R.id.sp_address_list);
		llAddressContent = (LinearLayout)findViewById(R.id.ll_address_content);
		etAddressLine1 = (EditText)findViewById(R.id.et_address_line_1);
		etAddressPostalCode = (EditText)findViewById(R.id.et_address_postal_code);
		etAddressCity = (EditText)findViewById(R.id.et_address_city);

		llClubAdd = findViewById(R.id.ll_club_add);
		llClubSelection = findViewById(R.id.ll_club_selection);
		spClub = (Spinner)findViewById(R.id.sp_club_list);
		spClubAddress = (Spinner)findViewById(R.id.sp_club_address_list);
		llClubContent = (LinearLayout)findViewById(R.id.ll_club_content);

		llTournamentAdd = findViewById(R.id.ll_tournament_add);
		llTournamentSelection = findViewById(R.id.ll_tournament_selection);
		spTournament = (Spinner)findViewById(R.id.sp_tournament_list);
		spTournamentClub = (Spinner)findViewById(R.id.sp_tournament_club_list);
		llTournamentContent = (LinearLayout)findViewById(R.id.ll_tournament_content);

		llScoreContent = (LinearLayout)findViewById(R.id.ll_score_content);

		business = new InviteBusiness(this, NotifierMessageLogger.getInstance());
	}

	@Override
	protected void onResume() {
		super.onResume();
		initializeData();
		initializeListener();
		llAddressContent.setVisibility(visibilityAddressContent);
		llClubContent.setVisibility(visibilityClubContent);
		llTournamentContent.setVisibility(visibilityTournamentContent);

		visibilityScoreContent = (business.getScores() != null && business.getScores().length>0 ? View.VISIBLE : View.GONE);
		llScoreContent.setVisibility(visibilityScoreContent);
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
					long id = data.getLongExtra(ListPlayerActivity.EXTRA_PLAYER_ID, PlayerService.ID_EMPTY_PLAYER);
					if (id != PlayerService.ID_EMPTY_PLAYER) {
						idPlayerForResult = Long.valueOf(id);
					}
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
		saveAddress();
		saveClub();
		saveTournament();

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
		PLAYER_TYPE playerType = PLAYER_TYPE.ENTRAINEMENT;
		switch(business.getType()) {
			case ENTRAINEMENT:
				playerType = PLAYER_TYPE.ENTRAINEMENT;
				break;
			case MATCH:
				playerType = PLAYER_TYPE.MATCH;
				break;
		}
		intent.putExtra(PlayerActivity.EXTRA_TYPE, playerType);
		intent.putExtra(PlayerActivity.EXTRA_RANKING, business.getIdRanking());
		startActivityForResult(intent, RESULT_PLAYER);
	}

	public void onClickAddressCollapser(View view) {
		visibilityAddressContent = (visibilityAddressContent == View.GONE) ? View.VISIBLE : View.GONE;
		llAddressContent.setVisibility(visibilityAddressContent);
		llAddressAdd.setVisibility(View.GONE);
		llAddressSelection.setVisibility(View.VISIBLE);
	}
	
	public void onClickClubCollapser(View view) {
		visibilityClubContent = (visibilityClubContent == View.GONE) ? View.VISIBLE : View.GONE;
		llClubContent.setVisibility(visibilityClubContent);
		llClubAdd.setVisibility(View.GONE);
		llClubSelection.setVisibility(View.VISIBLE);
	}
	
	public void onClickTournamentCollapser(View view) {
		visibilityTournamentContent = (visibilityTournamentContent == View.GONE) ? View.VISIBLE : View.GONE;
		llTournamentContent.setVisibility(visibilityTournamentContent);
		llTournamentAdd.setVisibility(View.GONE);
		llTournamentSelection.setVisibility(View.VISIBLE);
	}
	
	public void onClickAddressAdd(View view) {
		llAddressAdd.setVisibility(View.VISIBLE);
		llAddressSelection.setVisibility(View.GONE);
	}
	
	public void onClickClubAdd(View view) {
		llClubAdd.setVisibility(View.VISIBLE);
		llClubSelection.setVisibility(View.GONE);
	}
	
	public void onClickTournamentAdd(View view) {
		llTournamentAdd.setVisibility(View.VISIBLE);
		llTournamentSelection.setVisibility(View.GONE);
	}

	public void onClickScoreCollapser(View view) {
		visibilityScoreContent = (visibilityScoreContent == View.GONE) ? View.VISIBLE : View.GONE;
		llScoreContent.setVisibility(visibilityScoreContent);
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
			idPlayerForResult = null;
		}

		initializeDataMode();
		initializeDataType();
		initializeDataDateTime();
		initializeDataPlayer();
		initializeDataScore();
		initializeRankingList();
		initializeRanking();
		initializeAddressList();
		initializeClubList();
		initializeTournamentList();
		initializeAddress();
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

	private void initializeDataType() {
		swType.setChecked(getTypePosition()==0);
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

	private void initializeAddressList() {
		ArrayAdapter<String> dataAdapter = null;

		spAddress.setVisibility(View.VISIBLE);
		dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, business.getListTxtAddress());
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spAddress.setAdapter(dataAdapter);
		spAddress.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				Address address = business.getListAddress().get(position);
				business.setAddress(address);
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		spClubAddress.setVisibility(View.VISIBLE);
		dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, business.getListTxtClub());
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spClubAddress.setAdapter(dataAdapter);
	}

	private void initializeClubList() {
		ArrayAdapter<String> dataAdapter = null;

		spClub.setVisibility(View.VISIBLE);
		dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, business.getListTxtClub());
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spClub.setAdapter(dataAdapter);
		
		spClub.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				Club club = business.getListClub().get(position);
				business.setClub(club);
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		
		spTournamentClub.setVisibility(View.VISIBLE);
		dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, business.getListTxtClub());
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spTournamentClub.setAdapter(dataAdapter);
	}
	
	private void initializeTournamentList() {
		ArrayAdapter<String> dataAdapter = null;

		spTournament.setVisibility(View.VISIBLE);
		dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, business.getListTxtTournament());
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spTournament.setAdapter(dataAdapter);
		
		spTournament.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				Tournament tournament = business.getListTournament().get(position);
				business.setTournament(tournament);
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

	private void initializeAddress() {
		Address address = business.getAddress();
		int position = 0;
		List<Address> listAddress = business.getListAddress();
		for(Address ranking : listAddress) {
			if (ranking.getId().equals(address.getId())) {
				spAddress.setSelection(position, true);
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
		swType.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				business.setType(isChecked ? INVITE_TYPE.ENTRAINEMENT : INVITE_TYPE.MATCH);
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

	private void saveAddress() {
		if (visibilityAddressContent == View.VISIBLE) {
			Address address = new Address();
			address.setLine1(getText(etAddressLine1));
			address.setPostalCode(getText(etAddressPostalCode));
			address.setCity(getText(etAddressCity));
			business.setAddress(address);
		}
	}

	private void saveClub() {
		if (visibilityClubContent == View.VISIBLE) {
			Club club = new Club();
			business.setClub(club);
		}
	}

	private void saveTournament() {
		if (visibilityTournamentContent == View.VISIBLE) {
			Tournament tournament = new Tournament();
			business.setTournament(tournament);
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
	
	private String getText(EditText editText) {
		String text = editText.getText().toString();
		if ("".equals(text)) {
			text = null;
		}
		return text;
	}
}