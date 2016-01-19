package com.justtennis.activity;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
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
import com.cameleon.common.tool.StringTool;
import com.justtennis.ApplicationConfig;
import com.justtennis.R;
import com.justtennis.adapter.CustomArrayAdapter;
import com.justtennis.adapter.manager.BonusListManager;
import com.justtennis.business.InviteBusiness;
import com.justtennis.db.service.PlayerService;
import com.justtennis.domain.Player;
import com.justtennis.domain.Ranking;
import com.justtennis.domain.Saison;
import com.justtennis.domain.ScoreSet;
import com.justtennis.listener.action.TextWatcherFieldEnableView;
import com.justtennis.manager.ContactManager;
import com.justtennis.manager.TypeManager;
import com.justtennis.notifier.NotifierMessageLogger;

public class InviteActivity extends GenericActivity {

	private static final String TAG = InviteActivity.class.getSimpleName();
	@SuppressWarnings("unused")
	private static final String KEY_LOCATION_FROM_RESULT = "KEY_LOCATION_FROM_RESULT";

	public enum MODE {
		INVITE_MODIFY
	};
	public static final String EXTRA_MODE = "MODE";
	public static final String EXTRA_INVITE = "INVITE";
	public static final String EXTRA_USER = "USER";
	public static final String EXTRA_PLAYER_ID = "PLAYER_ID";
	private static final int RESULT_PLAYER = 1;
	private static final int RESULT_LOCATION = 2;
	private static final int RESULT_LOCATION_DETAIL = 3;
	private static final int RESULT_SCORE = 4;

	private Bundle savedInstanceState;
	private InviteBusiness business;
	private Long idPlayerFromResult = null;
	private Serializable locationFromResult;
	private Serializable locationClubFromResult;

	private LinearLayout llInviteModify;
	private TextView tvFirstname;
	private TextView tvLastname;
	private TextView edDate;
	private TextView edTime;
	private ImageView ivPhoto;
	private Switch swType;
	private Spinner spRanking;
	private Spinner spSaison;
	private TextView tvLocation;
	private TextView tvLocationEmpty;

	private LinearLayout llLocationDetail;
	private TextView tvLocationName;
	private TextView tvLocationLine1;
	private TextView tvLocationLine2;

	// SCORE
	private TextView tvScore;
	private EditText etScore;
	private BonusListManager bonusListManager;
	private TypeManager typeManager;

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
		spSaison = (Spinner)findViewById(R.id.sp_main_saison);
		tvLocation = ((TextView)findViewById(R.id.tv_location));
		tvLocationEmpty = ((TextView)findViewById(R.id.et_location));
		llLocationDetail = (LinearLayout)findViewById(R.id.ll_location_detail);
		tvLocationName = ((TextView)findViewById(R.id.tv_location_name));
		tvLocationLine1 = ((TextView)findViewById(R.id.tv_location_line1));
		tvLocationLine2 = ((TextView)findViewById(R.id.tv_location_line2));
		tvScore = (TextView)findViewById(R.id.tv_score);
		etScore = (EditText)findViewById(R.id.et_score);

		NotifierMessageLogger notifier = NotifierMessageLogger.getInstance();
		business = new InviteBusiness(this, notifier);

		typeManager = TypeManager.getInstance();
		typeManager.initializeActivity(findViewById(R.id.layout_main), false);
		bonusListManager = BonusListManager.getInstance(this, notifier);

		etScore.addTextChangedListener(new TextWatcherFieldEnableView(tvScore, View.GONE));
	}

	@Override
	protected void onResume() {
		super.onResume();
		Intent intent = getIntent();
		if (savedInstanceState!=null) {
			business.initializeData(savedInstanceState);
			savedInstanceState = null;
		}
		else {
			business.initializeData(intent);
		}

		if (idPlayerFromResult != null) {
			business.setPlayer(idPlayerFromResult);
			idPlayerFromResult = null;
		}

		if (locationFromResult != null) {
			business.setLocation(locationFromResult);
			locationFromResult = null;
		}
		
		if (locationClubFromResult != null) {
			business.setLocationClub(locationClubFromResult);
			locationFromResult = null;
		}
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
					long id = data.getLongExtra(ListPlayerActivity.EXTRA_PLAYER_ID, PlayerService.ID_EMPTY_PLAYER);
					if (id != PlayerService.ID_EMPTY_PLAYER) {
						idPlayerFromResult = Long.valueOf(id);
					}
				}
				break;
			case RESULT_LOCATION:
				if (data != null) {
					locationFromResult = data.getSerializableExtra(LocationActivity.EXTRA_OUT_LOCATION);
				}
				break;
			case RESULT_LOCATION_DETAIL:
				if (data != null) {
					locationClubFromResult = data.getSerializableExtra(LocationActivity.EXTRA_OUT_LOCATION);
				}
				break;
	
			case RESULT_SCORE:
				String[][] score = null;
				if (resultCode == RESULT_OK) {
					score = deSerializeScore(data);
					business.setScores(score);
				} else {
					score = business.getScores();
				}
				List<ScoreSet> listScoreSet = business.computeScoreSet(score);
				business.getInvite().setListScoreSet(listScoreSet);
				business.getInvite().setScoreResult(business.computeScoreResult(listScoreSet));
				initializeDataScore();
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

	public void onClickInviteScore(View view) {
		Intent intent = new Intent(this,  ScoreActivity.class);
		intent.putExtra(ScoreActivity.EXTRA_SCORE, business.getScores());
		startActivityForResult(intent, RESULT_SCORE);
	}

	public void onClickCancel(View view) {
		finish();
	}

	public void onClickPlayer(View view) {
		Intent intent = new Intent(this, ListPlayerActivity.class);
		intent.putExtra(ListPlayerActivity.EXTRA_MODE, ListPlayerActivity.MODE.FOR_RESULT);
		TypeManager.TYPE playerType = TypeManager.TYPE.TRAINING;
		switch(business.getType()) {
			case TRAINING:
				playerType = TypeManager.TYPE.TRAINING;
				break;
			case COMPETITION:
				playerType = TypeManager.TYPE.COMPETITION;
				break;
		}
		intent.putExtra(PlayerActivity.EXTRA_TYPE, playerType);
		intent.putExtra(PlayerActivity.EXTRA_RANKING, business.getIdRanking());
		startActivityForResult(intent, RESULT_PLAYER);
	}

	public void onClickLocation(View view) {
		Intent intent = null;
		switch(business.getType()) {
			case TRAINING:
				intent = new Intent(this, LocationClubActivity.class);
				if (business.getInvite().getClub() != null) {
					intent.putExtra(GenericSpinnerFormActivity.EXTRA_DATA, business.getInvite().getClub());
				}
				break;
			case COMPETITION:
				intent = new Intent(this, LocationTournamentActivity.class);
				if (business.getInvite().getTournament() != null) {
					intent.putExtra(GenericSpinnerFormActivity.EXTRA_DATA, business.getInvite().getTournament());
				}
				break;
		}
		intent.putExtra(LocationActivity.EXTRA_INVITE, business.getInvite());
		startActivityForResult(intent, RESULT_LOCATION);
	}

	public void onClickLocationDetail(View view) {
		if (business.getType() == TypeManager.TYPE.COMPETITION) {
			Intent intent = new Intent(this, LocationClubActivity.class);
			if (business.getInvite().getClub() != null) {
				intent.putExtra(GenericSpinnerFormActivity.EXTRA_DATA, business.getInvite().getClub());
			}
			intent.putExtra(LocationActivity.EXTRA_INVITE, business.getInvite());
			startActivityForResult(intent, RESULT_LOCATION_DETAIL);
		}
	}

	public void onClickLocationMap(View view) {
		String[] locationLineUser = business.getLocationLineUser();
		String[] locationLine = business.getLocationLine();

		if (locationLineUser != null && locationLine != null) {
			String addressUser = null;
			String address = null;
			String line = null;

			for(int i=1 ; i<locationLineUser.length ; i++) {
				line = locationLineUser[i];
				if (addressUser == null) {
					addressUser = line;
				} else {
					addressUser += "," + line;
				}
			}
			
			for(int i=1 ; i<locationLine.length ; i++) {
				line = locationLine[i];
				if (address == null) {
					address = line;
				} else {
					address += "," + line;
				}
			}

			String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%s&daddr=%s", addressUser, address);
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
			intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
			startActivity(intent);
		}
	}

	private void initializeData() {
		initializeDataMode();
		initializeDataType();
		initializeDataDateTime();
		initializeDataPlayer();
		initializeDataScore();
		initializeDataLocation();
		initializeRankingList();
		initializeRanking();
		initializeBonus();
		initializeSaisonList();
		initializeSaison();
	}

	private void initializeDataPlayer() {
		Log.d(TAG, "initializeDataPlayer");
		Player player = business.getPlayer();
		if (player!=null) {
			tvFirstname.setText(player.getFirstName());
			tvLastname.setText(player.getLastName());
			if (player.getIdGoogle()!=null && player.getIdGoogle().longValue()>0l) {
				ivPhoto.setImageBitmap(ContactManager.getInstance(this).getPhoto(player.getIdGoogle()));
			}
		}
	}

	private void initializeDataDateTime() {
		Log.d(TAG, "initializeDataTime");
		Date date = business.getDate();

		DateFormat sdfD = new SimpleDateFormat(getString(R.string.msg_common_format_date), ApplicationConfig.getLocal());
		DateFormat sdfT = new SimpleDateFormat(getString(R.string.msg_common_format_time), ApplicationConfig.getLocal());
		edDate.setText(sdfD.format(date));
		edTime.setText(sdfT.format(date));
	}

	private void initializeDataType() {
		Log.d(TAG, "initializeDataType");
		swType.setChecked(getTypePosition()==0);
	}

	private void initializeRankingList() {
		Log.d(TAG, "initializeRankingList");
		spRanking.setVisibility(View.VISIBLE);
		CustomArrayAdapter<String> dataAdapter = new CustomArrayAdapter<String>(this, business.getListTxtRankings());
		spRanking.setAdapter(dataAdapter);

		spRanking.setOnItemSelectedListener(dataAdapter.new OnItemSelectedListener<Ranking>() {
			@Override
			public Ranking getItem(int position) {
				return business.getListRanking().get(position);
			}

			@Override
			public boolean isHintItemSelected(Ranking item) {
				return business.isEmptyRanking(item);
			}

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id, Ranking item) {
				business.setIdRanking(item.getId());
			}
		});
	}

	private void initializeSaisonList() {
		Log.d(TAG, "initializeSaisonList");
		CustomArrayAdapter<String> dataAdapter = new CustomArrayAdapter<String>(this, business.getListTxtSaisons());
		spSaison.setAdapter(dataAdapter);

		spSaison.setOnItemSelectedListener(dataAdapter.new OnItemSelectedListener<Saison>() {
			@Override
			public Saison getItem(int position) {
				return business.getListSaison().get(position);
			}

			@Override
			public boolean isHintItemSelected(Saison item) {
				return business.isEmptySaison(item);
			}

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id, Saison item) {
				business.setSaison(business.getListSaison().get(position));
			}
		});
	}

	private void initializeSaison() {
		Log.d(TAG, "initializeSaison");
		Saison saison = business.getSaison();
		int position = 0;
		List<Saison> listSaison = business.getListSaison();
		for(Saison item : listSaison) {
			if (item.equals(saison)) {
				spSaison.setSelection(position, true);
				break;
			} else {
				position++;
			}
		}
	}

	private void initializeBonus() {
		bonusListManager.manage(this, business.getInvite());
	}

	private void initializeRanking() {
		Log.d(TAG, "initializeRanking");
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

	private void initializeDataMode() {
		Log.d(TAG, "initializeDataMode");
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
		Log.d(TAG, "initializeDataScore");
		String textScore = business.getScoresText();
		etScore.setText(textScore == null ? "" : Html.fromHtml(textScore));
	}

	private void initializeDataLocation() {
		Log.d(TAG, "initializeDataLocation");
		String[] location = business.getLocationLine();
		if (business.getType() == TypeManager.TYPE.TRAINING) {
			tvLocation.setText(getString(R.string.txt_club));
			tvLocationEmpty.setText(getString(R.string.txt_club));
		} else {
			tvLocation.setText(getString(R.string.txt_tournament));
			tvLocationEmpty.setText(getString(R.string.txt_tournament));
		}
		((EditText)tvLocationEmpty).setTextColor(((EditText)tvLocationEmpty).getCurrentHintTextColor());

		if (location != null) {
			tvLocationName.setText(location[0]);
			tvLocationLine1.setText(location[1]);
			tvLocationLine2.setText(location[2]);

			tvLocationName.setVisibility(StringTool.getInstance().isEmpty(location[0]) ? View.GONE : View.VISIBLE);
			tvLocationLine1.setVisibility(StringTool.getInstance().isEmpty(location[1]) ? View.GONE : View.VISIBLE);
			tvLocationLine2.setVisibility(StringTool.getInstance().isEmpty(location[2]) ? View.GONE : View.VISIBLE);

			tvLocation.setVisibility(View.VISIBLE);
			llLocationDetail.setVisibility(View.VISIBLE);
			tvLocationEmpty.setText("");
			tvLocationEmpty.setTextSize(2);
		} else {
			tvLocation.setVisibility(View.GONE);
			llLocationDetail.setVisibility(View.GONE);
			tvLocationEmpty.setVisibility(View.VISIBLE);
			tvLocationEmpty.setTextSize(22);
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
				business.setType(isChecked ? TypeManager.TYPE.TRAINING : TypeManager.TYPE.COMPETITION);
			}
		});
	}
	
	private int getTypePosition() {
		switch(business.getInvite().getType()) {
			case COMPETITION:
				return 1;
			case TRAINING:
			default:
				return 0;
		}
	}

	private String[][] deSerializeScore(Intent data) {
		String[][] score = null;
		if (data != null) {
			Object[] d = (Object[]) data.getSerializableExtra(ScoreActivity.EXTRA_SCORE);
			if (d != null && d.length > 0) {
				score = new String[d.length][];
				for(int i=0 ; i<d.length ; i++) {
					score[i] = (String[]) d[i];
				}
			}
		}
		return score;
	}
}